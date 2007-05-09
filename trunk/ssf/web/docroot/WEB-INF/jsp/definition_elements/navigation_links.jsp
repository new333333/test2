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
<% // Navigation links %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${empty ss_breadcrumbsShowIdRoutine}">
  <c:set var="ss_breadcrumbsShowIdRoutine" value="ss_treeShowId" scope="request" />
</c:if>
<c:if test="${empty ss_breadcrumbsTreeName}">
  <c:set var="ss_breadcrumbsTreeName" value="wsTree" scope="request" />
</c:if>
<div class="ss_breadcrumb">
  <ssHelpSpot helpId="navigation_bar/breadcrumbs" offsetX="0" 
    title="<ssf:nlt tag="helpSpot.breadCrumbs"/>">

<ul style="margin-left:-15px;">
<c:if test="${!empty ssDefinitionEntry.parentBinder}">
<c:set var="parentBinder" value="${ssDefinitionEntry.parentBinder}"/>
<jsp:useBean id="parentBinder" type="java.lang.Object" />
<%
	Stack parentTree = new Stack();
	while (parentBinder != null) {
		//if (((Binder)parentBinder).getEntityType().equals(com.sitescape.team.domain.EntityIdentifier.EntityType.profiles)) break;
		parentTree.push(parentBinder);
		parentBinder = ((Binder)parentBinder).getParentBinder();
	}
	while (!parentTree.empty()) {
		Binder nextBinder = (Binder) parentTree.pop();
%>
<c:set var="nextBinder" value="<%= nextBinder %>"/>
<li style="float:left;">
<c:if test="${empty ssNavigationLinkTree[nextBinder.id]}">
<a
<c:if test="${nextBinder.entityType == 'folder'}">
  href="<ssf:url 
  folderId="${nextBinder.id}" 
  action="view_folder_listing"/>"
</c:if>
<c:if test="${nextBinder.entityType == 'workspace'}">
  href="<ssf:url 
  folderId="${nextBinder.id}" 
  action="view_ws_listing"/>"
</c:if>
<c:if test="${nextBinder.entityType == 'profiles'}">
  href="<ssf:url 
  folderId="${nextBinder.id}" 
  action="view_profile_listing"/>"
</c:if>
  onClick="return(ss_navigation_goto(this.href));"
>
<c:if test="${empty nextBinder.title}" >
--<ssf:nlt tag="entry.noTitle" />--
</c:if>
<c:out value="${nextBinder.title}" /></a>
</c:if>
<c:if test="${!empty ssNavigationLinkTree[nextBinder.id]}">
<div style="display:inline">
<ssf:tree treeName="${ss_breadcrumbsTreeName}${nextBinder.id}${renderResponse.namespace}" treeDocument="${ssNavigationLinkTree[nextBinder.id]}" 
  topId="${nextBinder.id}" rootOpen="false" showImages="false" showIdRoutine="${ss_breadcrumbsShowIdRoutine}" />
</div>
</c:if>
</li>
<li style="float:left; padding-top:2px;">&nbsp;&nbsp;//&nbsp;&nbsp;</li>
<%
	}
%>
</c:if>
<li style="float:left;">
<c:if test="${ssDefinitionEntry.entityType == 'folderEntry' || empty ssNavigationLinkTree[ssDefinitionEntry.id]}">
<a class="ss_bold"
<c:if test="${ssDefinitionEntry.entityType == 'folderEntry'}">
  href="<ssf:url 
  folderId="${ssDefinitionEntry.parentBinder.id}" 
  entryId="${ssDefinitionEntry.id}" 
  action="view_folder_entry"/>"
</c:if>
<c:if test="${ssDefinitionEntry.entityType == 'folder'}">
  href="<ssf:url 
  folderId="${ssDefinitionEntry.id}" 
  action="view_folder_listing"/>"
</c:if>
<c:if test="${ssDefinitionEntry.entityType == 'workspace'}">
  href="<ssf:url 
  folderId="${ssDefinitionEntry.id}" 
  action="view_ws_listing"/>"
</c:if>
  onClick="return(ss_navigation_goto(this.href));"
>
<c:if test="${empty ssDefinitionEntry.title}" >
--<ssf:nlt tag="entry.noTitle" />--
</c:if>
<c:out value="${ssDefinitionEntry.title}" /><img border="0" <ssf:alt/>
  style="width:1px;height:14px;" src="<html:imagesPath/>pics/1pix.gif"/></a>
</c:if>
<c:if test="${ssDefinitionEntry.entityType != 'folderEntry' && !empty ssNavigationLinkTree[ssDefinitionEntry.id]}">
<div style="display:inline">
<ssf:tree treeName="${ss_breadcrumbsTreeName}${ssDefinitionEntry.id}${renderResponse.namespace}" 
  treeDocument="${ssNavigationLinkTree[ssDefinitionEntry.id]}" 
  topId="${ssDefinitionEntry.id}" rootOpen="false" 
  showImages="false" showIdRoutine="${ss_breadcrumbsShowIdRoutine}" 
  highlightNode="${ssDefinitionEntry.id}" />
</div>
</c:if>
</li>
</ul>
</div>
<div class="ss_clear"></div>

