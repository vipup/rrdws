package cc.co.llabor.websocket;

import java.net.URI;
import java.net.URISyntaxException;
 
// https://stackoverflow.com/questions/26452903/javax-websocket-client-simple-example
public class HelloPixWS2 {

    public static void main(String[] args) {
        try {
            // open websocket
            final PoloWebsocketClientEndpoint clientEndPoint = new PoloWebsocketClientEndpoint(new URI("wss://api2.poloniex.com"), null);
            

 System.out.println(1);
            
            // add listener
            clientEndPoint.addMessageHandler(new PoloWebsocketClientEndpoint.MessageHandler() {
                public void handleMessage(String message) {
                    System.out.println("<<<<<<<<"+message);
                }
            });
System.out.println(2);            
            

            // send message to websocket
//            clientEndPoint.sendMessage("{\"command\":\"subscribe\",\"channel\":1001}");
//            clientEndPoint.sendMessage("{\"command\":\"subscribe\",\"channel\":1002}");
//            clientEndPoint.sendMessage("{\"command\":\"subscribe\",\"channel\":1003}");
//            clientEndPoint.sendMessage("{\"command\":\"subscribe\",\"channel\":\"USDT_BTC\"}");

            // wait 5 seconds for messages from websocket
            Thread.sleep(115000);
            
             
            
            Thread.sleep(50000);
        } catch (InterruptedException ex) {
            System.err.println("InterruptedException exception: " + ex.getMessage());
        } catch (URISyntaxException ex) {
            System.err.println("URISyntaxException exception: " + ex.getMessage());
        }
    }
}