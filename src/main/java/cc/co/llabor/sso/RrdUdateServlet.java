package cc.co.llabor.sso;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ws.rrd.csv.RrdUpdateAction;

public class RrdUdateServlet extends HttpServlet {

	private static final long serialVersionUID = -45112312301546L;
	private static final int MAX_COUNTERS = 10;
	private static volatile long callCounter = 0L;

	private static volatile long callCounters[] = new long[MAX_COUNTERS];
	private static volatile long updateCounters[] = new long[MAX_COUNTERS];
	private static volatile long errorCounters[] = new long[MAX_COUNTERS];

	private static volatile long lastUpdateMS = 0L;
	private static volatile long startedAtMS = System.currentTimeMillis();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		RrdUpdateAction aTmp = new RrdUpdateAction();

		lastUpdateMS = System.currentTimeMillis();
		String timestamp = "" + lastUpdateMS;

		int updatedID = (int) ((callCounter++) % MAX_COUNTERS);
		String xpath = "RrdUdateServlet" + (updatedID);

		callCounters[updatedID]++;
		// skip update for any 10 ** level times
		if (callCounters[updatedID] % Math.pow(10, updatedID) != 0) {
			String retvalTmp = "<html> <title>" + updatedID + "</title>  <meta http-equiv=\"" + "refresh\""
					+ " content=\"" + "30\"" + "				></head><body>" + "#" + updatedID + "# "
					+ "</body></html>";
			for (int i = 0; i < MAX_COUNTERS; i++) {
				retvalTmp += ": :" + updateCounters[i];
				retvalTmp += "." + errorCounters[i];
			}
			retvalTmp += "/" + callCounter;
			resp.getOutputStream().write(retvalTmp.getBytes());
			return;
		}
		updateCounters[updatedID]++;

		double reqPerSec = 999.999999 * callCounters[updatedID]
				/ (0.00000000000000000000000001 + lastUpdateMS - startedAtMS);
		String data = "" + reqPerSec;
		Object oretval = aTmp.perform(xpath, timestamp, data);
		if (oretval instanceof Throwable) {
			errorCounters[updatedID]++;
			aTmp.perform(xpath + "error", timestamp, "" + errorCounters[updatedID]);
			//throw new ServletException((Throwable) oretval);
			resp.setStatus(500);
			((Throwable)oretval).printStackTrace(  new PrintWriter( resp.getOutputStream() ));
			 
		}
		String rrdData = "<html><head> <title>" + updatedID + "</title> <meta http-equiv=\"" + "refresh\""
				+ " content=\"" + "30\"" + "				></head><body>" + " aTmp .perform(" + xpath + ","
				+ timestamp + "," + data + ")" + "</body></html>";
		;
		resp.getOutputStream().write(rrdData.getBytes());

	}

}
