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
function ss_checkSlidingTableLayout() {
	if (ss_getDivTop(ss_slidingTableId_2) != ss_sTableLastTop || ss_getDivLeft(ss_slidingTableId_2) != ss_sTableLastLeft) {
		//The layout changed, go reposition things
		ss_sTableLastTop = ss_getDivTop(ss_slidingTableId_2)
		ss_sTableLastLeft = ss_getDivLeft(ss_slidingTableId_2)
		ss_showSlidingTableCols()
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
    var top = parseInt(ss_getDivTop(ss_slidingTableId_2) + ss_sTableMarginTop)
    var left = ss_getDivLeft(ss_slidingTableId_2) + ss_sTableMarginLeft
    var deltaLeft = 0
    var w = parseInt(ss_sTableInnerWidth - deltaLeft) + "px"
    
    //Set the position of each column (put col0 at the left edge, 
    //  then position the others successively to the right)
	ss_position_sTableCol("col0", left, top, w)
    for (var i = 1; i <= ss_columnCount; i++) {
	    //See if the user re-positioned the columns. If so, use those settings.
	    if (ss_colWidthsUser[i]) {
	    	deltaLeft = parseInt(ss_colWidthsUser[i])
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
	    		ss_colWidthsUser[i] = deltaLeft
	    	}
	    }

    	w = parseInt(ss_sTableInnerWidth - deltaLeft) + "px"
    	if (parseInt(w) < 0) w = defColWidth + "px";
    	
	    //Position the column inside the inner boundaries of ss_ssTable
	    ss_position_sTableCol("col"+i, parseInt(left + deltaLeft), top, w)
    	ss_showHideObj("col"+i, "visible", "inline")
	    
	    //Now, get the position of the next column (using the default width of the current column)
	    //  This may get overridden (above) if the user has re-positioned the columns
	    if (!ss_colWidthsUser[i]) {
	    	if (ss_colWidths[i]) {
	    		var cw = ss_colWidths[i]
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
    var divObj = self.document.getElementById(divName)
    if (divObj.style.position == "absolute") ss_moveDivToBody(divName);
    var ss_sTableHeight = ss_getDivHeight(ss_slidingTableId_2)
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
    var ss_sTableLeft = ss_getDivLeft(ss_slidingTableId_2)
    var ss_sTableWidth = ss_getDivWidth(ss_slidingTableId_2)
    if (ss_sTableWidth == 0) ss_sTableWidth = parseInt(ss_defTableWidth);
    var ss_sTableInnerWidth = parseInt(ss_sTableWidth - ss_sTableMarginLeft - ss_sTableMarginRight);
    var ss_sTableHeight = ss_getDivHeight(ss_slidingTableId_2)
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
        if (parseInt(dObjLeft) > parseInt(ss_sTableLeft + ss_sTableMarginLeft + ss_sTableInnerWidth - 8)) {
        	dObjLeft = parseInt(ss_sTableLeft + ss_sTableMarginLeft + ss_sTableInnerWidth - 8)
        }
        ss_slidingTableDragObj.style.left = dObjLeft
        dObjLeft = parseInt(dObjLeft)
        var dObjClipWidth = parseInt(ss_sTableLeft + ss_sTableMarginLeft + ss_sTableInnerWidth - dObjLeft)
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
var ss_slidingTableMouseOverInfoDisabled = false;
function ss_showMouseOverInfo(obj) {
	if (ss_slidingTableMouseOverInfoDisabled) return;
	
	if (obj != ss_slidingTableMosueOverObj) {
		ss_moveObjectToBody(document.getElementById('ss_info_popup'))
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
		    	ss_showHideObj("ss_info_popup", "hidden", "none");
				var s = "<table cellspacing='0' cellpadding='0' style='border: solid black 1px;'>"
				s += "<tr><td class='" + obj.className + "' nowrap>"
				s += obj.innerHTML
				s += "&nbsp;&nbsp;&nbsp;</td></tr></table>"
				ss_setDivHtml("ss_info_popup", s)
				if (self.document.getElementById("ss_info_popup")) {
					var x = parseInt(obj.offsetParent.offsetParent.style.left) - 1
					var y = parseInt(parseInt(obj.offsetTop) + parseInt(obj.offsetParent.offsetTop) + parseInt(obj.offsetParent.offsetParent.style.top)) - 1
					self.document.getElementById("ss_info_popup").style.left = x + "px"
					self.document.getElementById("ss_info_popup").style.top = y + "px"
					ss_showHideObj("ss_info_popup", "visible", "block")
					
					//See if this is a new maximum width
					ss_moveObjectToBody(document.getElementById('ss_info_popup_sizer'))
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
	if (obj != ss_slidingTableMosueOverObj) {
		ss_slidingTableMosueOverObj = obj
		ss_showHideObj("ss_info_popup", "hidden", "none")
	}
}

function ss_saveSlidingTableCoords() {
	ss_setupStatusMessageDiv()
    var s = ""
    var ss_sTableLeft = ss_getDivLeft(ss_slidingTableId_2)
    for (var i = 0; i <= ss_columnCount; i++) {
    	var colLeft = parseInt(parseInt(self.document.getElementById("col"+i).style.left) - ss_sTableLeft - ss_sTableMarginLeft)
	    s += colLeft+" "
	    ss_colWidthsUser[i] = colLeft
    }
    self.document.forms['ss_columnPositionForm'].column_positions.value = s;
	var url = ss_saveColumnPositionsUrl;
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
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	} else {
		ss_showSlidingTableCols200()
	}
}

ss_createOnLoadObj('ss_showSlidingTableCols', ss_showSlidingTableCols200);
ss_createOnResizeObj('ss_showSlidingTableCols', ss_showSlidingTableCols);
ss_createOnLayoutChangeObj('ss_checkSlidingTableLayout', ss_checkSlidingTableLayout);
ss_createEventObj('ss_slidingTableDrag', 'MOUSEMOVE');
