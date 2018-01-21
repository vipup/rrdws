package cc.co.llabor.websocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.apache.batik.dom.util.HashTable;

import com.fasterxml.jackson.databind.JsonNode;

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
        userSession.getAsyncRemote().sendText("{\"command\":\"subscribe\",\"channel\":\"USDT_BTC\"}");
        userSession.getAsyncRemote().sendText("{\"command\":\"subscribe\",\"channel\":\"USDT_XMR\"}");
        userSession.getAsyncRemote().sendText("{\"command\":\"subscribe\",\"channel\":\"USDT_ETH\"}");
        userSession.getAsyncRemote().sendText("{\"command\":\"subscribe\",\"channel\":\"USDT_REP\"}");
        userSession.getAsyncRemote().sendText("{\"command\":\"subscribe\",\"channel\":\"USDT_ETC\"}");
        
        userSession.getAsyncRemote().sendText("{\"command\":\"subscribe\",\"channel\":1002}");
        userSession.getAsyncRemote().sendText("{\"command\":\"subscribe\",\"channel\":1003}");
        
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
        		
//        		InputStream inStream = this.getClass().getClassLoader().getResourceAsStream("cc/co/llabor/websocket/polo.csv");
//				BufferedReader in= new BufferedReader( new InputStreamReader(  inStream ));
//				// put some known
// 				pairs.put("USDT_BTC", "121");
//				pairs.put("USDT_XMR", "126");
//				pairs.put("USDT_ETH", "149");
//				pairs.put("USDT_REP", "175");
//				pairs.put("USDT_ETC", "173");
//        		String lineTmp = in.readLine();
//        		while(lineTmp!=null) {
//        			if (lineTmp.startsWith("Name")) {
//        				lineTmp = in.readLine();
//        				continue;
//        			}
//        			else {
//        				String pairTMP = lineTmp.split("\t")[0];
//        				pairTMP = pairTMP .replaceAll("/", "_");
//        				String pairID = pairs.get(pairTMP);
//        				if (pairID == null) { // unknown pair
//        					pairs.put("TODO", pairTMP);
//        					// subscribe
//        					userSession.getAsyncRemote().sendText("{\"command\":\"unsubscribe\",\"channel\":\""+pairTMP+"\"}");
//        					userSession.getAsyncRemote().sendText("{\"command\":\"unsubscribe\",\"channel\":\"BTC_XRP\"}");
//        					
//        					// cansel till next time
//        					break;
//        				}else {
//        					// ignore
//        					
//        				}
//        				lineTmp = in.readLine();
//        			}
//        		}
        		this.messageHandler.handleMessage(message); 
        	}catch(ErrorProcessingException e) {
        		if (e.getMessage().contains("1001,")) return;
        		if (e.getMessage().contains("1002,")) return;
        		if (e.getMessage().contains("1003,")) return;
        		//pairs.get("TODO")
        		userSession.getAsyncRemote().sendText("{\"command\":\"unsubscribe\",\"channel\":\"USDT_ETC\"}");
        		System.err.println(e.getMessage());
        	} 
//        	catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
        }
    }
    
    Map<String, String> pairs= new HashMap<String, String>();

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
}