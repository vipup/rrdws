var Chat = {};

Chat.socket = null;

Chat.connect = (function(host) {
	if ('WebSocket' in window) {
		Chat.socket = new WebSocket(host);
	} else if ('MozWebSocket' in window) {
		Chat.socket = new MozWebSocket(host);
	} else {
		TheConsole.log('Error: WebSocket is not supported by this browser.');
		return;
	}

	Chat.socket.onopen = function() {
		TheConsole.log('Info: WebSocket connection opened.');
		document.getElementById('chat').onkeydown = function(event) {
			if (event.keyCode == 13) {
				Chat.sendMessage();
			}
		};
	};

	Chat.socket.onclose = function() {
		document.getElementById('chat').onkeydown = null;
		TheConsole.log('Info: WebSocket closed.');
	};

	Chat.socket.onmessage = function(message) {
		TheConsole.log(message.data);	
		CPU.update(message.data);
	};
});

Chat.initialize = function() {
	if (window.location.protocol == 'http:') { 
		Chat
				.connect('ws://' + window.location.host
						+ '/rrdsaas/websocket/tabledata');
	} else {
		Chat.connect('wss://' + window.location.host
				+ '/rrdsaas/websocket/tabledata');
	}
};

// On-Post-New-text-in chat
Chat.sendMessage = (function() {
	var message = document.getElementById('chat').value;
	if (message != ''  ) {
		TheConsole.log("> : "+message);
		Chat.socket.send(message);
	}else{
		TheConsole.log(message);
	}
	
	document.getElementById('chat').value = '';

});

var CPU = {};
// ws-> textarea-> data -> json -> table
CPU.update = (function(message) {
	var usernameTmp =  message.slice(0,message.indexOf(":"));
	var textTmp =  message.slice(message.indexOf(":")+1); 
	// fill rawdatacontainer
	var rawdatacontainer = document.getElementById('rawdatacontainer');
	rawdatacontainer.innerHTML = message;
	textToData( );
	transform();
	
});

var TheConsole = {};
// sys logger
TheConsole.log = (function(message) {
	var usernameTmp =  message.slice(0,message.indexOf(":"));
	var textTmp =  message.slice(message.indexOf(":")+1); 

	//console.log(usernameTmp +":"+textTmp);
	
});

Chat.initialize();

document.addEventListener("DOMContentLoaded", function() {
	// Remove elements with "noscript" class - <noscript> is not allowed in XHTML
	var noscripts = document.getElementsByClassName("noscript");
	for (var i = 0; i < noscripts.length; i++) {
		noscripts[i].parentNode.removeChild(noscripts[i]);
	}
}, false);
