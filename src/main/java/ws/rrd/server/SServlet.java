package ws.rrd.server;   
import java.io.IOException;  
import javax.servlet.ServletOutputStream; 
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse; 
  

import cc.co.llabor.cache.js.Item;
import cc.co.llabor.cache.js.JSStore; 

public class SServlet extends HttpServlet{ /* SCRIPT-mastering servlet*/
	private static final long serialVersionUID = -5308225516841490806L; 
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		ServletOutputStream out = resp.getOutputStream();
		String uriTmp =   req.getRequestURL().toString() ;
		System.out.println("sendback "+uriTmp+" ...");
		
		resp.setContentType("text/javascript");
		String scriptValue = "";

		// assumes already cached
		final JSStore instanse = JSStore.getInstanse();
		Item scriptTmp = instanse.getByURL(uriTmp);
		scriptValue = scriptTmp.getValue() ;
		out.write(scriptValue.getBytes()); 
		out.flush();
		instanse.putOrCreate(uriTmp, scriptValue, scriptTmp.getRefs().toArray(new String[]{})[0] );
	}

	 
	
 
}


 