<%
/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
<% // Profile listing %>
<%@ page import="java.util.Set" %>
<%@ page import="org.kablink.teaming.domain.Principal" %>
<%@ page import="org.kablink.teaming.domain.User" %>
<%@ page import="org.kablink.teaming.domain.Workspace" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssUserFolderProperties" type="java.util.Map" scope="request" />
<jsp:useBean id="ssUser" type="org.kablink.teaming.domain.User" scope="request" />

<script type="text/javascript" src="<html:rootPath/>js/forum/ss_folder.js"></script>

<%
	String slidingTableStyle = "sliding";
/** Vertical mode has been removed
	if (ObjectKeys.USER_DISPLAY_STYLE_VERTICAL.equals(ssUser.getDisplayStyle())) {
		slidingTableStyle = "sliding_scrolled";
	}
*/
	String ssFolderTableHeight = "";
	if (ssUserFolderProperties != null && ssUserFolderProperties.containsKey("folderEntryHeight")) {
		ssFolderTableHeight = (String) ssUserFolderProperties.get("folderEntryHeight");
	}
	if (ssFolderTableHeight == null || ssFolderTableHeight.equals("") || 
			ssFolderTableHeight.equals("0")) ssFolderTableHeight = "400";
%>
<jsp:include page="/WEB-INF/jsp/common/presence_support.jsp" />

<div class="ss_folder">


<div class="ss_folder_border">
<ssf:toolbar style="ss_actions_bar5 ss_actions_bar" >
<ssHelpSpot 
  		helpId="workspaces_folders/menus_toolbars/folder_toolbar" offsetX="0" offsetY="0" 
  		title="<ssf:nlt tag="helpSpot.folderControlAndFiltering"/>"></ssHelpSpot>
	<c:if test="${!empty ssEntryToolbar}">
		<ssf:toolbar toolbar="${ssEntryToolbar}" style="ss_actions_bar5 ss_actions_bar" item="true" />
	</c:if>
</ssf:toolbar>
<div class="ss_clear"></div>
</div>
<jsp:include page="/WEB-INF/jsp/forum/page_navigation_bar.jsp" />
	<c:set var="slidingTableTableStyle" value=""/>
	<c:if test="${slidingTableStyle == 'fixed'}">
  		<c:set var="slidingTableTableStyle" value="ss_fixed_table"/>
	</c:if>
	<c:set var="slidingTableRowStyle" value="ss_table_oddRow"/>
	<c:set var="slidingTableRowOddStyle" value="ss_table_oddRow"/>
	<c:set var="slidingTableRowEvenStyle" value="ss_table_evenRow"/>
	<c:set var="slidingTableColStyle" value=""/>
	<c:if test="${slidingTableStyle == 'fixed'}">
  		<c:set var="slidingTableRowStyle" value=""/>
  		<c:set var="slidingTableRowOddStyle" value="ss_fixed_odd_TR"/>
  		<c:set var="slidingTableRowEvenStyle" value="ss_fixed_even_TR"/>
  		<c:set var="slidingTableColStyle" value="ss_fixed_TD"/>
	</c:if>
	<ssf:ifaccessible>
  		<c:set var="slidingTableRowStyle" value=""/>
  		<c:set var="slidingTableRowOddStyle" value="ss_fixed_odd_TR"/>
  		<c:set var="slidingTableRowEvenStyle" value="ss_fixed_even_TR"/>
  		<c:set var="slidingTableColStyle" value="ss_fixed_TD"/>
	</ssf:ifaccessible>

<ssf:slidingTable id="ss_folder_table" type="<%= slidingTableStyle %>" tableStyle="${slidingTableTableStyle}"
 height="<%= ssFolderTableHeight %>" folderId="${ssBinder.id}">

<ssf:slidingTableRow headerRow="true" style="ss_tableheader_style" >
  <ssf:slidingTableColumn width="30%"><ssf:nlt tag="profile.element.title"/></ssf:slidingTableColumn>
  <ssf:slidingTableColumn width="50%"><ssf:nlt tag="profile.element.emailAddress"/></ssf:slidingTableColumn>
  <ssf:slidingTableColumn width="20%"><ssf:nlt tag="profile.element.name"/></ssf:slidingTableColumn>
</ssf:slidingTableRow>

<c:forEach var="entry" items="${ssEntries}" >
<jsp:useBean id="entry" type="java.util.HashMap" />
<%
	String folderLineId = "";
	String docId = "";
	if (entry != null && entry.get("_docId") != null) {
		docId = (String) entry.get("_docId");
		folderLineId = "folderLine2_" + docId;
	}
	
	User user = ((User) entry.get("_principal"));
	String userEMA = ((null == user) ? null : user.getEmailAddress());
	boolean showProfileEntry = false;
	boolean workspacePreDeleted = false;
	Long showUser_workspaceId = ((null == user) ? null : user.getWorkspaceId());
	if (null == showUser_workspaceId) {
		workspacePreDeleted = false;
	}
	else {
		Set showUser_workspaces = org.kablink.teaming.util.ResolveIds.getBinders(String.valueOf(showUser_workspaceId));
		if (!showUser_workspaces.isEmpty()) {
			org.kablink.teaming.domain.Workspace showUser_workspace = ((Workspace) (showUser_workspaces.iterator().next()));
			workspacePreDeleted = showUser_workspace.isPreDeleted();
		} else {
			showProfileEntry = true;
		}
	}
%>

<ssf:slidingTableRow id="<%= folderLineId %>" style="${slidingTableRowStyle}" 
  oddStyle="${slidingTableRowOddStyle}" evenStyle="${slidingTableRowEvenStyle}">

  <ssf:slidingTableColumn style="${slidingTableColStyle}">
  <ssf:showUser user='<%= user %>' workspacePreDeleted="<%= workspacePreDeleted %>" 
    showProfileEntry="<%= showProfileEntry %>" />
  </ssf:slidingTableColumn>
  
  <ssf:slidingTableColumn style="${slidingTableColStyle}">
  	<% if ((null != userEMA) && (0 < userEMA.length())) { %>
    	<ssf:mailto email="<%= userEMA %>" noLink="<%= workspacePreDeleted %>" />
    <% } %>
  </ssf:slidingTableColumn>

  <ssf:slidingTableColumn style="${slidingTableColStyle}">
    <span><c:out value="${entry._loginName}"/></span>
  </ssf:slidingTableColumn>
  
 </ssf:slidingTableRow>
</c:forEach>
</ssf:slidingTable>
</div>
