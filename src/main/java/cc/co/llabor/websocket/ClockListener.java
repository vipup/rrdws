package cc.co.llabor.websocket;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.event.map.MapEventBean;

public class ClockListener implements UpdateListener {

	private int callCounter;

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		for (Object e:newEvents) {
			MapEventBean eBean = (MapEventBean)e;
			callCounter++;
			//System.out.println("+"+callCounter+"+-TIME :"+eBean.getProperties());
		}

	}

}
