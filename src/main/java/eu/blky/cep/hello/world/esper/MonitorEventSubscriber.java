package eu.blky.cep.hello.world.esper;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ws.rrd.csv.Action;
import ws.rrd.csv.RrdKeeper;
import ws.rrd.csv.RrdUpdateAction; 

/**
 * Wraps Esper Statement and Listener. No dependency on Esper libraries.
 */
 
public class MonitorEventSubscriber implements StatementSubscriber { 
    /** Logger */
    private static Logger LOG = LoggerFactory.getLogger(WarningEventSubscriber.class);
	

    /**
     * {@inheritDoc}
     */
    public String getStatement() {

        // Example of simple EPL with a Time Window
        return "select avg(temperature) as avg_val from TemperatureEvent.win:time_batch(10 sec)";
    }

    public Double avg;
    
    
	/**
	 * Listener method called when Esper has detected a pattern match.
	 */
	public void update(Map<String, Double> eventMap) {
	
	    // average temp over 10 secs
		avg = (Double) eventMap.get("avg_val");
		String timeMs = ""+  System.currentTimeMillis() ; 
		//Thread.currentThread().setContextClassLoader(RrdKeeper.class.getClassLoader());
		Action rrdUpdateAction =  new RrdUpdateAction(); 
		 
		Object retval = rrdUpdateAction.perform(   "esper/chernoshima/average10sec" ,  timeMs , ""+ avg );
		LOG.trace("RRD action retval", retval);
	
	    StringBuilder sb = new StringBuilder();
	    sb.append("---------------------------------");
	    sb.append("\n- [MONITOR] Average Temp = " + avg);
	    sb.append("\n---------------------------------");
	
	    LOG.info(sb.toString());
	}
    
}
