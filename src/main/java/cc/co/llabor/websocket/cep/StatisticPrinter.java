package cc.co.llabor.websocket.cep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.event.map.MapEventBean;

public class StatisticPrinter implements UpdateListener {

	/** Logger */
	private static Logger LOG = LoggerFactory.getLogger(DiffTracker.class);

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		for (Object e:newEvents) {
			MapEventBean eBean = (MapEventBean)e; 
			LOG.info( "STAT::"+eBean.getProperties());
		}
	}

}
