package cc.co.llabor.websocket;
 
import java.net.URI;
import javax.websocket.ClientEndpoint; 
import javax.websocket.ContainerProvider; 
import javax.websocket.WebSocketContainer; 

/**
 * ChatServer Client
 *
 * @author Jiji_Sasidharan
 */
@ClientEndpoint
public class RRDWSEndpoint extends DestroyableWebSocketClientEndpoint{

	public RRDWSEndpoint(URI endpointURI, DestroyTracker watchDog) {
		super(watchDog);
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

  
}