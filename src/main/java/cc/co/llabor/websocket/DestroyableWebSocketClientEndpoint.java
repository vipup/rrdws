package cc.co.llabor.websocket;

import java.io.IOException;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.websocket.CloseReason.CloseCodes;
 

@ClientEndpoint
public class DestroyableWebSocketClientEndpoint {
    /**
     * Message handler.
     *
     * @author Jiji_Sasidharan
     */
    public static interface MessageHandler {

        public void handleMessage(String message) throws ErrorProcessingException;
    }
 
    Session userSession = null;
	
    public void destroy() throws IOException {
    	WebSocketContainer container = userSession.getContainer();
		container.setDefaultMaxTextMessageBufferSize(1);
    	CloseReason reason = new CloseReason(CloseCodes.CLOSED_ABNORMALLY,  "destroyed by owner");
    	userSession.close( reason  );
    }

    protected MessageHandler messageHandler ;
    
    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession) {
        System.out.println("opening websocket:"+this);
        this.userSession = userSession;
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason the reason for connection close
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        System.out.println("closing websocket :::"+this);
        this.userSession = null;
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param message The text message
     * @throws ErrorProcessingException 
     */
    @OnMessage
    public void onMessage(String message) throws ErrorProcessingException {
        if (this.messageHandler != null) {
            this.messageHandler.handleMessage(message);
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
    	messageCunter ++; messagesPerSec++; sizePerSec+=message.length();
		if ( System.currentTimeMillis() -1000 >lastHandledTimestamp ) {
			
			System.out.println("RRDSENDED:<"+(lastHandledTimestamp-System.currentTimeMillis())+">>>   " + "/ "+messagesPerSec +" msg/sec  // "+sizePerSec+"  bytes/per sec  :::" + (sizePerSec/messagesPerSec) +" bytes/message["+messageCunter );
			lastHandledTimestamp = System.currentTimeMillis();
			messagesPerSec = 0;
			sizePerSec =0;
		}
        this.userSession.getAsyncRemote().sendText(message);
    }
    
	long lastHandledTimestamp = 0;
	long messageCunter = 0;
	long messagesPerSec = 0;
	long sizePerSec = 0;
	     
    
}
