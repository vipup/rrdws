package eu.blky.springmvc;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import cc.co.llabor.system.StatusMonitor;
import cc.co.llabor.threshold.AlertCaptain;
import eu.blky.cep.polo2rrd.Polo2RddForwarderService;
import ws.rrd.csv.RrdKeeper;

public class StatusController extends AbstractController{
	@Autowired // works
	private StatusMonitor statusMonitor;
	@Autowired
	Polo2RddForwarderService p2r;

	{
		System.out.println("StatusController inited. statusMonitor=="+statusMonitor);
	}
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
		HttpServletResponse response) throws Exception {

		ModelAndView model = new ModelAndView("RRDKeeperStatus");
		if (RrdKeeper.getInstance() != null) {
			model.addObject("msg", RrdKeeper.getInstance().isAlive() ?"alive":"DEAD");
			Map<String, Long> allExceptionsTmp  = RrdKeeper.getInstance().getExceptionsRTRepo();
			String allExceptionsAsString = exToString (allExceptionsTmp);
			model.addObject("runtimeexceptions", allExceptionsAsString );
			model.addObject("runtimeexceptionMessages", mapToString( RrdKeeper.getInstance().getExceptionsRepo() ) );
		}
		model.addObject("todo", AlertCaptain.getInstance().getToDo().toString() );
		model.addObject("status", mapToString ( statusMonitor.getStatus()  ) );
		
		Map<String, Polo2RddForwarderService> x = statusMonitor.getObjectList();
		
		model.addObject("Polo2RddForwarderService", x.get ( com.journaldev.spring.service.CepService.POLO2RRD2  ) );
		
		String cmd = request.getParameter("cmd");
		if ("stopp2r".equals(cmd)) {
			x.get ( com.journaldev.spring.service.CepService.POLO2RRD2  ).destroy(); 
		}
		if ("startp2r".equals(cmd)) {
			x.get ( com.journaldev.spring.service.CepService.POLO2RRD2  ).init(); 
		}
		
		return model;
	}

	public StatusMonitor getStatusMonitor() {
		return statusMonitor;
	}

	public void setStatusMonitor(StatusMonitor statusMonitor) {
		this.statusMonitor = statusMonitor;
	}
	
	public static String exToString(Map<String, Long> map) {
		StringBuilder stringBuilder = new StringBuilder();

		for (String key : map.keySet()) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(", ");
			}
			String value = ""+map.get(key);
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
	public static String mapToString(Map<String, String> map) {
		StringBuilder stringBuilder = new StringBuilder();

		for (String key : map.keySet()) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(", ");
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
 
	public static String mapToURL(Map<String, String> map) {
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