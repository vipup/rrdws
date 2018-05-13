package eu.blky.cep.polo2rrd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.co.llabor.websocket.DestroyTracker;
import cc.co.llabor.websocket.DestroyableWebSocketClientEndpoint; 
public class   DestroyTrackerImplementation implements DestroyTracker {
	/** Logger */
	private static Logger LOG = LoggerFactory.getLogger(DestroyTrackerImplementation.class);

	@Override
	public void stop() {
		LOG.error("Watchdog stop called");
		
	}

	@Override
	public void destroyed(DestroyableWebSocketClientEndpoint destroyableWebSocketClientEndpoint) {
		LOG.error("Watchdog destroyed({}) called::: ",destroyableWebSocketClientEndpoint);
		
	}
}