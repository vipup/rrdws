<HTML><HEAD> 
<%@ page isELIgnored="false" %> 
<META http-equiv=Refresh content=30><META http-equiv=Expire content=now>
<TITLE>WebStatistik :</TITLE>
<DIV ID="testdiv1" STYLE="position:absolute;visibility:hidden;background-color:white;layer-background-color:white;">
</DIV>
<SCRIPT LANGUAGE="JavaScript"  SRC="js/CalendarPopup.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">  
	var cal = new CalendarPopup(" testdiv1" );
	var cal = new CalendarPopup( );
		cal.setMonthNames('Januar','Februar','März','April','Mag','Juni','Juli','August','September','Oktober','November','Dezember');
	cal.setDayHeaders('S','M','D','M','D','F','S');
	cal.setWeekStartDay(1);
	cal.setTodayText("Heute");
</SCRIPT>
</HEAD>
<body>
	<img src="xgen.jsp?db=${dbTmp}&_h=${_h}&_w=${_w}&_start=${_start}&_end=${_end}&_v=${_v}&_t=${_t}"/>.
	<a href="list.jsp"> <img alt="back to list" src="img/btn_prev.gif"/>  back</a>
	<form name= "RRD" method="post">
		 <input class="input" type=text name=_start value="${startDate}">
		 
			<A HREF="#" 
				onClick="cal.select(document.forms['RRD']._start,'anchor4','dd.MM.yyyy'); return false;" 
				TITLE="start" NAME="anchor4" ID="anchor4"><img src="img/icon-calendar.gif"/></A>
		 <input class="input" type=text name=_end  value="${startDate}">
		 	<A HREF="#" 
		 		onClick="cal.select(document.forms['RRD']._end,'tfEndtermin_A','dd.MM.yyyy'); return false;" 
				TITLE="end" 
				NAME="anchor5" ID="tfEndtermin_A">
				<img src="img/icon-calendar.gif"/></A>
		 <input class="input" type=text name=db value="${dbTmp}">
		 <input type="submit">
	 </form>
	
	<img src="xgen.jsp?db=${dbTmp}&_start=end-5minutes&_t=5minutes"/>.
	<img src="xgen.jsp?db=${dbTmp}&_start=end-20minutes&_t=20minutes"/>.
	<img src="xgen.jsp?db=${dbTmp}&_start=end-1hours&_t=end-1hours"/>.
	<img src="xgen.jsp?db=${dbTmp}&_start=end-3hours&_t=end-3hours"/>.
	<img src="xgen.jsp?db=${dbTmp}&_start=end-6hours&_t=end-6hours"/>.
	<img src="xgen.jsp?db=${dbTmp}&_start=end-12hours&_t=end-12hours"/>.
	<img src="xgen.jsp?db=${dbTmp}&_start=end-1day&_t=end-1day"/>.
	<img src="xgen.jsp?db=${dbTmp}&_start=end-1week&_t=end-1week"/>.
	<img src="xgen.jsp?db=${dbTmp}&_start=end-2week&_t=end-2week"/>. 
	<img src="xgen.jsp?db=${dbTmp}&_start=end-4week&_t=end-4week"/>. 
	 
 </body>
</HTML>