if (window.adcm = {
        session: Math.round(1e15 * Math.random()),
        config: {
            script: window.location.protocol + "//tag.digitaltarget.ru/processor.js?i=" + Math.round(1e15 * Math.random()),
            id: null
        },
        loaded: !1,
        configure: function(a, b) {
            var c = this.config.id;
            for (var d in a) a.hasOwnProperty(d) && (this.config[d] = a[d]);
            if (this.session && this.config.script && this.config.id) {
                var e = this;
                return this.loaded ? (c != this.config.id && e.relocate(), b && b()) : this.load(function() {
                    b && b(), e.relocate()
                }), this
            }
            console && console.log && console.log("Wrong adcm configuration...")
        },
        ext: function(a) {
            for (var b in a) a.hasOwnProperty(b) && (this[b] = a[b])
        },
        ready: function(a, b) {
            function c() {
                if (!f) {
                    f = !0;
                    for (var a = 0; a < e.length; a++) e[a].fn.call(window, e[a].ctx);
                    e = []
                }
            }

            function d() {
                "complete" === document.readyState && c()
            }
            var e = [],
                f = !1,
                g = !1;
            return f ? void setTimeout(function() {
                a(b)
            }, 1) : (e.push({
                fn: a,
                ctx: b
            }), void("complete" === document.readyState || !document.attachEvent && "interactive" === document.readyState ? setTimeout(c, 1) : g || (document.addEventListener ? (document.addEventListener("DOMContentLoaded", c, !1), window.addEventListener("load", c, !1)) : (document.attachEvent("onreadystatechange", d), window.attachEvent("onload", c)), g = !0)))
        },
        load: function(a, b) {
            var c = this;
            (!this.loaded || b) && this.ready(function() {
                var d = document.createElement("script");
                d.src = b || c.config.script, d.onload = function() {
                    c.loaded = !0, a && a()
                }, document.getElementsByTagName("head")[0].appendChild(d)
            })
        }
    }, window.adcm_config) {
    var cleaned_config = {},
        init = null;
    for (var i in window.adcm_config) window.adcm_config.hasOwnProperty(i) && ("init" != i ? this.cleaned_config[i] = window.adcm_config[i] : window.adcm_config[i] && "function" == typeof window.adcm_config[i] && (init = window.adcm_config[i]));
    window.adcm.configure(cleaned_config, init)
}