package eu.blky.cep.polo2rrd.updaters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.event.map.MapEventBean;

import ws.rrd.csv.Action;
import ws.rrd.csv.RrdUpdateAction;

public class Statistic2RddUpdater implements UpdateListener {
	private final String logns = Statistic2RddUpdater.class.getName();
	private final String ns; 
	/** Logger */
	private static Logger LOG = LoggerFactory.getLogger(Statistic2RddUpdater.class);	 
	public String toString() {
		return Statistic2RddUpdater.class.getName()+":"+ns;
	} 

	
	public Statistic2RddUpdater(String ns) {
		this.ns = logns +"/"+ ns;
	}

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) { 
		int totalTmp = 0 ;
		int pairsTmp = 0 ;
		// {cnt=10, type=PoloTick, pair=ETH_ZEC}
		for (Object e:newEvents) {
			pairsTmp++;
			MapEventBean eBean = (MapEventBean)e;		
			String cnt = ""+ eBean.get("cnt");
			String pair = ""+ eBean.get("pair");
			try {
				LOG.trace(logns,  eBean );
				rrdUpdate( pair, cnt );				
			}catch(Exception ex) {
				LOG.trace("{}", ex);
			} 
			totalTmp+=Integer.parseInt(cnt);
		}
		rrdUpdate( "TOTAL", ""+totalTmp	);
		rrdUpdate( "PAIRS", ""+pairsTmp	);
	}
	
	private void rrdUpdate(String suffix, String value) {
		Action rrdUpdateAction =  new RrdUpdateAction(); 
		String now = ""+System.currentTimeMillis();
		Object retval = rrdUpdateAction.perform(   ns +"/" +suffix,  now  , value );
		LOG.trace(logns,retval);
	}
}
