package cc.co.llabor.websocket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit; ;
 

public class WS2RRDPump implements DestroyTracker {

	private static final String WSS_API2_POLONIEX_COM = "wss://api2.poloniex.com";
	private static final String WS_SSO_AT_THE_HOST_8080_RRDSAAS_WEBSOCKET_CHAT = "ws://sso.at.the.host:8080/rrdsaas/websocket/chat";
	static final String PO_LO = "/PoLo";
	RRDWebsocketClientEntPoint rrdWS ;
	PoloWebsocketClientEndpoint poloWS;
	private boolean alive;
	@Override
	public void destroyed(DestroyableWebSocketClientEndpoint destroyableWebSocketClientEndpoint) {
		System.out.println("initially was DESTROYED:"+destroyableWebSocketClientEndpoint);
		// close the rest...
		this.destroy();
		
	}
	
	public WS2RRDPump () throws URISyntaxException {
		// start();
	}

	private void start() throws URISyntaxException {
		// open RRD-websocket
		createRRDWS(this);  
		// open POLO- websocket
		createPoloWS(this);
		this.alive = true;
	}

	private void createPoloWS(DestroyTracker watchDog) throws URISyntaxException {
		URI endpointURI = new URI(WSS_API2_POLONIEX_COM);
		poloWS = new PoloWebsocketClientEndpoint(endpointURI, watchDog );
					// add listener - parce and "distribute
					poloWS.addMessageHandler(new PoloHandler(this));
	}

	private void createRRDWS(DestroyTracker watchDog) throws URISyntaxException {
		URI endpointURI = new URI(WS_SSO_AT_THE_HOST_8080_RRDSAAS_WEBSOCKET_CHAT);
		rrdWS = new RRDWebsocketClientEntPoint( endpointURI, watchDog );
					// add listener - just print + ignore
					rrdWS.addMessageHandler(new RRDHandler(this));
	}

	public static void main(String[] args) {
        final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        
        ses.scheduleWithFixedDelay(new Runnable() {
        	
        	WS2RRDPump pump = null;
            @Override
            public void run() {
                // Check pump any minute , and restart if something wrong
            	if (pump == null) {
            		try {
            			System.out.println("new Pump created:"+pump);
            			pump = new WS2RRDPump ();
            			pump.start();
            			
					} catch (URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
            	if (!pump.isAlive()) { // check fo alive, and reInit
            		pump = null;
            		System.out.println("Pump should be GCed.. ");
            		System.gc();
            	}
            	
            	
            }
        }, 0, 1, TimeUnit.MINUTES);
	}
	
	
 

	protected boolean isAlive() { 
		return alive;
	}

	private void destroy() {
		try {
			this.rrdWS.addMessageHandler(null);
			this.rrdWS.destroy();
			this.rrdWS = null;
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.poloWS.addMessageHandler(null);
			this.poloWS.destroy();
			this.poloWS = null;
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		alive = false;
	}


	static Map<String, String> xpathREPO = new HashMap<String, String>();
	
	static void createRRDandPushXpathToRegistry(final RRDWebsocketClientEntPoint rrdWS, String xpath2rrd) {
		// TODO : here we are using the common rrd-create command with the same hash-function for 
		// transformation XPATH->X-FILENAME.rrd
		// after creating the same xpath have to be "synchronized with rrd-registry
		
		String cmp =   xpathREPO .get(xpath2rrd);
		String alloweddebugging1= "1111"; 
		if (cmp == null) {
		
			String cmdCreateTmp = makeCreateCMD(System.currentTimeMillis(), xpath2rrd );
			//System.err.println(cmdCreateTmp);
			rrdWS.sendMessage(cmdCreateTmp);
			xpathREPO.put(xpath2rrd, cmdCreateTmp);
			// TODO: currently it is not 100% fullproof sync - rrd will get the xpath and push it into REG
			rrdWS.sendMessage("checkreg "+xpath2rrd);
		}else {
			if (alloweddebugging1.length()>10)
			for (String key:xpathREPO.keySet()) {
				System.out.println(key);
			}
			// 
			//System.out.println("skipped");xpathREPO.clear();
		}
		
	}

	/**
	 * BE CAREFULL WITH reimpelmentation THIS METHOD! The risk is to look all
	 * exisitng RRD-Databases
	 * 
	 * @author vipup
	 * @param xpath
	 * @return
	 */
	public static final String xpath2Hash(String xpath) {
		String rrddb = "X" + xpath.hashCode() + ".rrd";
		// checkReg(rrddb, xpath);
		return rrddb;
	}

	public final static String makeUpdateCMD(String data, long timestampTmp, String xpath) {
		String rrddb = xpath2Hash(xpath);
		String cmdTmp = "rrdtool update " + rrddb + " " + (timestampTmp / 1000L) + ":" + data;
		return cmdTmp;
	}

	public static final String makeCreateCMD(long timestampTmp, String xpath) {
		String rrddb = xpath2Hash(xpath);
		String cmdCreate = "rrdtool create " + "" + rrddb + " --start " + (((timestampTmp - 10000) / 1000L)) + " --no-overwrite "
				+ " --step 1 " + "				DS:data:GAUGE:240:U:U " + "				RRA:AVERAGE:0.5:3:480 "
				+ "				RRA:AVERAGE:0.5:17:592 " + "				RRA:AVERAGE:0.5:131:340 "
				+ "				RRA:AVERAGE:0.5:731:719 " + "				RRA:AVERAGE:0.5:10000:273 "
				+ "				RRA:MAX:0.5:3:480 " + "				RRA:MAX:0.5:17:592 "
				+ "				RRA:MAX:0.5:131:340 " + "				RRA:MAX:0.5:731:719 "
				+ "				RRA:MAX:0.5:10000:273 " + "				RRA:MIN:0.5:3:480 "
				+ "				RRA:MIN:0.5:17:592 " + "				RRA:MIN:0.5:131:340 "
				+ "				RRA:MIN:0.5:731:719 " + "				RRA:MIN:0.5:10000:273 " + " ";
		return cmdCreate;
	}

}