package eu.blky.cep.polo2rrd;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

import cc.co.llabor.websocket.DestroyTracker;
import cc.co.llabor.websocket.DestroyableWebSocketClientEndpoint;
import cc.co.llabor.websocket.MessageHandler;
import cc.co.llabor.websocket.PoloWSEndpoint;
import cc.co.llabor.websocket.cep.OrderTick;
import cc.co.llabor.websocket.cep.PoloTick; 
import cc.co.llabor.websocket.cep.StatisticPrinter;

@Service
public class Polo2RddForwarderService {


	private static final String WSS_API2_POLONIEX_COM = "wss://api2.poloniex.com";
	// @Autowired not works correctly - use applicationContext.xml def
	@Autowired
	private PoloWSEndpoint poloWS;
	
	private int engineCounter;

    // The Configuration is meant only as an initialization-time object.
	@Autowired
	private Configuration cepConfig ;//= new Configuration();
    
	private EPRuntime cepRT;
	private EPServiceProvider cep;
	private EPAdministrator cepAdm;
	String esper1002PROPS[] = {"N/A", "PRICELAST", "priceMax","PriceMin","PriceDiff", "volume24H","volumeTotal", "hight24H","low24H"};

	
	@PostConstruct
	public void init(){
		System.out.println("Polo2RddForwarderService init method called. cepConfig == "+getCepConfig());
// will be done over applicationContex.xml		
//		try {
//			createPoloWS();			 
//			System.out.println("createPoloWS() done");
//		} catch (URISyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Throwable e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
// TODO		
//		try {			 
//			EPRuntime cepTmp = initCEP();
//			System.out.println("initCEP() done ::"+cepTmp);
//		} catch (Throwable e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public Polo2RddForwarderService(){
		System.out.println("Polo2RddForwarderService no-args constructor called");
	}
	
	@PreDestroy
	public void destroy(){
		System.out.println("Polo2RddForwarderService destroy method called");
		try {
			getPoloWS().destroy();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		
	}		
	// will be done over applicationContex.xml	
//	private void createPoloWS() throws URISyntaxException {
//		URI endpointURI = new URI(WSS_API2_POLONIEX_COM);
//		DestroyTracker watchDog = new DestroyTrackerImplementation();
//		MessageHandler directMessageHandler = new DirectRddUpdater();
//		setPoloWS(new PoloWSEndpoint(endpointURI, watchDog));
//
//		// add listener - parce and "distribute
//		getPoloWS().addMessageHandler(directMessageHandler);
//	}

	private EPRuntime initCEP(){
	

	    getCepConfig().addEventType("PoloTick", PoloTick.class.getName());	
	    //
	    getCepConfig().addEventType("OrderTick", OrderTick.class.getName());
	    
	    cep = EPServiceProviderManager.getProvider("myCEPEngine#"+engineCounter++, getCepConfig());
		cepRT = cep.getEPRuntime(); 
	    cepAdm = cep.getEPAdministrator();
	    int intPairCounter = 0;

	
		
	    for ( Object key : getPoloWS().id2pairs.keySet()) {
	    	intPairCounter++;
	    	//if (intPairCounter<10)
	    	for (int pi=1; pi<esper1002PROPS.length;pi++) { // 
	    		String properyNameTmp =  esper1002PROPS[pi];
	    		String symTmp = (String) getPoloWS().id2pairs.get(key);
	    		
	    		String merticTmp = "_"+symTmp +"_"+properyNameTmp;
	    		{// TODO GOTO process1001
		    		String CID=symTmp;
					String theNameOfProp=properyNameTmp; 
	    		} 
			    // step 2 :  split / agregate by 10 sec	    
			    String avg10sec = "insert into BigEvents "
			    		+ "select "
			    		+ "		'"+merticTmp+"' metric, "
			    		+ "		(sum(price)/count(price)) avgA   "
			    		+ " "
			    		+ "from PoloTick.win:time_batch(10 sec) "
			    		+ "where  ("
			    		+ "			name='"+properyNameTmp+"' and "
			    				+ "	symbol='"+symTmp+"'  "
			    		+ "  "
			    				+ ")"
						+ "";  
			    //System.out.println(avg10sec);
			    EPStatement notNullEventsTmp = cepAdm.createEPL(avg10sec); 	
			    notNullEventsTmp.addListener(new RrdDirectUpdated(symTmp,properyNameTmp ));
			    				
			     
	    	}
	    }
	    
	    // step 4: summaryze that all
	    String eql4 = "insert into TicksPerSecond\n" + 
	    		"select  'PoloTick' type,  symbol, count(*) as cnt\n" + 
	    		"from PoloTick.win:time_batch(11 second)\n" + 
	    		"group by symbol";
	    EPStatement statStmtTmp = cepAdm.createEPL(eql4); 
	    //statStmtTmp.addListener(new StatisticPrinter());
	    
	    
	    
	    // step 5: summaryze that all
	    String eql5 = "" + 
	    		"select   type , sum(  cnt  )" + 
	    		"from TicksPerSecond.win:time_batch(1 second) " + 
	    		"group by type";
	    EPStatement statByTypeTmp = cepAdm.createEPL(eql5); 
	    statByTypeTmp.addListener(new StatisticPrinter());	    
	    
	    return cepRT;
	
	}

	public Configuration getCepConfig() {
		return cepConfig;
	}

	public void setCepConfig(Configuration cepConfig) {
		this.cepConfig = cepConfig;
	}

	public PoloWSEndpoint getPoloWS() {
		return poloWS;
	}

	public void setPoloWS(PoloWSEndpoint poloWS) {
		this.poloWS = poloWS;
	}
}
