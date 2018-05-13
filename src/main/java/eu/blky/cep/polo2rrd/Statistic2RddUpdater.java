package eu.blky.cep.polo2rrd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.event.map.MapEventBean;

import ws.rrd.csv.Action;
import ws.rrd.csv.RrdUpdateAction;

public class Statistic2RddUpdater implements UpdateListener {
	private final String logns = Statistic2RddUpdater.class.getName();
	private final String ns;
	private double value = 0;
	/** Logger */
	private static Logger LOG = LoggerFactory.getLogger(Statistic2RddUpdater.class);	
	public Statistic2RddUpdater(String ns) {
		this.ns = logns +"/"+ ns;
	}

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) { 
		for (Object e:newEvents) { 
			MapEventBean eBean = (MapEventBean)e;			
			try {
				LOG.trace(logns,  eBean );
				rrdUpdate( value++ );				
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
