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
<% // Profile listing %>
<%@ page import="com.sitescape.team.domain.Principal" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssUserFolderProperties" type="com.sitescape.team.domain.UserProperties" scope="request" />
<jsp:useBean id="ssUser" type="com.sitescape.team.domain.User" scope="request" />

<script type="text/javascript" src="<html:rootPath/>js/forum/ss_folder.js"></script>

<%
	String slidingTableStyle = "sliding";
	if (ssUser.getDisplayStyle() != null && 
	        ssUser.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL)) {
		slidingTableStyle = "sliding_scrolled";
	}
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
