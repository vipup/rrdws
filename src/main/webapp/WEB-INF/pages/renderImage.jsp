<HTML><HEAD> 
<%@ page isELIgnored="false" %><META http-equiv=Expire content=now>
<TITLE>WebStatistik :</TITLE>
<DIV ID="testdiv1" STYLE="position:absolute;visibility:hidden;background-color:white;layer-background-color:white;">
</DIV>
<SCRIPT LANGUAGE="JavaScript"  SRC="js/CalendarPopup.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">  
	var cal = new CalendarPopup(" testdiv1" );
	var cal = new CalendarPopup( );
		cal.setMonthNames('Januar','Februar','M�rz','April','Mag','Juni','Juli','August','September','Oktober','November','Dezember');
	cal.setDayHeaders('S','M','D','M','D','F','S');
	cal.setWeekStartDay(1);
	cal.setTodayText("Heute");
</SCRIPT>
<head>
	<meta charset='UTF-8'>

	<title>Lazy Loading</title>

	<link rel='stylesheet' href='css/style.css'>

	<style>
		.zone img {
				margin: 800px 0;
				display: block;
		}
	</style>

	<script type="text/javascript" src="js/jquery.js"></script>
	<script type="text/javascript" src="js/renderImage.js"></script>

 	
</head>
</HEAD>
<body>

 
 
<noscript>
    <meta http-equiv="Refresh" id="refresh" content="30"/>
</noscript>

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
	<div id="imagesblock">
		<img  id="${imgId}"  class="lazy" src="xgen.jsp?db=${dbTmp}&_start=end-5minutes&_t=5minutes"/>.
		<img  id="${imgId}"  class="lazy" src="xgen.jsp?db=${dbTmp}&_start=end-20minutes&_t=20minutes"/>.
		<img  id="${imgId}"  class="lazyload lazy" src="xgen.jsp?db=${dbTmp}&_start=end-1hours&_t=end-1hours"/>.
		<img  id="${imgId}"  class="lazy" src="xgen.jsp?db=${dbTmp}&_start=end-3hours&_t=end-3hours"/>.
		<img  id="${imgId}"  class="lazy" src="xgen.jsp?db=${dbTmp}&_start=end-6hours&_t=end-6hours"/>.
		<img  id="${imgId}"  class="lazy" src="xgen.jsp?db=${dbTmp}&_start=end-12hours&_t=end-12hours"/>.
		<img  id="${imgId}"  class="lazy" src="xgen.jsp?db=${dbTmp}&_start=end-1day&_t=end-1day"/>.
		<img  id="${imgId}"  class="lazy" src="xgen.jsp?db=${dbTmp}&_start=end-1week&_t=end-1week"/>.
		<img  id="${imgId}"  class="lazy" src="xgen.jsp?db=${dbTmp}&_start=end-2week&_t=end-2week"/>. 
		<img  id="${imgId}"  class="lazy" src="xgen.jsp?db=${dbTmp}&_start=end-4week&_t=end-4week"/>.
		<img  id="${imgId}"  class="lazy" src="xgen.jsp?db=${dbTmp}&_start=end-8week&_t=end-8week"/>.
		<img  id="${imgId}"  class="lazy" src="xgen.jsp?db=${dbTmp}&_start=end-16week&_t=end-16week"/>.
	</div> 
	 
 </body>
</HTML>