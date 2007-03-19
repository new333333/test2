<%
// The guestbook summary portlet
/**
 * Copyright (c) 2006 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
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