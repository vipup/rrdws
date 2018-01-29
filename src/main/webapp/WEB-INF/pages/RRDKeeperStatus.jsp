<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<%@ page isELIgnored="false" %>
</head>
<body>
	<h3>Spring MVC Hello World Example</h3>
<h6>S-MVC Not works:</h6>
	<h5>${msg}</h5>
<h6>BUT works:</h6>	
	<h4><%=request.getAttribute("msg")%></h4>
</body>
</html>