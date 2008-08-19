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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<ssf:ifadapter>
<body class="tundra">
</ssf:ifadapter>
<c:set var="wsTreeName" value="${renderResponse.namespace}_wsTree"/>
<script type="text/javascript">
function ${wsTreeName}_showId(forum, obj, action) {
	return ss_checkTree(obj, "ss_tree_radio${wsTreeName}destination" + forum)
}
</script>

<div class="ss_style ss_portlet">
<div style="padding:4px;">
<c:if test="${ssOperation == 'move'}">
<span class="ss_bold ss_largerprint"><ssf:nlt tag="move.entry"/></span>
</c:if>
<c:if test="${ssOperation != 'move'}">
<span class="ss_bold ss_largerprint"><ssf:nlt tag="copy.entry"/></span>
</c:if>
<br/>
<br/>
<span><ssf:nlt tag="move.currentEntry"/>: </span>
<span><ssf:nlt tag="${ssBinder.title}" checkIfTag="true"/></span>
  //
<span class="ss_bold">${ssEntry.title}</span>
  
<br/>
<form class="ss_style ss_form" method="post" 
	action="<ssf:url
	action="modify_folder_entry"
	operation="${ssOperation}"
	folderId="${ssBinder.id}"
	entryId="${ssEntry.id}"/>" name="${renderResponse.namespace}fm">
<br/>

<span class="ss_bold"><ssf:nlt tag="move.selectDestination"/></span>
<br/>
<div class="ss_indent_large">
<ssf:tree treeName="${wsTreeName}"
	treeDocument="${ssWsDomTree}"  
 	rootOpen="true"
	singleSelect="${ssDefaultSaveLocationId}" 
	singleSelectName="destination" />
</div>

<br/>

<!-- Displays the current save location, if one has already been
	specified in current user session-->

<span class="ss_bold"><ssf:nlt tag="move.currentLocation" /></span>
<br/>

<% // similar to Navigation links %>

<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ page import="com.sitescape.team.util.NLT" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<%
boolean isIE = BrowserSniffer.is_ie(request);
%>
<div class="ss_breadcrumb">
  <ssHelpSpot helpId="workspaces_folders/misc_tools/breadcrumbs" offsetX="0" 
  <c:if test="<%= !isIE %>">
   offsetY="4"
  </c:if>
  <c:if test="<%= isIE %>">
   offsetY="2" xAlignment="center"
  </c:if>
    title="<ssf:nlt tag="helpSpot.breadCrumbs"/>"></ssHelpSpot>

<ul style="margin-left:-15px;">
<c:if test="${!empty ssDefaultSaveLocation.parentBinder}">
<c:set var="parentBinder" value="${ssDefaultSaveLocation.parentBinder}"/>
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
<br style="float:left;">
<c:if test="${empty ssNavigationLinkTree[nextBinder.id]}">
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
</br>
<br style="float:left; padding-top:2px;">&nbsp;&nbsp;//&nbsp;&nbsp;</li>
<%
	}
%>
</c:if>
<br style="float:left;">
<c:if test="${ssDefaultSaveLocation.entityType == 'folderEntry' || empty ssNavigationLinkTree[ssDefaultSaveLocation.id]}">
<c:if test="${empty ssDefaultSaveLocation.title}" >
--<ssf:nlt tag="move.locationUnspecified" />--
</c:if>
<c:out value="${ssDefaultSaveLocation.title}" /><img border="0" <ssf:alt/>
  style="width:1px;height:14px;" src="<html:imagesPath/>pics/1pix.gif"/></a>
</c:if>
<c:if test="${ssDefaultSaveLocation.entityType != 'folderEntry' && !empty ssNavigationLinkTree[ssDefaultSaveLocation.id]}">
<div style="display:inline">
<ssf:tree treeName="${ss_breadcrumbsTreeName}${ssDefaultSaveLocation.id}${renderResponse.namespace}" 
  treeDocument="${ssNavigationLinkTree[ssDefaultSaveLocation.id]}" 
  topId="${ssDefaultSaveLocation.id}" rootOpen="false" 
  showImages="false" showIdRoutine="${ss_breadcrumbsShowIdRoutine}" 
  highlightNode="${ssDefaultSaveLocation.id}" />
</div>
</c:if>
</br>
</ul>
</div>
<div class="ss_clear"></div>

<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />">
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>">
</form>
</div>
</div>
<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
