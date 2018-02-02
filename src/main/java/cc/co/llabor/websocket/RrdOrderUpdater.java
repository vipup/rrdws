package cc.co.llabor.websocket;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class RrdOrderUpdater implements UpdateListener {

	private String nsTmp;
	private RRDWSEndpoint rrdWS;
	private int callCounter = 0;
	private String propPar = "data";
	static int commonCounter = 0;
	

	public RrdOrderUpdater(RRDWSEndpoint rrdWS, String nsTmp, String propPar2) {
		this.nsTmp = nsTmp;
		this.rrdWS = rrdWS;
		this.propPar = propPar2;
		WS2RRDPump.createRRDandPushXpathToRegistry(rrdWS, nsTmp  ); 		
	}

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		for (EventBean e:newEvents) {
			
			String cmdTmp = WS2RRDPump.makeUpdateCMD(""+e.get("data") , System.currentTimeMillis() , nsTmp );
			//System.out.println("+"+callCounter+"/"+commonCounter+" ::CMD ::"+cmdTmp);
			rrdWS.sendMessage(cmdTmp);
			callCounter++;
			commonCounter++;
			if (nsTmp.contains("/BTC_ETH/")) { 
				if (nsTmp.contains("/price"))
					System.out.println("+"+nsTmp+"+--:"+e.get("data") );
				else
					System.out.println("+"+nsTmp+"+--:"+e.get("data") );
//				if (nsTmp.contains("/volume"))
//					System.out.println("+"+nsTmp+"+--:"+e.get("volume") );
//				if (nsTmp.contains("/total"))
//					System.out.println("+"+nsTmp+"+--:"+e.get("total") );
			}			
			//System.out.println("+"+callCounter+"+--:"+symbol+"."+propertyName+"  ==:"+eBean.getProperties());			
			//System.out.println("+"+callCounter+"/"+commonCounter+" ::update "+nsTmp.toString());
		}

	}

}
