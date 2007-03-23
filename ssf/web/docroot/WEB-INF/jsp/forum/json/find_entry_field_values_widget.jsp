<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<% // This is JSON type AJAX response  %>
{
<c:forEach var="entryValue" items="${ssEntryDefinitionElementData}" varStatus="status">
	"<ssf:escapeJavaScript value="${entryValue.key}"/>":"<ssf:escapeJavaScript value="${entryValue.value}"/>"<c:if test="${!status.last}">,</c:if>
</c:forEach>
}