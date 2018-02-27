package eu.blky.springmvc;
//<%@page import="org.jrobin.cmd.RrdCommander"%>
//<%@page import="org.jrobin.svg.RrdGraphInfo"%>
import org.jrobin.cmd.RrdCommander;
import org.jrobin.svg.RrdGraphInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class ExecController extends AbstractController {

	{
		System.out.println(this.getClass().getName() + " inited");
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		HttpSession session = request.getSession();
		ModelAndView model = new ModelAndView("exec"); // .jsp
		System.out.println("== RrdWS/RRDTool commander exec...");
		Object o = null;
		String cmdTmp = request.getParameter("cmd");
		java.io.ByteArrayOutputStream bufOut = new java.io.ByteArrayOutputStream ();
		java.io.PrintWriter parceOutputWriter = new java.io.PrintWriter(bufOut, true) ;
		try{ 
			if (cmdTmp != null) {
				System.out.println(cmdTmp);
				cmdTmp = cmdTmp.replace("\\", "\n");
				//RrdCommander.setRrdDbPoolUsed(false);
				o = RrdCommander.execute(cmdTmp);
				
				
				if (o instanceof org.jrobin.svg.RrdGraphInfo) {
					org.jrobin.svg.RrdGraphInfo oInf = (org.jrobin.svg.RrdGraphInfo) o;
					session.setAttribute("svg", oInf.getBytes());
				}
				if (o instanceof org.jrobin.graph.RrdGraphInfo) {
					org.jrobin.graph.RrdGraphInfo oInf = (org.jrobin.graph.RrdGraphInfo) o;
					session.setAttribute("gif", oInf.getBytes());
				}
			}
		}catch(Throwable e){
			e.printStackTrace(parceOutputWriter);
		}
		bufOut.flush();
		bufOut.close();		
 

 
		model.addObject("bufAsString", bufOut.toString());// bufAsStringbufOut.toString()
		model.addObject("cmd", cmdTmp);  
		model.addObject("cmdTmp", cmdTmp);  
		model.addObject("o", o);  
		
		
		
		// part#2
		//<%
		String testCOLOR = "FFFFFF000000CCAA";
		testCOLOR  = testCOLOR   .substring(  (int)(System.currentTimeMillis()%10));
		testCOLOR   = testCOLOR  .substring(0,6);
		String cccTMP = (""+cmdTmp).hashCode()%2==0?"graph":"graphsvg";
		String lnnTMP = ""+ (((int)(System.currentTimeMillis()%3)+1));
		String ttTMP = new String[]{"amazing","greate ", "fine", "super", "geil", "perfect", "bombastisch", "excelent", "ideal", "fantastic", "unique", "unreal"}[Math.abs((""+cmdTmp).hashCode()%11)];
		String vvvTMP = new String[]{"vip","vasja ", "pupkin", "vasilij", "ivanovich", "!no pasaran!", "bl-ky!", "Gra-Vi-Ca pa", "KinDzaDza", "Ma-Ma, Ma-Ma, ..", "Ky!", " "}[Math.abs((""+cmdTmp).hashCode()%11)];
		String testCMD = " rrdtool "+cccTMP + " speed.gif  -v '"+vvvTMP+"'  -t 'RRDWS is "+ttTMP+"!'  --start 920804400 --end 920808000  DEF:myspeed=test.rrd:speed:AVERAGE  LINE"+lnnTMP+":myspeed#"+testCOLOR;
		//%> 
		
		model.addObject("testCMD", testCMD);  

		return model;
	}
}