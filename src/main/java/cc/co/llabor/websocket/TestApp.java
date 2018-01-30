package cc.co.llabor.websocket;

import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * basic app for push one update over Websocket to rrdws.
 * 
 * @author i1
 *
 */
public class TestApp {
    /** Logger */
    private static Logger LOG = LoggerFactory.getLogger(TestApp.class);	
	

    public static void main(String[] args) {
        try {
            // open websocket
            final RRDWSEndpoint clientEndPoint = new RRDWSEndpoint(new URI("ws://sso.at.the.host:8080/rrdsaas/websocket/chat"), null);

            // add listener
            clientEndPoint.addMessageHandler(new MessageHandler() {
                public void handleMessage(String message) {
                    LOG.debug("<<<<<<<<"+message);
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