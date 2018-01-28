package eu.blky.springmvc;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import cc.co.llabor.threshold.AlertCaptain;
import ws.rrd.csv.RrdKeeper;

public class StatusController extends AbstractController{

	{
		System.out.println("HelloWorldController inited");
	}
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
		HttpServletResponse response) throws Exception {

		ModelAndView model = new ModelAndView("RRDKeeperStatus");
		model.addObject("msg", RrdKeeper.getInstance().isAlive() ?"alive":"DEAD");
		model.addObject("todo", AlertCaptain.getInstance().getToDo().toString() );
		

		return model;
	}
}