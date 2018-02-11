package cc.co.llabor.websocket;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.websocket.CloseReason.CloseCodes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 

@ClientEndpoint
public class DestroyableWebSocketClientEndpoint {
    /** Logger */
    private static Logger LOG = LoggerFactory.getLogger(DestroyableWebSocketClientEndpoint.class);	
	
    Session userSession = null;
	private DestroyTracker watchDog;
	
    public DestroyableWebSocketClientEndpoint(DestroyTracker watchDog ) {
		this.watchDog = watchDog;
	}

	public void destroy() throws IOException {
    	MessageHandler bak = this.messageHandler ;
    	System.err.println("DestroyableWebSocketClientEndpoint::"+bak);
    	LOG.error("DestroyableWebSocketClientEndpoint::",bak);
    	
    	this.addMessageHandler(null);
    		
    	try {
	    	WebSocketContainer container = userSession.getContainer();
	    	for (Session sessionTmp : userSession.getOpenSessions()) {
	    		System.out.println("try to close "+sessionTmp+"..");
	    		sessionTmp .close();
	    		System.out.println("done.");
	    	}
	    	container.setDefaultMaxTextMessageBufferSize(1);
	    	container.setDefaultMaxBinaryMessageBufferSize(1);
	    	container.setDefaultMaxSessionIdleTimeout(1);
	    	CloseReason reason = new CloseReason(CloseCodes.CLOSED_ABNORMALLY,  "destroyed by owner");
	
    		userSession.close( reason  );
    	}catch(Throwable e) {
    		e.printStackTrace();
    		LOG.error("public void destroy() throws IOException {"+this, e);
    	}
    	
    	this.userSession = null;
    	if (watchDog!=null) {
    		watchDog.destroyed(this);
    	}else {
    		System.err.println("watchDog is NULL....");
    	}
    }

    protected MessageHandler messageHandler ;
    
    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession) {
    	LOG.debug("opening websocket:"+this);
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
        LOG.debug("closing websocket :::"+this);
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
    	inMessageCounter ++;
        if (this.messageHandler != null) {
        	try {
        		this.messageHandler.handleMessage(message);
        	}catch(Throwable e) {
        		LOG.error("public void onMessage(String ::"+message+") throws ErrorProcessingException {",e);
        		errorCounter++;
        		processFinalizationAfterError(message);
        	}
        }
    }

    /**
     * register message handler (overwrite, if any )
     *
     * @param msgHandler
     */
    public void addMessageHandler(MessageHandler msgHandler) {
    	if (this.messageHandler != null)
    	try {
    		messageHandler.destroy();
    	}catch(Throwable e) {
    		e.printStackTrace();
    		LOG.error("public void addMessageHandler(MessageHandler msgHandler) {"+this.messageHandler,e);
    		
    	}
        this.messageHandler = msgHandler;
    }

    
    
    /**
     * Send a message.
     *
     * @param message
     */
    public void sendMessage(String message) {
    	outMessageCounter++; 
    	messagesPerSec++; 
    	sizePerSec+=message.length();
		if (errorCounter > 1000  || (lastHandledTimestamp>111111111111L && System.currentTimeMillis()  -lastHandledTimestamp  >100000 )) { // FULL Restart
    	//	if ( System.currentTimeMillis() -1000 >lastHandledTimestamp ) {
			
			LOG.debug("RRDSENDED:<"+(lastHandledTimestamp-System.currentTimeMillis())+">>>   " +
			"/ "+messagesPerSec +" msg/sec  // "+sizePerSec+"  bytes/per sec  :::"			+
			(sizePerSec/messagesPerSec) +" bytes/message["+inMessageCounter + "///"+ outMessageCounter 
			);
			lastHandledTimestamp = System.currentTimeMillis();
			messagesPerSec = 0;
			sizePerSec =0;
		}
		try {
			if (message.indexOf("X1088538178")>0) {
				System.out.println("------------------X1088538178[" + System.currentTimeMillis() +"]"+message);
			}
			this.userSession.getAsyncRemote().sendText(message);
		}catch(Throwable e) {
			errorCounter++;
			LOG.error("public void sendMessage(String ::"+message+") {"+errorCounter+"]]]",e);
			processFinalizationAfterError(message);
			
		} 
    }

	private void processFinalizationAfterError(String message) {
		final DestroyableWebSocketClientEndpoint me = this;
		if (errorCounter> 3) {
			synchronized (DestroyableWebSocketClientEndpoint.class) {
				
				
		        final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
		        
		        ses.schedule(new Runnable() {
		            @Override
		            public void run() {
		            	try {
		            		me.stop();
		            		me.destroy();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							LOG.error("public void sendMessage(String message) {"+errorCounter+"]]]"+me,e);
						}
		            }
		        }, 1, TimeUnit.SECONDS ); //1, TimeUnit.MINUTES);
			
			} 
			
		}else {
			LOG.error("SENDRCVMESSAGEERROR:::["+message+"]");
		}
	}
    
    protected void stop() {
    	this.messageHandler = null;
		if (watchDog!=null)watchDog.stop();
		
	}

	long lastHandledTimestamp = 0;
    long outMessageCounter = 0;
	long inMessageCounter = 0;
	long messagesPerSec = 0;
	long sizePerSec = 0;
	long errorCounter = 0;
	     
    
}
