<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/xml; charset=UTF-8" %>
  <?xml version="1.0" encoding="UTF-8" ?> 
  <changes>
<c:forEach var="change" items="${changeLogs}">
	<c:out value="${change.xmlNoHeader}" escapeXml="false"/>
</c:forEach>
	</changes>