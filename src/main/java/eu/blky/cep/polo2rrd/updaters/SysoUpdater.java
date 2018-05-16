package eu.blky.cep.polo2rrd.updaters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.event.map.MapEventBean;

public class SysoUpdater implements UpdateListener {

	/** Logger */
	private static Logger LOG = LoggerFactory.getLogger(SysoUpdater.class);
	private String logns;
	private String properyName;
	private int uuid;
	private int updateCounter;
	private static int UUID = 0;
	public String toString() {
		return SysoUpdater.class.getName()+":"+uuid+":"+logns+"/"+properyName;
	} 	

	public SysoUpdater(String lognsPar, String properyNamePar) {
		this.logns = lognsPar;
		this.properyName = properyNamePar;
		this.uuid = UUID++;
	}

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		updateCounter++;
		for (Object e:newEvents) { 
			MapEventBean eBean = (MapEventBean)e;
			double value = 0;
			if (uuid==17)
			try {
				LOG.trace(logns,  eBean );
				value = Double.valueOf(""+ eBean.get(properyName )) ;
				System.out.println("-"+uuid+"+["+updateCounter+"]!"+properyName+" == \\\\"+ value+ "///"+logns);				 
			}catch(Exception ex) {
				LOG.trace("{}", ex);
			}
		}		

	}

}
