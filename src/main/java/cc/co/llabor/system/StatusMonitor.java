package cc.co.llabor.system;

import java.util.Map;
import java.util.TreeMap;

public class StatusMonitor {
	private Map<String, String> status = new TreeMap<String, String>();
	{
		status.put("created:", ""+System.currentTimeMillis());
		status.put("ID:", ""+this);
	}

	public Map<String, String> getStatus() {
		return status;
	}

 
	

}
