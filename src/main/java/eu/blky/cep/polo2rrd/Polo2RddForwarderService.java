package eu.blky.cep.polo2rrd;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

import cc.co.llabor.websocket.MessageHandler;
 
import cc.co.llabor.websocket.PoloWSEndpoint;
import cc.co.llabor.websocket.cep.OrderTick;
import cc.co.llabor.websocket.cep.PoloTick; 
import cc.co.llabor.websocket.cep.StatisticPrinter;

import cc.co.llabor.system.StatusMonitor; 


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
	/** Logger */
	private static Logger LOG = LoggerFactory.getLogger(Polo2RddForwarderService.class);

	private StatusMonitor statusMonitor;
//	@PostConstruct -  workaround via StartStopServlet 
	public void setStatusMonitor(StatusMonitor sm){
		this.statusMonitor = sm;
	}
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
		try {			 
			initCEP();
			System.out.println("initCEP() done");
			System.out.println("initCEP() done");
			System.out.println("initCEP() done");
			System.out.println("initCEP() done");
			System.out.println("initCEP() done");
			System.out.println("initCEP() done");
			System.out.println("initCEP() done");
			System.out.println("initCEP() done");
			statusMonitor.getStatus().put("initCEP", "done");
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
	}
	
	public Polo2RddForwarderService(){
		System.out.println("Polo2RddForwarderService no-args constructor called");
	}
	
	@PreDestroy
	public void destroy(){
		System.out.println("Polo2RddForwarderService destroy method called");
		System.out.println("Polo2RddForwarderService destroy method called");
		System.out.println("Polo2RddForwarderService destroy method called");
		System.out.println("Polo2RddForwarderService destroy method called");
		System.out.println("Polo2RddForwarderService destroy method called");
		LOG.error("Polo2RddForwarderService destroy method called");
		LOG.error("Polo2RddForwarderService destroy method called");
		LOG.error("Polo2RddForwarderService destroy method called");
		LOG.error("Polo2RddForwarderService destroy method called");
		LOG.error("Polo2RddForwarderService destroy method called");
		statusMonitor.getStatus().put("destroyCEP", "Polo2RddForwarderService destroy method called");
		
		try {
			getPoloWS().destroy();
		} catch (IOException e) {
			LOG.error("public void destroy(){}", e);
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
	    
	    setCep(EPServiceProviderManager.getProvider("myCEPEngine#"+engineCounter++, getCepConfig()));
		setCepRT(getCep().getEPRuntime()); 
	    setCepAdm(getCep().getEPAdministrator());
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
			    EPStatement notNullEventsTmp = getCepAdm().createEPL(avg10sec); 	
			    notNullEventsTmp.addListener(new SysoUpdater(symTmp,properyNameTmp ));
			    				
			     
	    	}
	    }
	    
	    // step 4: summaryze that all
	    String eql4 = "insert into TicksPerSecond\n" + 
	    		"select  'PoloTick' type,  symbol, count(*) as cnt\n" + 
	    		"from PoloTick.win:time_batch(11 second)\n" + 
	    		"group by symbol";
	    EPStatement statStmtTmp = getCepAdm().createEPL(eql4); 
	    //statStmtTmp.addListener(new StatisticPrinter());
	    
	    
	    
	    // step 5: summaryze that all
	    String eql5 = "" + 
	    		"select   type , sum(  cnt  )" + 
	    		"from TicksPerSecond.win:time_batch(1 second) " + 
	    		"group by type";
	    EPStatement statByTypeTmp = getCepAdm().createEPL(eql5); 
	    statByTypeTmp.addListener(new StatisticPrinter());	    
	    
	    return getCepRT();
	
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
		MessageHandler msgHandler = new RrdCountUpdater();
		this.poloWS.addMessageHandler(msgHandler );
		MessageHandler XXX = new XXXEsperHandler(this);
		this.poloWS.addMessageHandler(XXX );
	}

	public EPRuntime getCepRT() {
		return cepRT;
	}

	public void setCepRT(EPRuntime cepRT) {
		this.cepRT = cepRT;
	}

	public EPServiceProvider getCep() {
		return cep;
	}

	public void setCep(EPServiceProvider cep) {
		this.cep = cep;
	}

	public EPAdministrator getCepAdm() {
		return cepAdm;
	}

	public void setCepAdm(EPAdministrator cepAdm) {
		this.cepAdm = cepAdm;
	}
}
