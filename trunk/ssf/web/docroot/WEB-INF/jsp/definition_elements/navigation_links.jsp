<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<% // Navigation links %>

<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page import="com.sitescape.team.util.NLT" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${!empty ssDefinitionEntry}">
<c:if test="${empty ss_breadcrumbsShowIdRoutine}">
  <c:set var="ss_breadcrumbsShowIdRoutine" value="ss_treeShowIdNoWS" scope="request" />
</c:if>
<c:if test="${empty ss_breadcrumbsTreeName}">
  <c:set var="ss_breadcrumbsTreeName" value="wsTree" scope="request" />
</c:if>

<c:choose>
<c:when test="${empty ss_nav_linksCount}">
	<c:set var="ss_nav_linksCount" value="0" scope="request"/>
</c:when>
<c:otherwise>
	<c:set var="ss_nav_linksCount" value="${ss_nav_linksCount + 1}" scope="request"/>
</c:otherwise>
</c:choose>

<ssf:skipLink tag="<%= NLT.get("skip.navigation.links") %>" id="navigationLinks_${ss_nav_linksCount}_${renderResponse.namespace}">

<div class="ss_clear_float"></div>
<div class="ss_breadcrumb">
  <ssHelpSpot helpId="workspaces_folders/misc_tools/breadcrumbs" offsetX="0" 
  <c:if test="<%= !BrowserSniffer.is_ie(request) %>">
   offsetY="4"
  </c:if>
  <c:if test="<%= BrowserSniffer.is_ie(request) %>">
   offsetY="2" xAlignment="center"
  </c:if>
    title="<ssf:nlt tag="helpSpot.breadCrumbs"/>"></ssHelpSpot>

<ul>
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
<li>
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
<ssf:tree treeName="${ss_breadcrumbsTreeName}${nextBinder.id}${renderResponse.namespace}" 
  treeDocument="${ssNavigationLinkTree[nextBinder.id]}" 
  topId="${nextBinder.id}" rootOpen="false" showImages="false" 
  showIdRoutine="${ss_breadcrumbsShowIdRoutine}"
  namespace="${renderResponse.namespace}" />
</div>
</c:if>
</li>
<li style="padding-top:11px;">//</li>
<%
	}
%>
</c:if>

<c:if test="${ssDefinitionEntry.entityType == 'folderEntry' && !empty ssDefinitionEntry.parentEntry}">
<c:set var="parentEntry" value="${ssDefinitionEntry.parentEntry}"/>
<jsp:useBean id="parentEntry" type="java.lang.Object" />
<%
	Stack parentEntryTree = new Stack();
	while (parentEntry != null) {
		parentEntryTree.push(parentEntry);
		parentEntry = ((FolderEntry)parentEntry).getParentEntry();
	}
	while (!parentEntryTree.empty()) {
		FolderEntry nextEntry = (FolderEntry) parentEntryTree.pop();
%>
<c:set var="nextEntry" value="<%= nextEntry %>"/>
<li>
<div class="ss_treeWidget">
<a
  href="<ssf:url 
  folderId="${ssDefinitionEntry.parentBinder.id}" 
  entryId="${nextEntry.id}" 
  action="view_folder_entry"/>"
  onClick="return(ss_navigation_goto(this.href));"
>
<span><c:if test="${empty nextEntry.title}" >--<ssf:nlt tag="entry.noTitle" />--</c:if><c:out value="${nextEntry.title}" /></span>
<img class="ss_twNone" border="0" <ssf:alt/>
  src="<html:imagesPath/>pics/1pix.gif"/></a><br/>
</div>
</li>
<li style="padding-top:11px;">//</li>
<%
	}
%>
</c:if>

<li>
<c:if test="${ssDefinitionEntry.entityType == 'folderEntry' || empty ssNavigationLinkTree[ssDefinitionEntry.id]}">
<div style="display:inline">
<div class="ss_treeWidget">
<a
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
<span><c:if test="${empty ssDefinitionEntry.title}" >--<ssf:nlt tag="entry.noTitle" />--</c:if><c:out value="${ssDefinitionEntry.title}" /></span>
<img class="ss_twNone" border="0" <ssf:alt/>
  src="<html:imagesPath/>pics/1pix.gif"/></a><br/>
</div>
</div>
</c:if>
<c:if test="${ssDefinitionEntry.entityType != 'folderEntry' && !empty ssNavigationLinkTree[ssDefinitionEntry.id]}">
<div style="display:inline">
<ssf:tree treeName="${ss_breadcrumbsTreeName}${ssDefinitionEntry.id}${renderResponse.namespace}" 
  treeDocument="${ssNavigationLinkTree[ssDefinitionEntry.id]}" 
  topId="${ssDefinitionEntry.id}" rootOpen="false" 
  showImages="false" showIdRoutine="${ss_breadcrumbsShowIdRoutine}" 
  namespace="${renderResponse.namespace}"
  highlightNode="${ssDefinitionEntry.id}" />
</div>
</c:if>
</li>
</ul>
</div>
<div class="ss_clear_float"></div>

</ssf:skipLink>
</c:if>
