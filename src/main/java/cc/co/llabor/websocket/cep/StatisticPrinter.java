package cc.co.llabor.websocket.cep;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.event.map.MapEventBean; 

 

public class StatisticPrinter implements UpdateListener { 
	private static final Logger LOG  = LoggerFactory.getLogger(StatisticPrinter.class .getName());

	public String toString() {
		return StatisticPrinter.class.getName() ;
	} 	

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		for (Object e:newEvents) {
			MapEventBean eBean = (MapEventBean)e; 
			LOG.info("STAT::{}",eBean.getProperties());
		}
	}

}
