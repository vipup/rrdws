package cc.co.llabor.websocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader; 
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper; 
 

public class PoloPairListener {

	static Set<String> valutas= new  HashSet<String>();
	static Set<String> bases= new  HashSet<String>();
    
	static int x=0;
	static int y=0;
	
    
	public static final String getNextUnknownPair() {
		x+=1;
		if (x>bases.size()-1) {
			x=0;
			y+=1;
		}
		if (y>valutas.size()-1) { // roll it
			x=0;
			y=0;
		}
		String srcTMP = ""+bases.toArray()[x];
		String  tarTMP = ""+valutas.toArray()[y];
		while (tarTMP .equals(srcTMP)) {
			y++;
			if (y>valutas.size()-1)break;
			tarTMP = ""+valutas.toArray()[y];
		}
		return srcTMP+"_"+tarTMP;
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
						//System.out.println(nodeTmp);
						String theType = "" + nodeTmp.get(0); 
						pairs.put(poloWS.getPairName(), ""+theType);
						String currencyPair = nodeTmp .get(2) .get(0).get(1).get("currencyPair").asText();//:"BTC_NEOS",
						System.out.println(currencyPair +" == "+ theType);
						pairs.put(currencyPair, ""+theType);


					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (RuntimeException e) {
						// ignore
						//System.out.println("error processing  message: "+message);
						//throw new ErrorProcessingException(message);
					}
					
					String nextUnknownPair = getNextUnknownPair();
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
		bases.add( "USDT");
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
				String pairID = pairs.get(pairTMP);
				valutas.add(pairTMP.split("_")[0]);
				valutas.add(pairTMP.split("_")[1]);
				lineTmp = in.readLine();
			}
		}
		System.out.println(valutas);
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