package cc.co.llabor.websocket;

  
import java.io.IOException;
import java.io.InputStream; 
import java.net.URI;
import java.util.Enumeration;
import java.util.Properties;

import javax.websocket.ClientEndpoint; 
import javax.websocket.ContainerProvider; 
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 

@ClientEndpoint
public class PoloWSEndpoint extends DestroyableWebSocketClientEndpoint{

    /** Logger */
    private static Logger LOG = LoggerFactory.getLogger(PoloWSEndpoint.class);	
 

    public PoloWSEndpoint(URI endpointURI, DestroyTracker watchDog, Object just4IgnoreExc) {
    	super(watchDog);
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.setDefaultMaxBinaryMessageBufferSize(1116*1024);
            container.setDefaultMaxTextMessageBufferSize(1132*1024);
             
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            //throw new RuntimeException(e);
        	e.printStackTrace();
        }  	
    }
    public PoloWSEndpoint(URI endpointURI, DestroyTracker watchDog) {
    	super(watchDog);
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.setDefaultMaxBinaryMessageBufferSize(1116*1024);
            container.setDefaultMaxTextMessageBufferSize(1132*1024);
             
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     *
     *	 The channels are:
     *   1001 = trollbox (you will get nothing but a heartbeat)
     *   1002 = ticker
     *   1003 = base coin 24h volume stats
     *   1010 = heartbeat
     *   'MARKET_PAIR' = market order books
     */
    @OnOpen
    public void onOpen(Session userSession) {
    	LOG.debug("opening websocket");
        this.userSession = userSession;
        userSession.getAsyncRemote().sendText("{\"command\":\"subscribe\",\"channel\":1001}");
        userSession.getAsyncRemote().sendText("{\"command\":\"subscribe\",\"channel\":1002}");
        userSession.getAsyncRemote().sendText("{\"command\":\"subscribe\",\"channel\":1003}");
        
 
		InputStream inStream = PoloPairListener.class.getClassLoader().getResourceAsStream("cc/co/llabor/websocket/polo.txt");
		Properties pairsToSubscribe= new Properties();
		try {
			pairsToSubscribe.load(inStream);

	        Enumeration<String> e = (Enumeration<String>) pairsToSubscribe.propertyNames();
	
	        while (e.hasMoreElements()) {
	          String key = e.nextElement();
	          String value = pairs.getProperty(key);
	          LOG.debug(key + " -- " + pairs.getProperty(key));
	          userSession.getAsyncRemote().sendText("{\"command\":\"subscribe\",\"channel\":\""+key+"\"}");
	          try {
				Thread.sleep(5);
			} catch (InterruptedException e1) {
				initPairsFromFile() ;;
			}
	        }
		} catch (IOException e) {
			LOG.error("this.poloWS.destroy();;", e) ;
		}
      
        
    }
    
    Properties pairs= new Properties();
    public Properties id2pairs= new Properties();
    
    {
    	try {
			initPairsFromFile() ;
		} catch (IOException e) {
			LOG.error("initPairsFromFile() ;", e) ;
		}
    }
    
	private void initPairsFromFile() throws IOException {
		
		InputStream inStream = PoloPairListener.class.getClassLoader().getResourceAsStream("cc/co/llabor/websocket/poloALL.txt");
		pairs.load(inStream);
		Enumeration keys= pairs.propertyNames();
		while(keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value =  pairs.getProperty(key);
			id2pairs.put(value, key );
		}
	}
    
 
    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param message The text message
     */
    @OnMessage
    public void onMessage(String message) {
        if (this.messageHandler != null) {
        	try {
        		super.onMessage(message);
        	}catch(ErrorProcessingException e) {
        		if (e.getMessage().contains("1001,")) return;
        		if (e.getMessage().contains("1002,")) return;
        		if (e.getMessage().contains("1003,")) return;
        		System.err.println("public void onMessage(String message) {"+e.getMessage());
        	} 
        }
    }
    

 
	public String getPairNameByID(String theID) {
		return  this.id2pairs.getProperty(theID);
	}
}