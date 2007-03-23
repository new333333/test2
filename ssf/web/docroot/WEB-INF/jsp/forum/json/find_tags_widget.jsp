<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<% // This is JSON type AJAX response  %>
{
<c:forEach var="tag" items="${ss_tags}" varStatus="status">
	"<ssf:escapeJavaScript value="${tag.ssTag}"/>":"<ssf:escapeJavaScript value="${tag.ssTag}"/>"<c:if test="${!status.last}">,</c:if>
</c:forEach>
}