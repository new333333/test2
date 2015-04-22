<%
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%
	//Define the z-index offset for table columns to handle the overlays
	int slidingTableColumnZ = 11;
	int slidingTableInfoZ = 40;
%>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="portletadapter" uri="http://www.sitescape.com/tags-portletadapter" %>
<%@ page import="org.kablink.util.ParamUtil" %>
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
	Map userFolderPropertiesMap = (Map) request.getAttribute("ssUserFolderProperties");
	Map userProperties = (Map)  request.getAttribute("ssUserProperties");
	if (userFolderPropertiesMap != null && userFolderPropertiesMap.containsKey("folderColumnPositions")) {
		folderColumnPositions = (String) userFolderPropertiesMap.get("folderColumnPositions");
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

<%
		String browserType = request.getHeader("User-Agent");
		String sizingTableHeaderCellStyle;
		String sizingTableRowCellStyle;
		String sizingDivMargin;
		if ((null != browserType) && ((-1) != browserType.toLowerCase().indexOf("safari"))) {
			sizingTableHeaderCellStyle = "padding-bottom: 3px;";
			sizingTableRowCellStyle    = "padding-bottom: 2px;";
			sizingDivMargin = "margin:1px;";
		}
		else {
			sizingTableHeaderCellStyle = "";
			sizingTableRowCellStyle    = "";
			sizingDivMargin = "margin:0px;";
		}
%>
<script type="text/javascript">
var ss_columnCount = <%= String.valueOf(colSize) %>;
</script>
  <ssHelpSpot helpId="workspaces_folders/misc_tools/folder_table" offsetX="0" 
    title="<ssf:nlt tag="helpSpot.folderTable"/>"></ssHelpSpot>
<div id="${ss_slidingTableId}_2" 
 style="<%= sizingDivMargin %> padding-bottom:2px;
 " width="100%"
 onMouseOver="if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo(this);">

<div id="col0" class="ss_style ss_sliding_table_column0" width="100%">
<table id="findThisTable" cellspacing="0" cellpadding="2" width="100%">
 <tr class="ss_sliding">
  <td class="ss_sliding_table_row0" width="100%" style="<%= sizingTableHeaderCellStyle %>">&nbsp; </td>
 </tr>
</table>
<table id="findThisTable2" cellspacing="0" cellpadding="2" width="100%" style="padding-bottom: 2px;">
<%		
		int rowCount = 1;
		String rowStyle = "ss_sliding_table_row0";
		for (int iRow = 0; iRow < slidingTableRows.size(); iRow++) {
			rowStyle = "ss_sliding_table_row0";
			if ((rowCount % 2) == 0) rowStyle = "ss_sliding_table_row1";
			if (rowCount == 1) {
				rowStyle = "ss_tableheader_style";
%>
<tr class="ss_tableheader_style"><th nowrap width="100%" style="<%= sizingTableRowCellStyle %>">&nbsp;</th></tr>
<%
			} else {
%>
<tr class="<%= rowStyle %>"><td nowrap width="100%" >&nbsp;</td></tr>
<%
			}
			rowCount++;
		}		
%>
</table>
</div>
</div>

<%

		//Process the same column in each row
		for (int iCol = 0; iCol < colSize; iCol++) {

			rowCount = 0;
			rowStyle = "ss_sliding_table_row0";
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
  class="ss_sliding_table_column1">
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
<th class="<%= rowStyle %>" align="left" nowrap width="2000"
  onMouseOver="if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo(this);">&nbsp;<%= columnText %>&nbsp;</th>
<%
					} else {
%>
<td class="<%= rowStyle %>" <%= colIdText %> nowrap width="2000"
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
  style="position:absolute; z-index:<%= String.valueOf(iCol + slidingTableColumnZ) %>;padding-left:0px;">
<table cellspacing="0" cellpadding="2" width="100%">
<tr class="<%= rowStyle %>" onMouseOver="if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo(this);">
<td class="<%= rowStyle %>" ><div style="position:absolute; padding-left:4px;"><a id="drag<%= String.valueOf(iCol + 1) %>" 
  style="text-decoration:none;"
  onMousedown="ss_slidingTableStartDragCol(this, 'col<%= String.valueOf(iCol + 1) %>');"
  ><span class="<%= rowStyle %>" 
  style="cursor:w-resize; cursor:col-resize; color:darkgreen; line-height:15px;
  font-size:small; text-decoration:none;
  background-position:center left;
  background-image:url(<html:imagesPath/>pics/sym_s_arrows_eastwest.gif);
  background-repeat:no-repeat; border-bottom: 0px;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></a></div>&nbsp;</td>
</tr>
</table>
<table cellspacing="0" cellpadding="2" width="100%">
<tr <%= rowIdText %>>
<%
					if (headerRow.booleanValue()) {
%>
<th class="<%= rowStyle %>" align="left" nowrap width="2000"
  onMouseOver="if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo(this);">&nbsp;<%= columnText %>&nbsp;</th>
<%
					} else {
%>
<td class="<%= rowStyle %>" <%= colIdText %> nowrap width="2000"
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
<th class="<%= rowStyle %>" align="left" nowrap width="2000"
  onMouseOver="if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo(this);">&nbsp;<%= columnText %>&nbsp;</th>
<%
					} else {
%>
<td class="<%= rowStyle %>" <%= colIdText %> nowrap width="2000"
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
<div id="ss_info_popup" class="ss_style ss_sliding_table_info_popup" 
  style="z-index: <%= slidingTableInfoZ %>; overflow:hidden; padding-left:1px;"></div>
<div id="ss_info_popup_sizer" style="position:absolute; visibility:hidden; overflow:hidden;"></div>
