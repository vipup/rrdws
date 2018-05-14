<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<%@ page isELIgnored="false"%>
</head>
<body>
	<h3>CEP page</h3>
	
	StatementNames:
	
	<pre>${StatementNames}</pre>
	statements_:
	
	<pre>${statements}</pre>
	
	ENV: 
	<pre>${env}</pre>
	
	dataflows:
	<pre>${dataflows}</pre>	
	savedConfiguratios:
	<pre>${savedConfiguratios}</pre>	
	savedInstances:
	<pre>${savedInstances}</pre>
	
		 	VARs: 
	<pre>${vars}</pre>
	
		 	getEventTypeNames: 
	<pre>${etnames}</pre>
	
</body>
</html>