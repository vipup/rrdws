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
	private URI endpointURI;	
 

    public PoloWSEndpoint(URI endpointURI, DestroyTracker watchDog, Object thisObjectIsUsedOnlyAsFlagForCallThisConstructor) {
    	super(watchDog);
        try {
        	ClassLoader clBAK = Thread.currentThread().getContextClassLoader();
        	ClassLoader cl = PoloWSEndpoint.class.getClassLoader();
        	Thread.currentThread().setContextClassLoader(cl );
            container = ContainerProvider.getWebSocketContainer();
            container.setDefaultMaxBinaryMessageBufferSize(1116*1024);
            container.setDefaultMaxTextMessageBufferSize(1132*1024);
            this. endpointURI = endpointURI;
            //USE START to initial connect container.connectToServer(this, endpointURI);
            Thread.currentThread().setContextClassLoader( clBAK );
        } catch (Exception e) {
            //throw new RuntimeException(e);
        	e.printStackTrace();
        }  	
    }
	WebSocketContainer container;
	/**
	 * @deprecated -  use constructor with modified ClassLoader for correct start within tomcat
	 * 
	 * @param endpointURI
	 * @param watchDog
	 */
    public PoloWSEndpoint(URI endpointURI, DestroyTracker watchDog) {
    	super(watchDog);
        try {
        	container = ContainerProvider.getWebSocketContainer();
            container.setDefaultMaxBinaryMessageBufferSize(1116*1024);
            container.setDefaultMaxTextMessageBufferSize(1132*1024);
            this. endpointURI = endpointURI;
            //USE START to initial connect container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void start() {
        try { 
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
        sendText(userSession, "{\"command\":\"subscribe\",\"channel\":1001}");
        sendText(userSession, "{\"command\":\"subscribe\",\"channel\":1002}");
        sendText(userSession, "{\"command\":\"subscribe\",\"channel\":1003}");
        
 
		InputStream inStream = PoloPairListener.class.getClassLoader().getResourceAsStream("cc/co/llabor/websocket/polo.txt");
		Properties pairsToSubscribe= new Properties();
		try {
			pairsToSubscribe.load(inStream);

	        Enumeration<String> e = (Enumeration<String>) pairsToSubscribe.propertyNames();
	
	        while (e.hasMoreElements()) {
	          String key = e.nextElement();
	          String value = pairs.getProperty(key);
	          LOG.debug(key + " -- " + pairs.getProperty(key));
	          sendText(userSession, "{\"command\":\"subscribe\",\"channel\":\""+key+"\"}");
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
	private void sendText(Session userSession, String textPar ) {
		try {
			userSession.getAsyncRemote().sendText(textPar);
		}catch(Exception e) {
			LOG.error("sendText(Session userSession, String textPar ) { ;", e) ;
		}
	}
    
    Properties pairs= new Properties();
    public final Properties id2pairs= new Properties();
    
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
    @Override
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