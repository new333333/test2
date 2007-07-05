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
	String displayStyle = ssUser.getDisplayStyle();
	if (displayStyle == null) displayStyle = "";
	
	String slidingTableStyle = "sliding";
	if (ssUser.getDisplayStyle() != null && 
	        ssUser.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL)) {
		slidingTableStyle = "sliding_scrolled";
	}
	boolean useAdaptor = true;
	if (ssUser.getDisplayStyle() != null && 
	        ssUser.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
		useAdaptor = false;
	}
	String ssFolderTableHeight = "400";
			
%>
<script type="text/javascript">
var ss_displayStyle = "<%= displayStyle %>";
var ss_saveFolderColumnsUrl = "<portlet:actionURL windowState="maximized"><portlet:param 
		name="action" value="view_folder_listing"/><portlet:param 
		name="binderId" value="${ssBinder.id}"/><portlet:param 
		name="operation" value="save_folder_columns"/></portlet:actionURL>";

</script>
<div id="ss_folder_table_parent" class="ss_folder">

<div style="margin:0px;">

<div align="right" class="ssPageNavi">
    
<table width="99%" border="0" cellspacing="0px" cellpadding="0px">

	<tr>
		<td align="left" width="55%">
		
<%@ include file="/WEB-INF/jsp/forum/view_forum_page_navigation.jsp" %>

		</td>

<td align="right" width="20%">
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
		</td>
	</tr>
</table>

</div>
<div class="ss_folder_border">

<% // Add the toolbar with the navigation widgets, commands and filter %>
<ssf:toolbar style="ss_actions_bar2 ss_actions_bar">


<% // Entry toolbar %>
<ssf:toolbar toolbar="${ssEntryToolbar}" style="ss_actions_bar2 ss_actions_bar" item="true" />

<ssf:toolbar style="ss_actions_bar2 ss_actions_bar" item="true" >
<%@ include file="/WEB-INF/jsp/forum/view_forum_user_filters.jsp" %>
</ssf:toolbar>

</ssf:toolbar>

</div>
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