<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<% // This is JSON type AJAX response  %>
{
<c:forEach var="user" items="${ssUsers}" varStatus="status">
	"<ssf:escapeJavaScript value="${user._docId}"/>":"<ssf:escapeJavaScript value="${user.title}"/>"<c:if test="${!status.last}">,</c:if>
</c:forEach>
}