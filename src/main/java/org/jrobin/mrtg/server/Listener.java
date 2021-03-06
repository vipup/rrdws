/* ============================================================
 * JRobin : Pure java implementation of RRDTool's functionality
 * ============================================================
 *
 * Project Info:  http://www.jrobin.org
 * Project Lead:  Sasa Markovic (saxon@jrobin.org);
 *
 * (C) Copyright 2003, by Sasa Markovic.
 *
 * Developers:    Sasa Markovic (saxon@jrobin.org)
 *                Arne Vandamme (cobralord@jrobin.org)
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */
package org.jrobin.mrtg.server;
 

import org.apache.xmlrpc.WebServer; 
import org.jrobin.mrtg.Debug;
import org.jrobin.mrtg.MrtgConstants;
import org.jrobin.mrtg.MrtgException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
  
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
@SuppressWarnings("unchecked")
class Listener implements MrtgConstants {
	private static final String MRTG = "mrtg";
	private WebServer webServer;
	private static final Logger log = LoggerFactory.getLogger(Listener.class .getName());

	Listener(String[] clients) {
		webServer = new WebServer(SERVER_PORT);
		webServer.addHandler(MRTG, new EventHandler());
		if(clients != null && clients.length > 0) {
			webServer.setParanoid(true);
			for(int i = 0; i < clients.length; i++) {
				webServer.acceptClient(clients[i]);
			}
		}
		webServer.start();
		log.info("WWWWWWWWWWWWWWWWW XmlRpcServer started on port " + SERVER_PORT);
	}

	void terminate() {
		if(webServer != null) {
			webServer.removeHandler(MRTG) ;
			webServer.shutdown();
			log.info(".w.w.w.w.w.w.w.w.w.w.w.w.w.w.w.   XmlRpcServer closed + + + + + + + + +  + ");
			log.info(".w.w.w.w.w.w.w.w.w.w.w.w.w.w.w.   XmlRpcServer closed + + + + + + + + +  + ");
			log.info(".w.w.w.w.w.w.w.w.w.w.w.w.w.w.w.   XmlRpcServer closed + + + + + + + + +  + ");
			webServer = null;
		}
	}

	protected void finalize() {
		terminate();
	}

	public class EventHandler {
		public int addRouter(String host, String community, String descr, boolean active) {
			try {
				int status = Server.getInstance().addRouter(host, community, descr, active);
				Debug.print("Router " + host + " added [" + status + "]");
				return status;
			} catch (MrtgException e) {
				Debug.print("Event handler error: " + e);
				return -10;
			}
		}

		public int updateRouter(String host, String community, String descr, boolean active) {
			try {
				int status = Server.getInstance().updateRouter(host, community, descr, active);
				Debug.print("Router " + host + " updated [" + status + "]");
				return status;
			} catch (MrtgException e) {
				Debug.print("Event handler error: " + e);
				return -10;
			}
		}

		public int removeRouter(String host) {
			try {
				int status = Server.getInstance().removeRouter(host);
				Debug.print("Router " + host + " removed [" + status + "]");
				return status;
			} catch (MrtgException e) {
				Debug.print("Event handler error: " + e);
				return -10;
			}
		}

		public int addLink(String host, String ifDescr, String descr, int samplingInterval,
						   boolean active) {
			try {
				Server instance = Server.getInstance();
				int status =
					instance.addLink(host, ifDescr, descr, samplingInterval, active);
				Debug.print("Interface " + ifDescr + "@" + host + " added [" + status + "]");
				return status;
			} catch (MrtgException e) {
				Debug.print("Event handler error: " + e);
				return -10;
			}
		}

		public int updateLink(String host, String ifDescr, String descr,
							  int samplingInterval, boolean active) {
			try {
				int status =
					Server.getInstance().updateLink(host, ifDescr, descr, samplingInterval, active);
				Debug.print("Interface " + ifDescr + "@" + host + " updated [" + status + "]");
				return status;
			} catch (MrtgException e) {
				Debug.print("Event handler error: " + e);
				return -10;
			}
		}

		public int removeLink(String host, String ifDescr) {
			try {
				int status = Server.getInstance().removeLink(host, ifDescr);
				Debug.print("Interface " + ifDescr + "@" + host + " removed [" + status + "]");
				return status;
			} catch (MrtgException e) {
				Debug.print("Event handler error: " + e);
				return -10;
			}
		}

		public byte[] getPngGraph(String host, String ifDescr, Date startDate, Date stopDate) {
			byte[] graph = new byte[0];
			long start = startDate.getTime() / 1000L;
			long stop = stopDate.getTime() / 1000L;
			try {
				Server instance = Server.getInstance();
				graph = instance.getPngGraph(host, ifDescr, start, stop);
			} catch (MrtgException e) {
				Debug.print("Event handler error: " + e);
			}
			return graph;
		}

		public Vector getAvailableLinks(String host) {
			Vector result = new Vector();
			try {
				Server instance = Server.getInstance();
				String[] links = instance.getAvailableLinks(host);
				for (int i = 0; i < links.length; i++) {
					result.add(links[i]);
				}
			} catch (MrtgException e) {
				Debug.print("Event handler error: " + e);
			}
			Debug.print(result.size() + " interfaces found on " + host);
			return result;
		}

		public Vector getRouters() throws MrtgException {
			Vector result = new Vector();
			Device[] routers = Server.getInstance().getRouters();
			for (int i = 0; i < routers.length; i++) {
				Hashtable routerInfo = routers[i].getRouterInfo();
				result.add(routerInfo);
			}
			Debug.print("Sending router data [" + result.size() + " routers found]");
			return result;
		}

		public Hashtable getServerInfo() throws MrtgException {
			Hashtable hash = new Hashtable();
			Server server = Server.getInstance();
			hash = server.getServerInfo();
			Debug.print("Sending MRTG server info");
			return hash;
		}

		
		public Hashtable getMrtgInfo() throws MrtgException {
			Hashtable mrtgInfo = new Hashtable();
			Hashtable serverInfo = getServerInfo();
			mrtgInfo.put("serverInfo", serverInfo);
			Vector routers = getRouters();
			mrtgInfo.put("routers", routers);
			return mrtgInfo;
		}
	}
}
