<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"><meta http-equiv=Content-Type content="text/html;charset=utf-8">
<%@page import="ws.rrd.csv.Registry"%> 
<%@page import="net.sf.jsr107cache.Cache"%>
<%@page import="cc.co.llabor.cache.Manager"%>
<html><head><title>intro to JRRD...</title>
<link rel="stylesheet" type="text/css" media="screen,print" href="css/table.css" /> 
<link rel="stylesheet" type="text/css" href="css/main.css" />
<link rel="stylesheet" type="text/css" media="all" href="css/screen.css" />
<link rel="stylesheet" type="text/css" media="print" href="css/print.css" />

<style>
.td-1 { 
    background-color: #CCFFFF;
}
.td-2 { 
    background-color: #FFFFCC;
}
.td-3 { 
    background-color: #EEEEEE;
}
</style>
 <script src="js/jquery.min.js" type="text/javascript"></script>
 <script src="js/jquery.lazyload.js" type="text/javascript"></script>
      <script>
        $(function(){
          $('img.lazy').lazyload({threshold: 30});
        });
      </script>
 </head>
<body bgcolor=#FFFFFF link=#006890 vlink=#003860 alink=#800000 text=#000000 topmargin="0" marginheight="0">
 <table class="data-table-3" summary="this is the table >;-)" >
<%
//list.jsp provide shor preview of all stored in REGISTY rrd.DBs
 
Cache cache = Manager.getCache();
Registry reg = (Registry) cache.get("REGISTRY");
if (null != reg){
int i=0;
for (String key:reg.getPath2db().keySet() ){
i++;
%>
<tr class="td-<%=1+(i%3)%>">
    <td >
		<%=i %>
	</td>
	<td>
		<a href="xwin.htm?db=<%=reg.getPath2db().get(key)%>&_t='<%=key%>'" target="_blank">
		<%=key %>
		</a>
	</td>
	<td>==</td>
	<td><%=reg.getPath2db().get(key)%></td>
	<td>
		<a href="xgen.jsp?db=<%=reg.getPath2db().get(key)%>&_h=200&_w=320&_start=end-1day" target="_blank"> 
			<%
				String imgUrl = "gen.jsp?db="+reg.getPath2db().get(key) ;
			%>
			<img class="lazy" src="img/grey.gif" original-src="<%=imgUrl%>" width="64" height="32" />.
	      <noscript>
    	    <img src="<%=imgUrl%>" />
      	</noscript>			
		</a>	
	</td>
	<td>
			<a href="removeFromRegistry.jsp?rrd=<%=reg.getPath2db().get(key)%>&key='<%=key%>'" >
			X
		</a>
	</td>
</tr>
<%
} // for (String key:reg.getPath2db().keySet() ){
} // if (null != reg){
%>
   
</table>