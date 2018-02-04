package cc.co.llabor.websocket;

import java.io.IOException;

/**
 * Message handler.
 *
 * @author Jiji_Sasidharan
 */
public interface MessageHandler {

    public void handleMessage(String message) throws ErrorProcessingException;
    
    public void destroy() throws IOException ;
}