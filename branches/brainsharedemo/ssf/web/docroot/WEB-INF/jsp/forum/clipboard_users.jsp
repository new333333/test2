<%@ page language="java" pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<% // This is JSON type AJAX response  %>
[
<c:forEach var="user" items="${ssClipboardPrincipals}" varStatus="status">
	[<c:out value="${user.id}"/>, "<ssf:escapeJavaScript value="${user.title}"/>"]<c:if test="${!status.last}">,</c:if>
</c:forEach>
]