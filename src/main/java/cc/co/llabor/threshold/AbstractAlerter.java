package cc.co.llabor.threshold;
 
import java.util.Properties;

import cc.co.llabor.threshold.rrd.Threshold;

/**
 * <b>Description:TODO</b>
 * 
 * @author vipup<br>
 * <br>
 *         <b>Copyright:</b> Copyright (c) 2006-2008 Monster AG <br>
 *         <b>Company:</b> Monster AG <br>
 * 
 *         Creation: 01.09.2011::16:46:16<br>
 */
public abstract class AbstractAlerter implements Threshold {
	/**
	 * @author vipup
	 */
	private static final long serialVersionUID = -4419251070739231053L;

 
	
	final void performChunk(long timestamp,  double val) {
		boolean isInIncident = checkIncident(val,  timestamp);
		if (isInIncident) {
			incident( timestamp);
		} else {
			clear(timestamp);
		}
		reactIncidentIfAny(timestamp);
	}	

	@Override
	public Properties toProperties() {
		Properties retval = new Properties();
		try{
		retval.setProperty( CLASS , this.getClass().getName() );
		}catch(Exception e){}try{
		retval.setProperty( ACTION , this.getAction());
		}catch(Exception e){}try{
		retval.setProperty( DS_NAME , this.getDsName() );
		}catch(Exception e){}try{
		retval.setProperty(ACTION_ARGS , this.getActionArgs());
		}catch(Exception e){}try{
		retval.setProperty(DATASOURCE , this.getDatasource());
		}catch(Exception e){}try{
		retval.setProperty(MONITOR_TYPE , this.getMonitorType());
		}catch(Exception e){}try{
		retval.setProperty(MONITOR_ARGS , this.getMonitorArgs());
		}catch(Exception e){}try{
		retval.setProperty(SPAN_LENGTH , ""+this.getSpanLength());
		}catch(Exception e){}try{
		retval.setProperty(BASE_LINE, ""+this.getBaseLine());
		}catch(Exception e){}
		return retval;
	}


	protected String rrdName;
	protected double baseLine;
	// have to be triggered immediately 
	protected long activationTimeoutInSeconds=600;//10 mins
 

	public String toString(){
		String dsName = this.getDsName();
		String datasource2 = this.getDatasource();
		String monitorType = this.getMonitorType();
		String monitorArgs = this.getMonitorArgs();
		String action2 = this.getAction();
		String actionArgs = this.getActionArgs();
		return dsName + ":" + 
				datasource2 + 
				"@" + 
				monitorType + 
				"://" + 
				monitorArgs + 
				"?" + 
				action2 + 
				" ( " + 
				actionArgs + 
				" )";
	}
	
	
	protected long incidentTime = -1;
	
	private long lastSecond;
	private long lastSecondPrice;
	private long lastSecondPriceNano;
	
	@Override
	public long getSpanLength() {
		return activationTimeoutInSeconds;
	}

	/**
	 * return the time since the Threshold goes into its 
	 * limit-incident ( limit is reached ) 
	 * OR -1 if not the case.
	 * @see getSpanLength()
 	 * @author vipup
	 * @return
	 */ 
	protected long inIncidentTime() {
		return incidentTime;
	} 
	/**
	 * Start incident....
	 * mark it as Activated-Threshold
	 * 
	 * @author vipup
	 * @param timestampSec
	 */
	protected void incident(long timestamp) {
		if (incidentTime == -1)
			incidentTime = timestamp;
	}
 
	/**
	 * clear incident - mark it as inactive(passive)
	 * @author vipup
	 * @param timestampSec
	 */ 
	protected void clear(long timestamp) {
		incidentTime = -1;
	}
	
	

	/**
	 * will be called for any update-action
	 * (pre-action for reactIncidentIfAny)
	 * 
	 * the value have to be checked for triggering the Threshold
	 */
	protected abstract boolean checkIncident(double val, long timestamp);	
	
	@Override
	public double getBaseLine() {
		return this.baseLine;
	}

	/**
	 * have to implement alert-reaction for incident-state
	 * @author vipup
	 * @param timestampSec
	 */ 
	protected void reactIncidentIfAny(long timestampSec) {
		if (isInIncident(timestampSec )) {			
			this.performAction(timestampSec);
		} else {
			this.performSleep(timestampSec);
		}

	}
	
	

	/**
	 * basic logic for triggering Alerter-state 
	 * 
	 * @author vipup
	 * @param timestampSec
	 * @return
	 */
	public final boolean isInIncident(long timestampSec ) {
		long inIncidentTime = this.inIncidentTime();
		long spanLength = this.getSpanLength();
		return 
			inIncidentTime >= 0 && 
			timestampSec > (inIncidentTime + spanLength) ;
	}

	@Override
	public String getDatasource() {
		return this.rrdName;
	}
 

	/**
	 * something like Cpu_Load
	 */
	@Override
	public String getMonitorType() {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since 30.08.2011");
		else {
			return null;
		}
	}
 
	@Override
	/**
	 * ...and _Average:1Hour _?
	 */
	public String getMonitorArgs() {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since 30.08.2011");
		else {
			return null;
		}
	}

	
	@Override
	/**
	 * shell://kill
	 */
	public String getAction() {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since 30.08.2011");
		else {
			return null;
		}
	}

	@Override
	/**
	 * pid
	 */
	public String getActionArgs() {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since 30.08.2011");
		else {
			return null;
		}
	}

	public void billPrice(
			long timestampInSeconds, 
			long executeTime,
			long executeTimeNano) {
		 if (lastSecond < timestampInSeconds ){// new second
			 lastSecond = timestampInSeconds ;
			 lastSecondPrice = 0;
			 lastSecondPriceNano = 0;
		 }else{ // accumulate price
			 lastSecondPrice += executeTime;
			 lastSecondPriceNano += executeTimeNano;
		 }
	}

	public long getQuote() {
		// TODO Currently max is 10 million nanosecs ( 10 millisecs)
		return 10000000;
	}

	public long getPrice() { 
		return lastSecondPriceNano;
	}

	
	
	public void performChunkAsync(long timestampInSeconds, double val,
			long price) {
		// TODO AsynchExecutor asCaller = AsynchExecutor.getI (); asCaller.exec(timestampInSeconds, val,price);
		performChunk(timestampInSeconds, val);
	}

 
}
