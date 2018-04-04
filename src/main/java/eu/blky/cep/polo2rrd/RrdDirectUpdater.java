package eu.blky.cep.polo2rrd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.event.map.MapEventBean;

import cc.co.llabor.websocket.PoloHandler;
import ws.rrd.csv.Action;
import ws.rrd.csv.RrdUpdateAction;

public class RrdDirectUpdater implements UpdateListener {
 
	private String ns;
	private String logns;
	private String propertyName = "data";
	/** Logger */
	private static Logger LOG = LoggerFactory.getLogger(RrdDirectUpdater.class);


	public RrdDirectUpdater(String nsPar, String properyNameTmp) { 
		if (null!=properyNameTmp) {
			propertyName  = properyNameTmp;
		}
		this.ns = nsPar;
		this.logns = ns+":{}";
	}


	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) { 
		for (Object e:newEvents) { 
			MapEventBean eBean = (MapEventBean)e;
			double value = 0;
			try {
				LOG.trace(logns,  eBean );
				value = Double.valueOf(""+ eBean.get(propertyName )) ;
				rrdUpdate( value );
				
			}catch(Exception ex) {
				LOG.trace("{}", ex);
			}
		}		
	}
	
	private void rrdUpdate(double value) {
		Action rrdUpdateAction =  new RrdUpdateAction(); 
		String now = ""+System.currentTimeMillis();
		Object retval = rrdUpdateAction.perform(   ns ,  now  , ""+value );
		LOG.trace(logns,retval);
	}


}
