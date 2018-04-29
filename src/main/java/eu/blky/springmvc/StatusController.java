package eu.blky.springmvc;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import cc.co.llabor.system.StatusMonitor;
import cc.co.llabor.threshold.AlertCaptain;
import ws.rrd.csv.RrdKeeper;

public class StatusController extends AbstractController{
	
	private StatusMonitor statusMonitor;

	{
		System.out.println("HelloWorldController inited");
	}
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
		HttpServletResponse response) throws Exception {

		ModelAndView model = new ModelAndView("RRDKeeperStatus");
		model.addObject("msg", RrdKeeper.getInstance().isAlive() ?"alive":"DEAD");
		model.addObject("todo", AlertCaptain.getInstance().getToDo().toString() );
		model.addObject("status", mapToString ( statusMonitor.getStatus()  ) );
		

		return model;
	}

	public StatusMonitor getStatusMonitor() {
		return statusMonitor;
	}

	public void setStatusMonitor(StatusMonitor statusMonitor) {
		this.statusMonitor = statusMonitor;
	}
	
	 public static String mapToString(Map<String, String> map) {
		   StringBuilder stringBuilder = new StringBuilder();

		   for (String key : map.keySet()) {
		    if (stringBuilder.length() > 0) {
		     stringBuilder.append("&");
		    }
		    String value = map.get(key);
		    try {
		     stringBuilder.append((key != null ? URLEncoder.encode(key, "UTF-8") : ""));
		     stringBuilder.append("=");
		     stringBuilder.append(value != null ? URLEncoder.encode(value, "UTF-8") : "");
		    } catch (UnsupportedEncodingException e) {
		     throw new RuntimeException("This method requires UTF-8 encoding support", e);
		    }
		   }

		   return stringBuilder.toString();
		  }
}