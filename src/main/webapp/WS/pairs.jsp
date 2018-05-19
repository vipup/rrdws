<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <link rel="stylesheet" type="text/css" href="css/style.css?v=${deploymentId}">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <script type="text/javascript" src="WS/d3.v2.js?v=${deploymentId}"></script>
    
    <title>Table sort example in D3</title> 

</head>
<body onLoad="console.log('0....')">
<h1>Sortable Table</h1>
<table>
    <thead></thead>
    <tbody></tbody>
</table>
<TEXTAREA rows="100" cols="100" id="rawdatacontainer">
[
    {id:1111, ort: "Schlosskeller", name: "DnB for live", beginn: "1.11.2011, ab 22 Uhr"},
    {id:2222, ort: "603qm", name: "Electro Technik", beginn: "1.11.2011, ab 22 Uhr"},
    {id:3333, ort: "Krone", name: "da geht der Punk ", beginn: "1.11.2011, ab 20 Uhr"},
    {id:4444, ort: "Schlosskeller", name: "Wuerstchenfest", beginn: "2.11.2011, ab 20 Uhr"},
    {id:5555, ort: "Krone", name: "Karaoke", beginn: "2.11.2011, ab 21 Uhr"}
]
</TEXTAREA>
		<p>
			<input type="text" placeholder="type and press enter to chat.'rrd' - to insert fake rrd."
				id="chat" />
		</p>


 <script>    
function loadScript(url, callback){

    var script = document.createElement("script")
    script.type = "text/javascript";

    if (script.readyState){  //IE
        script.onreadystatechange = function(){
            if (script.readyState == "loaded" ||
                    script.readyState == "complete"){
                script.onreadystatechange = null;
                callback();
            }
        };
    } else {  //Others
        script.onload = function(){
            callback();
        };
    }

    script.src = url;
    document.getElementsByTagName("head")[0].appendChild(script);
}   
loadScript("WS/d3.v2.js?v=${deploymentId}", function(){    
	console.log("1....");
	loadScript("WS/sortedtable.js?v=${deploymentId}", function(){    
			console.log("2....");
			loadScript("WS/pairscalculator.js?v=${deploymentId}", function(){    
				console.log("3....");});
				transform('name');
			});
});


 
</script>
</body>
</html> 