package eu.blky.cep.hello.world.esper;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;

 

public class ChernoshimaService {
	
	private static Logger log = LoggerFactory.getLogger(ChernoshimaService.class);

	RandomTemperatureEventGenerator generator = null;
	EPServiceProvider provider ;
	
	private void initTschernoschima() {
		try {
	        Configuration configuration = new Configuration();
	        configuration.addEventType("TemperatureEvent", TemperatureEvent.class.getName()); 
	        provider = EPServiceProviderManager.getProvider("NucleaTemperatureHandling", configuration);
			// temperatureEventHandler.epService = provider;
	        
			TemperatureEventHandler temperatureEventHandler = new TemperatureEventHandler(provider); 
			temperatureEventHandler.subscribe(new MonitorEventSubscriber()); 
			temperatureEventHandler.subscribe(new CriticalEventSubscriber()); 
			temperatureEventHandler.subscribe(new WarningEventSubscriber()); 
			generator = new RandomTemperatureEventGenerator(temperatureEventHandler);
			generator .startSendingTemperatureReadings(Long.MAX_VALUE );
			
		}catch(Throwable e) {
			log.error("private void initTschernoschima() {", e);
		}
	}

	
	@PostConstruct
	public void init(){
		System.out.println("ChernoshimaService init method called");
		initTschernoschima();
	}
	
	public ChernoshimaService(){
		System.out.println("ChernoshimaService no-args constructor called");
	}
	
	@PreDestroy
	public void destroy(){
		System.out.println("ChernoshimaService destroy method called");
		provider.destroy();
		generator.stop();
		
	}	
}
