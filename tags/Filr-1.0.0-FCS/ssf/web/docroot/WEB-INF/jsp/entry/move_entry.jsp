<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("window.title.moveEntry") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<ssf:ifadapter>
<body class="tundra">
</ssf:ifadapter>
<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
<c:set var="wsTreeName" value="${renderResponse.namespace}_wsTree"/>
<script type="text/javascript">
function ${wsTreeName}_showId(forum, obj, action) {
	return ss_checkTree(obj, "ss_tree_radio${wsTreeName}destination" + forum)
}

function ss_saveDestinationBinderId(id) {
	var formObj = document.getElementById("ss_move_form");
	formObj.idChoices.value = "destination_" + id
}
</script>

<div class="ss_style ss_portlet diag_modal">
	<div style="padding:10px;">
		<c:if test="${ssOperation == 'move'}">
			<div class="marginbottom3">
				<span class="ss_size_18px ss_bold"><ssf:nlt tag="move.entry"/></span>
				<span style="padding-left:5px;"><ssf:nlt tag="move.andfiles"/></span>
			</div>
		</c:if>

		<c:if test="${ssOperation != 'move'}">
			<div class="marginbottom3">
				<span class="ss_size_18px ss_bold"><ssf:nlt tag="copy.entry"/></span>
				<span style="padding-left:5px;"><ssf:nlt tag="move.andfiles"/></span>
			</div>
		</c:if>
		<div class="margintop3">
			<span><ssf:nlt tag="move.currentEntry"/>: </span>
			<span><ssf:nlt tag="${ssBinder.title}" checkIfTag="true"/></span>
			  //
			<span class="ss_bold">${ssEntry.title}</span>
		</div>

<form class="ss_style ss_form" method="post" id="ss_move_form" name="ss_move_form"
	action="<ssf:url
	action="modify_folder_entry"
	operation="${ssOperation}"
	folderId="${ssBinder.id}"
	entryId="${ssEntry.id}"/>" name="${renderResponse.namespace}fm">
<br/>

<span class="ss_bold"><ssf:nlt tag="move.findDestinationFolder"/></span>
<br/>
<ssf:find formName="ss_move_form" 
    formElement="binderId" 
    type="places"
    foldersOnly="true"
    width="180px" 
    singleItem="true"
    clickRoutine="ss_saveDestinationBinderId"
    /> 

<br/>
<br/>

<c:if test="${!empty ssWsDomTree}">
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
</c:if>

<!-- Displays the current save location, if one has already been
	specified in current user session-->

<span class="ss_bold"><ssf:nlt tag="move.currentLocation" /></span>
<br/>

<% // similar to Navigation links %>

<%@ page import="org.kablink.util.BrowserSniffer" %>
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<%
boolean isIE = BrowserSniffer.is_ie(request);
%>
<div class="ss_breadcrumb" style="padding:16px 6px 10px 6px;">
  <ssHelpSpot helpId="workspaces_folders/misc_tools/breadcrumbs" offsetX="0" 
  <c:if test="<%= !isIE %>">
   offsetY="4"
  </c:if>
  <c:if test="<%= isIE %>">
   offsetY="2" xAlignment="center"
  </c:if>
    title="<ssf:nlt tag="helpSpot.breadCrumbs"/>"></ssHelpSpot>

<ul>
<c:if test="${!empty ssDefaultSaveLocation.parentBinder}">
<c:set var="parentBinder" value="${ssDefaultSaveLocation.parentBinder}"/>
<jsp:useBean id="parentBinder" type="java.lang.Object" />
<%
	Stack parentTree = new Stack();
	while (parentBinder != null) {
		//if (((Binder)parentBinder).getEntityType().equals(org.kablink.teaming.domain.EntityIdentifier.EntityType.profiles)) break;
		parentTree.push(parentBinder);
		parentBinder = ((Binder)parentBinder).getParentBinder();
	}
	while (!parentTree.empty()) {
		Binder nextBinder = (Binder) parentTree.pop();
%>
<c:set var="nextBinder" value="<%= nextBinder %>"/>
<c:if test="${empty ssNavigationLinkTree[nextBinder.id]}">
<li>
<c:if test="${empty nextBinder.title}" >
--<ssf:nlt tag="entry.noTitle" />--
</c:if>
<c:out value="${nextBinder.title}" />
</li>
</c:if>
<c:if test="${!empty ssNavigationLinkTree[nextBinder.id]}">
<li>
<div style="display:inline">
<ssf:tree treeName="${ss_breadcrumbsTreeName}${nextBinder.id}${renderResponse.namespace}" treeDocument="${ssNavigationLinkTree[nextBinder.id]}" 
  topId="${nextBinder.id}" rootOpen="false" showImages="false" showIdRoutine="${ss_breadcrumbsShowIdRoutine}" />
</div>
</li>
</c:if>
<li>//</li>
<%
	}
%>
</c:if>
<c:if test="${ssDefaultSaveLocation.entityType == 'folderEntry' || empty ssNavigationLinkTree[ssDefaultSaveLocation.id]}">
<li>
<c:if test="${empty ssDefaultSaveLocation.title}" >
--<ssf:nlt tag="move.locationUnspecified" />--
</c:if>
<c:out value="${ssDefaultSaveLocation.title}" /></a>
</li>
</c:if>
<c:if test="${ssDefaultSaveLocation.entityType != 'folderEntry' && !empty ssNavigationLinkTree[ssDefaultSaveLocation.id]}">
<li>
<div style="display:inline">
<ssf:tree treeName="${ss_breadcrumbsTreeName}${ssDefaultSaveLocation.id}${renderResponse.namespace}" 
  treeDocument="${ssNavigationLinkTree[ssDefaultSaveLocation.id]}" 
  topId="${ssDefaultSaveLocation.id}" rootOpen="false" 
  showImages="false" showIdRoutine="${ss_breadcrumbsShowIdRoutine}" 
  highlightNode="${ssDefaultSaveLocation.id}" />
</div>
</li>
</c:if>
</ul>
</div>

	<div class="teamingDlgBoxFooter">
		<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />">
		<input type="button" value="<ssf:nlt tag="button.cancel"/>" class="ss_submit" name="cancelBtn"
		  onclick="ss_cancelButtonCloseWindow();return false;"/>
	</div>
</form>
</div>
</div>
<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
