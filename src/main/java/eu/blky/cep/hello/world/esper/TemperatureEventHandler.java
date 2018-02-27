package eu.blky.cep.hello.world.esper;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
 
import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

/**
 * This class handles incoming Temperature Events. It processes them through the EPService, to which
 * it has attached the 3 queries.
 */
 
public class TemperatureEventHandler {
	
	final long uuid = this.hashCode() * System.currentTimeMillis();
	public String toString() {
		return "#"+uuid +"$";
	}

    /** Logger */
    private static Logger LOG = LoggerFactory.getLogger(TemperatureEventHandler.class);

    /** Esper service */
    EPServiceProvider epService;

	private Map<Class,StatementSubscriber> supscribers=new HashMap<Class, StatementSubscriber>();
 

    public TemperatureEventHandler(EPServiceProvider provider) {
		this.epService = provider;
	} 
    /**
     * Handle the incoming TemperatureEvent.
     */
    public void handle(TemperatureEvent event) {

        LOG.trace(event.toString());
        epService.getEPRuntime().sendEvent(event);

    }

	public void subscribe(StatementSubscriber subscriber) {
		this.supscribers.put(subscriber.getClass(), subscriber);
 		String expressionText = subscriber.getStatement();
		EPStatement stTMP = epService.getEPAdministrator().createEPL(expressionText);
		stTMP.setSubscriber(subscriber);		
	}
	public StatementSubscriber getSubscriberByClass(Class<MonitorEventSubscriber> class1) {
		return supscribers.get(class1);
	}

 
}
