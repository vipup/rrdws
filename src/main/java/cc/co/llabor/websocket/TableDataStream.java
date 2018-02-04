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

import cc.co.llabor.cache.CacheManager;
import net.sf.jsr107cache.Cache; 
 
@ServerEndpoint(value = "/websocket/tabledata")
public class TableDataStream {

    private static final Log LOG = LogFactory.getLog(TableDataStream.class);

    private static final String GUEST_PREFIX = "Herr";
    private static final AtomicInteger connectionIds = new AtomicInteger(0);
    private static final Set<TableDataStream> connections =
            new CopyOnWriteArraySet<TableDataStream>();

    private final String nickname;
    private Session session;

    final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
    public TableDataStream() {
        nickname = GUEST_PREFIX + connectionIds.getAndIncrement();
        
        
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
    	ses.shutdown();
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
				"    {id: \""+i+++ "\", ort: \"Berlin\", name: \"Ivan\", DoB: \"1.11.2011, ab 11 Uhr\"},\n" + 
				"    {id: \""+i+++ "\", ort: \"Sindey\", name: \"Hans\", DoB: \"2.11.2011, ab 22 Uhr\"},\n" + 
				"    {id: \""+i+++ "\", ort: \"New York\", name: \"John \", DoB: \"3.11.2011, ab 13 Uhr\"},\n" + 
				"    {id: \""+i+++ "\", ort: \"Tokio\", name: \"Wahno\", DoB: \"2.11.2011, ab 20 Uhr\"},\n" + 
				"    {id: \""+i+++ "\", ort: \"Moscow\", name: \"Don Juan\", DoB: \"4.11.2011, ab 03 Uhr\"}   ,\n" + 
				"    {id: \""+i+++ "\", ort: \"Madrid\", name: \"Diego\", DoB: \"5.11.2001, ab 03 Uhr\"}\n" + 
				"]";
		try {
			Cache diffCacher = CacheManager.getCache("DiffTracker");
			Object data = diffCacher .get("last");//diffCacher.put("last", tableDataMessage);
			tableDataMessage = data==null?""+data:tableDataMessage;
		}catch (Exception e) {
			// ignore
		}
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





    

