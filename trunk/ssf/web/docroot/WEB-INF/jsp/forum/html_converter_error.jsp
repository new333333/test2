<%@ page language="java" pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<html>
  <head>
    <title>HTML Conversion Error</title>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
  </head>
  <body>
  	<h4 style="text-align:center"><ssf:nlt tag="html.converterError"/></h4>
    Error: <%=request.getParameter("ssf-error")%><br>
  </body>
</html>
