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
	private String symbol;
	private String properyName;
	private String ns;
	private String logns;
	/** Logger */
	private static Logger LOG = LoggerFactory.getLogger(PoloHandler.class);


	public RrdDirectUpdater(String symTmp, String properyNameTmp) {
		this.symbol = symTmp;
		this.properyName = properyNameTmp;
		this.ns = "rrdws/RrdDirectUpdater/"+symbol+"/"+properyName;
		this.logns = ns+":{}";
	}


	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) { 
		for (Object e:newEvents) { 
			MapEventBean eBean = (MapEventBean)e;
			double value = 0;
			try {
				System.out.println(logns + eBean );
				value = Double.valueOf(""+ eBean.get("data")) ;
				rrdUpdate( value );
				
			}catch(Exception ex) {
				LOG.error("{}", ex);
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
