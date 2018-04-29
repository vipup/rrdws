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
	<script> 

		$(function() {
			var $q = function(q, res) {
				if (document.querySelectorAll) {
					res = document.querySelectorAll(q);
				} else {
					var d = document, a = d.styleSheets[0]
							|| d.createStyleSheet();
					a.addRule(q, 'f:b');
					for (var l = d.all, b = 0, c = [], f = l.length; b < f; b++)
						l[b].currentStyle.f && c.push(l[b]);

					a.removeRule(0);
					res = c;
				}
				return res;
			}, addEventListener = function(evt, fn) {
				window.addEventListener ? this.addEventListener(evt, fn, false)
						: (window.attachEvent) ? this.attachEvent('on' + evt,
								fn) : this['on' + evt] = fn;
			}, _has = function(obj, key) {
				return Object.prototype.hasOwnProperty.call(obj, key);
			};

			//Where el is the DOM element you'd like to test for visibility
			function isHidden(el) {
			    var style = window.getComputedStyle(el);
			    return (style.display === 'none')
			}
			
			function elementInViewport(el) {
				var rect = el.getBoundingClientRect()

				return (rect.top >= 0 && rect.left >= 0 && rect.top <= (window.innerHeight || document.documentElement.clientHeight))
			}
			 
			function loadImage(el, fn) {  
				var img = new Image(); 
				if (!el._bak_src ){ // first assignment
					el._bak_src = el.getAttribute("src"); 
				}else{
					el._bak_src = el._bak_src; // nothingtodo
				} 
				var src = el._bak_src + "&uuidtimestamp=" + new Date().getTime();  // + "#IiD#" + new Date().getTime()  ; 
				img.src 		= src;
				img.id 			= el.id;
				img._bak_src 	= el._bak_src;
				// 200%
				img.setAttribute("_bak_src",  el._bak_src);
				img.setAttribute("class",  "lazy"); 
				img.setAttribute("id",  el.id); 
				
				img.onload = function() {
					 
					if (el.parentElement) {
						el.parentElement.replaceChild(img, el);
						img.src = src;
					} else {
						el.src = src;
					}
					fn ? fn() : null; // callback
				}

			}
			
			var processScroll = function() {
				var images = new Array();
				var query = $q('img.lazy');
				// Array.prototype.slice.call is not callable under our lovely IE8
				for (var i = 0; i < query.length; i++) {
					images.push(query[i]);
				}  
				for (var i = 0; i < images.length; i++) {
					if (elementInViewport(images[i])) {
						loadImage(images[i], function(newImageObj) {
							//images.splice(i, i);
							//images.splice(0,1,newImageObj);
							console.log("loadImage("+images[i]+", ->("+newImageObj+") ") ;
						});
					}
				}
				;
			}; 
			var updateImage = function (){
				console.log(" reload image... &uuidtimestamp=" + new Date().getTime() );
				processScroll();
			    //fadeImg(img, 100, true);
			    setTimeout(updateImage, 11000);
			}

			// -  processScroll() --not need it by scroll at all -  processScroll();
			//addEventListener('scroll', processScroll);
			
			var processScrollTimeout = 0;
			var scheduleProcessScroll = function(){
				processScrollTimeout += 1000; // in 100 ms
			}
			var scrollUpdated = function(){
				setTimeout(scrollUpdated, 1000); // call itselft any sec
				if (processScrollTimeout >0){
					setTimeout(processScroll, 200);
					processScrollTimeout = 0;
				}
			}
			addEventListener('scroll', scheduleProcessScroll);
			
			setTimeout(updateImage , 1000);
			setTimeout(scrollUpdated , 1000);

		});
	</script>
 	
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