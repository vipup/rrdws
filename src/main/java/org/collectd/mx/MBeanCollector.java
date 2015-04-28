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

import java.util.HashMap;
import java.util.Map;
import java.util.Set; 

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;

import org.collectd.protocol.Network;
import org.collectd.protocol.ValueList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Query MBeans and dispatch results upstream.
 */
public class MBeanCollector implements Runnable {

    private static final Logger _log =
        LoggerFactory.getLogger(MBeanCollector.class.getName());
    private MBeanSender _sender;
    private long _interval = 60;
    private Map<String,MBeanQuery> _queries =
        new HashMap<String,MBeanQuery>();

    public MBeanSender getSender() {
        return _sender;
    }

    public void setSender(MBeanSender sender) {
        _sender = sender;
    }

    public long getInterval() {
        return _interval;
    }

    public void setInterval(long interval) {
        _interval = interval;
    }

    public Map<String,MBeanQuery> getQueries() {
        return _queries;
    }

    public MBeanQuery addMBean(String objectName) {
        MBeanQuery query = _queries.get(objectName);
        if (query == null) {
            try {
                query = new MBeanQuery(new ObjectName(objectName));
            } catch (Exception e) {
                throw new IllegalArgumentException(objectName);
            }
            _queries.put(objectName, query);
        }
        return query;
    }

    public MBeanAttribute addMBeanAttribute(String objectName,
                                            String attributeName) {
        MBeanQuery query = addMBean(objectName);
        if (attributeName != null) {
            MBeanAttribute attr = new MBeanAttribute(attributeName);
            query.addAttribute(attr);
            return attr;
        }
        else {
            return null;
        }
    }

    private boolean isNumber(Object obj) {
        if (obj == null) {
            return false;
        }
        return Number.class.isAssignableFrom(obj.getClass());
    }

    private String getBeanName(ObjectName query, ObjectName name) {
        Map<String,String> skip;
        if (query == null) {
            skip = new HashMap<String,String>();
        }
        else {
            skip = query.getKeyPropertyList();
        }
        StringBuffer iname = new StringBuffer();
        for (Object key : name.getKeyPropertyList().keySet()) {
            if (skip.get(key) != null) {
                continue;
            }
            if (iname.length() > 0) {
                iname.append(' ');
            }
            iname.append(name.getKeyProperty((String)key));
        }
        return iname.toString();
    }

    private void dispatch(MBeanQuery query, String plugin,
                          String typeInstance,
                          ObjectName name, MBeanAttribute attr,
                          Number val) {
        if (attr.getDataType() == Network.DS_TYPE_GAUGE) {
            val = new Double(val.doubleValue());
        }
        else {
            val = new Long(val.longValue());
        }

        String pluginInstance = query.getPluginInstance();
        if (pluginInstance == null) {
            pluginInstance = _sender.getInstanceName();
        }
        String beanName = query.getAlias();
        ValueList vl = new ValueList();
        vl.setInterval(getInterval());
        vl.setPlugin(plugin);
        if (beanName == null){
            beanName = getBeanName(null, name);
        }
        else if (query.getName().isPattern()) {
            String instName = getBeanName(query.getName(), name);
            
            if (instName != null) {
            	instName = instName.replace("\"", "_");
            	instName = instName.replace("\'", "_");
            	instName = instName.replace("\b", "_");
            	instName = instName.replace("\t", "_");
            	instName = instName.replace("\n", "_");
            	instName = instName.replace(" ", "_");
            	instName = instName.replace(":", "_");
                beanName += "/" + instName;
            }
        }
        vl.setPluginInstance(pluginInstance + "-" + beanName);
        vl.setType(attr.getTypeName());
        vl.setTypeInstance(typeInstance);
        vl.addValue(val);
        _sender.dispatch(vl);
    }

    /** this methor will called for any "collected" que 
     * 
     * @param query
     * @param name
     * @throws Exception
     */
    public void collect(MBeanQuery query, ObjectName name) throws Exception {
        String plugin = query.getPlugin();
        if (plugin == null) {
            plugin = name.getDomain();
        }

        for (MBeanAttribute attr : query.getAttributes()) {
            String attrName = attr.getAttributeName();
            Object obj;
            try {
                MBeanServerConnection mBeanServerConnection = _sender.getMBeanServerConnection();
				obj = mBeanServerConnection.getAttribute(name,  attrName);
            } catch (Exception e) {
            	if (e instanceof RuntimeException){
            		if (!_sender.isActive()){
            			_log.warn("DESTROY sender:::["+_sender.getInstanceName()+"]::"+e.getMessage() , e );
            			_sender.shutdown();
            		}else{
                		throw e;
                	}
            		
            	}
            		
                continue;
            }
            if (obj instanceof CompositeData) {
                CompositeData data = (CompositeData)obj;
                String key = attr.getCompositeKey();
                if (key == null) {
                    //no key specified; collect all
                    Set<String> keys = data.getCompositeType().keySet();
                    for (String ckey : keys) {
                        obj = data.get(ckey);
                        if (!isNumber(obj)) {
                            continue;
                        }
                        dispatch(query, plugin,
                                 attrName + "." + ckey,
                                 name, attr, (Number)obj);
                    }
                    continue;
                }
                else {
                    obj = data.get(key);
                }
            }
            if (!isNumber(obj)) {
                continue;
            }
            dispatch(query, plugin,
                     attr.getName(),
                     name, attr, (Number)obj);
        }
        _sender.flush();
    }

    private void run(MBeanQuery query, ObjectName name) {
        try {
            if (query.getAttributes().size() == 0) {
                query = queryAll(name);
            }
            this.collect(query, name);
        } catch (Exception e) {
            //MBean might not be registered yet
            _log.error(  "collect " + name, e);
        }
    }

    private MBeanQuery queryAll(ObjectName name)
        throws Exception {
        MBeanQuery query = new MBeanQuery(name);
        MBeanInfo info = _sender.getMBeanServerConnection().getMBeanInfo(name);
        MBeanAttributeInfo[] attrs = info.getAttributes();
        for (int i=0; i<attrs.length; i++) {
            query.addAttribute(new MBeanAttribute(attrs[i].getName()));
        }
        return query;
    }

    public void run() {
    	if (!this._sender.isActive()) // the sender was stopped - terminate scheduler for future.
    	{
    		throw new RuntimeException("execution after stop - "+ this);
    	}
        for (MBeanQuery query : _queries.values()) {
            ObjectName name = query.getName();
            if (name.isPattern()) {
                Set<ObjectName> beans;
                try {
                    beans = 
                        _sender.getMBeanServerConnection().queryNames(name, null);
                } catch (Exception e) {
                    _log.warn("queryNames(" + name + "): " + e);
                    return;
                }
                for (ObjectName oname : beans) {
                    run(query, oname);
                }
            }
            else {
                run(query, name);
            }
        }
    }
}
