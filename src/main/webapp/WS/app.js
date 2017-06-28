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

Chat.sendMessage = (function() {
	var message = document.getElementById('chat').value;
	if (message != '') {
		Chat.socket.send(message);
		document.getElementById('chat').value = '';
	}
});

var ChatConsole = {};

ChatConsole.log = (function(message) {
	var usernameTmp =  message.slice(0,message.indexOf(":"));
	var textTmp =  message.slice(message.indexOf(":")+1);
	// add into console
	var ViewConsole = document.getElementById('ViewConsole');
	var p = document.createElement('p');
	p.style.wordWrap = 'break-word';
	p.innerHTML = message;
	ViewConsole.appendChild(p);
	while (ViewConsole.childNodes.length > 11) {
		ViewConsole.removeChild(ViewConsole.firstChild);
	}
	ViewConsole.scrollTop = ViewConsole.scrollHeight;
	// add into message-pane
	var messagePanel = document.getElementById('messagePanel');
	var messageTmp = "<div>" +
			"<a class=\"label\" href=\"#\">"+usernameTmp+"</a> " +
			"<img src=\"/rrdsaas/speed.gif\"/>"+
			"<p>" + textTmp + "</p>"+
			"</div>";
	var liTmp = document.createElement('li');
	liTmp.innerHTML = messageTmp;
	messagePanel.prepend(liTmp); 
	for( i=3; i<messagePanel.children.length;i++){
		console.log("del "+(messagePanel.children[i]));
		messagePanel.children[i].remove();
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
