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
<%@ page session="false" %>
<%@ page contentType="text/html" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ssf" uri="http://www.sitescape.com/tags-ssf" %>
<%@ taglib prefix="portletadapter" uri="http://www.sitescape.com/tags-portletadapter" %>
<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>
<%@ page import="com.sitescape.util.ParamUtil" %>
<%@ page import="com.sitescape.ef.domain.UserProperties" %>
<%@ page import="java.lang.String" %>
<%@ page import="java.lang.Boolean" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<portletadapter:defineObjects1/>
<ssf:ifadapter><portletadapter:defineObjects2/></ssf:ifadapter>
<ssf:ifnotadapter><portlet:defineObjects/></ssf:ifnotadapter>
<script language="javascript">
<!--
var ss_colWidths = new Array();
var ss_colWidthsUser = new Array();

<%
	//Get the row and column data
	List slidingTableRows = (List) request.getAttribute("ss_slidingTableRows");
	String slidingTableFolderId = (String) request.getAttribute("ss_slidingTableFolderId");
	
	//Get the user's column positions (if set)
	UserProperties userFolderProperties = (UserProperties) request.getAttribute("ssUserFolderProperties");
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

var ss_sTableLastHeight = 0;
function ss_checkSlidingTableLayout() {
	if (getDivTop("ss_sTable") != ss_sTableLastHeight) {
		//The layout changed, go reposition things
		ss_sTableLastHeight = getDivTop("ss_sTable")
		ss_showSlidingTableCols()
	}
}

function ss_showSlidingTableCols() {
	ss_sTableMarginLeft = parseInt(self.document.getElementById("ss_sTable").style.marginRight)
	ss_sTableMarginRight =  parseInt(self.document.getElementById("ss_sTable").style.marginLeft)
	ss_sTableMarginTop = parseInt(self.document.getElementById("ss_sTable").style.marginTop)
	ss_sTableMarginBottom =  parseInt(self.document.getElementById("ss_sTable").style.marginBottom)
	//Calculate the default column positions
    var ss_sTableWidth = getDivWidth("ss_sTable")
    if (ss_sTableWidth == 0) ss_sTableWidth = parseInt(ss_defTableWidth);
    var maxColLeft = ss_sTableWidth - ss_sTableMarginLeft - ss_sTableMarginRight - 8
    var defColWidth = parseInt((ss_sTableWidth - ss_sTableMarginLeft - ss_sTableMarginRight) / ss_columnCount)
    var deltaW = defColWidth
    var widthTotalPercentage = 0
    var widthTotalPixels = 0
    for (var i = 1; i <= ss_columnCount; i++) {
    	if (ss_colWidths["col"+i]) {
    		var cw = ss_colWidths["col"+i]
    		if (cw.indexOf("%") > 0) {
    			//This is a percentage; add it to the total percent
    			cw = cw.substr(0, cw.indexOf("%"));
    			widthTotalPercentage += parseInt(cw) * parseInt((ss_sTableWidth - ss_sTableMarginLeft - ss_sTableMarginRight) / 100)
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
    var percentageWidth = parseInt(ss_sTableWidth - ss_sTableMarginLeft - ss_sTableMarginRight - widthTotalPixels)
	
    //Now, position the columns
    var top = parseInt(getDivTop("ss_sTable") + ss_sTableMarginTop)
    var left = getDivLeft("ss_sTable")
    var deltaLeft = 0
    var w = parseInt(ss_sTableWidth - ss_sTableMarginLeft - ss_sTableMarginRight - deltaLeft) + "px"
    
    //Set the position of each column (put col0 at the left edge, 
    //  then position the others successively to the right)
	ss_position_sTableCol("col0", left, top, w)
    for (var i = 1; i <= ss_columnCount; i++) {
	    //See if the user re-positioned the columns. If so, use those settings.
	    if (ss_colWidthsUser["col"+i]) {
	    	deltaLeft = parseInt(ss_colWidthsUser["col"+i])
	    	deltaLeft = deltaLeft + ss_sTableMarginLeft
	    }
    	//But, always start the first column at the left edge
    	if (i == 1) deltaLeft = 0;
	    if (deltaLeft < 0) deltaLeft = 0
	    if (deltaLeft > maxColLeft) deltaLeft = maxColLeft
    	w = parseInt(ss_sTableWidth - ss_sTableMarginLeft - ss_sTableMarginRight - deltaLeft) + "px"
    	if (parseInt(w) < 0) w = defColWidth;
	    ss_position_sTableCol("col"+i, parseInt(left + deltaLeft), top, w)
	    if (!ss_colWidthsUser["col"+i]) {
	    	if (ss_colWidths["col"+i]) {
	    		var cw = ss_colWidths["col"+i]
	    		if (cw.indexOf("%") > 0) {
	    			cw = cw.substr(0, cw.indexOf("%"));
	    			var dw = percentageWidth * parseInt(cw) / 100
	     			deltaLeft = deltaLeft + dw
	    		} else {
	    			deltaLeft = deltaLeft + parseInt(cw)
	    		}
	    	} else {
	     		deltaLeft = deltaLeft + deltaW
	    	}
	    }
    	ss_showHideObj("col"+i, "visible", "inline")
    }
}

function ss_position_sTableCol(divName, x, y, w) {
    var ss_sTableHeight = getDivHeight("ss_sTable")

    if (isNSN || isNSN6 || isMoz5) {
    	if (self.document.getElementById(divName) && self.document.getElementById(divName).offsetParent) {
	        self.document.getElementById(divName).style.left= (x - ss_sTableMarginLeft - parseInt(self.document.getElementById(divName).offsetParent.offsetLeft)) + "px"
	        self.document.getElementById(divName).style.top= (y - ss_sTableMarginTop - parseInt(self.document.getElementById(divName).offsetParent.offsetTop)) + "px"
	    } else {
	        self.document.getElementById(divName).style.left= x - ss_sTableMarginLeft + "px"
	        self.document.getElementById(divName).style.top= y - ss_sTableMarginTop + "px"
	    }
        self.document.getElementById(divName).style.width = parseInt(parseInt(w) - ss_sTableMarginLeft - ss_sTableMarginRight)
        self.document.getElementById(divName).style.clip = "rect("+ss_sTableMarginTop+"px " + parseInt(parseInt(w) - ss_sTableMarginLeft - ss_sTableMarginRight) + "px "+parseInt(ss_sTableHeight - ss_sTableMarginBottom)+"px 0px)"
    } else {
        if (self.document.all[divName] && self.document.all[divName].offsetParent) {
	        self.document.all[divName].style.left=x - self.document.all[divName].offsetParent.offsetLeft
	        self.document.all[divName].style.top=y - self.document.all[divName].offsetParent.offsetTop
    	} else {
	        self.document.all[divName].style.left=x
	        self.document.all[divName].style.top=y
    	}
        self.document.all[divName].style.width = w
        self.document.all[divName].style.clip = "rect("+ss_sTableMarginTop+"px " + w + " "+parseInt(ss_sTableHeight - ss_sTableMarginBottom)+"px 0px)"
    }
}

var ss_slidingTableStartingToDrag = null;
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
    self.document.onmousemove = ss_slidingTableDrag
    self.document.onmouseup = ss_slidingTableStopDrag
	ss_showHideObj("ss_info_popup", "hidden", "none")

    return false
}

function ss_slidingTableDrag(evt) {
    var ss_sTableLeft = getDivLeft("ss_sTable")
    var ss_sTableWidth = getDivWidth("ss_sTable")
    var ss_sTableHeight = getDivHeight("ss_sTable")
    if (ss_sTableWidth == 0) ss_sTableWidth = parseInt(ss_defTableWidth);
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
        var dObjLeft
        if (isNSN || isNSN6 || isMoz5) {
            dObjLeft = evt.pageX - ss_slidingTableOffsetX;
        } else {
            dObjLeft = evt.clientX - ss_slidingTableOffsetX;
        }
        //Don't let the column go beyond the right or left margins
        if (parseInt(dObjLeft) < ss_sTableLeft) dObjLeft = ss_sTableLeft
        //Leave some room to grab the table to drag it back (if necessary)
        if (parseInt(dObjLeft) > parseInt(ss_sTableLeft + ss_sTableWidth - 8)) {
        	dObjLeft = parseInt(ss_sTableLeft + ss_sTableWidth - 8)
        }
        ss_slidingTableDragObj.style.left = dObjLeft
        dObjLeft = parseInt(dObjLeft)
        var dObjWidth = parseInt(ss_sTableWidth - ss_sTableMarginLeft - ss_sTableMarginRight - dObjLeft + ss_sTableLeft)
    	if (isNSN || isNSN6 || isMoz5) {
    		dObjWidth = parseInt(ss_sTableWidth - ss_sTableMarginLeft - ss_sTableMarginRight - dObjLeft + ss_sTableLeft)
    	}
        if (dObjWidth < 4) dObjWidth = 4
        ss_slidingTableDragObj.style.width = dObjWidth + "px"
        
        var clipRight = parseInt(dObjLeft + dObjWidth + ss_sTableLeft)
        if (parseInt(dObjLeft + dObjWidth) > ss_sTableWidth) {
        	//Don't allow the clip region to become wider than the base table
        	clipRight = parseInt(ss_sTableLeft + ss_sTableWidth - ss_sTableMarginRight - dObjLeft)
        }
        clipRight = parseInt(ss_sTableLeft + ss_sTableWidth - ss_sTableMarginRight - dObjLeft)
        ss_slidingTableDragObj.style.clip = "rect("+ss_sTableMarginTop+"px "+clipRight+" "+parseInt(ss_sTableHeight - ss_sTableMarginBottom)+"px 0px)"
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
    self.document.onmousemove = ''
    self.document.onmouseup = ''
    setTimeout("ss_saveSlidingTableCoords();", 200)
    return false
}

var ss_slidingTableMosueOverObj = null
function ss_showMouseOverInfo(obj) {
	if (obj != ss_slidingTableMosueOverObj) {
		if (obj.innerHTML != "" && obj.innerHTML != "&nbsp;" && obj.innerHTML != "&nbsp;&nbsp;" && obj.innerHTML != "&nbsp;&nbsp;&nbsp;") {
			var s = "<table class='ss_content' cellspacing='0' cellpadding='0' style='border: solid black 1px;'><tr><td nowrap>"
			s += obj.innerHTML
			s += "&nbsp;&nbsp;&nbsp;</td></tr></table>"
			ss_setDivHtml("ss_info_popup", s)
			if (self.document.getElementById("ss_info_popup")) {
				var x = parseInt(obj.offsetParent.offsetParent.style.left) - 1
				var y = parseInt(parseInt(obj.offsetTop) + parseInt(obj.offsetParent.offsetTop) + parseInt(obj.offsetParent.offsetParent.style.top)) - 1
				self.document.getElementById("ss_info_popup").style.left = x + "px"
				self.document.getElementById("ss_info_popup").style.top = y + "px"
				ss_showHideObj("ss_info_popup", "visible", "block")
			}
		} else {
			ss_showHideObj("ss_info_popup", "hidden", "none")
		}
	}
	ss_slidingTableMosueOverObj = obj
}

function ss_clearMouseOverInfo(obj) {
	if (obj != ss_slidingTableMosueOverObj) {
		ss_slidingTableMosueOverObj = obj
		ss_showHideObj("ss_info_popup", "hidden", "none")
	}
}

createOnLoadObj('ss_showSlidingTableCols', ss_showSlidingTableCols);
createOnResizeObj('ss_showSlidingTableCols', ss_showSlidingTableCols);
createOnLayoutChangeObj('ss_checkSlidingTableLayout', ss_checkSlidingTableLayout);
-->
</script>

<%
	//Loop through the rows, splitting their columns into separate divs
	if (slidingTableRows.size() > 0) {
		int colSize = ((List)((Map) slidingTableRows.get(0)).get("columns")).size();
		
		//Output the main div that holds them all
%>
<script language="javascript">
var ss_columnCount = <%= String.valueOf(colSize) %>;
</script>
<div id="ss_sTable" style="margin: 2px; border: #666666 1px solid; width:100%;"
 onMouseOver="ss_clearMouseOverInfo(this)">

<div id="col0" class="ss_sliding_table_column1">
<table class="ss_content" cellspacing="0" cellpadding="0"><tr><td>&nbsp;</td></tr></table>
<table class="ss_content" cellspacing="0" cellpadding="0">
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

			for (int iRow = 0; iRow < slidingTableRows.size(); iRow++) {
				String rowId = (String)((Map) slidingTableRows.get(iRow)).get("id");
				Boolean headerRow = (Boolean)((Map) slidingTableRows.get(iRow)).get("headerRow");
				List columns = (List)((Map) slidingTableRows.get(iRow)).get("columns");
				
				//Get the row id text
				String rowIdText = "";
				if (rowId != null && !rowId.equals("")) {
					rowIdText = "id='" + rowId + "_" + String.valueOf(iCol) + "' ";
				}

				//Process the columns
				Map columnMap = (Map) columns.get(iCol);
				String columnWidth = "";
				if (columnMap.containsKey("width")) columnWidth = (String) columnMap.get("width");
					
				//Output the containing div
				if (iRow == 0 && iCol == 0) {
					if (!columnWidth.equals("")) {
%>
<script language="javascript">
ss_colWidths['col<%= String.valueOf(iCol + 1) %>'] = '<%= columnWidth %>';
</script>
<%
					}
%>
<div id="col<%= String.valueOf(iCol + 1) %>" class="ss_sliding_table_column">
<table class="ss_content" cellspacing="0" cellpadding="0"><tr><td>&nbsp;</td></tr></table>
<table class="ss_content" cellspacing="0" cellpadding="0" width="100%">
<tr <%= rowIdText %>>
<%
					if (headerRow.booleanValue()) {
%>
<th align="left">&nbsp;<%= columnMap.get("text") %>&nbsp;</th>
<%
					} else {
%>
<td nowrap width="100%"
  onMouseOver="ss_showMouseOverInfo(this)" onMouseOut="ss_clearMouseOverInfo(this)"
  >&nbsp;<%= columnMap.get("text") %></td>
<%
					}
%>
</tr>
<%
				} else if (iRow == 0 && iCol > 0) {
					if (!columnWidth.equals("")) {
%>
<script language="javascript">
ss_colWidths['col<%= String.valueOf(iCol + 1) %>'] = '<%= columnWidth %>';
</script>
<%
					}
%>
<div id="col<%= String.valueOf(iCol + 1) %>" class="ss_sliding_table_column"  style="z-index:<%= String.valueOf(iCol + 11) %>;">
<table class="ss_content" cellspacing="0" cellpadding="0" width="100%">
<tr>
<td><a id="drag<%= String.valueOf(iCol + 1) %>" style="text-decoration:none;"
onMousedown="ss_slidingTableStartDragCol(this, 'col<%= String.valueOf(iCol + 1) %>');"
><span style="cursor:w-resize; color:darkgreen; font-size:small; text-decoration:none;
  background-position:center left;
  background-image:url(<html:imagesPath/>pics/sym_s_arrows_eastwest.gif);
  background-repeat:no-repeat;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></a></td>
</tr>
</table>
<table class="ss_content" cellspacing="0" cellpadding="0" width="100%">
<tr <%= rowIdText %>>
<%
					if (headerRow.booleanValue()) {
%>
<th align="left">&nbsp;<%= columnMap.get("text") %>&nbsp;</th>
<%
					} else {
%>
<td nowrap width="100%"
  onMouseOver="ss_showMouseOverInfo(this)" onMouseOut="ss_clearMouseOverInfo(this)"
 >&nbsp;<%= columnMap.get("text") %></td>
<%
					}
%>
</tr>
<%
				} else {
					if (!columnWidth.equals("")) {
%>
<script language="javascript">
ss_colWidths['col<%= String.valueOf(iCol + 1) %>'] = '<%= columnWidth %>';
</script>
<%
					}
%>
<tr <%= rowIdText %>>
<%
					if (headerRow.booleanValue()) {
%>
<th align="left">&nbsp;<%= columnMap.get("text") %>&nbsp;</th>
<%
					} else {
%>
<td nowrap width="100%"
  onMouseOver="ss_showMouseOverInfo(this)" onMouseOut="ss_clearMouseOverInfo(this)"
 >&nbsp;<%= columnMap.get("text") %></td>
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
<script language="JavaScript" src="<html:rootPath/>js/common/taconite-client.js"></script>
<script language="JavaScript" src="<html:rootPath/>js/common/taconite-parser.js"></script>
<script language="javascript">
var count = 0
function ss_saveSlidingTableCoords() {
    var s = ""
    var ss_sTableLeft = getDivLeft("ss_sTable")
    for (var i = 0; i <= ss_columnCount; i++) {
    	var colLeft = parseInt(parseInt(self.document.getElementById("col"+i).style.left) - ss_sTableLeft - ss_sTableMarginLeft)
	    s += colLeft+" "
    }
    self.document.forms['ss_columnPositionForm'].column_positions.value = s;
	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__view_unseen" 
    	actionUrl="true" >
		<ssf:param name="operation" value="save_column_positions" />
		<ssf:param name="binderId" value="<%= slidingTableFolderId %>" />
    	</ssf:url>"
	var ajaxRequest = new AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addFormElements("ss_columnPositionForm")
	//ajaxRequest.setEchoDebugInfo();
	//ajaxRequest.setPreRequest(ss_preRequest);
	ajaxRequest.setPostRequest(ss_postRequest);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_preRequest(obj) {
	alert('preRequest: ' + obj.getQueryString());
}
function ss_postRequest(obj) {
	//alert('postRequest: ' + obj.getXMLHttpRequestObject().responseText);
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		if (self.ss_showNotLoggedInMsg) self.ss_showNotLoggedInMsg();
	}
}
function ss_showNotLoggedInMsg() {
	alert("<ssf:nlt tag="forum.unseenCounts.notLoggedIn" text="Your session has timed out. Please log in again."/>");
}
</script>
<form name="ss_columnPositionForm" id="ss_columnPositionForm" >
<input type="hidden" name="column_positions">
</form>
<div id="ss_status_message" style="visibility:hidden; display:none;"></div>
<div id="ss_info_popup" class="ss_sliding_table_info_popup"></div>
