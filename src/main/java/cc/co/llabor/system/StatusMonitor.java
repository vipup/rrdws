package cc.co.llabor.system;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import eu.blky.cep.polo2rrd.Polo2RddForwarderService;

/**
 * nice MemoryLeak implementation 8-)
 * @author i1
 *
 */
@Service
public class StatusMonitor {
	long lastRead = System.currentTimeMillis();
	private Map<String, String> status = new TreeMap<String, String>();
	private Map<String, Polo2RddForwarderService> objectList = new TreeMap<String, Polo2RddForwarderService>();
	{
		status.put("created:", ""+System.currentTimeMillis());
		status.put("ID:", ""+this);
	}

	public Map<String, String> getStatus() {
		lastRead = System.currentTimeMillis();
		status.put("lastRead:", ""+lastRead);
		return status;
	}

	public void addObjectForMonitoring(String key, Polo2RddForwarderService polo2rrd) {
		this.objectList.put(key,polo2rrd);
	}

 
	public Map<String, Polo2RddForwarderService> getObjectList() {
		return Collections.unmodifiableMap( objectList);
	}

}
