package cc.co.llabor.websocket.cep;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.event.map.MapEventBean;

 

public class StatisticPrinter implements UpdateListener {
	public String toString() {
		return StatisticPrinter.class.getName() ;
	} 	

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		for (Object e:newEvents) {
			MapEventBean eBean = (MapEventBean)e; 
			System.out.println("STAT::"+eBean.getProperties());
		}
	}

}
