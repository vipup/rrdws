<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<%@ page isELIgnored="false"%>
</head>
<body>
	<h3>RRDStatus page</h3>
	<h6>S-MVC Not works:</h6>
	<h5>${msg}</h5>
	<h6>BUT works:</h6>
	<h4>RrdKeeper.getInstance().isAlive() :: <%=request.getAttribute("msg")%></h4>

	<h4>runtimeexceptions:: <%=request.getAttribute("runtimeexceptions")%></h4>

	<h4>runtimeexceptionMessages :: <%=request.getAttribute("runtimeexceptionMessages")%></h4>
	
 
 <h3>Status::</h3>
 <pre>${status}</pre>
 
 
 <h3>Polo2RddForwarderService::</h3>
 <pre>${Polo2RddForwarderService}</pre>
</body>
</html>