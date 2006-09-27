<%
/**
 * Copyright (c) 2005 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%
	//Define the z-index offset for table columns to handle the overlays
	int slidingTableColumnZ = 11;
	int slidingTableInfoZ = 40;
%>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="portletadapter" uri="http://www.sitescape.com/tags-portletadapter" %>
<%@ page import="com.sitescape.util.ParamUtil" %>
<%@ page import="com.sitescape.ef.domain.UserProperties" %>
<%@ page import="java.lang.String" %>
<%@ page import="java.lang.Boolean" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<portletadapter:defineObjects1/>
<ssf:ifadapter><portletadapter:defineObjects2/></ssf:ifadapter>
<ssf:ifnotadapter><portlet:defineObjects/></ssf:ifnotadapter>
<%
	//Get the row and column data
	List slidingTableRows = (List) request.getAttribute("ss_slidingTableRows");
	String slidingTableFolderId = (String) request.getAttribute("ss_slidingTableFolderId");
	if (slidingTableFolderId == null) slidingTableFolderId = "";
%>
<script type="text/javascript">
var ss_slidingTableId = '${ss_slidingTableId}';
var ss_slidingTableParentId = '${ss_slidingTableParentId}';
var ss_slidingTableId_2 = ss_slidingTableId + "_2";
var ss_colWidths = new Array();
var ss_colWidthsUser = new Array();
var ss_saveColumnPositionsUrl = "<ssf:url 
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="true" >
	<ssf:param name="operation" value="save_column_positions" />
	<ssf:param name="binderId" value="<%= slidingTableFolderId %>" />
	</ssf:url>"
</script>
<script type="text/javascript" src="<html:rootPath/>js/sliding_table/sliding_table_common.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/sliding_table/sliding_table.js"></script>
<script type="text/javascript">
<%
	//Get the user's column positions (if set)
	String folderColumnPositions = null;
	UserProperties userFolderProperties = (UserProperties) request.getAttribute("ssUserFolderProperties");
	Map userProperties = (Map)  request.getAttribute("ssUserProperties");
	if (userFolderProperties != null) {
		Map userFolderPropertiesMap = userFolderProperties.getProperties();
		if (userFolderPropertiesMap != null && userFolderPropertiesMap.containsKey("folderColumnPositions")) {
			folderColumnPositions = (String) userFolderPropertiesMap.get("folderColumnPositions");
		}
	} else if (userProperties != null && userProperties.containsKey("folderColumnPositions")) {
		folderColumnPositions = (String) userProperties.get("folderColumnPositions");
	}
	if (folderColumnPositions != null) {
		String[] columnPositions = folderColumnPositions.split(" ");
		for (int i = 0; i < columnPositions.length; i++) {
%>
ss_colWidthsUser[<%= String.valueOf(i) %>] = '<%= columnPositions[i] %>';
<%		
		}
	}
%>

</script>

<%
	//Loop through the rows, splitting their columns into separate divs
	if (slidingTableRows.size() > 0) {
		int colSize = ((List)((Map) slidingTableRows.get(0)).get("columns")).size();
		
		//Output the main div that holds them all
%>
<script type="text/javascript">
var ss_columnCount = <%= String.valueOf(colSize) %>;
</script>
<div id="${ss_slidingTableId}_2" 
 style="margin: 2px; border: #666666 1px solid;" width="100%"
 onMouseOver="if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo(this);">

<div id="col0" class="ss_style ss_sliding_table_column0">
<table cellspacing="0" cellpadding="2">
 <tr>
  <td >&nbsp;</td>
 </tr>
</table>
<table cellspacing="0" cellpadding="2">
<%		
		for (int iRow = 0; iRow < slidingTableRows.size(); iRow++) {
%>
<tr><td nowrap width="100%">&nbsp;</td></tr>
<%
		}		
%>
</table>
</div>
</div>

<%

		//Process the same column in each row
		for (int iCol = 0; iCol < colSize; iCol++) {

			int rowCount = 0;
			String rowStyle = "ss_sliding_table_row0";
			for (int iRow = 0; iRow < slidingTableRows.size(); iRow++) {
				String rowId = (String)((Map) slidingTableRows.get(iRow)).get("id");
				if (rowId == null) rowId = "";
				Boolean headerRow = (Boolean)((Map) slidingTableRows.get(iRow)).get("headerRow");
				if (headerRow == null) headerRow = new Boolean(false);
				List columns = (List)((Map) slidingTableRows.get(iRow)).get("columns");
				if (columns == null) break;
				
				//Get the row id text
				String rowIdText = "";
				String colIdText = "";
				if (rowId != null && !rowId.equals("")) {
					rowIdText = "id='" + rowId + "_" + String.valueOf(iCol) + "' ";
					colIdText = "id='" + rowId + "_col_" + String.valueOf(iCol) + "' ";
				}
				
				//Get the row class
				if (!headerRow.booleanValue()) {
					rowStyle = "ss_sliding_table_row0";
					if ((rowCount % 2) == 0) rowStyle = "ss_sliding_table_row1";
					rowCount++;
				}

				//Process the columns
				if (iCol >= columns.size()) break;
				Map columnMap = (Map) columns.get(iCol);
				String columnWidth = "";
				if (columnMap != null && columnMap.containsKey("width")) columnWidth = (String) columnMap.get("width");
				
				String columnText = "";
				if (columnMap != null && columnMap.containsKey("text")) columnText = (String) columnMap.get("text");
					
				//Output the containing div
				if (iRow == 0 && iCol == 0) {
					if (!columnWidth.equals("")) {
%>
<script type="text/javascript">
ss_colWidths[<%= String.valueOf(iCol + 1) %>] = '<%= columnWidth %>';
</script>
<%
					}
%>
<div id="col<%= String.valueOf(iCol + 1) %>" 
  style="position:absolute; z-index:<%= String.valueOf(iCol + slidingTableColumnZ) %>;" 
  class="ss_style ss_sliding_table_column1">
<table cellspacing="0" cellpadding="2" width="100%">
<tr class="<%= rowStyle %>" onMouseOver="if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo(this);">
<td class="<%= rowStyle %>" >&nbsp;</td>
</tr>
</table>
<table cellspacing="0" cellpadding="2" width="100%">
<tr class="<%= rowStyle %>" <%= rowIdText %>>
<%
					if (headerRow.booleanValue()) {
%>
<th class="<%= rowStyle %>" align="left" 
  onMouseOver="if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo(this);">&nbsp;<%= columnText %>&nbsp;</th>
<%
					} else {
%>
<td class="<%= rowStyle %>" <%= colIdText %> nowrap width="100%"
  onMouseOver="if (self.ss_showMouseOverInfo) ss_showMouseOverInfo(this);" 
  onMouseOut="if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo(this);"
  >&nbsp;<%= columnText %></td>
<%
					}
%>
</tr>
<%
				} else if (iRow == 0 && iCol > 0) {
					if (!columnWidth.equals("")) {
%>
<script type="text/javascript">
ss_colWidths[<%= String.valueOf(iCol + 1) %>] = '<%= columnWidth %>';
</script>
<%
					}
%>
<div id="col<%= String.valueOf(iCol + 1) %>" class="ss_style ss_sliding_table_column"  
  style="position:absolute; z-index:<%= String.valueOf(iCol + slidingTableColumnZ) %>;">
<table cellspacing="0" cellpadding="2" width="100%">
<tr class="<%= rowStyle %>" onMouseOver="if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo(this);">
<td class="<%= rowStyle %>" ><div style="position:absolute; left:-9; top:0;"><a id="drag<%= String.valueOf(iCol + 1) %>" style="text-decoration:none;"
  onMousedown="ss_slidingTableStartDragCol(this, 'col<%= String.valueOf(iCol + 1) %>');"
  ><span class="<%= rowStyle %>" 
  style="cursor:w-resize; cursor:col-resize; color:darkgreen; 
  font-size:small; text-decoration:none;
  background-position:center left;
  background-image:url(<html:imagesPath/>pics/sym_s_arrows_eastwest.gif);
  background-repeat:no-repeat;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></a></div>&nbsp;</td>
</tr>
</table>
<table cellspacing="0" cellpadding="2" width="100%">
<tr <%= rowIdText %>>
<%
					if (headerRow.booleanValue()) {
%>
<th class="<%= rowStyle %>" align="left" 
  onMouseOver="if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo(this);">&nbsp;<%= columnText %>&nbsp;</th>
<%
					} else {
%>
<td class="<%= rowStyle %>" <%= colIdText %> nowrap width="100%"
  onMouseOver="if (self.ss_showMouseOverInfo) ss_showMouseOverInfo(this);" 
  onMouseOut="if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo(this);"
 >&nbsp;<%= columnText %></td>
<%
					}
%>
</tr>
<%
				} else {
					if (!columnWidth.equals("")) {
%>
<script type="text/javascript">
ss_colWidths[<%= String.valueOf(iCol + 1) %>] = '<%= columnWidth %>';
</script>
<%
					}
%>
<tr class="<%= rowStyle %>" <%= rowIdText %>>
<%
					if (headerRow.booleanValue()) {
%>
<th class="<%= rowStyle %>" align="left" 
  onMouseOver="if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo(this);">&nbsp;<%= columnText %>&nbsp;</th>
<%
					} else {
%>
<td class="<%= rowStyle %>" <%= colIdText %> nowrap width="100%"
  onMouseOver="if (self.ss_showMouseOverInfo) ss_showMouseOverInfo(this);" 
  onMouseOut="if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo(this);"
 >&nbsp;<%= columnText %></td>
<%
					}
%>
</tr>
<%
				}
			}
			//Output the closing divs for each column
%>
</table>
</div>

<%
		}
	}
	
%>
<form class="ss_style ss_form" name="ss_columnPositionForm" id="ss_columnPositionForm" >
<input type="hidden" name="column_positions">
</form>
<div id="ss_info_popup" class="ss_style ss_sliding_table_info_popup" style="z-index: <%= slidingTableInfoZ %>;"></div>
<div id="ss_info_popup_sizer" style="position:absolute; visibility:hidden;"></div>
