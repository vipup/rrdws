var i = 0, data = "";
function textToData( ) {
	data = eval (document.getElementById('rawdatacontainer').innerText);
}
 

function transform(attrName) {
    d3.select("tbody").selectAll("tr").remove();

// Header
    var th = d3.select("thead").selectAll("th")
            .data(jsonToArray(data[0]))
          .enter().append("th")
            .attr("onclick", function (d, i) { return "transform('" + d[0] + "');";})
            .text(function(d) { return d[0]; })

// Rows
    var tr = d3.select("tbody").selectAll("tr")
            .data(data)
          .enter().append("tr")
            .sort(function (a, b) { return a == null || b == null ? 0 : stringCompare(a[attrName], b[attrName]); });

// Cells
    var td = tr.selectAll("td")
            .data(function(d) { return jsonToArray(d); })
          .enter().append("td")
            .attr("onclick", function (d, i) { return "transform('" + d[0] + "');";})
            .text(function(d) { return d[1]; });

}

function stringCompare(a, b) {
    a = a.toLowerCase();
    b = b.toLowerCase();
    return a > b ? 1 : a == b ? 0 : -1;
}

function jsonKeyValueToArray(k, v) {return [k, v];}

function jsonToArray(json) {
    var ret = new Array();
    var key;
    for (key in json) {
        if (json.hasOwnProperty(key)) {
            ret.push(jsonKeyValueToArray(key, json[key]));
        }
    }
    return ret;
}; 