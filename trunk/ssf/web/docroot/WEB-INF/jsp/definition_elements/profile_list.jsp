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
<% // Profile listing %>
<%@ page import="com.sitescape.team.domain.Principal" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssUserFolderProperties" type="com.sitescape.team.domain.UserProperties" scope="request" />
<jsp:useBean id="ssUser" type="com.sitescape.team.domain.User" scope="request" />

<script type="text/javascript" src="<html:rootPath/>js/forum/ss_folder.js"></script>

<%
	String slidingTableStyle = "sliding";
/** Vertical mode has been removed
	if (ObjectKeys.USER_DISPLAY_STYLE_VERTICAL.equals(ssUser.getDisplayStyle())) {
		slidingTableStyle = "sliding_scrolled";
	}
*/
	String ssFolderTableHeight = "";
	Map ssFolderPropertiesMap = ssUserFolderProperties.getProperties();
	if (ssFolderPropertiesMap != null && ssFolderPropertiesMap.containsKey("folderEntryHeight")) {
		ssFolderTableHeight = (String) ssFolderPropertiesMap.get("folderEntryHeight");
	}
	if (ssFolderTableHeight == null || ssFolderTableHeight.equals("") || 
			ssFolderTableHeight.equals("0")) ssFolderTableHeight = "400";
%>
<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>

<div class="ss_folder">


<div class="ss_folder_border">
<ssf:toolbar style="ss_actions_bar2 ss_actions_bar" >
	<c:if test="${!empty ssEntryToolbar}">
		<ssf:toolbar toolbar="${ssEntryToolbar}" style="ss_actions_bar2 ss_actions_bar" item="true" />
	</c:if>
</ssf:toolbar>
<div class="ss_clear"></div>
</div>
<%@ include file="/WEB-INF/jsp/forum/page_navigation_bar.jsp" %>
<ssf:slidingTable id="ss_folder_table" type="<%= slidingTableStyle %>" 
 height="<%= ssFolderTableHeight %>" folderId="${ssBinder.id}">

<ssf:slidingTableRow headerRow="true">
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
		folderLineId = "folderLine_" + docId;
	}
%>

<ssf:slidingTableRow id="<%= folderLineId %>">

  <ssf:slidingTableColumn>
  <ssf:showUser user="<%=(User)entry.get("_principal")%>" />
  </ssf:slidingTableColumn>
  
  <ssf:slidingTableColumn>
    <c:if test="${!empty entry._email}">
	  <a href="mailto:${entry._email}">
      <span><c:out value="${entry._email}"/></span></a>
    </c:if>
  </ssf:slidingTableColumn>

  <ssf:slidingTableColumn>
    <span><c:out value="${entry._loginName}"/></span>
  </ssf:slidingTableColumn>
  
 </ssf:slidingTableRow>
</c:forEach>
</ssf:slidingTable>
</div>
