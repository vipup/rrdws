package cc.co.llabor.websocket;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory; 
 
@ServerEndpoint(value = "/websocket/tabledata")
public class TableDataStream {

    private static final Log LOG = LogFactory.getLog(TableDataStream.class);

    private static final String GUEST_PREFIX = "Guest";
    private static final AtomicInteger connectionIds = new AtomicInteger(0);
    private static final Set<TableDataStream> connections =
            new CopyOnWriteArraySet<TableDataStream>();

    private final String nickname;
    private Session session;

    public TableDataStream() {
        nickname = GUEST_PREFIX + connectionIds.getAndIncrement();
        final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        
        ses.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
	            String tableDataMessage = generateNewFakeData();
				broadcast(tableDataMessage); 
			} 
        }, 0, 7, TimeUnit.SECONDS ); //1, TimeUnit.MINUTES);
    } 

    @OnOpen
    public void start(Session session) {
        this.session = session;
        connections.add(this);
        String tableDataMessage = generateNewFakeData();
        broadcast(tableDataMessage);
    }


    @OnClose
    public void end() {
        connections.remove(this);
        String message = String.format("* %s %s",
                nickname, "has disconnected.");
        broadcast(message);
    }
    
    int i = 0;
    
    @OnMessage
    public void incoming(String message) {
        {
            String tableDataMessage = generateNewFakeData();
			broadcast(tableDataMessage);
    	}
    }

	private String generateNewFakeData() {
		String tableDataMessage= " [\n" + 
				"    {id: \""+i+++ "\", ort: \"Schlosskeller\", name: \"DnB for live\", beginn: \"1.11.2011, ab 22 Uhr\"},\n" + 
				"    {id: \""+i+++ "\", ort: \"603qm\", name: \"Electro Technik\", beginn: \"1.11.2011, ab 22 Uhr\"},\n" + 
				"    {id: \""+i+++ "\", ort: \"Krone\", name: \"da geht der Punk \", beginn: \"1.11.2011, ab 20 Uhr\"},\n" + 
				"    {id: \""+i+++ "\", ort: \"Schlosskeller\", name: \"Wuerstchenfest\", beginn: \"2.11.2011, ab 20 Uhr\"},\n" + 
				"    {id: \""+i+++ "\", ort: \"Krone\", name: \"Karaoke\", beginn: \"2.11.2011, ab 21 Uhr\"}\n" + 
				"]";
		return tableDataMessage;
	}

  


	@OnError
    public void onError(Throwable t) throws Throwable {
		LOG.error("Chat Error: " + t.toString(), t);
    }


    private static void broadcast(String msg) {
        for (TableDataStream client : connections) {
            try {
                synchronized (client) {
                    client.session.getBasicRemote().sendText(msg);
                }
            } catch (IOException e) {
            	LOG.fatal("Chat Error: Failed to send message to client", e);
                connections.remove(client);
                try {
                    client.session.close();
                } catch (IOException e1) {
                    // Ignore
                	System.exit(-1111);
                	LOG.error("client.session.close();", e1);
                }
                String message = String.format("* %s %s",
                        client.nickname, "has been disconnected.");
                broadcast(message);
            }
        }
    }
}





    

