package eu.blky.cep.polo2rrd.updaters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.event.map.MapEventBean;
 

public class PriceCalculator implements UpdateListener {
	/** Logger */
	private static Logger LOG = LoggerFactory.getLogger(PriceCalculator.class);
 
	
	public String toString() {
		return PriceCalculator.class.getName();
	} 	
	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		for (Object e:newEvents) {
 
			MapEventBean eBean = (MapEventBean)e;		
//			String cnt = ""+ eBean.get("cnt");
			String pair = ""+ eBean.get("dTOV");
			try {
				LOG.trace(PriceCalculator.class.getName(),  pair );
				 	
			}catch(Exception ex) {
				LOG.trace("{}", ex);
			} 
			 
		}

	}

}
