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
<% // Folder listing %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssUser" type="com.sitescape.team.domain.User" scope="request" />
<jsp:useBean id="ssBinder" type="com.sitescape.team.domain.Binder" scope="request" />
<%
	Map ssFolderColumns = (Map)ssBinder.getProperty("folderColumns");
	if (ssFolderColumns == null) {
		ssFolderColumns = new java.util.HashMap();
		ssFolderColumns.put("number", "number");
		ssFolderColumns.put("title", "title");
		ssFolderColumns.put("state", "state");
		ssFolderColumns.put("author", "author");
		ssFolderColumns.put("date", "date");
	}
%>

<c:set var="ssFolderColumns" value="<%= ssFolderColumns %>" scope="request"/>

<script type="text/javascript" src="<html:rootPath/>js/datepicker/date.js"></script>

<%
	
	String slidingTableStyle = "sliding";
/** Vertical mode has been removed
	if (ObjectKeys.USER_DISPLAY_STYLE_VERTICAL.equals(ssUser.getDisplayStyle())) {
		slidingTableStyle = "sliding_scrolled";
	}
*/
	boolean useAdaptor = true;
	if (ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE.equals(ssUser.getDisplayStyle()) &&
			!ObjectKeys.GUEST_USER_INTERNALID.equals(ssUser.getInternalId())) {
		useAdaptor = false;
	}
	String ssFolderTableHeight = "400";
			
%>
<script type="text/javascript">

var ss_saveFolderColumnsUrl = "<ssf:url action="view_folder_listing" actionUrl="true"><ssf:param 
		name="binderId" value="${ssBinder.id}"/><ssf:param 
		name="operation" value="save_folder_columns"/></ssf:url>";

</script>
<div id="ss_folder_table_parent" class="ss_folder">

<div class="ss_folder_border">
<% // Add the toolbar with the navigation widgets, commands and filter %>
<ssf:toolbar style="ss_actions_bar2 ss_actions_bar">
<% // Entry toolbar %>
<ssf:toolbar toolbar="${ssEntryToolbar}" style="ss_actions_bar2 ss_actions_bar" item="true" />
</ssf:toolbar>
<div class="ss_clear"></div>
</div>

<div align="right" class="ssPageNavi">
<ssf:ifAccessAllowed  binder="${ssBinder}" operation="setProperty">
		  <a href="<ssf:url
			adapter="true" 
			portletName="ss_forum" 
			action="__ajax_request" 
			actionUrl="true" >
			<ssf:param name="operation" value="configure_folder_columns" />
			<ssf:param name="binderId" value="${ssBinder.id}" />
			<ssf:param name="rn" value="ss_randomNumberPlaceholder" />
			</ssf:url>" onClick="ss_createPopupDiv(this, 'ss_folder_column_menu');return false;">
		    <span class="ss_muted_label_small"><ssf:nlt tag="misc.configureColumns"/></span></a>
</ssf:ifAccessAllowed>
</div>



<ssf:slidingTable id="ss_folder_table" parentId="ss_folder_table_parent" type="<%= slidingTableStyle %>" 
 height="<%= ssFolderTableHeight %>" folderId="${ssFolder.id}">

<ssf:slidingTableRow headerRow="true">
  <c:if test="${!empty ssFolderColumns['number']}">
    <ssf:slidingTableColumn width="12%">
    	<div class="ss_title_menu"><ssf:nlt tag="folder.column.Number"/></div>
    </ssf:slidingTableColumn>
  </c:if>

  <c:if test="${!empty ssFolderColumns['title']}">
    <ssf:slidingTableColumn width="28%">
      <div class="ss_title_menu"><ssf:nlt tag="folder.column.Title"/> </div>
     </ssf:slidingTableColumn>
  </c:if>
  <c:if test="${!empty ssFolderColumns['comments']}">
    <ssf:slidingTableColumn width="13%">
      <div class="ss_title_menu"><ssf:nlt tag="folder.column.Comments"/> </div>
    </ssf:slidingTableColumn>
  </c:if>
  <c:if test="${!empty ssFolderColumns['size']}">
    <ssf:slidingTableColumn width="13%">
      <div class="ss_title_menu"><ssf:nlt tag="folder.column.Size"/> </div>
    </ssf:slidingTableColumn>
  </c:if>
  <c:if test="${!empty ssFolderColumns['download']}">
    <ssf:slidingTableColumn width="13%">
      <div class="ss_title_menu"><ssf:nlt tag="folder.column.Download"/> </div>
    </ssf:slidingTableColumn>
  </c:if>

  <c:if test="${!empty ssFolderColumns['html']}">
    <ssf:slidingTableColumn width="10%">
      <div class="ss_title_menu"><ssf:nlt tag="folder.column.Html"/> </div>
    </ssf:slidingTableColumn>
  </c:if>
  
  <c:if test="${!empty ssFolderColumns['state']}">
    <ssf:slidingTableColumn width="20%">
		<div class="ss_title_menu"><ssf:nlt tag="folder.column.State"/></div>
    </ssf:slidingTableColumn>
  </c:if>

  <c:if test="${!empty ssFolderColumns['author']}">
    <ssf:slidingTableColumn width="20%">
		<div class="ss_title_menu"><ssf:nlt tag="folder.column.Author"/></div>
    </ssf:slidingTableColumn>
  </c:if>

  <c:if test="${!empty ssFolderColumns['date']}">
    <ssf:slidingTableColumn width="20%">
		<div class="ss_title_menu"><ssf:nlt tag="folder.column.LastActivity"/></div>
    </ssf:slidingTableColumn>
  </c:if>
  
    <c:forEach var="column" items="${ssFolderColumns}">
    <c:set var="colName" value="${column.key}"/>
    <c:set var="defId" value=""/>
    <c:set var="eleType" value=""/>
    <c:set var="eleName" value=""/>
    <c:set var="eleCaption" value=""/>
	<jsp:useBean id="colName" type="java.lang.String" scope="page"/>
	<jsp:useBean id="defId" type="java.lang.String" scope="page"/>
	<jsp:useBean id="eleType" type="java.lang.String" scope="page"/>
	<jsp:useBean id="eleName" type="java.lang.String" scope="page"/>
	<jsp:useBean id="eleCaption" type="java.lang.String" scope="page"/>
<%
	if (colName.contains(",")) {
		String[] temp = colName.split(",");
		if (temp.length == 4) {
			defId = temp[0];
			eleType = temp[1];
			eleName = temp[2];
			eleCaption = temp[3];
		}
	}
	if (!defId.equals("")) {
%>
	  <c:set var="eleName" value="<%= eleName %>"/>
	  <c:set var="eleCaption" value="<%= eleCaption %>"/>
	  <ssf:slidingTableColumn width="20%">
	    <div class="ss_title_menu">${eleCaption}</div>
	  </ssf:slidingTableColumn>
<%
	}
%>
  </c:forEach>
</ssf:slidingTableRow>

</ssf:slidingTable>
</div>