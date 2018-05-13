<%@ page session="false" %><%@page import="ws.rrd.json.Tool"%><%@page 
import="java.util.HashMap"%><%@page 
import="java.util.Map"%><%@page 
import="ws.rrd.csv.Registry"%><%@page 
import="net.sf.jsr107cache.Cache"%><%@page 
import="cc.co.llabor.cache.Manager"%><% 
response.setContentType("text/plain;charset=UTF-8");
response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
response.setDateHeader ("Expires", 0); //prevents caching at the proxy server

%><% 
// 
Cache cache = Manager.getCache();
Registry reg = (Registry) cache.get("REGISTRY");
reg.dirtiClean();
%>{   "dirtiClean": "done"  }