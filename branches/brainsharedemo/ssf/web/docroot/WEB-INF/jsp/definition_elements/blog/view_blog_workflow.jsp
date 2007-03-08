<% // View blog workflow %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<c:if test="${!empty ss_blog_workflowStateCaption}">
<div>
<span class="ss_bold"><ssf:nlt tag="folder.column.State"/>: </span>
<span>${ss_blog_workflowStateCaption}</span>
</div>
</c:if>
