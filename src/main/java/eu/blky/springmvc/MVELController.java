package eu.blky.springmvc;

import java.io.*;
import java.util.*;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.mvel2.MVEL;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class MVELController extends AbstractController
{

    public MVELController()
    {
        System.out.println((new StringBuilder()).append(getClass().getName()).append(" inited").toString());
    }
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        HttpSession session = request.getSession();
        Object result = session.getAttribute("result");
        String expression = request.getParameter("expression");
        try
        {        
	        
	        expression = expression != null ? StringEscapeUtils.unescapeHtml(expression) : "foobar > 99";
	        Serializable compiled = MVEL.compileExpression(expression);
	        Map vars = (Map)session.getAttribute(VARSVARSVARSVARSVARSVARSVARSVARSVARSVARSVARSVARSVARS);
	        if(vars == null)
	        {
	            vars = new HashMap();
	            session.setAttribute(VARSVARSVARSVARSVARSVARSVARSVARSVARSVARSVARSVARSVARS, vars);
	        }
	        VariableResolverFactory myVarFactory = new MapVariableResolverFactory(vars);
	        String names = "";
	        String aTmp;
	        for(Enumeration eTmp = session.getAttributeNames(); eTmp.hasMoreElements(); vars.put(aTmp, session.getAttribute(aTmp)))
	        {
	            aTmp = (String)eTmp.nextElement();
	            names = (new StringBuilder()).append(names).append(aTmp).toString();
	            names = (new StringBuilder()).append(names).append(",").toString();
	        }
	
	        
	        if(result != null)
	            vars.put("result", result);

            result = MVEL.eval(expression, myVarFactory);
        }
        catch(Throwable e)
        {
            result = (new StringBuilder()).append("").append(e).toString();
        }
        session.setAttribute("result", result);
        String STARTLINE = "<tr class=\\\"mvelline\\\">";
        String histTmp = (new StringBuilder()).append(STARTLINE).append("\t<td class=\\\\\\\"mvelrow\\\\\\\">").append(new Date()).append("</td><td class=\\\\\\\"mvelrow\\\\\\\">:").append(expression).append("</td><td class=\\\\\\\\\\\\\\\"mvelrow\\\\\\\\\\\\\\\">=</td><td class=\\\\\\\"mvelrow\\\\\\\">").append(result).append("</td></tr>\n").append(session.getAttribute("history")).toString();
        histTmp = histTmp.length() <= 4096 ? histTmp : histTmp.substring(0, histTmp.lastIndexOf(STARTLINE));
        session.setAttribute("history", histTmp);
        String retval = "";
        retval = (new StringBuilder()).append(retval).append("<html>").toString();
        retval = (new StringBuilder()).append(retval).append("<body onload=\"document.forms[0].expression.focus()\">").toString();
        retval = (new StringBuilder()).append(retval).append(expression).toString();
        retval = (new StringBuilder()).append(retval).append(" = ").toString();
        retval = (new StringBuilder()).append(retval).append(result).toString();
        retval = (new StringBuilder()).append(retval).append("<form method=\"POST\">").toString();
        retval = (new StringBuilder()).append(retval).append("<input  name=\"expression\" type=\"text\" name=\"expression\"  size=\"100\" value=\"").toString();
        retval = (new StringBuilder()).append(retval).append(StringEscapeUtils.escapeHtml(expression)).toString();
        retval = (new StringBuilder()).append(retval).append("\">").toString();
        retval = (new StringBuilder()).append(retval).append("<input type=\"submit\">").toString();
        retval = (new StringBuilder()).append(retval).append("\t</form>").toString();
        retval = (new StringBuilder()).append(retval).append("\t<TABLE class=\\\\\\\"mveltable\\\\\\\">").toString();
        retval = (new StringBuilder()).append(retval).append(histTmp).toString();
        retval = (new StringBuilder()).append(retval).append("\t</TABLE>").toString();
        retval = (new StringBuilder()).append(retval).append("\t</body>").toString();
        retval = (new StringBuilder()).append(retval).append("</html>").toString();
        InputStream fio = new ByteArrayInputStream(retval.getBytes());
        byte buf[] = new byte[1023];
        for(int i = fio.read(buf); i > 0; i = fio.read(buf))
        {
            response.getOutputStream().write(buf, 0, i);
            response.getOutputStream().flush();
        }

        return null;
    }

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        return null;
    }

    private static final String VARSVARSVARSVARSVARSVARSVARSVARSVARSVARSVARSVARSVARS = (new StringBuilder()).append("").append("varsvarsvarsvarsvarsvarsvarsvarsvarsvarsvarsvarsvars".hashCode()).toString();
    @Autowired
    private ServletContext servletContext;

}