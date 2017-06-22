package eu.blky.springmvc;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class RenderingController extends AbstractController {

	{
		System.out.println(this.getClass().getName() + " inited");
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ModelAndView model = new ModelAndView("renderImage");

		// acm721
		String _t = request.getParameter("_t");
		_t = _t == null ? "-" : _t;
		_t = _t.replace(" ", "_");
		_t = _t.replace(":", "_");
		_t = _t.replace("\"", "_");
		_t = _t.replace("\'", "_");
		_t = _t.replace("\t", "_");
		_t = _t.replace("\b", "_");
		_t = _t.replace("\n", "_");

		String dbTmp = "X-2113039516.rrd";
		dbTmp = request.getParameter("db") == null ? dbTmp : request.getParameter("db");
		String _h = "320";
		_h = request.getParameter("_h") == null ? _h : request.getParameter("_h");
		String _w = "640";
		_w = request.getParameter("_w") == null ? _w : request.getParameter("_w");
		String _end = "now";
		_end = request.getParameter("_end") == null ? _end : request.getParameter("_end");

		String _start = "end-1hour";
		_start = request.getParameter("_start") == null ? _start : request.getParameter("_start");

		String _v = request.getParameter("_v");
		_v = _v == null ? "- " + _start + " to " + _end : _v;

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		String startDate = sdf.format(new Date());

		model.addObject("_v", _v);
		model.addObject("_start", _start);
		model.addObject("dbTmp", dbTmp);
		model.addObject("_h", _h);
		model.addObject("_end", _end);
		model.addObject("_end", _end);
		model.addObject("startDate", startDate);
		model.addObject("_w", _w);

		return model;
	}
}