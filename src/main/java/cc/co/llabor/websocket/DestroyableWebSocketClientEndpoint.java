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
    private static final int MAX_ALLOWED_ERROR_BEFORE_RESTART = 333;

	/** Logger */
    private static Logger LOG = LoggerFactory.getLogger(DestroyableWebSocketClientEndpoint.class);	
	
    Session userSession = null;
	private DestroyTracker watchDog;
	
    public DestroyableWebSocketClientEndpoint(DestroyTracker watchDog ) {
		this.watchDog = watchDog;
		System.out.println("T4");

	}

	public void destroy() throws IOException {
		this.isAlive = false;
		
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
	
    		if (userSession!=null) userSession.close( reason  ); // TODO WTF?!
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

	private boolean isAlive=true;
    
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
        	if (isAlive )
        	try {        		
        		this.messageHandler.handleMessage(message);
        		lastHandledTimestamp = System.currentTimeMillis();
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
 
		if (isAlive)
		try {
			if (message.indexOf("X1088538178")>0) {
				System.out.println("------------------X1088538178[" + System.currentTimeMillis() +"]"+message);
			}
			this.userSession.getAsyncRemote().sendText(message);
			lastHandledTimestamp = System.currentTimeMillis();
		}catch(Throwable e) {
			errorCounter++;
			LOG.error("public void sendMessage(String ::"+message+") {"+errorCounter+"]]]",e);
			processFinalizationAfterError(message);
			
		} 
    }

	private void processFinalizationAfterError(String message) {
		if (WS2RRDPump.DISABLE_REPAIR_JOBS) return;
		final DestroyableWebSocketClientEndpoint me = this;
		
		if (errorCounter> MAX_ALLOWED_ERROR_BEFORE_RESTART) {
			synchronized (DestroyableWebSocketClientEndpoint.class) {
				isAlive = false; // stop all firther errors...
				LOG.error("errorCounter> MAX_ALLOWED_ERROR_BEFORE_RESTART");
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
