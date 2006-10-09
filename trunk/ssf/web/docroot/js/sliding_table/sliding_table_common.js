//Routines that support the sliding table

if (self.Event && self.Event.MOUSEDOWN) self.document.captureEvents(Event.MOUSEDOWN);
if (self.Event && self.Event.MOUSEUP) self.document.captureEvents(Event.MOUSEUP);
if (self.Event && self.Event.MOUSEMOVE) self.document.captureEvents(Event.MOUSEMOVE);

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
var ss_sTableLastParentWidth = 0;

function ss_checkSlidingTableLayout200() {
	setTimeout("ss_checkSlidingTableLayout();", 200)
}

function ss_checkSlidingTableLayout() {
	if (ss_getSlidingTableY(ss_slidingTableId_2) != ss_sTableLastTop || 
			ss_getSlidingTableX(ss_slidingTableId_2) != ss_sTableLastLeft || 
			ss_getDivWidth(ss_slidingTableParentId) != ss_sTableLastParentWidth) {
		//The layout changed, go reposition things
		ss_sTableLastTop = ss_getSlidingTableY(ss_slidingTableId_2)
		ss_sTableLastLeft = ss_getSlidingTableX(ss_slidingTableId_2)
		if (ss_slidingTableParentId != "") ss_sTableLastParentWidth = ss_getDivWidth(ss_slidingTableParentId);
		ss_showSlidingTableCols()
		if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo();
	}
	if (ss_checkIfParentDivHidden(ss_slidingTableId_2)) {
		ss_hideSlidingTableCols()
		if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo();
	}
}

function ss_showSlidingTableCols200() {
	setTimeout("ss_showSlidingTableCols();", 200)
}

function ss_showSlidingTableCols() {
	ss_sTableMarginLeft = parseInt(self.document.getElementById(ss_slidingTableId_2).style.marginRight)
	ss_sTableMarginRight =  parseInt(self.document.getElementById(ss_slidingTableId_2).style.marginLeft)
	ss_sTableMarginTop = parseInt(self.document.getElementById(ss_slidingTableId_2).style.marginTop)
	ss_sTableMarginBottom =  parseInt(self.document.getElementById(ss_slidingTableId_2).style.marginBottom)
	
	//Calculate the default column positions
    var ss_sTableWidth = ss_getDivWidth(ss_slidingTableId_2)
    if (ss_sTableWidth == 0) ss_sTableWidth = parseInt(ss_defTableWidth);
    var ss_sTableInnerWidth = parseInt(ss_sTableWidth - ss_sTableMarginLeft - ss_sTableMarginRight);
    var maxColLeft = ss_sTableInnerWidth - 8
    var defColWidth = parseInt(ss_sTableInnerWidth / ss_columnCount)
    var deltaW = defColWidth
    var widthTotalPercentage = 0
    var widthTotalPixels = 0
    for (var i = 1; i <= ss_columnCount; i++) {
    	if (ss_colWidths[i]) {
    		var cw = ss_colWidths[i]
    		if (cw.indexOf("%") > 0) {
    			//This is a percentage; add it to the total percent
    			cw = cw.substr(0, cw.indexOf("%"));
    			widthTotalPercentage += parseFloat(cw)
    		} else {
    			//This is a pixel count; add it to the total pixels
    			widthTotalPixels += parseFloat(cw)
    		}
    	} else {
    		//This width was not specified. 1/nth of the table width as a default
    		widthTotalPixels += deltaW
    	}
    }
    //Finally, get the pixels available to divide amongst the columns that use a percentage
    var percentageWidth = parseInt(ss_sTableInnerWidth - widthTotalPixels)
	
    //Now, position the columns (starting at the left inner side of the ss_sTable boundary)
    var top = parseInt(ss_getSlidingTableY(ss_slidingTableId_2) + ss_sTableMarginTop)
    var left = ss_getSlidingTableX(ss_slidingTableId_2) + ss_sTableMarginLeft
    var deltaLeft = 0
    var w = parseInt(ss_sTableInnerWidth - deltaLeft) + "px"
    
    //Set the position of each column (put col0 at the left edge, 
    //  then position the others successively to the right)
	ss_position_sTableCol("col0", left, top, w)
    for (var i = 1; i <= ss_columnCount; i++) {
	    //See if the user re-positioned the columns. If so, use those settings.
	    if (ss_colWidthsUser[i]) {
	    	deltaLeft = ss_colWidthsUser[i] + "";
     		if (deltaLeft.indexOf("%") > 0) {
    			//This is a percentage; 
    			deltaLeft = deltaLeft.substr(0, deltaLeft.indexOf("%"));
    			deltaLeft = parseFloat((deltaLeft * ss_sTableInnerWidth) / 100);
    		}
    		deltaLeft = parseInt(deltaLeft);
	    }
    	//But, always start the first column at the left edge
    	if (i == 1) deltaLeft = 0;
	    
	    //The column cannot start to the left of the table boundary or be wider than the table
	    //Also leave enough room at the right edge of each column so it can be grabbed
	    var maxColDelta = parseInt((ss_columnCount - i) * 8);
	    var maxColLeftAdjusted = parseInt(maxColLeft - maxColDelta)
	    if (deltaLeft < 0) deltaLeft = 0
	    if (deltaLeft > maxColLeftAdjusted) {
	    	deltaLeft = maxColLeftAdjusted
	    	if (ss_colWidthsUser[i]) {
	    		//Save the adjusted value
	    		//ss_colWidthsUser[i] = deltaLeft
	    	}
	    }

    	w = parseInt(ss_sTableInnerWidth - deltaLeft) + "px"
    	if (parseInt(w) < 0) w = defColWidth + "px";
    	
	    //Position the column inside the inner boundaries of ss_ssTable
	    ss_position_sTableCol("col"+i, parseInt(left + deltaLeft), top, w)
    	ss_showHideObj("col"+i, "visible", "block")
	    
	    //Now, get the position of the next column (using the default width of the current column)
	    //  This may get overridden (above) if the user has re-positioned the columns
	    if (!ss_colWidthsUser[i]) {
	    	if (ss_colWidths[i]) {
	    		var cw = ss_colWidths[i]
	    		if (cw.indexOf("%") > 0) {
	    			cw = cw.substr(0, cw.indexOf("%"));
	    			//Get the width by taking a percentage of the available pixels 
	    			var dw = parseFloat(percentageWidth * parseFloat(cw) / widthTotalPercentage)
	     			deltaLeft = deltaLeft + dw
	    		} else {
	    			deltaLeft = deltaLeft + parseFloat(cw)
	    		}
	    	} else {
	     		deltaLeft = deltaLeft + deltaW
	    	}
	    }
    }
}

function ss_hideSlidingTableCols200() {
	setTimeout("ss_hideSlidingTableCols();", 200)
}

function ss_hideSlidingTableCols() {
    for (var i = 1; i <= ss_columnCount; i++) {
    	ss_showHideObj("col"+i, "hidden", "none")
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
var ss_slidingTableMouseOverInfoDisabled = false;

function ss_clearMouseOverInfo(obj) {
	if (!obj || obj == null) {
		ss_slidingTableMosueOverObj = null;
		ss_showHideObj("ss_info_popup", "hidden", "none")
	} else if (obj != ss_slidingTableMosueOverObj) {
		ss_slidingTableMosueOverObj = obj
 		ss_showHideObj("ss_info_popup", "hidden", "none")
	}
}

function ss_saveSlidingTableCoords() {
	ss_setupStatusMessageDiv()
    var s = ""
    var ss_sTableLeft = ss_getDivLeft(ss_slidingTableId_2);
    var tableWidth = parseFloat(ss_getDivWidth(ss_slidingTableId_2) - ss_sTableMarginLeft - ss_sTableMarginRight);
    for (var i = 0; i <= ss_columnCount; i++) {
    	var colLeft = parseFloat(parseFloat(ss_getDivLeft("col"+i)) - ss_sTableLeft - ss_sTableMarginLeft)
    	if (colLeft < 0) colLeft = 0;
    	var percentage = parseFloat((colLeft * 100) / tableWidth);
	    s += percentage+"% "
	    ss_colWidthsUser[i] = percentage+"%"
    }

    ss_debug('Save col widths: ' + s)
    self.document.forms['ss_columnPositionForm'].column_positions.value = s;
	var url = ss_saveColumnPositionsUrl;
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
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
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	} else {
		ss_showSlidingTableCols200()
	}
}

ss_createOnLoadObj('ss_showSlidingTableCols', ss_showSlidingTableCols200);
ss_createOnResizeObj('ss_showSlidingTableCols', ss_showSlidingTableCols200);
ss_createOnLayoutChangeObj('ss_checkSlidingTableLayout', ss_checkSlidingTableLayout200);
