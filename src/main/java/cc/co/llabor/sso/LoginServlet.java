package cc.co.llabor.sso;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginServlet extends HttpServlet {
	
    private static final String jwtTokenCookieName = "JWT-TOKEN";
    private static final String signingKey = "signingKey";
	private String servicesAuth;

    public void setServicesAuth(String servicesAuth){
    	 this.servicesAuth = servicesAuth;
    }

	/**
	 * 
	 */
	private static final long serialVersionUID = -4511189347772401546L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String username = JwtUtil.getSubject(req, jwtTokenCookieName, signingKey); 
        if(username == null){
            String authService = this.servicesAuth != null? this.servicesAuth: this.getInitParameter("servicesAuth");
            resp.sendRedirect(authService + "?redirect=" + req.getRequestURL());
        } else{
        	req.setAttribute("username", username);
            
        }
		//req.getRequestDispatcher("WEB-INF/freemarker/loginRedirect.ftl").forward(req, resp);
	}

}
