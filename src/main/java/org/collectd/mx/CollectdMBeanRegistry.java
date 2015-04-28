/*
 * jcollectd
 * Copyright (C) 2009 Hyperic, Inc.
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; only version 2 of the License is applicable.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA  02110-1301 USA
 */

package org.collectd.mx;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import net.sf.jsr107cache.Cache; 

import org.collectd.protocol.DataSource;
import org.collectd.protocol.Network;
import org.collectd.protocol.Notification;
import org.collectd.protocol.Dispatcher;
import org.collectd.protocol.PluginData;
import org.collectd.protocol.UdpReceiver;
import org.collectd.protocol.ValueList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.co.llabor.cache.Manager;

/**
 * Convert collectd value_list_t structures to JMX MBeans.
 * collectd notifications are broadcast as JMX notifications.  
 */
public class CollectdMBeanRegistry
    implements Dispatcher, NotificationBroadcaster, CollectdMBeanRegistryMBean {

    private static final String COLLECTD_RRD_DATA_CACHE_KEY = "collectd_4_RRD_data";

	public static final String DOMAIN = "collectd";

    private Map<ObjectName,Map<String,Number>> beans =
        new HashMap<ObjectName,Map<String,Number>>();
    private NotificationBroadcasterSupport _broadcaster =
        new NotificationBroadcasterSupport();
    private static Pattern _hosts = hostPattern();

    private long _notifSequence = 0;
    private boolean _doSummary =
        !"false".equals(Network.getProperty("mx.summary"));



	private static final Logger _log = LoggerFactory.getLogger(CollectdMBeanRegistry.class .getName());

    public void init() throws Exception {
        MBeanServer bs =
            ManagementFactory.getPlatformMBeanServer();    	
        ObjectName name = new ObjectName(DOMAIN + ":" + "type=" + "MBeanRegistry");
        try{
        	bs.registerMBean(this, name);
        }catch(InstanceAlreadyExistsException e)//javax.management.InstanceAlreadyExistsException: collectd:type=MBeanRegistry
        {
        	MBeanInfo oldOne = bs.getMBeanInfo(name);
        	System.out.println("ungeristered MBean:"+oldOne);
        	bs.unregisterMBean(name);
        	bs.registerMBean(this, name);
        }
        
   }

    private static Pattern hostPattern() {
        String hosts = Network.getProperty("mx.hosts");
        if (hosts == null) {
            return null;
        }
        return Pattern.compile(hosts);
    }

    private static boolean excludeHost(PluginData data) {
        if (_hosts == null) {
            return false;
        }
        return !_hosts.matcher(data.getHost()).find();
    }

    public void dispatch(Notification notif) {
        if (excludeHost(notif)) {
            return;
        }
        _broadcaster.sendNotification(new javax.management.
                Notification(notif.getSeverityString(),
                             notif.getSource(),
                             ++_notifSequence,
                             notif.getTime(),
                             notif.getMessage()));
    }

    private String getRootName(String host, ValueList vl) {
        StringBuffer name = new StringBuffer();
        name.append(DOMAIN).append(':');
        if (host != null) {
            name.append("host=").append(host).append(',');
        }
        name.append("plugin=").append(vl.getPlugin());
        if (host == null) {
            name.append(",*");
        }
        return name.toString();
    }

    Map<String,Number> getMBean(ObjectName name) {
        return beans.get(name);
    }

    Number getMBeanAttribute(ObjectName name, String attribute) {
        Map<String,Number> bean = getMBean(name);
        if (bean == null) {
            return null;
        }
        return bean.get(attribute);
    }

    private Map<String,Number> getMBean(ValueList vl) {
        String instance = vl.getPluginInstance();

        StringBuffer bname = new StringBuffer();
        bname.append(getRootName(vl.getHost(), vl));
        if (!vl.defined(instance)) {
            List<DataSource> ds = vl.getDataSource();
            if ((ds != null) && (ds.size() > 1)) {
                //e.g. ds = {rx,tx} -> type=if_octets,typeInstance=en1 
                instance = vl.getTypeInstance();
            }
        }
        if (vl.defined(instance)) {
            bname.append(',').append("name=").append(instance);
        }

        ObjectName name;
        try {
            name = new ObjectName(bname.toString());
        } catch (MalformedObjectNameException e) {
            throw new IllegalArgumentException(bname + ": " + e);
        }

        Map<String,Number> metrics = getMBean(name);
        if (metrics != null) {
            return metrics;
        }

        metrics = new HashMap<String,Number>();
        beans.put(name, metrics);

        try {
            MBeanServer bs =
                ManagementFactory.getPlatformMBeanServer();
        	try{
        		bs.unregisterMBean(name);
        	}catch(InstanceNotFoundException e){/*  ignore this case */}
            CollectdMBean newCollectdMBean = new CollectdMBean(metrics);
			bs.registerMBean(newCollectdMBean, name);

            if (_doSummary) {
                ObjectName sname =
                    new ObjectName(getRootName("__summary__", vl));
                if (!bs.isRegistered(sname)) {
                    ObjectName query = new ObjectName(getRootName(null, vl));
                    CollectdSummaryMBean summary =
                        new CollectdSummaryMBean(query, metrics);
                    summary.setMBeanRegistry(this);
                    bs.registerMBean(summary, sname);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return metrics;
    }

    public void dispatch(ValueList vl) {
        if (excludeHost(vl)) {
            return;
        }
        String type = vl.getType();
        List<Number> values = vl.getValues();
        int size = values.size();
        Map<String,Number> metrics = getMBean(vl);
        String key;

        if (size == 1) {
            String ti = vl.getTypeInstance();
            if (vl.defined(ti)) {
                key = type + "." + ti;
            }
            else {
                key = type;
            }
            Number number = values.get(0);
			metrics.put(key, number);
			Cache cache = Manager.getCache(DOMAIN);
			String o = ""+cache.get(COLLECTD_RRD_DATA_CACHE_KEY);
			o = o==null?"":o;
			o+="\n";
			String data = ""+vl;
			if (o.indexOf(data)==-1){// workaround for duplicate data
				o+=vl;
				cache.put(COLLECTD_RRD_DATA_CACHE_KEY,o);
				_log.trace( "collected:{} :={}:", vl );//+key, number
			}
        }
        else {
            List<DataSource> ds = vl.getDataSource();
            for (int i=0; i<size; i++) {
                if (ds != null) {
                    key = type + "." + ds.get(i).getName();
                }
                else {
                    key = type + "." + "unknown" + i;
                }
                metrics.put(key, values.get(i));
            }
        }
    }

    public void addNotificationListener(NotificationListener listener,
                                        NotificationFilter filter,
                                        Object handback) {
        _broadcaster.addNotificationListener(listener, filter, handback);
    }

    public MBeanNotificationInfo[] getNotificationInfo() {
        return new MBeanNotificationInfo[] {
            new MBeanNotificationInfo(Notification.SEVERITY,
                                      javax.management.Notification.class.getName(),
                                      "Collectd Notifications"),
        };
    }

    public void removeNotificationListener(NotificationListener listener)
        throws ListenerNotFoundException {
        _broadcaster.removeNotificationListener(listener);
    }
}
