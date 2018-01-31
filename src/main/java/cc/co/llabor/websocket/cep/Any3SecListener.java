package cc.co.llabor.websocket.cep;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.event.map.MapEventBean;

public class Any3SecListener implements UpdateListener {

	private String symbol;
	private String propertyName;
	private int callCounter = 0;

	public Any3SecListener(String symbol, String propertyName) {
		this.symbol = symbol;
		this.propertyName = propertyName;
		
	}

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		for (Object e:newEvents) {
			MapEventBean eBean = (MapEventBean)e;
			callCounter++;
			System.out.println("++"+callCounter+"++"+symbol+":"+propertyName+" ==:"+eBean.getProperties());
		}
	}

}
