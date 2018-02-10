package cc.co.llabor.websocket.cep;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.event.map.MapEventBean;

public class SummPrinter implements UpdateListener {

	private int callCounter;

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		callCounter++;
		System.out.println("....");
		for (Object e:newEvents) {
			MapEventBean eBean = (MapEventBean)e; 
			if ((""+eBean.get("pair")).contains("33332222222222222222USDT_BTC")) { 
				 System.out.println("+SUMMs+"+callCounter+"+--:"+" ==:"+eBean.getProperties());
				 Object arrprPRICVE = eBean.get("prPRICE") ;
			}
		}
	}

}
