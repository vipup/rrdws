package cc.co.llabor.websocket;
 
import java.net.URI;
import javax.websocket.ClientEndpoint; 
import javax.websocket.ContainerProvider; 
import javax.websocket.WebSocketContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 

/**
 * ChatServer Client
 *
 * @author Jiji_Sasidharan
 */
@ClientEndpoint
public class RRDWSEndpoint extends DestroyableWebSocketClientEndpoint{

	/** Logger */
	private static Logger LOG = LoggerFactory.getLogger(RRDWSEndpoint.class);

	public RRDWSEndpoint(URI endpointURI, DestroyTracker watchDog) {
		super(watchDog);
		System.out.println("T5");
		
        try {
    		System.out.println("T6");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
    		System.out.println("T7");

            container.connectToServer(this, endpointURI);
    		System.out.println("T8");

        } catch (Exception e) {
    		System.out.println("T9");
    		LOG.error("public RRDWSEndpoint(URI: "+endpointURI+", DestroyTracker: "+watchDog+") {", e);
            throw new RuntimeException(e);
        }
    }

  
}