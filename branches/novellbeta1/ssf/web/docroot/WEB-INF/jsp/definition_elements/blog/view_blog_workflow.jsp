<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<% // View blog workflow %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<c:if test="${!empty ss_blog_workflowStateCaption}">
<div>
<span class="ss_bold"><ssf:nlt tag="folder.column.State"/>: </span>
<span>${ss_blog_workflowStateCaption}</span>
</div>
</c:if>
