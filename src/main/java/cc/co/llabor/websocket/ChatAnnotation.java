package cc.co.llabor.websocket;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.jrobin.cmd.RrdCommander;
import org.jrobin.core.RrdException;
import org.jrobin.graph.RrdGraphInfo;
  

//import util.HTMLFilter;

@ServerEndpoint(value = "/websocket/chat")
public class ChatAnnotation {

    private static final Log log = LogFactory.getLog(ChatAnnotation.class);

    private static final String GUEST_PREFIX = "Guest";
    private static final AtomicInteger connectionIds = new AtomicInteger(0);
    private static final Set<ChatAnnotation> connections =
            new CopyOnWriteArraySet<ChatAnnotation>();

    private final String nickname;
    private Session session;

    public ChatAnnotation() {
        nickname = GUEST_PREFIX + connectionIds.getAndIncrement();
    }


    @OnOpen
    public void start(Session session) {
        this.session = session;
        connections.add(this);
        String message = String.format("* %s %s", nickname, "has joined.");
        broadcast(message);
    }


    @OnClose
    public void end() {
        connections.remove(this);
        String message = String.format("* %s %s",
                nickname, "has disconnected.");
        broadcast(message);
    }
    
    @OnMessage
    public void incoming(String message) {
    	//
    	if ("who".equals(message)) {
    		String listOfUsers ="RRDDAIMON";
    		try {
    			for(ChatAnnotation con:connections) {
    				listOfUsers += ",";
    				listOfUsers += con.nickname;
    			}
    			session.getBasicRemote().sendText("connected: "+ listOfUsers);
    		}catch(IOException e) {
    			// Ignore
    		}
    	} 
    	else if ("help".equals(message)) {
    		try {
    			
    			session.getBasicRemote().sendText("available commands: "+RrdCommander.getRrdCommands());
    		}catch(IOException e) {
    			// Ignore
    		}
    	} 
    	else if ((""+message).toLowerCase().startsWith("rrdtool")) {
    		try {
    			session.getBasicRemote().sendText("RRDTOOL command:"+HTMLFilter_filter(message.toString()));
	    		try {
	    			String command = message;
	    			command = command.replaceAll("\\\\", " ");
	    			Object xxx = RrdCommander.execute(command);
	    			// for ex.: rrdtool dump test.rrd
	    			if (xxx instanceof RrdGraphInfo) {
	    				//SEND BACK ::: graph - --color BACK#11111111 --only-graph -o -h  32  -w  64  --start=end-1week DEF:dbdata=X1228639772.rrd:data:AVERAGE  LINE2:dbdata#44EE4499  LINE1:dbdata#003300AA
	    				String magicTAG = "GIFGENGIFGEN";
	    				//session.getBasicRemote().sendText(magicTAG+":"+HTMLFilter_filter(command.toString()));
	    				session.getBasicRemote().sendText(magicTAG+":"+command.toString()  );
	    			}else {
	    				session.getBasicRemote().sendText("RRDTOOL result :"+HTMLFilter_filter(xxx.toString()));
	    			}
	    		}catch(RuntimeException e) {
	    			// Ignore
	    			session.getBasicRemote().sendText("RRDTOOL ERROR! :"+e.getMessage());
	    		}catch(IOException e) {
	    			// Ignore
	    			session.getBasicRemote().sendText("RRDTOOL ioEx! :"+e.getMessage());
	    			e.printStackTrace();
	    		} catch (RrdException e) {
						session.getBasicRemote().sendText("ERROR"+e.getMessage());e.printStackTrace();
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	    		
    	}    	else if ("help mama".equals(message)) {
    		try {
    			session.getBasicRemote().sendText("mama mia!");
    		}catch(IOException e) {
    			// Ignore
    		}
    	}    	else if ("help mama".equals(message)) {
    		try {
    			session.getBasicRemote().sendText("mama mia!");
    		}catch(IOException e) {
    			// Ignore
    		}
    	}
    	else {
	        // Never trust the client
	        String filteredMessage = String.format("%s: %s",
	                nickname, HTMLFilter_filter(message.toString()));
	        broadcast(filteredMessage);
    	}
    }



    private Object HTMLFilter_filter(String message) {
        if (message == null)
            return (null);

        char content[] = new char[message.length()];
        message.getChars(0, message.length(), content, 0);
        StringBuilder result = new StringBuilder(content.length + 50);
        for (int i = 0; i < content.length; i++) {
            switch (content[i]) {
            case '<':
                result.append("&lt;");
                break;
            case '>':
                result.append("&gt;");
                break;
            case '&':
                result.append("&amp;");
                break;
            case '"':
                result.append("&quot;");
                break;
            default:
                result.append(content[i]);
            }
        }
        String retval = (result.toString());
        return retval;

	}


	@OnError
    public void onError(Throwable t) throws Throwable {
        log.error("Chat Error: " + t.toString(), t);
    }


    private static void broadcast(String msg) {
        for (ChatAnnotation client : connections) {
            try {
                synchronized (client) {
                    client.session.getBasicRemote().sendText(msg);
                }
            } catch (IOException e) {
                log.debug("Chat Error: Failed to send message to client", e);
                connections.remove(client);
                try {
                    client.session.close();
                } catch (IOException e1) {
                    // Ignore
                }
                String message = String.format("* %s %s",
                        client.nickname, "has been disconnected.");
                broadcast(message);
            }
        }
    }
}





    

