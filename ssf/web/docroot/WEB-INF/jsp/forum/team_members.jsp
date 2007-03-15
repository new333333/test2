<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<% // This is JSON type AJAX response  %>
[
<c:forEach var="user" items="${ssTeamMembers}" varStatus="status">
	[<c:out value="${user.id}"/>, "<ssf:escapeJavaScript value="${user.title}"/>"]<c:if test="${!status.last}">,</c:if>
</c:forEach>
]