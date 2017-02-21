<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<body>
	<h1>Spring MVC Hello World Example</h1>
<h1>S-MVC Not works:</h1>
	<h2>${msg}</h2>
<h1>BUT works:</h1>	
	<h2><%=request.getAttribute("msg")%></h2>
</body>
</html>