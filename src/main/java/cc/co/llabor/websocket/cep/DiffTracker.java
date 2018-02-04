package cc.co.llabor.websocket.cep;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.event.map.MapEventBean;

public class DiffTracker implements UpdateListener {
	private int callCounter = 0;

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		for (Object e:newEvents) {
			MapEventBean eBean = (MapEventBean)e;
			callCounter++;
			//if ((""+eBean.get("pair")).contains("BTC_ETH")) {
			if ((""+eBean.get("pair")).contains("USDT_LTC")) {
			

			 System.out.println("+DIFF+"+callCounter+"+--:"+" ==:"+eBean.getProperties());
			}
		}
	}

}
