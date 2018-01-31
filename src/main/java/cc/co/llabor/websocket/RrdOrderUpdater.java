package cc.co.llabor.websocket;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class RrdOrderUpdater implements UpdateListener {

	private String nsTmp;
	private RRDWSEndpoint rrdWS;
	private int callCounter = 0;
	static int commonCounter = 0;
	

	public RrdOrderUpdater(RRDWSEndpoint rrdWS, String nsTmp) {
		this.nsTmp = nsTmp;
		this.rrdWS = rrdWS;
		WS2RRDPump.createRRDandPushXpathToRegistry(rrdWS, nsTmp  ); 		
	}

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		for (EventBean e:newEvents) {
			
			String cmdTmp = WS2RRDPump.makeUpdateCMD(""+e.get("data") , System.currentTimeMillis() , nsTmp );
			rrdWS.sendMessage(cmdTmp);
			callCounter++;
			commonCounter++;
			//System.out.println("+"+callCounter+"+--:"+symbol+"."+propertyName+"  ==:"+eBean.getProperties());			
			//System.out.println("+"+callCounter+"/"+commonCounter+" ::update "+nsTmp.toString());
		}

	}

}
