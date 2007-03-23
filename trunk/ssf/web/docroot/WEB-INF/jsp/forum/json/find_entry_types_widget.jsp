<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<% // This is JSON type AJAX response  %>
{
<c:forEach var="entryType" items="${ssEntry}" varStatus="status">
	"<ssf:escapeJavaScript value="${entryType.id}"/>":"<ssf:escapeJavaScript value="${entryType.name}"/>"<c:if test="${!status.last}">,</c:if>
</c:forEach>
}