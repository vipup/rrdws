package cc.co.llabor.system;

import java.util.Map;
import java.util.TreeMap;

/**
 * nice MemoryLeak implementation 8-)
 * @author i1
 *
 */
public class StatusMonitor {
	long lastRead = System.currentTimeMillis();
	private Map<String, String> status = new TreeMap<String, String>();
	{
		status.put("created:", ""+System.currentTimeMillis());
		status.put("ID:", ""+this);
	}

	public Map<String, String> getStatus() {
		lastRead = System.currentTimeMillis();
		status.put("lastRead:", ""+lastRead);
		return status;
	}

 
	

}
