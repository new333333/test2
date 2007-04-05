<%
// The guestbook summary portlet
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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<table class="ss_style" width="100%"><tr><td>
<c:if test="${!empty ssDashboard.beans[ssComponentId].ssBinder}">
<c:if test="${!empty ssDashboard.beans[ssComponentId].ssBinder.parentBinder}">
<span class="ss_normal">
<c:if test="${ssDashboard.beans[ssComponentId].ssBinder.parentBinder.entityIdentifier.entityType == 'folder'}">
  <a href="<portlet:renderURL windowState="maximized"><portlet:param 
		name="action" value="view_folder_listing"/><portlet:param 
		name="binderId" value="${ssDashboard.beans[ssComponentId].ssBinder.parentBinder.id}"/></portlet:renderURL>">
		${ssDashboard.beans[ssComponentId].ssBinder.parentBinder.title}</a> // 
</c:if>
<c:if test="${ssDashboard.beans[ssComponentId].ssBinder.parentBinder.entityIdentifier.entityType != 'folder'}">
  <a href="<portlet:renderURL windowState="maximized"><portlet:param 
		name="action" value="view_ws_listing"/><portlet:param 
		name="binderId" value="${ssDashboard.beans[ssComponentId].ssBinder.parentBinder.id}"/></portlet:renderURL>">
		${ssDashboard.beans[ssComponentId].ssBinder.parentBinder.title}</a> // 
</c:if>
</span>
</c:if>
<span class="ss_bold">
  <a href="<portlet:renderURL windowState="maximized"><portlet:param 
		name="action" value="view_folder_listing"/><portlet:param 
		name="binderId" value="${ssDashboard.beans[ssComponentId].ssBinder.id}"/></portlet:renderURL>">
		${ssDashboard.beans[ssComponentId].ssBinder.title}</a>
</span>
</c:if>
<br/><br/>
<div class="ss_decor-border5">
  <div class="ss_decor-border6">
    <div class="ss_content_window">

<%@ include file="/WEB-INF/jsp/dashboard/wiki_view.jsp" %>

</div></div></div>
</td></tr></table>