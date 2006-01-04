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
<%@ page import="java.lang.String" %>
<%@ page import="java.lang.Boolean" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<portletadapter:defineObjects1/>
<ssf:ifadapter><portletadapter:defineObjects2/></ssf:ifadapter>
<ssf:ifnotadapter><portlet:defineObjects/></ssf:ifnotadapter>

<style>
div.ss_sliding_table_column_blank {
	display:inline;
}
div.ss_sliding_table_column0 {
   visibility: hidden;
  border-left: #cccccc 1px solid;
  background-color: #ffffff; 
  color: #000000;
}
div.ss_sliding_table_column {
   visibility: hidden;
  border-left: #cccccc solid 1px;
  background-color: #ffffff; 
  color: #000000;
}
</style>

<script language="javascript">
<!--

var isNSN = (navigator.appName == "Netscape");
var isNSN4 = isNSN && ((navigator.userAgent.indexOf("Mozilla/4") > -1));
var isNSN6 = ((navigator.userAgent.indexOf("Netscape6") > -1));
var isMoz5 = ((navigator.userAgent.indexOf("Mozilla/5") > -1) && !isNSN6);
var isMacIE = ((navigator.userAgent.indexOf("IE ") > -1) && (navigator.userAgent.indexOf("Mac") > -1));
var isIE = ((navigator.userAgent.indexOf("IE ") > -1));

if (self.Event) self.document.captureEvents(Event.MOUSEDOWN);
if (self.Event) self.document.captureEvents(Event.MOUSEUP);
if (self.Event) self.document.captureEvents(Event.MOUSEMOVE);

var dragObj = null
var offsetX
var offsetY

var defTableWidth = 600
var dTableMargin = 2
var dTableMarginHalf = 1
var dTableMarginTop = 4
function ss_showSlidingTableCols() {
    var top = parseInt(getDivTop("dtable") + dTableMarginTop)
    var left = getDivLeft("dtable")
    var dtableWidth = getDivWidth("dtable")
    var dtableHeight = getDivHeight("col_blank")
    if (dtableWidth == 0) dtableWidth = parseInt(defTableWidth);
    var deltaW = parseInt(dtableWidth / ss_columnCount)
    var deltaLeft = dTableMargin
    var w = parseInt(dtableWidth - dTableMargin) + "px"
    
    //Set the position and clipping region of each column
	positionDtableCol("col_blank", left, top, parseInt(dtableWidth - dTableMargin*2) + "px")
    for (var i = 0; i < ss_columnCount; i++) {
	    positionDtableCol("col"+i, parseInt(left + deltaLeft), top, w)
    	w = parseInt(parseInt(w) - deltaW) + "px"
    	deltaLeft = deltaLeft + deltaW
    	ss_showHideObj("col"+i, "visible", "inline")
    }
}

function positionDtableCol(divName, x, y, w) {
    var dtableHeight = getDivHeight("dtable")
    var divObj = self.document.getElementById(divName)
    if (isNSN || isNSN6 || isMoz5) {
    	if (divObj && divObj.offsetParent) {
	        divObj.style.left= (x - dTableMarginHalf - parseInt(divObj.offsetParent.offsetLeft)) + "px"
	    } else {
	        divObj.style.left= x - dTableMarginHalf + "px"
	    }
        divObj.style.width = parseInt(parseInt(w) + dTableMargin)
        divObj.style.clip = "rect(0px " + parseInt(parseInt(w) + dTableMargin + dTableMarginHalf) + "px "+dtableHeight+"px 0px)"
    } else {
        if (divObj && divObj.offsetParent) {
	        divObj.style.left=x - divObj.offsetParent.offsetLeft
    	} else {
	        divObj.style.left=x
    	}
        divObj.style.width = w
        divObj.style.clip = "rect(0px " + w + " "+dtableHeight+"px 0px)"
    }
}

var dragDivName = null;
var startingToDrag = null;
function startDragCol(obj, divName) {
    var id = divName
    if (isNSN || isNSN6 || isMoz5) {
        dragObj = self.document.getElementById(id);
    } else {
        dragObj = self.document.all[id];
        offsetX = window.event.offsetX
        offsetY = window.event.offsetY
    }

    startingToDrag = 1;
    self.document.onmousemove = drag
    self.document.onmouseup = stopDrag

    return false
}

function drag(evt) {
    var dtableLeft = getDivLeft("dtable")
    var dtableWidth = getDivWidth("dtable")
    var dtableHeight = getDivHeight("col0")
    if (dtableWidth == 0) dtableWidth = parseInt(defTableWidth);
    if (!evt) evt = window.event;
    if (dragObj) {
        if (startingToDrag == 1) {
            if (evt.layerX) {
                if (isNSN || isNSN6 || isMoz5) {
                    offsetX = evt.layerX;
                    offsetY = evt.layerY;
                }
            }
            startingToDrag = 0
        }
        var dObjLeft
        if (isNSN || isNSN6 || isMoz5) {
            dObjLeft = evt.pageX - offsetX;
        } else {
            dObjLeft = evt.clientX - offsetX;
        }
        if (parseInt(dObjLeft) < dtableLeft) dObjLeft = dtableLeft
        if (parseInt(dObjLeft) > parseInt(dtableLeft + dtableWidth - 8)) {
        	dObjLeft = parseInt(dtableLeft + dtableWidth - 8)
        }
        dragObj.style.left = dObjLeft
        dObjLeft = parseInt(dObjLeft)
        var dObjWidth = parseInt(dtableWidth - dObjLeft + dtableLeft)
        if (dObjWidth < 4) dObjWidth = 4
        dragObj.style.width = dObjWidth + "px"
        
        var clipRight = parseInt(dObjWidth + dtableLeft)
        if (parseInt(dObjLeft + dObjWidth) > dtableWidth) {
        	//Don't allow the clip region to become wider than the base table
        	clipRight = parseInt(dtableLeft + dtableWidth - dObjLeft)
    		if (isNSN || isNSN6 || isMoz5) {
    			clipRight = parseInt(clipRight - dTableMargin - dTableMarginHalf)
    		}
        }
        dragObj.style.clip = "rect(0px "+clipRight+" "+dtableHeight+"px 0px)"
        return false
    
    } else {
        return true
    }
}

function stopDrag(evt) {
    if (!evt) evt = window.event;
    if (dragObj) {
        dragObj = null
    }
    self.document.onmousemove = ''
    self.document.onmouseup = ''
    return false
}


createOnLoadObj('ss_showSlidingTableCols', ss_showSlidingTableCols);
createOnResizeObj('ss_showSlidingTableCols', ss_showSlidingTableCols);
-->
</script>

<%
	List slidingTableRows = (List) request.getAttribute("ss_slidingTableRows");
	
	//Loop through the rows, splitting their columns into separate divs
	if (slidingTableRows.size() > 0) {
		int colSize = ((List)((Map) slidingTableRows.get(0)).get("columns")).size();
		
		//Output the main div that holds them all
%>
<script language="javascript">
var ss_columnCount = <%= String.valueOf(colSize) %>;
</script>

<div id="dtable" style="border: #cccccc 1px solid; width:100%;">

<div id="col_blank" class="ss_sliding_table_column_blank">
<table class="ss_content" cellspacing="0" cellpadding="0" width="1">
<tr><td nowrap>&nbsp;</td></tr>
<%		
		//Output a table of blanks to give the parent div the proper height
		for (int iRow = 0; iRow < slidingTableRows.size(); iRow++) {
%>
<tr><td nowrap>&nbsp;</td></tr>
<%
		}		
%>
</td></tr></table>
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
					
				//Output the containing div
				if (iRow == 0 && iCol == 0) {
%>
<div style="display:inline;">
<div id="col<%= String.valueOf(iCol) %>" class="ss_sliding_table_column0">
<table class="ss_content" cellspacing="0" cellpadding="0" width="100%">
<tr><td nowrap>&nbsp;</td></tr>
<tr <%= rowIdText %>><td nowrap>&nbsp;<%= columnMap.get("text") %></td></tr>
<%
				} else if (iRow == 0 && iCol > 0) {
%>

<div style="display:inline;">
<div id="col<%= String.valueOf(iCol) %>" class="ss_sliding_table_column">
<table class="ss_content" cellspacing="0" cellpadding="0" width="100%">
<tr><td><a id="drag<%= String.valueOf(iCol) %>" onMousedown="startDragCol(this, 'col<%= String.valueOf(iCol) %>');"><<<&nbsp;>>></a></td></tr>
<tr <%= rowIdText %>><td nowrap>&nbsp;<%= columnMap.get("text") %></td></tr>
<%
				} else {
%>
<tr <%= rowIdText %>><td nowrap>&nbsp;<%= columnMap.get("text") %></td></tr>
<%
				}
			}
			//Output the closing divs for each column
%>
</td></tr></table>
</div>
</div>

<%
		}
%>
</div>
<%		
	}
	
%>
