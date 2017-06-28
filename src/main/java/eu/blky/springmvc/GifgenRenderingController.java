package eu.blky.springmvc;
 

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jrobin.core.RrdException;
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController; 
 
public class GifgenRenderingController extends AbstractController{
    @Autowired
    private ServletContext servletContext;
    
	{
		System.out.println(this.getClass().getName() + " inited");
	}

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {		  
	    response.setContentType("image/gif");
	    response.setHeader("Content-Disposition", "inline;filename=sss.gif" );
	    response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
		response.setHeader("Pragma","no-cache"); //HTTP 1.0
		response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
		
		
		Object o = null;
		String cmdTmp = request.getParameter("cmd");
		HttpSession session = request.getSession();
		if (cmdTmp != null) {
			System.out.println(cmdTmp);
			cmdTmp = cmdTmp.replace("\\", "\n");
			try {
				o = org.jrobin.cmd.RrdCommander.execute(cmdTmp);
			} catch (RrdException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (o instanceof org.jrobin.svg.RrdGraphInfo) {
				org.jrobin.svg.RrdGraphInfo oInf = (org.jrobin.svg.RrdGraphInfo) o;
				session.setAttribute("svg", oInf.getBytes());
			}
			if (o instanceof org.jrobin.graph.RrdGraphInfo) {
				org.jrobin.graph.RrdGraphInfo oInf = (org.jrobin.graph.RrdGraphInfo) o;
				session.setAttribute("gif", oInf.getBytes());
			}
		}

		
		java.io.InputStream fio = new  java.io.ByteArrayInputStream( (byte[])session.getAttribute("gif") );
		byte[]buf = new byte[1023];
		for (int i=fio.read(buf);i>0;i=fio.read(buf)){
			response.getOutputStream().write(buf,0,i);
			response.getOutputStream().flush();
		}	
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}