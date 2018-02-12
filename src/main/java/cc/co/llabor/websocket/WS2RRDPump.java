package cc.co.llabor.websocket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map; 
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; ;
 

public class WS2RRDPump implements DestroyTracker {
    /** Logger */
    private static Logger LOG = LoggerFactory.getLogger(WS2RRDPump.class);	
	
	
	 	

	private static final String WSS_API2_POLONIEX_COM = "wss://api2.poloniex.com";
	private static final String WS_SSO_AT_THE_HOST_8080_RRDSAAS_WEBSOCKET_CHAT = "ws://sso.at.the.host:8080/rrdsaas/websocket/chat";
	public static final String PO_LO = "/PoLo";
	RRDWSEndpoint rrdWS ;
	PoloWSEndpoint poloWS;
	private boolean alive;
	@Override
	public void destroyed(DestroyableWebSocketClientEndpoint destroyableWebSocketClientEndpoint) {
		LOG.debug("initially was DESTROYED:"+destroyableWebSocketClientEndpoint);
		// close the rest...
		// looks like we have to restart....
		System.err.println("looks like we have to restart....");
		pumpAllBeOne = null ; // suicide
        startAllOfThis(11);
		
	}
	
	public void destroyed(RRDHandler rrdHandler) {
		LOG.error("RRDHandler rrdHandler  was DESTROYED:"+rrdHandler);		
	}	
	
	long created = System.currentTimeMillis();




	private ScheduledExecutorService scheduler;
	
	public WS2RRDPump (ScheduledExecutorService ses) throws URISyntaxException {
		this.scheduler = ses;
		System.out.println("T1");
	}
	@Override
	public void stop() {
		synchronized (WS2RRDPump.class) {
			System.out.println("TODO : Stop"+poloWS);
			poloWS.addMessageHandler(null);
			System.out.println("TODO : Stop"+rrdWS);
			rrdWS.addMessageHandler(null);
		}
		
	}	

	private void start() throws URISyntaxException {
		System.out.println("T2");
		synchronized (WS2RRDPump.class) {
			LOG.info("start RRDWS...");
			// open RRD-websocket
			createRRDWS(this);  
			LOG.info("start poloWS...");
			// open POLO- websocket
			createPoloWS(this);
			this.alive = true;
			LOG.info("started.");
		}	
	}
	private void createPoloWS(DestroyTracker watchDog) throws URISyntaxException {
		URI endpointURI = new URI(WSS_API2_POLONIEX_COM);
		poloWS = new PoloWSEndpoint(endpointURI, watchDog );
					// add listener - parce and "distribute
					poloWS.addMessageHandler(new PoloHandler(this));
	}

	private void createRRDWS(DestroyTracker watchDog) throws URISyntaxException {
		URI endpointURI = new URI(WS_SSO_AT_THE_HOST_8080_RRDSAAS_WEBSOCKET_CHAT);
		System.out.println("T3");
		rrdWS = new RRDWSEndpoint( endpointURI, watchDog );
					// add listener - just print + ignore
					rrdWS.addMessageHandler(new RRDHandler(this));
	}

	static int restartCounter = 0;
	public static void main(String[] args) {
        startAllOfThis(1);
	}
	private static final void cleanUpAllGarbageIfPossible(WS2RRDPump invalidPumpIsHere) {
		System.out.println("Last chance to die...");
		LOG.error( "Last chance to die...");
		LOG.info( "Last chance to die...");
		LOG.debug( "Last chance to die...");
		LOG.trace( "Last chance to die...");
		invalidPumpIsHere.destroy("EOL");
	}
	
	private static WS2RRDPump pumpAllBeOne = null;
	
 
	static volatile boolean newStartRequested = false;
	
	private static synchronized void startAllOfThis(long delayPar) {
		if (newStartRequested ) return;
		newStartRequested = true;
		final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
		
        ses.schedule(new Runnable() { 
            @Override
            public void run() {
            	newStartRequested  = false;
            	long inOUTMessageCounter = 0;// not used 
            	long outOUTMessageCounter = 0;// ->RRD 
            	long outINMessageCounter = 0;// not used
            	long inINMessageCounter = 0; // POLO->                  	
            	System.out.println("Check Pump::#"+inINMessageCounter+"/"+outOUTMessageCounter+" STATUS:"+pumpAllBeOne);
                // Check pump any minute , and restart if something wrong
            	while (pumpAllBeOne == null) {
            		synchronized (WS2RRDPump.class) {
            			if (pumpAllBeOne == null)
	            		try {
	            			pumpAllBeOne = new WS2RRDPump (ses);
	            			LOG.info("start#"+(restartCounter++)+" ...");
	            			 
	            			LOG.debug("new Pump created:"+pumpAllBeOne);
	            			pumpAllBeOne.start();
	            			LOG.debug("..and started.");
	            			 
	            			
	            		} catch (URISyntaxException e) {
							LOG.error("LOG.debug(\"new Pump created:\"+pump);", e) ;	
							pumpAllBeOne = null;
						}catch (Throwable e) {
							LOG.error("Hmmm... Something goes wrong with restart... -> try again...", e) ;
							startAllOfThis( 10 * restartCounter ) ; // with hope, that wrong restrt will take not more than this growing timep-period 
							cleanUpAllGarbageIfPossible(pumpAllBeOne);	
							pumpAllBeOne = null;
						}
            			 
            		}
           		
            	} 
			}
        }, delayPar , TimeUnit.SECONDS ); //1, TimeUnit.MINUTES);
	}
	
	
 

	protected boolean isAlive() { 
		return alive;
	}

	private void destroy(String reasonPar) {
		
		System.out.println("Destroy initiated..[" +reasonPar +"]");
		// first schedule new start in 33 sec ... 
		System.out.println("..I'll be back...");
		LOG.error( "..I'll be back..." );

		startAllOfThis(33);
		
		LOG.error("Destroy initiated..");
		try {
			this.rrdWS.destroy();
			this.rrdWS = null;
		} catch (RuntimeException e) {
			LOG.error("this.rrdWS.destroy();", e) ;
		} catch (IOException e) {
			LOG.error("this.rrdWS.destroy();", e) ;
		}
		try {
			this.poloWS.destroy();
			this.poloWS = null;
		} catch (RuntimeException e) {
			LOG.error("this.poloWS.destroy();;", e) ;
		} catch (IOException e) {
			LOG.error("this.poloWS.destroy();;", e) ;
		}
		alive = false;
		
		scheduler.shutdown();
	}


	static Map<String, String> xpathREPO = new HashMap<String, String>();
	
	static void createRRDandPushXpathToRegistry(final RRDWSEndpoint rrdWS, String xpath2rrd) {
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
				LOG.debug(key);
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