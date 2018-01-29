package hello.world.esper;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ws.rrd.csv.Action;
import ws.rrd.csv.RrdKeeper;
import ws.rrd.csv.RrdUpdateAction; 
 

/**
 * Wraps Esper Statement and Listener. No dependency on Esper libraries.
 */
 
public class WarningEventSubscriber implements StatementSubscriber {

    /** Logger */
    private static Logger LOG = LoggerFactory.getLogger(WarningEventSubscriber.class);

    /** If 2 consecutive temperature events are greater than this - issue a warning */
    private static final String WARNING_EVENT_THRESHOLD = "400";

    
    /**
     * {@inheritDoc}
     */
    public String getStatement() {
        
        // Example using 'Match Recognise' syntax.
        String warningEventExpression = "select * from TemperatureEvent "
                + "match_recognize ( "
                + "       measures A as temp1, B as temp2 "
                + "       pattern (A B) " 
                + "       define " 
                + "               A as A.temperature > " + WARNING_EVENT_THRESHOLD + ", "
                + "               B as B.temperature > " + WARNING_EVENT_THRESHOLD + ")";
        
        return warningEventExpression;
    }
    
    /**
     * Listener method called when Esper has detected a pattern match.
     */
    public void update(Map<String, TemperatureEvent> eventMap) {


        // 1st Temperature in the Warning Sequence
        TemperatureEvent temp1 = (TemperatureEvent) eventMap.get("temp1");
        // 2nd Temperature in the Warning Sequence
        TemperatureEvent temp2 = (TemperatureEvent) eventMap.get("temp2");
        
		String timeMs = ""+  System.currentTimeMillis() ; 
		//Thread.currentThread().setContextClassLoader(RrdKeeper.class.getClassLoader());
		Action rrdUpdateAction =  new RrdUpdateAction(); 
		 
		Object retval = rrdUpdateAction.perform(   "esper/chernoshima/warning/temp1"  ,  timeMs , ""+ temp1.getTemperature() );
		LOG.trace("RRD action retval", retval);
		 retval = rrdUpdateAction.perform(   "esper/chernoshima/warning/temp2"  ,  timeMs , ""+ temp2.getTemperature() );
		LOG.trace("RRD action retval", retval);
		        

        StringBuilder sb = new StringBuilder();
        sb.append("--------------------------------------------------");
        sb.append("\n- [WARNING] : TEMPERATURE SPIKE DETECTED = " + temp1 + "," + temp2);
        sb.append("\n--------------------------------------------------");

        LOG.warn( sb.toString());
    }

 
}
