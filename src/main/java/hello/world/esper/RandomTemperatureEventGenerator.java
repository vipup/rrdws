package hello.world.esper;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;  

/**
 * Just a simple class to create a number of Random TemperatureEvents and pass them off to the
 * TemperatureEventHandler.
 */
 
public class RandomTemperatureEventGenerator {

    /** Logger */
    private static Logger LOG = LoggerFactory.getLogger(RandomTemperatureEventGenerator.class);

    /** The TemperatureEventHandler - wraps the Esper engine and processes the Events  */ 
    private TemperatureEventHandler temperatureEventHandler;

    public RandomTemperatureEventGenerator(TemperatureEventHandler par1) {
		this.temperatureEventHandler = par1;
	}


	/**
     * Creates simple random Temperature events and lets the implementation class handle them.
     */
    public void startSendingTemperatureReadings(final long noOfTemperatureEvents) {

        ExecutorService xrayExecutor = Executors.newSingleThreadExecutor();

        xrayExecutor.submit(new Runnable() {
        	final TemperatureEventHandler xxx = temperatureEventHandler ;
            public void run() {
                LOG.debug(getStartingMessage());
                
                int count = 0;
                while (count < noOfTemperatureEvents) {
                	try {
	                    TemperatureEvent ve = new TemperatureEvent(new Random().nextInt(500), new Date());
	                    xxx.handle(ve);
	                    count++;
                    
                        Thread.sleep(10011);
                    } catch (InterruptedException e) {
                        LOG.error("Thread Interrupted", e);
                    } catch (Throwable e) {
                        LOG.error("???", e);
                    }
                }

            }
        });
    }

    
    private String getStartingMessage(){
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n************************************************************");
        sb.append("\n* STARTING - ");
        sb.append("\n* PLEASE WAIT - TEMPERATURES ARE RANDOM SO MAY TAKE");
        sb.append("\n* A WHILE TO SEE WARNING AND CRITICAL EVENTS!");
        sb.append("\n************************************************************\n");
        return sb.toString();
    }
}
