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
<script type="text/javascript">
<!--
var ss_colWidths = new Array();
var ss_colWidthsUser = new Array();

<%
	//Get the row and column data
	List slidingTableRows = (List) request.getAttribute("ss_slidingTableRows");
	String slidingTableFolderId = (String) request.getAttribute("ss_slidingTableFolderId");
	if (slidingTableFolderId == null) slidingTableFolderId = "";
	
	String slidingTableFolderHeight = (String) request.getAttribute("ss_slidingTableScrollHeight");
	
	//Get the user's column positions (if set)
	UserProperties userFolderProperties = (UserProperties) request.getAttribute("ssUserFolderProperties");
	if (userFolderProperties != null) {
		Map userFolderPropertiesMap = userFolderProperties.getProperties();
		if (userFolderPropertiesMap != null && userFolderPropertiesMap.containsKey("folderColumnPositions")) {
			String folderColumnPositions = (String) userFolderPropertiesMap.get("folderColumnPositions");
			String[] columnPositions = folderColumnPositions.split(" ");
			for (int i = 0; i < columnPositions.length; i++) {
%>
ss_colWidthsUser['col<%= String.valueOf(i) %>'] = '<%= columnPositions[i] %>';
<%		
			}
		}
	}
%>

if (self.Event) self.document.captureEvents(Event.MOUSEDOWN);
if (self.Event) self.document.captureEvents(Event.MOUSEUP);
if (self.Event) self.document.captureEvents(Event.MOUSEMOVE);

var ss_slidingTableDragObj = null
var ss_slidingTableOffsetX
var ss_slidingTableOffsetY

var ss_defTableWidth = 600
var ss_sTableMarginRight = 2
var ss_sTableMarginLeft = 2
var ss_sTableMarginTop = 2
var ss_sTableMarginBottom = 2

var ss_sTableLastLeft = 0;
var ss_sTableLastTop = 0;
function ss_checkSlidingTableLayout() {
	if (getObjAbsY("<c:out value="${ss_slidingTableId}"/>_2") != ss_sTableLastTop || getObjAbsX("<c:out value="${ss_slidingTableId}"/>_2") != ss_sTableLastLeft) {
		//The layout changed, go reposition things
		ss_sTableLastTop = getObjAbsY("<c:out value="${ss_slidingTableId}"/>_2")
		ss_sTableLastLeft = getObjAbsX("<c:out value="${ss_slidingTableId}"/>_2")
		ss_showSlidingTableCols()
		if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo();
	}
}

function ss_showSlidingTableCols200() {
	setTimeout("ss_showSlidingTableCols();", 200)
}

function ss_showSlidingTableCols() {
	ss_sTableMarginLeft = parseInt(self.document.getElementById("<c:out value="${ss_slidingTableId}"/>_2").style.marginRight)
	ss_sTableMarginRight =  parseInt(self.document.getElementById("<c:out value="${ss_slidingTableId}"/>_2").style.marginLeft)
	ss_sTableMarginTop = parseInt(self.document.getElementById("<c:out value="${ss_slidingTableId}"/>_2").style.marginTop)
	ss_sTableMarginBottom =  parseInt(self.document.getElementById("<c:out value="${ss_slidingTableId}"/>_2").style.marginBottom)
	
	//Calculate the default column positions
    var ss_sTableWidth = ss_getDivWidth("<c:out value="${ss_slidingTableId}"/>_2")
    if (ss_sTableWidth == 0) ss_sTableWidth = parseInt(ss_defTableWidth);
    var ss_sTableInnerWidth = parseInt(ss_sTableWidth - ss_sTableMarginLeft - ss_sTableMarginRight);
    var maxColLeft = ss_sTableInnerWidth - 8
    var defColWidth = parseInt(ss_sTableInnerWidth / ss_columnCount)
    var deltaW = defColWidth
    var widthTotalPercentage = 0
    var widthTotalPixels = 0
    for (var i = 1; i <= ss_columnCount; i++) {
    	if (ss_colWidths["col"+i]) {
    		var cw = ss_colWidths["col"+i]
    		if (cw.indexOf("%") > 0) {
    			//This is a percentage; add it to the total percent
    			cw = cw.substr(0, cw.indexOf("%"));
    			widthTotalPercentage += parseInt(cw)
    		} else {
    			//This is a pixel count; add it to the total pixels
    			widthTotalPixels += parseInt(cw)
    		}
    	} else {
    		//This width was not specified. 1/nth of the table width as a default
    		widthTotalPixels += deltaW
    	}
    }
    //Finally, get the pixels available to divide amongst the columns that use a percentage
    var percentageWidth = parseInt(ss_sTableInnerWidth - widthTotalPixels)
	
    //Now, position the columns (starting at the left inner side of the ss_sTable boundary)
    var top = parseInt(getObjAbsY("<c:out value="${ss_slidingTableId}"/>_2") + ss_sTableMarginTop)
    var left = getObjAbsX("<c:out value="${ss_slidingTableId}"/>_2") + ss_sTableMarginLeft
    var deltaLeft = 0
    var w = parseInt(ss_sTableInnerWidth - deltaLeft) + "px"
    
    //Set the position of each column (put col0 at the left edge, 
    //  then position the others successively to the right)
	ss_position_sTableCol("col0", left, top, w)
    for (var i = 1; i <= ss_columnCount; i++) {
	    //See if the user re-positioned the columns. If so, use those settings.
	    if (ss_colWidthsUser["col"+i]) {
	    	deltaLeft = parseInt(ss_colWidthsUser["col"+i])
	    }
    	//But, always start the first column at the left edge
    	if (i == 1) deltaLeft = 0;
	    
	    //The column cannot start to the left of the table boundary or be wider than the table
	    if (deltaLeft < 0) deltaLeft = 0
	    if (deltaLeft > maxColLeft) deltaLeft = maxColLeft
    	
    	w = parseInt(ss_sTableInnerWidth - deltaLeft) + "px"
    	if (parseInt(w) < 0) w = defColWidth + "px";
    	
	    //Position the column inside the inner boundaries of ss_ssTable
	    ss_position_sTableCol("col"+i, parseInt(left + deltaLeft), top, w)
    	ss_showHideObj("col"+i, "visible", "inline")
	    
	    //Now, get the position of the next column (using the default width of the current column)
	    //  This may get overridden (above) if the user has re-positioned the columns
	    if (!ss_colWidthsUser["col"+i]) {
	    	if (ss_colWidths["col"+i]) {
	    		var cw = ss_colWidths["col"+i]
	    		if (cw.indexOf("%") > 0) {
	    			cw = cw.substr(0, cw.indexOf("%"));
	    			//Get the width by taking a percentage of the available pixels 
	    			var dw = parseInt(percentageWidth * parseInt(cw) / widthTotalPercentage)
	     			deltaLeft = deltaLeft + dw
	    		} else {
	    			deltaLeft = deltaLeft + parseInt(cw)
	    		}
	    	} else {
	     		deltaLeft = deltaLeft + deltaW
	    	}
	    }
    }
}

function ss_position_sTableCol(divName, x, y, w) {
    var ss_sTableHeight = ss_getDivHeight("<c:out value="${ss_slidingTableId}"/>_2")
    var ss_sTableInnerHeight = parseInt(ss_sTableHeight- ss_sTableMarginTop - ss_sTableMarginBottom)

    if (isNSN || isNSN6 || isMoz5) {
        self.document.getElementById(divName).style.left= x + "px"
        self.document.getElementById(divName).style.top= y + "px"
        self.document.getElementById(divName).style.clip = "rect(-9px " + parseInt(w) + "px " + ss_sTableInnerHeight + "px -9px)"
    } else {
        self.document.all[divName].style.left=x
        self.document.all[divName].style.top=y
        self.document.all[divName].style.clip = "rect(-9px " + parseInt(w) + "px " + ss_sTableInnerHeight + "px -9px)"
    }
}

var ss_slidingTableStartingToDrag = null;
var ss_slidingTableSavedMouseMove = '';
var ss_slidingTableSavedMouseUp = '';
function ss_slidingTableStartDragCol(obj, divName) {
    var id = divName
    if (isNSN || isNSN6 || isMoz5) {
        ss_slidingTableDragObj = self.document.getElementById(id);
    } else {
        ss_slidingTableDragObj = self.document.all[id];
        ss_slidingTableOffsetX = window.event.offsetX
        ss_slidingTableOffsetY = window.event.offsetY
    }

    ss_slidingTableStartingToDrag = 1;
    if (self.document.onmousemove) ss_slidingTableSavedMouseMove = self.document.onmousemove;
    if (self.document.onmouseup) ss_slidingTableSavedMouseUp = self.document.onmouseup;
    self.document.onmousemove = ss_slidingTableDrag
    self.document.onmouseup = ss_slidingTableStopDrag
	ss_showHideObj("ss_info_popup", "hidden", "none")

    return false
}

function ss_slidingTableDrag(evt) {
    var ss_sTableLeft = getObjAbsX("<c:out value="${ss_slidingTableId}"/>_2")
    var ss_sTableWidth = ss_getDivWidth("<c:out value="${ss_slidingTableId}"/>_2")
    if (ss_sTableWidth == 0) ss_sTableWidth = parseInt(ss_defTableWidth);
    var ss_sTableInnerWidth = parseInt(ss_sTableWidth - ss_sTableMarginLeft - ss_sTableMarginRight);
    var ss_sTableHeight = ss_getDivHeight("<c:out value="${ss_slidingTableId}"/>_2")
    var ss_sTableInnerHeight = parseInt(ss_sTableHeight- ss_sTableMarginTop - ss_sTableMarginBottom)

    if (!evt) evt = window.event;
    if (ss_slidingTableDragObj) {
        if (ss_slidingTableStartingToDrag == 1) {
            if (evt.layerX) {
                if (isNSN || isNSN6 || isMoz5) {
                    ss_slidingTableOffsetX = evt.layerX;
                    ss_slidingTableOffsetY = evt.layerY;
                }
            }
            ss_slidingTableStartingToDrag = 0
        }
		var scrollDivLeft = parseInt(ss_getDivLeft("<c:out value="${ss_slidingTableId}"/>")) - parseInt(ss_getDivScrollLeft("<c:out value="${ss_slidingTableId}"/>"))
        var dObjLeft
        if (isNSN || isNSN6 || isMoz5) {
            dObjLeft = evt.pageX - ss_slidingTableOffsetX;
        } else {
            dObjLeft = evt.clientX - ss_slidingTableOffsetX;
        }
        //Don't let the column go beyond the right or left margins
        if (parseInt(dObjLeft) < parseInt(ss_sTableLeft + ss_sTableMarginLeft)) {
        	dObjLeft = parseInt(ss_sTableLeft + ss_sTableMarginLeft)
        }
        //Leave some room to grab the table to drag it back (if necessary)
        if (parseInt(dObjLeft) > parseInt(ss_sTableLeft + ss_sTableMarginLeft + ss_sTableInnerWidth + scrollDivLeft - 8)) {
        	dObjLeft = parseInt(ss_sTableLeft + ss_sTableMarginLeft + ss_sTableInnerWidth + scrollDivLeft - 8)
        }
        dObjLeft = parseInt(dObjLeft)
        ss_slidingTableDragObj.style.left = parseInt(dObjLeft - scrollDivLeft) + "px"
        var dObjClipWidth = parseInt(ss_sTableLeft + ss_sTableMarginLeft + ss_sTableInnerWidth - dObjLeft + scrollDivLeft)
        if (dObjClipWidth < 4) dObjClipWidth = 4
        
        ss_slidingTableDragObj.style.clip = "rect(-9px " + dObjClipWidth + "px " + ss_sTableInnerHeight + "px -9px)"
        return false
    
    } else {
        return true
    }
}

function ss_slidingTableStopDrag(evt) {
    if (!evt) evt = window.event;
    if (ss_slidingTableDragObj) {
        ss_slidingTableDragObj = null
    }
    self.document.onmousemove = ss_slidingTableSavedMouseMove;
    self.document.onmouseup = ss_slidingTableSavedMouseUp;
    setTimeout("ss_saveSlidingTableCoords();", 200)
    ss_slidingTableStartingToDrag = 0;
    return true
}

var ss_popUp_sizer_width = 0;
var ss_slidingTableMosueOverObj = null
function ss_showMouseOverInfo(obj) {
	if (obj != ss_slidingTableMosueOverObj) {
		var ihtml = obj.innerHTML;
		if (ihtml != "" && ihtml != "&nbsp;" && ihtml != "&nbsp;&nbsp;" && 
		    ihtml != "&nbsp;&nbsp;&nbsp;") {
		    if (ihtml.length == 1 && ihtml.charCodeAt(0) == 160) {
		    	ss_showHideObj("ss_info_popup", "hidden", "none");
		    } else if (ihtml.length == 2 && ihtml.charCodeAt(0) == 160 && ihtml.charCodeAt(1) == 160) {
		    	ss_showHideObj("ss_info_popup", "hidden", "none");
		    } else if (ihtml.length == 3 && ihtml.charCodeAt(0) == 160 && ihtml.charCodeAt(1) == 160 && ihtml.charCodeAt(2) == 160) {
		    	ss_showHideObj("ss_info_popup", "hidden", "none");
		    } else {
				var s = "<table cellspacing='0' cellpadding='0' style='border: solid black 1px;'>"
				s += "<tr><td class='" + obj.className + "' nowrap>"
				s += obj.innerHTML
				s += "&nbsp;&nbsp;&nbsp;</td></tr></table>"
				ss_setDivHtml("ss_info_popup", s)
				if (self.document.getElementById("ss_info_popup")) {
					var x = parseInt(obj.offsetParent.offsetParent.style.left)
					x = parseInt(ss_getDivLeft("<c:out value="${ss_slidingTableId}"/>")) - parseInt(ss_getDivScrollLeft("<c:out value="${ss_slidingTableId}"/>")) + x
					var y = parseInt(parseInt(obj.offsetTop) + parseInt(obj.offsetParent.offsetTop) + parseInt(obj.offsetParent.offsetParent.style.top))
					y = parseInt(ss_getDivTop("<c:out value="${ss_slidingTableId}"/>")) - parseInt(ss_getDivScrollTop("<c:out value="${ss_slidingTableId}"/>")) + y
					self.document.getElementById("ss_info_popup").style.left = x + "px"
					self.document.getElementById("ss_info_popup").style.top = y + "px"
					ss_showHideObj("ss_info_popup", "visible", "block")
					
					//See if this is a new maximum width
					var w = parseInt(x + ss_getDivWidth("ss_info_popup"))
					if (w > ss_popUp_sizer_width) {
						ss_popUp_sizer_width = w;
						ss_setObjectLeft(self.document.getElementById("ss_info_popup_sizer"), "0px")
						ss_setObjectWidth(self.document.getElementById("ss_info_popup_sizer"), w)
					}
				}
			}
		} else {
			ss_showHideObj("ss_info_popup", "hidden", "none")
		}
	}
	ss_slidingTableMosueOverObj = obj
}

function ss_clearMouseOverInfo(obj) {
	if (!obj || obj == null) {
		ss_slidingTableMosueOverObj = null;
		ss_showHideObj("ss_info_popup", "hidden", "none")
	} else if (obj != ss_slidingTableMosueOverObj) {
		ss_slidingTableMosueOverObj = obj
 		ss_showHideObj("ss_info_popup", "hidden", "none")
	}
}

function ss_clearMouseOverInfoOnScroll(e) {
	//Note, this event is not available on some browsers (e.g., ie)
	ss_clearMouseOverInfo()
	return true
}

ss_createOnLoadObj('ss_showSlidingTableCols', ss_showSlidingTableCols200);
ss_createOnResizeObj('ss_showSlidingTableCols', ss_showSlidingTableCols);
ss_createOnLayoutChangeObj('ss_checkSlidingTableLayout', ss_checkSlidingTableLayout);
ss_createEventObj('ss_slidingTableDrag', 'MOUSEMOVE');
ss_createEventObj('ss_clearMouseOverInfoOnScroll', 'SCROLL');

-->
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
<div id="<c:out value="${ss_slidingTableId}"/>" style="position:relative; 
 height:<%= slidingTableFolderHeight %>px; overflow:scroll; 
 margin:2px; border: #666666 1px solid;">
<div id="<c:out value="${ss_slidingTableId}"/>_2" style="margin:0px;" width="100%"
 onMouseOver="if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo(this);"
 onMouseOut="if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo(this);">

<div id="col0" class="ss_style ss_sliding_table_column0">
<table cellspacing="0" cellpadding="0">
 <tr>
  <td>&nbsp;</td>
 </tr>
</table>
<table cellspacing="0" cellpadding="0">
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
ss_colWidths['col<%= String.valueOf(iCol + 1) %>'] = '<%= columnWidth %>';
</script>
<%
					}
%>
<div id="col<%= String.valueOf(iCol + 1) %>" class="ss_style ss_sliding_table_column1">
<table cellspacing="0" cellpadding="0" width="100%">
<tr class="<%= rowStyle %>" onMouseOver="if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo(this);">
<td class="<%= rowStyle %>" >&nbsp;</td>
</tr>
</table>
<table cellspacing="0" cellpadding="0" width="100%">
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
ss_colWidths['col<%= String.valueOf(iCol + 1) %>'] = '<%= columnWidth %>';
</script>
<%
					}
%>
<div id="col<%= String.valueOf(iCol + 1) %>" class="ss_style ss_sliding_table_column"  
  style="z-index:<%= String.valueOf(iCol + 11) %>;">
<table cellspacing="0" cellpadding="0" width="100%">
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
<table cellspacing="0" cellpadding="0" width="100%">
<tr <%= rowIdText %>>
<%
					if (headerRow.booleanValue()) {
%>
<th class="<%= rowStyle %>" align="left" onMouseOver="if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo(this);">&nbsp;<%= columnText %>&nbsp;</th>
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
ss_colWidths['col<%= String.valueOf(iCol + 1) %>'] = '<%= columnWidth %>';
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
</div>
<script type="text/javascript">

function ss_saveSlidingTableCoords() {
    var s = ""
    var ss_sTableLeft = getObjAbsX("<c:out value="${ss_slidingTableId}"/>_2")
    for (var i = 0; i <= ss_columnCount; i++) {
    	var colLeft = parseInt(parseInt(self.document.getElementById("col"+i).style.left) - ss_sTableLeft - ss_sTableMarginLeft)
	    s += colLeft+" "
    }
    self.document.forms['ss_columnPositionForm'].column_positions.value = s;
	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="true" >
		<ssf:param name="operation" value="save_column_positions" />
		<ssf:param name="binderId" value="<%= slidingTableFolderId %>" />
    	</ssf:url>"
	var ajaxRequest = new AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addFormElements("ss_columnPositionForm")
	//ajaxRequest.setEchoDebugInfo();
	//ajaxRequest.setPreRequest(ss_preSlidingTableRequest);
	ajaxRequest.setPostRequest(ss_postSlidingTableRequest);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_preSlidingTableRequest(obj) {
	//alert('preRequest: ' + obj.getQueryString());
}
function ss_postSlidingTableRequest(obj) {
	//See if there was an error
	if (self.document.getElementById("ss_sliding_table_status_message").innerHTML == "error") {
		alert("<ssf:nlt tag="general.notLoggedIn" text="Your session has timed out. Please log in again."/>");
	}
}
</script>
<form class="ss_style ss_form" name="ss_columnPositionForm" id="ss_columnPositionForm" >
<input type="hidden" name="column_positions">
</form>
<div id="ss_sliding_table_status_message" style="visibility:hidden; display:none;"></div>
<div id="ss_info_popup" class="ss_style ss_sliding_table_info_popup"></div>
<div id="ss_info_popup_sizer" style="position:absolute; visibility:hidden;"></div>
