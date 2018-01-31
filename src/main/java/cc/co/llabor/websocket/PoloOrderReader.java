package cc.co.llabor.websocket;
 
import java.net.URI; 

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
 
@ClientEndpoint
public class PoloOrderReader {
    /** Logger */
    private static Logger LOG = LoggerFactory.getLogger(PoloOrderReader.class);	
 	

    Session userSession = null;
    private MessageHandler  messageHandler = new MessageHandler() {
        public void handleMessage(String message) {
            LOG.debug("<<<<<<<<"+message);
        }
    };
	private String pairName = "USDT_BTC";

    public PoloOrderReader(URI endpointURI) {
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
        userSession.getAsyncRemote().sendText("{\"command\":\"subscribe\",\"channel\":1001}");
        userSession.getAsyncRemote().sendText("{\"command\":\"subscribe\",\"channel\":1002}");
        userSession.getAsyncRemote().sendText("{\"command\":\"subscribe\",\"channel\":1003}"); 
        
        this.userSession = userSession; 
        userSession.getAsyncRemote().sendText("{\"command\":\"subscribe\",\"channel\":\""+this.getPairName() +"\"}");
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason the reason for connection close
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        LOG.debug("closing websocket. Reason:"+reason.getReasonPhrase());
        LOG.debug("closing websocket. Reason:"+reason.getReasonPhrase());
        LOG.debug("closing websocket. Reason:"+reason.getReasonPhrase());
        LOG.debug("closing websocket. Reason:"+reason.getReasonPhrase());
        LOG.debug("closing websocket. Reason:"+reason.getReasonPhrase());
        LOG.debug("closing websocket. Reason:"+reason.getReasonPhrase());
        LOG.debug("closing websocket. Reason:"+reason.getReasonPhrase());
        LOG.debug("closing websocket. Reason:"+reason.getReasonPhrase());
        LOG.debug("closing websocket. Reason:"+reason.getReasonPhrase());
        LOG.debug("closing websocket. Reason:"+reason.getReasonPhrase());
        LOG.debug("closing websocket. Reason:"+reason.getReasonPhrase());
        LOG.debug("closing websocket. Reason:"+reason.getReasonPhrase());
        LOG.debug("closing websocket. Reason:"+reason.getReasonPhrase());
        LOG.debug("closing websocket. Reason:"+reason.getReasonPhrase());
        LOG.debug("closing websocket. Reason:"+reason.getReasonPhrase());
        LOG.debug("closing websocket. Reason:"+reason.getReasonPhrase());
        LOG.debug("closing websocket. Reason:"+reason.getReasonPhrase());
        LOG.debug("closing websocket. Reason:"+reason.getReasonPhrase());
        LOG.debug("closing websocket. Reason:"+reason.getReasonPhrase());
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
        		//pairs.get("TODO")
        		//userSession.getAsyncRemote().sendText("{\"command\":\"unsubscribe\",\"channel\":\""+this.getPairName()+"\"}");
        		//System.err.println(e.getMessage());
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
    	LOG.debug(""+3); 
        this.userSession.getAsyncRemote().sendText(message);
    }



	public String getPairName() {
		// TODO Auto-generated method stub
		return this.pairName;
	}

	public void setPairName(String pairName) {
		unsubscribeCurrent();
		this.pairName = pairName;
		userSession.getAsyncRemote().sendText("{\"command\":\"subscribe\",\"channel\":\""+this.getPairName() +"\"}");
		
	}

	public void unsubscribeCurrent() {
		userSession.getAsyncRemote().sendText("{\"command\":\"unsubscribe\",\"channel\":\""+this.getPairName() +"\"}");
		
	}
}