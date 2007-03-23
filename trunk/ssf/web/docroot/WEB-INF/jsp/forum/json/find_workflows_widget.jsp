<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<% // This is JSON type AJAX response  %>
{
<c:forEach var="workflow" items="${ssWorkflowDefinitionMap}" varStatus="status">
	"<ssf:escapeJavaScript value="${workflow.id}"/>":"<ssf:escapeJavaScript value="${workflow.title}"/>"<c:if test="${!status.last}">,</c:if>
</c:forEach>
}