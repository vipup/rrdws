package cc.co.llabor.sso;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private static final String jwtTokenCookieName = "JWT-TOKEN";
    private static final String signingKey = "signingKey";
	private String servicesAuth;

    public void setServicesAuth(String servicesAuth){
    	 this.servicesAuth = servicesAuth;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String username = JwtUtil.getSubject(httpServletRequest, jwtTokenCookieName, signingKey);
        if(username == null){
            String authService = this.servicesAuth != null? this.servicesAuth: this.getFilterConfig().getInitParameter("servicesAuth");
            httpServletResponse.sendRedirect(authService + "?redirect=" + httpServletRequest.getRequestURL());
        } else{
            httpServletRequest.setAttribute("username", username);
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }
}
