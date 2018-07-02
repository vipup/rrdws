$(function() {
	var $q = function(q, res) {
		if (document.querySelectorAll) {
			res = document.querySelectorAll(q);
		} else {
			var d = document, a = d.styleSheets[0] || d.createStyleSheet();
			a.addRule(q, 'f:b');
			for (var l = d.all, b = 0, c = [], f = l.length; b < f; b++)
				l[b].currentStyle.f && c.push(l[b]);

			a.removeRule(0);
			res = c;
		}
		return res;
	}, addEventListener = function(evt, fn) {
		window.addEventListener ? this.addEventListener(evt, fn, false)
				: (window.attachEvent) ? this.attachEvent('on' + evt, fn)
						: this['on' + evt] = fn;
	}, _has = function(obj, key) {
		return Object.prototype.hasOwnProperty.call(obj, key);
	};

	// Where el is the DOM element you'd like to test for visibility
	function isHidden(el) {
		var style = window.getComputedStyle(el);
		return (style.display === 'none')
	}

	function elementInViewport(el) {
		var rect = el.getBoundingClientRect()

		return (rect.top >= 0 && rect.left >= 0 && rect.top <= (window.innerHeight || document.documentElement.clientHeight))
	}

	function loadImage(el, fn) {
		let img = new Image();
		if (!el._bak_src) { // first assignment 
			el._bak_src = el.getAttribute("src");
		} else {
			el._bak_src = el._bak_src; // nothingtodo
		}

		let src = el._bak_src + "&uuidtimestamp=" + (new Date().getTime()%10001);  
		
		img.src = src;
		img.id = el.id;
		img._bak_src = el._bak_src;
		
		img.setAttribute("_bak_src", el._bak_src);
		img.setAttribute("class", "lazy");
		img.setAttribute("id", el.id);

		img.onload = function() {

			if (el.parentElement) {
				el.parentElement.replaceChild(img, el);
				img.src = src;//''data:image/gif;base64,R0lGODlhCwALAIAAAAAA3pn/ZiH5BAEAAAEALAAAAAALAAsAAAIUhA+hkcuO4lmNVindo7qyrIXiGBYAOw==';//el._bak_src;//src;
			} else {
				el.src =src;//'data:image/gif;base64,R0lGODlhCwALAIAAAAAA3pn/ZiH5BAEAAAEALAAAAAALAAsAAAIUhA+hkcuO4lmNVindo7qyrIXiGBYAOw==';//el._bak_src;
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
					// images.splice(i, i);
					// images.splice(0,1,newImageObj);
					//  console.log("loadImage(" + images[i] + ", ->("							+ newImageObj + ") ");
				});
			}
		}
		;
	};
	var updateImage = function() {
		console.log(" reload image... &uuidtimestamp=" + new Date().getTime());
		processScroll();
		// fadeImg(img, 100, true);
		setTimeout(updateImage, 11000);
	}

	// - processScroll() --not need it by scroll at all - processScroll();
	// addEventListener('scroll', processScroll);

	var processScrollTimeout = 0;
	var scheduleProcessScroll = function() {
		processScrollTimeout += 1000; // in 100 ms
	}
	var scrollUpdated = function() {
		setTimeout(scrollUpdated, 1000); // call itselft any sec
		if (processScrollTimeout > 0) {
			setTimeout(processScroll, 200);
			processScrollTimeout = 0;
		}
	}
	addEventListener('scroll', scheduleProcessScroll);

	setTimeout(updateImage, 1000);
	setTimeout(scrollUpdated, 1000);

});
