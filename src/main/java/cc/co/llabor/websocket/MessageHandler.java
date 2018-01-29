package cc.co.llabor.websocket;

/**
 * Message handler.
 *
 * @author Jiji_Sasidharan
 */
public interface MessageHandler {

    public void handleMessage(String message) throws ErrorProcessingException;
}