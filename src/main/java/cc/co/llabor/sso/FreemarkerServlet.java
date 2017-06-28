package cc.co.llabor.sso;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FreemarkerServlet extends HttpServlet {

	/**
	 * TODO req.getRequestURI()
	 */
	private static final long serialVersionUID = -4511189347772401546L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute("word", "Blalblallsls");
		req.getRequestDispatcher("WEB-INF/freemarker/magic.ftl").forward(req, resp);
	}

}
