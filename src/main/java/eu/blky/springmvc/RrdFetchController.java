package eu.blky.springmvc;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jrobin.cmd.RrdCommander;
import org.jrobin.core.RrdException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RrdFetchController {
    // SpringWEb v.4.x.x
    @RequestMapping(value = "/fetch.json/{rrd}/start/{start}", method = RequestMethod.GET)  
    public @ResponseBody 
    String fetch(
    		@PathVariable("key") String rrd,
    		@PathVariable("value") String start) 
    throws IOException, RrdException {
    	String cmdTmp = "fetch "+rrd+" MIN -s now-"+start+"min -e now";
    	return doIt(cmdTmp);
    }
	// , produces = "application/json" 404 :((
	//  headers="content-type='application/json'" HTTP Status 415 – Unsupported Media Type
    // SpringWEb v.3.x.x
	@RequestMapping( value = "/fetch.json", method = RequestMethod.GET)
	public void fetch(
			HttpServletResponse response,
            HttpServletRequest request
			) throws IOException, RrdException {
	    response.setContentType("application/json; charset=utf-8" );
	    response.setHeader("Content-Disposition", "inline;filename=fetch.json" );
	    response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
		response.setHeader("Pragma","no-cache"); //HTTP 1.0
		response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
		
		String cmdTmp = "fetch X-1396676775.rrd MIN -s now-1min -e now";
		String retval = doIt(cmdTmp);
		
//		return retval;
		response.getOutputStream().write(retval.getBytes());
	}
	private String doIt(String cmdTmp) throws IOException, RrdException {
		Object o = RrdCommander.execute(cmdTmp );
		/*
	      data
	      
	      1527460000:  +3.3598240046E02
	      1527470000:               nan
	      1527840000:  +4.3611036117E02
	      1527850000:               nan
	      1527860000:               nan
	      1527870000:               nan
	      1527880000:               nan
	      1528140000:  +4.4121132789E02
	      1528150000:               nan
		*/ 
		String string = o.toString();
		String PREFIX = "";
		String retval = "[{\"data\":[";
		for (String l:string.split("\n")) {
			if(l.trim().isEmpty())continue;
			String[] lr = l.split(":");
			if(lr.length != 2)continue;
			
			retval += PREFIX;
			retval += "[";
			retval += lr[0];
			//retval += "\"";
			retval += ",";
			retval += lr[1].replace("+", "").replaceAll("nan", "NaN").trim();
			//retval += "\"";
			retval += "]";
			PREFIX = ",";
		}
		retval +="]}]";
		return retval;
	}

}
