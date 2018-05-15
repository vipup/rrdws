package eu.blky.springmvc;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jrobin.core.RrdException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod; 

/*
 * 
<context:component-scan base-package="eu.blky.springmvc"/>
 */
@Controller
public class AnnotationTODOGifgenRenderingController2 {
    @Autowired
    private ServletContext servletContext;
    
	{
		System.out.println(this.getClass().getName() + " inited");
	}
	@RequestMapping(value = "/anogifgen.htm", method = RequestMethod.GET)
	public void downloadFile(HttpServletResponse response,
	                         HttpServletRequest request) throws IOException
	{
	    
		System.out.println("servletContext:::::"+servletContext);
		
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
 

 
				
	}
}