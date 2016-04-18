window.adcm.ext({
    knownIds: [1, 1050, 2210],
    encode: function(a) {
        return a && a.indexOf && a.indexOf("%") < 0 ? encodeURIComponent(a) : a
    },
    equal: function(a, b) {
        if (!a || !b) return !1;
        if (a.length != b.length) return !1;
        for (var c = 0, d = a.length; d > c; c++)
            if (a[c] instanceof Array && b[c] instanceof Array) {
                if (!this.equal(a[c], b[c])) return !1
            } else if (a[c] != b[c]) return !1;
        return !0
    },
    blank: function(a) {
        return a || ""
    },
    aggregate: function(a) {
        var b = "i=" + this.session + "." + Math.round(1e15 * Math.random());
        a.profileId && a.platformId && (b += "&a=" + this.encode(a.platformId) + "&e=" + this.encode(a.profileId));
        var c = [],
            d = document.referrer,
            e = window.location.hash;
        a && a.event && c.push("et:" + this.encode(a.event)), this.params || (this.params = {
            referrer: d ? /\/\/([^#]*)/gi.exec(d)[1] : /\/\/([^#]*)/gi.exec(window.location.href)[1],
            hash: null,
            tags: null
        });
        var f = /\/\/([^#]*)/gi.exec(window.location.href)[1];
        return f !== this.params.referrer && (c.push("cr:" + this.encode(d)), this.params.referrer = f), this.blank(e) !== this.blank(this.params.hash) && (c.push("rh:" + this.encode(e.substr(1))), this.params.hash = e), a.tags && !this.equal(a.tags, this.params.tags) && (c.push("tg:" + this.encode(a.tags.join(" "))), this.params.tags = a.tags), c.length > 0 && (b += "&c=" + c.join(".")), b
    },
    relocate: function() {
        this.knownIds.indexOf(this.config.id) >= 0 && this.load(null, window.location.protocol + "//tag.digitaltarget.ru/extensions/extension_" + this.config.id + ".js?i=" + Math.round(1e15 * Math.random()))
    },
    call: function(a) {
        var b = this;
        a || (a = {}), a.tags = a.tags || this.config.tags, (a.profileId || this.config.profileId) && (a.profileId = a.profileId || this.config.profileId, a.platformId = a.platformId || this.config.platformId || this.config.id);
        var c = function() {
            var c = new Image;
            c.src = window.location.protocol + "//dmg.digitaltarget.ru/1/" + b.config.id + "/i/i?" + b.aggregate(a)
        };
        if (this.loaded) c();
        else var d = setInterval(function() {
            b.loaded && (c(), clearInterval(d))
        }, 1e3)
    },
    callpixel: function(a) {
        var b = this,
            c = "i=" + this.session + "." + Math.round(1e15 * Math.random()),
            d = function() {
                var b = new Image;
                b.src = window.location.protocol + "//dmg.digitaltarget.ru/1/" + a + "/i/i?" + c
            };
        if (this.loaded) d();
        else var e = setInterval(function() {
            b.loaded && (d(), clearInterval(e))
        }, 1e3)
    }
});