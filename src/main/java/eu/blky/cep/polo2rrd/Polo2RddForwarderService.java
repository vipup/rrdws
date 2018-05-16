package eu.blky.cep.polo2rrd;

import java.io.IOException; 

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import com.espertech.esper.client.EPRuntime; 
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

import cc.co.llabor.websocket.MessageHandler;
 
import cc.co.llabor.websocket.PoloWSEndpoint;
import cc.co.llabor.websocket.cep.OrderTick; 
import cc.co.llabor.websocket.cep.StatisticPrinter;
import eu.blky.cep.polo2rrd.updaters.RrdCountUpdater;
import eu.blky.cep.polo2rrd.updaters.Statistic2RddUpdater;
import cc.co.llabor.system.StatusMonitor; 


@Service
public class Polo2RddForwarderService {
	private CepKeeper cepKeeper;
	/**
	 * @return the cepKeeper
	 */
	public CepKeeper getCepKeeper() {
		return cepKeeper;
	}
	/**
	 * @param cepKeeper the cepKeeper to set
	 */
	public void setCepKeeper(CepKeeper cepKeeper) {
		this.cepKeeper = cepKeeper;
	}
	
	// @Autowired not works correctly - use applicationContext.xml def
	@Autowired
	private PoloWSEndpoint poloWS;
	
	private int engineCounter;

	
	/** Logger */
	private static Logger LOG = LoggerFactory.getLogger(Polo2RddForwarderService.class);

	private StatusMonitor statusMonitor;
//	@PostConstruct -  workaround via StartStopServlet 
	public void setStatusMonitor(StatusMonitor sm){
		System.out.println("Assigned StatusMonitor:"+sm);
		this.statusMonitor = sm;
	}
	@PostConstruct
	public void init(){
		System.out.println("Polo2RddForwarderService init method called. cepConfig == "+cepKeeper.getCepConfig()); 
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
 

	private EPRuntime initCEP(){ 
		// used /rrd/src/main/java/cc/co/llabor/websocket/cep/OrderTick.java
		// NOT user anymore ! cepKeeper.getCepConfig().addEventType("PoloTick", PoloTick.class.getName());
		// used /rrd/src/main/java/cc/co/llabor/websocket/cep/OrderTick.java
		cepKeeper.getCepConfig().addEventType("OrderTick", OrderTick.class.getName()); 
		cepKeeper.setCep(EPServiceProviderManager.getProvider("myCEPEngine#"+engineCounter++, cepKeeper.getCepConfig()));
	    cepKeeper.setCepRT(cepKeeper.getCep().getEPRuntime()); 
	    cepKeeper.setCepAdm(cepKeeper.getCep().getEPAdministrator());  
	    
	 // used / /rrd/src/main/java/cc/co/llabor/websocket/cep/OrderTick.java 
	    // step 2 :  split / agregate by 10 sec	    
//		private double price;
//		private double volume;
//		private String pair;
//		private boolean type;
//		private double total;	  	    
	    // step 4: summaryze that all
	    String eql4 = "insert into TicksPerSecond\n" + 
	    		"select  'PoloTick' type,  pair, count(*) as cnt\n" + 
	    		"from OrderTick.win:time_batch(11 second)\n" + 
	    		"group by pair";
	    EPStatement statStmtTmp = cepKeeper.getCepAdm().createEPL(eql4); 
	    statStmtTmp.addListener(new Statistic2RddUpdater("TicksPerSecond")); 
	    
	    // step 5: summaryze that all
	    String eql5 = "" + 
	    		"select   type , sum(  cnt  )" + 
	    		"from TicksPerSecond.win:time_batch(1 second) " + 
	    		"group by type";
	    EPStatement statByTypeTmp = cepKeeper.getCepAdm().createEPL(eql5); 
	    statByTypeTmp.addListener(new StatisticPrinter());	    
	    
	    return cepKeeper.getCepRT();
	
	}


	public void setPoloWS(PoloWSEndpoint poloWS) {
		this.poloWS = poloWS;
		MessageHandler msgHandler = new RrdCountUpdater();
		this.poloWS.addMessageHandler(msgHandler );
		MessageHandler XXX = new XXXEsperHandler(this);
		this.poloWS.addMessageHandler(XXX );
	}




	public PoloWSEndpoint getPoloWS() {
		return poloWS;
	}	


}
