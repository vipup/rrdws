package eu.blky.cep.polo2rrd;

import cc.co.llabor.websocket.DestroyTracker;
import cc.co.llabor.websocket.DestroyableWebSocketClientEndpoint;

public class   DestroyTrackerImplementation implements DestroyTracker {
	@Override
	public void stop() {
		System.out.println("Watchdog stop called");
		
	}

	@Override
	public void destroyed(DestroyableWebSocketClientEndpoint destroyableWebSocketClientEndpoint) {
		System.out.println("Watchdog destroyed() called::: "+destroyableWebSocketClientEndpoint);
		
	}
}