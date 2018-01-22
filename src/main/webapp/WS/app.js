var Chat = {};

Chat.socket = null;

Chat.connect = (function(host) {
	if ('WebSocket' in window) {
		Chat.socket = new WebSocket(host);
	} else if ('MozWebSocket' in window) {
		Chat.socket = new MozWebSocket(host);
	} else {
		ChatConsole.log('Error: WebSocket is not supported by this browser.');
		return;
	}

	Chat.socket.onopen = function() {
		ChatConsole.log('Info: WebSocket connection opened.');
		document.getElementById('chat').onkeydown = function(event) {
			if (event.keyCode == 13) {
				Chat.sendMessage();
			}
		};
	};

	Chat.socket.onclose = function() {
		document.getElementById('chat').onkeydown = null;
		ChatConsole.log('Info: WebSocket closed.');
	};

	Chat.socket.onmessage = function(message) {
		ChatConsole.log(message.data);
	};
});

Chat.initialize = function() {
	if (window.location.protocol == 'http:') {
		// Chat.connect('ws://' + window.location.host + '/examples/websocket/chat');
		Chat
				.connect('ws://' + window.location.host
						+ '/rrdsaas/websocket/chat');
	} else {
		Chat.connect('wss://' + window.location.host
				+ '/rrdsaas/websocket/chat');
	}
};

// On-Post-New-text-in chat
Chat.sendMessage = (function() {
	var message = document.getElementById('chat').value;
	if (message != '' &&  'rrd' != message  ) {
		ChatConsole.log("> : "+message);
		Chat.socket.send(message);
	}else{
		ChatConsole.log(message);
	}
	
	document.getElementById('chat').value = '';

});

var ChatConsole = {};
// create initial Chat-panel
ChatConsole.log = (function(message) {
	var usernameTmp =  message.slice(0,message.indexOf(":"));
	var textTmp =  message.slice(message.indexOf(":")+1);
	var ViewConsole = document.getElementById('ViewConsole');
		
	// add into message-pane
	if ("rrd" == textTmp){
		var messageTmp = "<div>" +
				"<a class=\"label\" href=\"#\">"+"_"+"</a> " +
				"<img src=\"/rrdsaas/speed.gif\"/>"+
				"<p>" + textTmp + "</p>"+
				"</div>";
		var liTmp = document.createElement('li');
		liTmp.innerHTML = messageTmp;
		ViewConsole.insertBefore(liTmp, ViewConsole.childNodes[0]) ;
	}else // GIFGENGIFGEN 
	if (message.indexOf( "GIFGENGIFGEN" ) == 0){
		var theGRAPHCMD = "GIFGENGIFGEN:" + "rrdtool%20graph%20speed.gif%20-v%20%27v-Title%27%20-t%20%27h-Title%27%20--start%20now-1week%20%20%20-c%20BACK%23ECEAEB%20-c%20CANVAS%23FAFAFC%20-c%20SHADEA%236E6A6F%20%20-c%20SHADEB%236F6A6E%20-c%20GRID%23FFEEFE%20-c%20MGRID%23CEFFFE%20-c%20FONT%230F0F50%20-c%20ARROW%235055F0%20DEF:my7=X-1481077485.rrd:data:AVERAGE%20DEF:my6=X1331131201.rrd:data:AVERAGE%20DEF:my5=X-566617595.rrd:data:AVERAGE%20LINE1:my7%2347F04F:.i1-LIFEBOOK-E756.java.lang.0@i1-LIFEBOOK-E756-Memory.gauge.NonHeap_max.%20LINE1:my6%230F0701:.i1-LIFEBOOK-E756.java.lang.0@i1-LIFEBOOK-E756-Memory.gauge.NonHeap_init.%20LINE1:my5%237A7F7A:.i1-LIFEBOOK-E756.java.lang.0@i1-LIFEBOOK-E756-Memory.gauge.Heap_used.";
		theGRAPHCMD = encodeURIComponent( textTmp );
		var messageTmp = "<div>" +
				"<a class=\"label\" href=\"#\">"+usernameTmp+"</a> " +
				"<img src=\"/rrdsaas/gifgen.htm?cmd="+theGRAPHCMD+"\"/>"+
				"<p>" + textTmp + "</p>"+
				"</div>";
		var liTmp = document.createElement('li');
		liTmp.innerHTML = messageTmp;
		ViewConsole.insertBefore(liTmp, ViewConsole.childNodes[0]) ;
	}else
	{
		// add into console
		var messageTmp = 
'<small data-timestamp="'+new Date()+'">'+(new Date()).toTimeString()+'</small>'+
					'<div>'+
						'<a class="label" href="#">'+
						usernameTmp+
						'</a><span class="spacer">▸</span>'+
						textTmp+
					'</div>' ;
		var liTmp = document.createElement('li');
		liTmp.innerHTML = messageTmp; 
		ViewConsole.insertBefore(liTmp, ViewConsole.childNodes[0]) ;
	}
	ViewConsole.scrollTop = ViewConsole.scrollHeight;
	
	var toDEL = ViewConsole.children.length - 5;
	for( i=0; i<toDEL;i++ ){ // kill the last
	    //var theNEXT = ViewConsole.childNodes [ViewConsole.childElementCount-1];
		//console.log("del "+theNEXT);
		try{
			//theNEXT.innerHTML = "";
			ViewConsole.children[5].remove();
		}catch(err){
			console.log("del ERROR!"+err);
		}
	};
});

Chat.initialize();

document.addEventListener("DOMContentLoaded", function() {
	// Remove elements with "noscript" class - <noscript> is not allowed in XHTML
	var noscripts = document.getElementsByClassName("noscript");
	for (var i = 0; i < noscripts.length; i++) {
		noscripts[i].parentNode.removeChild(noscripts[i]);
	}
}, false);
