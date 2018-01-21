package cc.co.llabor.websocket;

import java.net.URI;
import java.net.URISyntaxException;
// https://stackoverflow.com/questions/26452903/javax-websocket-client-simple-example
public class TestApp {

    public static void main(String[] args) {
        try {
            // open websocket
            final WebsocketClientEndpoint clientEndPoint = new WebsocketClientEndpoint(new URI("ws://sso.at.the.host:8080/rrdsaas/websocket/chat"));

            // add listener
            clientEndPoint.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
                public void handleMessage(String message) {
                    System.out.println("<<<<<<<<"+message);
                }
            });

            // send message to websocket
            clientEndPoint.sendMessage(" create test.rrd "
            		+ " --start 920804400 "
            		+ " DS:speed:COUNTER:600:U:U "
            		+ " RRA:AVERAGE:0.5:1:24 "
            		+ " RRA:AVERAGE:0.5:6:10");

            // wait 5 seconds for messages from websocket
            Thread.sleep(5000);
            
            //  fill the database as follows
            
            clientEndPoint.sendMessage("update test.rrd 920804700:12345 920805000:12357 920805300:12363 ");
			clientEndPoint.sendMessage("update test.rrd 920805600:12363 920805900:12363 920806200:12373 ");
			clientEndPoint.sendMessage("update test.rrd 920806500:12383 920806800:12393 920807100:12399 ");
			clientEndPoint.sendMessage("update test.rrd 920807400:12405 920807700:12411 920808000:12415 ");
			clientEndPoint.sendMessage("update test.rrd 920808300:12420 920808600:12422 920808900:12423 ");
			
            // wait 5 seconds for messages from websocket
            Thread.sleep(5000);
            
			clientEndPoint.sendMessage(" fetch test.rrd AVERAGE --start 920804400 --end 920809200 ");
            // wait 5 seconds for messages from websocket
			 Thread.sleep(5000);
            
            
            clientEndPoint.sendMessage("graph speed.gif \\"
            		+ "            --start 920804400 --end 920808000 \\"
            		+ "            DEF:myspeed=test.rrd:speed:AVERAGE \\"
            		+ "            LINE2:myspeed#FF0000"
            		+ "");
            
            Thread.sleep(50000);
        } catch (InterruptedException ex) {
            System.err.println("InterruptedException exception: " + ex.getMessage());
        } catch (URISyntaxException ex) {
            System.err.println("URISyntaxException exception: " + ex.getMessage());
        }
    }
}