package eu.blky.cep.polo2rrd.updaters;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.co.llabor.websocket.ErrorProcessingException;
import cc.co.llabor.websocket.MessageHandler;
import cc.co.llabor.websocket.PoloHandler;
import ws.rrd.csv.Action;
import ws.rrd.csv.RrdKeeper;
import ws.rrd.csv.RrdUpdateAction;

public class RrdCountUpdater implements MessageHandler {
	private int updateCounter = 0;
	private long lastOut = 0;
	private long lastUp = 0;
	/** Logger */
	private static Logger LOG = LoggerFactory.getLogger(RrdCountUpdater.class);
	

	@Override
	public void handleMessage(String message) throws ErrorProcessingException {
		if (updateCounter%1000==0) {
			long upPerSec = calcUpdatePerSecond();
			System.out.println("+#"+updateCounter+"#+-to  ==:" + message + ""+ upPerSec);
			lastOut = System.currentTimeMillis();
			lastUp = updateCounter;
			rrdUpdate(upPerSec); 
		}
		updateCounter++;
	}

	private void rrdUpdate(long upPerSec) {
		Action rrdUpdateAction =  new RrdUpdateAction(); 
		Object retval = rrdUpdateAction.perform(   "rrdws/POLO/updatesPerSec" ,  lastOut , ""+upPerSec );
	}

	private long calcUpdatePerSecond() {
		try {
			return (1000*(updateCounter-lastUp))/(System.currentTimeMillis() - lastOut);
		}catch(Throwable e) {
			return 1;
		}
	}

	@Override
	public void destroy() throws IOException {
		// TODO Auto-generated method stub
		LOG.error("public void destroy() throws IOException {}",this);

	}

}
