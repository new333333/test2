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
<% //Title view for folders %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<div class="ss_entryContent">
<span class="ss_entryTitle">
<a style="text-decoration: none;" href="<ssf:url 
    folderId="${ssDefinitionEntry.id}" 
    action="view_folder_listing" />">
<c:if test="${empty ssDefinitionEntry.title}" >
--<ssf:nlt tag="entry.noTitle" />--
</c:if>
<c:out value="${ssDefinitionEntry.title}"/></a></span>
</div>