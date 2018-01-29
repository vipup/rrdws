package cc.co.llabor.websocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader; 
import java.net.URI;
import java.net.URISyntaxException; 
import java.util.HashMap;
import java.util.HashSet; 
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper; 
 

public class PoloPairListener {
    /** Logger */
    private static Logger LOG = LoggerFactory.getLogger(PoloPairListener.class);	
 		

	static Set<String> valutas= new  HashSet<String>();
	static Set<String> bases= new  HashSet<String>();
	static Set<String> allpossiblepairs= new  HashSet<String>();
    
	static int lastCcheched=0;
	
	
    
	public static final String getNextUnknownPair() {
		String retval = (String) allpossiblepairs.toArray()[lastCcheched++]; 
		return retval;
	}  
    

	public static void main(String[] args) throws IOException {
		

		initPairsFromFile();
		try {
			
    		
			
			// open POLO- websocket
			URI endpointURI = new URI("wss://api2.poloniex.com");
			final PoloOrderReader poloWS = new PoloOrderReader(endpointURI);
			// add listener - parce and "distribute
			poloWS.addMessageHandler(new PoloOrderReader.MessageHandler_A() {
			
				public void handleMessage(String message) throws ErrorProcessingException { 
					ObjectMapper mapper = new ObjectMapper();
					
					try {
						JsonNode nodeTmp = mapper.readTree(message);
						//LOG.debug(nodeTmp);
						String theType = "" + nodeTmp.get(0); 
						pairs.put(poloWS.getPairName(), ""+theType);
						String currencyPair = nodeTmp .get(2) .get(0).get(1).get("currencyPair").asText();//:"BTC_NEOS",
						LOG.debug(currencyPair +" == "+ theType);
						pairs.put(currencyPair, ""+theType);


					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (RuntimeException e) {
						// ignore
						//LOG.debug("error processing  message: "+message);
						//throw new ErrorProcessingException(message);
					}
					
					String nextUnknownPair = getNextUnknownPair();
					//LOG.debug("------------------------------"+nextUnknownPair);
					poloWS.setPairName(nextUnknownPair); 

				}


				 		 
			});
			// wait 5 seconds for messages from websocket
			for (int i=0;i<111111;i++)
				Thread.sleep(511131000);

		} catch (InterruptedException ex) {
			System.err.println("InterruptedException exception: " + ex.getMessage());
		} catch (URISyntaxException ex) {
			System.err.println("URISyntaxException exception: " + ex.getMessage());
		}
	}

	static Map<String, String> pairs= new HashMap<String, String>();
	private static void initPairsFromFile() throws IOException {
		
		// put some known
		bases.add("USDT");
		bases.add("BTC" );
		bases.add("ETH" );
		bases.add("XMR" );
				
		
		InputStream inStream = PoloPairListener.class.getClassLoader().getResourceAsStream("cc/co/llabor/websocket/polo.csv");
		BufferedReader in= new BufferedReader( new InputStreamReader(  inStream ));

		String lineTmp = in.readLine();
		while(lineTmp!=null) {
			if (lineTmp.startsWith("Name")) {
				lineTmp = in.readLine();
				continue;
			}
			else {
				String pairTMP = lineTmp.split("\t")[0];
				pairTMP = pairTMP .replaceAll("/", "_");
				
				valutas.add(pairTMP.split("_")[0]);
				valutas.add(pairTMP.split("_")[1]);
				lineTmp = in.readLine();
			}
		}
		LOG.debug("valutas::"+valutas);
		
		for (int i=0; i<bases.size();i++) {
			for (int j=0; j<valutas.size();j++) {
				String tarTMP = (String) bases.toArray()[i];
				String srcTMP =(String) valutas.toArray()[j];
				allpossiblepairs.add(srcTMP+"_"+tarTMP);
				//allpossiblepairs.add(tarTMP+"_"+srcTMP);
			}			
		}
		LOG.debug("allpossiblepairs:"+allpossiblepairs);
	}
	
	
	
//	if (pairID == null) { // unknown pair
//		pairs.put("TODO", pairTMP);
//		// subscribe
////			userSession.getAsyncRemote().sendText("{\"command\":\"unsubscribe\",\"channel\":\""+pairTMP+"\"}");
////			userSession.getAsyncRemote().sendText("{\"command\":\"unsubscribe\",\"channel\":\"BTC_XRP\"}");
//		// cansel till next time
//		break;
//	}else {
//		// ignore
//		
//	}
  

}