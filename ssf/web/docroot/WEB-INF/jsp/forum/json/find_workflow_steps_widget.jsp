<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<% // This is JSON type AJAX response  %>
{
<c:forEach var="step" items="${ssWorkflowDefinitionStateData}" varStatus="status">
	"<ssf:escapeJavaScript value="${step.key}"/>":"<ssf:escapeJavaScript value="${step.value.caption}"/>"<c:if test="${!status.last}">,</c:if>
</c:forEach>
}