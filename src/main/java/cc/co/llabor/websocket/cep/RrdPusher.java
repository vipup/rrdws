package cc.co.llabor.websocket.cep;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.event.map.MapEventBean;

import cc.co.llabor.websocket.PoloHandler;
import cc.co.llabor.websocket.RRDWSEndpoint;
import cc.co.llabor.websocket.WS2RRDPump;

public class RrdPusher implements UpdateListener {

	private RRDWSEndpoint rrdws;
	private String symbol;
	private String propertyName;
	private int updateCounter = 0;	
	String XPATH_PMIN ;

	public RrdPusher(String symbol, String propertyName, RRDWSEndpoint rrdWS) {
		this.symbol = symbol;
		this.propertyName = propertyName;
		XPATH_PMIN = PoloHandler.calcXpath4RRD(symbol, propertyName);
		this.rrdws = rrdWS;
	}

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		updateCounter++;
		Object value = "";
		for (Object e : newEvents) {
			MapEventBean eBean = (MapEventBean) e;
			value = eBean.getProperties().get("avgA");
			if (value== null) continue;
			//System.out.println("+#"+updateCounter+"#+-to RRD>---"+symbol+"."+propertyName+"-- ==:" + eBean.getProperties());
			// TODO GOTO process1001
			Long timestampTmp = System.currentTimeMillis();
			String cmdTmp = WS2RRDPump.makeUpdateCMD(""+value, timestampTmp, XPATH_PMIN);
			if (XPATH_PMIN .startsWith("/PoLo/EQLOrder/"))
			if (XPATH_PMIN .equals("/PoLo/EQLOrder/USDT_DASH/0/price_300sec")) {
				System.out.println("cmdTmp  ...::"+cmdTmp);
			}
			//System.out.println("+#"+updateCounter+"#+-to RRD>---"+cmdTmp);
			rrdws.sendMessage(cmdTmp);					
		}

	}
}
