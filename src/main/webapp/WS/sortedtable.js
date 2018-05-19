var i = 0, data = "";
var lastActivatedAttribute = "name";
var lastSortingOrder  = 1;
function textToData( ) {
    document.getElementById('rawdatacontainer').style.display = 'none';
	data = eval (document.getElementById('rawdatacontainer').innerText);
}

function proxy(a,b,c,d,e){
     console.log("xxxxxx" );
}
 
function setSortOrder(d,i){
                        var colName = d[0]; 
                        var myName = "transform('" + colName + "');"; 
                        // console.log("Sort changed to:" +myName);
                        return myName;    
}

function setSortAttribute(toSort){
    if (toSort){
        if (toSort == lastActivatedAttribute) {
            lastSortingOrder = -1 *lastSortingOrder;
        } else {
            lastActivatedAttribute = toSort;
            console.log("lastActivatedAttribute;;"+lastActivatedAttribute );
        }
    }
    
}

function transform() {
    transform(lastActivatedAttribute) ;
}


function transform(attrName) {
    if (attrName){
        setSortAttribute(attrName);
    }
    attrName = lastActivatedAttribute;
    d3.select("tbody").selectAll("tr").remove();

// Header
    var th = d3.select("thead").selectAll("th")
            .data(jsonToArray(data[0]))
          .enter().append("th")
            .attr("onclick", setSortOrder)
            .text(function(d) { return d[0]; })

// Rows TPDP .attr("class", "data price")
    var tr = d3.select("tbody").selectAll("tr")
            .data(data)
          .enter().append("tr").attr("class", "data  ")
            .sort(function (a, b) { return a == null || b == null ? 0 : stringCompare(a[attrName], b[attrName]); });

 // Cells
    var td = tr.selectAll("td")
            .data(function(d) { return jsonToArray(d); })
          .enter().append("td").attr("class", function(d) {return decodeColor(d);} )
           // .attr("onclick",setSortOrder)
            .text(function(d) { return d[1]; });

 

}

function decodeColor(data){
    if (data[1]>0)return "pricePLUS";
    if (data[1]<0)return "priceMINUS";
    return "data";
}


function stringCompare(a, b) {
 
    if (!isNaN(parseFloat(b)) && isFinite(b) )
    if (!isNaN(parseFloat(a)) && isFinite(a) )
    {
        a = parseFloat(a);
        b = parseFloat(b);
    }else{
        a = (""+a).toLowerCase();
        b = (""+b).toLowerCase();
    }
    return (lastSortingOrder) * ( a > b ? 1 : a == b ? 0 : -1 );
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