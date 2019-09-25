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
	private URI endpointURI;
	WebSocketContainer container;

	public RRDWSEndpoint(URI endpointURI, DestroyTracker watchDog) {
		super(watchDog);
		System.out.println("T5");
		
        try {
    		System.out.println("T6");

    		container = ContainerProvider.getWebSocketContainer();
    		System.out.println("T7");
    		this.endpointURI = endpointURI;
// USE start() to activate 
//            container.connectToServer(this, endpointURI);
//    		System.out.println("T8");

        } catch (Exception e) {
    		System.out.println("T9");
    		LOG.error("public RRDWSEndpoint(URI: "+endpointURI+", DestroyTracker: "+watchDog+") {", e);
            throw new RuntimeException(e);
        }
    }

	@Override
	public void start() {
		try {
			container.connectToServer(this, endpointURI);
			System.out.println("T8");

		} catch (Exception e) {
			System.out.println("T9");
			LOG.error("public RRDWSEndpoint(URI: " + endpointURI + ", DestroyTracker: " +  ") {" + e);
			throw new RuntimeException(e);
		}
	}
  
}