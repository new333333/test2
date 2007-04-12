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
<div class="ss_portlet_style ss_portlet">
<c:if test="${ss_windowState == 'maximized'}">
<% // Navigation bar %>
<jsp:include page="/WEB-INF/jsp/definition_elements/navbar.jsp" />
</c:if>
<div class="ss_style" style="padding:4px;">

<script type="text/javascript">
//Define the url of this page in case the "add entry" operation needs to reload this page
var ss_reloadUrl = "<portlet:renderURL/>";
</script>
<table class="ss_style" width="100%"><tr><td>
<c:if test="${!empty ssDashboard.beans[ssComponentId].ssSearchFormData.ssGuestbookBinder}">
<span class="ss_normal">
<c:if test="${ssDashboard.beans[ssComponentId].ssSearchFormData.ssGuestbookBinder.parentBinder.entityIdentifier.entityType == 'folder'}">
  <a href="<portlet:renderURL windowState="maximized"><portlet:param 
		name="action" value="view_folder_listing"/><portlet:param 
		name="binderId" value="${ssDashboard.beans[ssComponentId].ssSearchFormData.ssGuestbookBinder.parentBinder.id}"/></portlet:renderURL>">
		${ssDashboard.beans[ssComponentId].ssSearchFormData.ssGuestbookBinder.parentBinder.title}</a> // 
</c:if>
<c:if test="${ssDashboard.beans[ssComponentId].ssSearchFormData.ssGuestbookBinder.parentBinder.entityIdentifier.entityType != 'folder'}">
  <a href="<portlet:renderURL windowState="maximized"><portlet:param 
		name="action" value="view_ws_listing"/><portlet:param 
		name="binderId" value="${ssDashboard.beans[ssComponentId].ssSearchFormData.ssGuestbookBinder.parentBinder.id}"/></portlet:renderURL>">
		${ssDashboard.beans[ssComponentId].ssSearchFormData.ssGuestbookBinder.parentBinder.title}</a> // 
</c:if>
</span>
<span class="ss_bold">
<a href="<portlet:renderURL windowState="maximized"><portlet:param 
		name="action" value="view_folder_listing"/><portlet:param 
		name="binderId" value="${ssDashboard.beans[ssComponentId].ssSearchFormData.ssGuestbookBinder.id}"/></portlet:renderURL>">
		${ssDashboard.beans[ssComponentId].ssSearchFormData.ssGuestbookBinder.title}</a>
</span>
</c:if>

<br/><br/>
<div class="ss_decor-border5">
  <div class="ss_decor-border6">
    <div class="ss_content_window">
<%@ include file="/WEB-INF/jsp/dashboard/guestbook_view.jsp" %>
</div></div></div>
</td></tr></table>
<div align="right">
  <a class="ss_linkButton" href="<portlet:renderURL 
      portletMode="edit" 
      windowState="maximized" />">
    <span><ssf:nlt tag="button.configure"/></span>
  </a>
</div>
</div>
</div>
