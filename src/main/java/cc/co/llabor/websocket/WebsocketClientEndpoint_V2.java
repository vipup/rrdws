package cc.co.llabor.websocket;

  
import java.io.IOException;
import java.io.InputStream; 
import java.net.URI;
import java.util.Enumeration;
import java.util.Properties;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
 
/**
 * ChatServer Client
 *
 * @author Jiji_Sasidharan
 */
@ClientEndpoint
public class WebsocketClientEndpoint_V2 {

    Session userSession = null;
    private MessageHandler messageHandler = new WebsocketClientEndpoint_V2.MessageHandler() {
        public void handleMessage(String message) {
            System.out.println("<<<<<<<<"+message);
        }
    };

    public WebsocketClientEndpoint_V2(URI endpointURI) {
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
        System.out.println("opening websocket");
        this.userSession = userSession;
        
        userSession.getAsyncRemote().sendText("{\"command\":\"subscribe\",\"channel\":1001}");
        userSession.getAsyncRemote().sendText("{\"command\":\"subscribe\",\"channel\":1002}");
        userSession.getAsyncRemote().sendText("{\"command\":\"subscribe\",\"channel\":1003}");
//        userSession.getAsyncRemote().sendText("{\"command\":\"subscribe\",\"channel\":\"USDT_BTC\"}");
//        userSession.getAsyncRemote().sendText("{\"command\":\"subscribe\",\"channel\":\"USDT_XMR\"}");
//        userSession.getAsyncRemote().sendText("{\"command\":\"subscribe\",\"channel\":\"USDT_ETH\"}");
//        userSession.getAsyncRemote().sendText("{\"command\":\"subscribe\",\"channel\":\"USDT_REP\"}");
//        userSession.getAsyncRemote().sendText("{\"command\":\"subscribe\",\"channel\":\"USDT_ETC\"}");
 
		InputStream inStream = PoloPairListener.class.getClassLoader().getResourceAsStream("cc/co/llabor/websocket/polo.txt");
		Properties pairsToSubscribe= new Properties();
		try {
			pairsToSubscribe.load(inStream);

	        Enumeration<String> e = (Enumeration<String>) pairsToSubscribe.propertyNames();
	
	        while (e.hasMoreElements()) {
	          String key = e.nextElement();
	          String value = pairs.getProperty(key);
	          System.out.println(key + " -- " + pairs.getProperty(key));
	          userSession.getAsyncRemote().sendText("{\"command\":\"subscribe\",\"channel\":\""+key+"\"}");
	          try {
				Thread.sleep(5);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        }
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
      
        
    }
    
    Properties pairs= new Properties();
    Properties id2pairs= new Properties();
    
    {
    	try {
			initPairsFromFile() ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason the reason for connection close
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        System.out.println("closing websocket. Reason:"+reason.getReasonPhrase());
        System.out.println("closing websocket. Reason:"+reason.getReasonPhrase());
        System.out.println("closing websocket. Reason:"+reason.getReasonPhrase());
        System.out.println("closing websocket. Reason:"+reason.getReasonPhrase());
        System.out.println("closing websocket. Reason:"+reason.getReasonPhrase());
        System.out.println("closing websocket. Reason:"+reason.getReasonPhrase());
        System.out.println("closing websocket. Reason:"+reason.getReasonPhrase());
        System.out.println("closing websocket. Reason:"+reason.getReasonPhrase());
        System.out.println("closing websocket. Reason:"+reason.getReasonPhrase());
        System.out.println("closing websocket. Reason:"+reason.getReasonPhrase());
        System.out.println("closing websocket. Reason:"+reason.getReasonPhrase());
        System.out.println("closing websocket. Reason:"+reason.getReasonPhrase());
        System.out.println("closing websocket. Reason:"+reason.getReasonPhrase());
        System.out.println("closing websocket. Reason:"+reason.getReasonPhrase());
        System.out.println("closing websocket. Reason:"+reason.getReasonPhrase());
        System.out.println("closing websocket. Reason:"+reason.getReasonPhrase());
        System.out.println("closing websocket. Reason:"+reason.getReasonPhrase());
        System.out.println("closing websocket. Reason:"+reason.getReasonPhrase());
        System.out.println("closing websocket. Reason:"+reason.getReasonPhrase());
        this.userSession = null;
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
        		this.messageHandler.handleMessage(message); 
        	}catch(ErrorProcessingException e) {
        		if (e.getMessage().contains("1001,")) return;
        		if (e.getMessage().contains("1002,")) return;
        		if (e.getMessage().contains("1003,")) return;
        		System.err.println(e.getMessage());
        	} 
        }
    }
    
 

    /**
     * register message handler
     *
     * @param msgHandler
     */
    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    /**
     * Send a message.
     *
     * @param message
     */
    public void sendMessage(String message) {
    	System.out.println(3);
        this.userSession.getAsyncRemote().sendText(message);
    }

    /**
     * Message handler.
     *
     * @author Jiji_Sasidharan
     */
    public static interface MessageHandler {

        public void handleMessage(String message) throws ErrorProcessingException;
    }


	public String getPairNameByID(String theID) {
		return  this.id2pairs.getProperty(theID);
	}
}