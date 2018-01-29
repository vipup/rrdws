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

public class CriticalEventSubscriber implements StatementSubscriber {

    /** Logger */
    private static Logger LOG = LoggerFactory.getLogger(CriticalEventSubscriber.class);

    /** Used as the minimum starting threshold for a critical event. */
    private static final String CRITICAL_EVENT_THRESHOLD = "100";
    
    /**
     * If the last event in a critical sequence is this much greater than the first - issue a
     * critical alert.
     */
    private static final String CRITICAL_EVENT_MULTIPLIER = "1.5";
    
    /**
     * {@inheritDoc}
     */
    public String getStatement() {
        
        // Example using 'Match Recognise' syntax.
        String crtiticalEventExpression = "select * from TemperatureEvent "
                + "match_recognize ( "
                + "       measures A as temp1, B as temp2, C as temp3, D as temp4 "
                + "       pattern (A B C D) " 
                + "       define "
                + "               A as A.temperature > " + CRITICAL_EVENT_THRESHOLD + ", "
                + "               B as (A.temperature < B.temperature), "
                + "               C as (B.temperature < C.temperature), "
                + "               D as (C.temperature < D.temperature) and D.temperature > (A.temperature * " + CRITICAL_EVENT_MULTIPLIER + ")" + ")";
        
        return crtiticalEventExpression;
    }
    
    /**
     * Listener method called when Esper has detected a pattern match.
     */
    public void update(Map<String, TemperatureEvent> eventMap) {

        // 1st Temperature in the Critical Sequence
        TemperatureEvent temp1 = (TemperatureEvent) eventMap.get("temp1");
        // 2nd Temperature in the Critical Sequence
        TemperatureEvent temp2 = (TemperatureEvent) eventMap.get("temp2");
        // 3rd Temperature in the Critical Sequence
        TemperatureEvent temp3 = (TemperatureEvent) eventMap.get("temp3");
        // 4th Temperature in the Critical Sequence
        TemperatureEvent temp4 = (TemperatureEvent) eventMap.get("temp4");
        
		String timeMs = ""+  System.currentTimeMillis() ; 
		//Thread.currentThread().setContextClassLoader(RrdKeeper.class.getClassLoader());
		Action rrdUpdateAction =  new RrdUpdateAction(); 
		 
		Object retval = rrdUpdateAction.perform(   "esper/helloworld/critical/temp1"  ,  timeMs , ""+ temp1.getTemperature() );
		LOG.trace("RRD action retval", retval);
		 retval = rrdUpdateAction.perform(   "esper/helloworld/critical/temp2"  ,  timeMs , ""+ temp2.getTemperature() );
		LOG.trace("RRD action retval", retval);
		 retval = rrdUpdateAction.perform(   "esper/helloworld/critical/temp3"  ,  timeMs , ""+ temp3.getTemperature() );
		LOG.trace("RRD action retval", retval);
		 retval = rrdUpdateAction.perform(   "esper/helloworld/critical/temp4"  ,  timeMs , ""+ temp4.getTemperature() );
		LOG.trace("RRD action retval", retval);
		        
        

        StringBuilder sb = new StringBuilder();
        sb.append("***************************************");
        sb.append("\n* [ALERT] : CRITICAL EVENT DETECTED! ");
        sb.append("\n* " + temp1 + " > " + temp2 + " > " + temp3 + " > " + temp4);
        sb.append("\n***************************************");

        LOG.error(sb.toString());
    }

    
}
