//Javascript routines for viewing vertical style folders

var ss_folderDivHeight = 400;
var ss_bottomHeight = 100;
var ss_minFolderDivHeight = 100;
var ss_minEntryDivHeight = 100;
var ss_scrollTopOffset = 4;
var ss_scrollbarHeight = 20;
var ss_positioningEntryDiv = 0;
var ss_marginLeft = 2
var ss_marginRight = 2
var ss_entryHeightHighWaterMark = 0

function ss_setEntryDivHeight() {
	try {
		if (window.ss_showentryframe && window.ss_showentryframe.document && 
				window.ss_showentryframe.document.body) {
		    var wObj3 = self.document.getElementById('ss_showentryframe')
			if (ss_minEntryDivHeight > ss_entryHeightHighWaterMark) {
				ss_entryHeightHighWaterMark = ss_minEntryDivHeight;
				ss_setObjectHeight(wObj3, ss_minEntryDivHeight);
			}
			setTimeout("ss_positionEntryDiv();", 100);
		}
	} catch(e) {
		ss_debug('ss_setEntryDivHeight failed: ' + e.message)
	}
}

function ss_positionEntryDiv() {
	ss_positioningEntryDiv = 1
	ss_showEntryDiv()

    var wObj = self.document.getElementById('ss_showfolder')
    var wObjB = self.document.getElementById('ss_showfolder_bottom')
    var wObj1 = self.document.getElementById('ss_showentrydiv')
    var wObj2 = self.document.getElementById(ss_iframe_box_div_name)
    var wObj3 = self.document.getElementById('ss_showentryframe')
        
    var width = parseInt(parseInt(ss_getObjectWidth(wObj)) - ss_marginLeft - ss_marginRight);
	ss_setObjectWidth(wObj1, width);
	ss_setObjectWidth(wObj2, width);

    //Allow the entry section to grow to as large as needed to show the entry
    try {
		if (window.ss_showentryframe && window.ss_showentryframe.document && 
				window.ss_showentryframe.document.body) {
		    var entryHeight = parseInt(window.ss_showentryframe.document.body.scrollHeight)
		    ss_debug('entryHeight = '+entryHeight)
		    if (entryHeight < ss_minEntryDivHeight) entryHeight = ss_minEntryDivHeight;
		    if (entryHeight > ss_entryHeightHighWaterMark) {
			    //Only expand the height. Never shrink it. Otherwise the screen jumps around.
			    ss_entryHeightHighWaterMark = entryHeight;
				ss_setObjectHeight(wObj1, entryHeight);
			}
		    ss_debug(' set entryHeight = '+entryHeight)
			ss_setObjectHeight(wObj3, entryHeight);
		}
	} catch(e) {
		ss_debug('ss_setEntryDivHeight failed: ' + e.message)
	}
	
	ss_positioningEntryDiv = 0
}

var ss_savedSliderClassName = ""
var ss_savedSliderBorder = ""
function ss_showEntryDiv() {
    var wObj1 = self.document.getElementById('ss_showentrydiv')
    wObj1.style.visibility = "visible";
    wObj1.style.display = "block";
}

function ss_checkLayoutChange() {
	//Reposition entry div, but only if not in the process of doing it
	if (ss_positioningEntryDiv != 1) {
		ss_positionEntryDiv();
	}
}

ss_createOnLoadObj("ss_positionEntryDiv", ss_positionEntryDiv);
ss_createOnResizeObj('ss_positionEntryDiv', ss_positionEntryDiv);
ss_createOnLayoutChangeObj('ss_checkLayoutChange', ss_checkLayoutChange);

function ss_showForumEntryInIframe(url) {
	if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo(null);
	ss_positionEntryDiv();
    var wObj = self.document.getElementById('ss_showentryframe')

	if (ss_minEntryDivHeight > ss_entryHeightHighWaterMark) {
		ss_setObjectHeight(wObj, ss_minEntryDivHeight);
		ss_entryHeightHighWaterMark = ss_minEntryDivHeight;
	}

    if (wObj.src && wObj.src == url) {
    	wObj.src = "_blank";
    }
    wObj.src = url

    return false;
}

var ss_divDragObj = null
var ss_divOffsetX
var ss_divOffsetY

var ss_startingToDragDiv = null;
var ss_draggingDragDiv = false;
var ss_divDragSavedMouseMove = '';
var ss_divDragSavedMouseUp = '';
function ss_startDragDiv(evt) {
    //ss_debug('start drag')
	if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo(null);
	ss_divDragObj = document.getElementById('ss_showfolder_slider')
	ss_divDragObj.style.zIndex = parseInt(ssDragEntryZ + 1);

	var lightBox = document.getElementById('ss_entry_light_box')
	if (!lightBox) {
		var bodyObj = document.getElementsByTagName("body").item(0)
		lightBox = document.createElement("div");
        lightBox.setAttribute("id", "ss_entry_light_box");
        lightBox.style.position = "absolute";
        bodyObj.appendChild(lightBox);
	}
	lightBox.style.backgroundColor = "#ffffff";
	dojo.style.setOpacity(lightBox, .1);
    lightBox.onclick = "ss_divStopDrag();";
    lightBox.style.top = 0;
    lightBox.style.left = 0;
    lightBox.style.width = parseInt(ss_getBodyWidth()) + 'px';
    lightBox.style.height = parseInt(ss_getBodyHeight()) + 'px';
    lightBox.style.display = "block";
    lightBox.style.zIndex = ssDragEntryZ;
    lightBox.style.visibility = "visible";			

	ss_divDragObj = document.getElementById('ss_showfolder_slider');
	
    if (isNSN || isNSN6 || isMoz5) {
    } else {
        ss_divOffsetX = window.event.offsetX
        ss_divOffsetY = window.event.offsetY
    }
    ss_startingToDragDiv = 1;
    if (self.document.onmousemove != '' && ss_divDragSavedMouseMove == '') 
    		ss_divDragSavedMouseMove = self.document.onmousemove;
    if (self.document.onmouseup != '' && ss_divDragSavedMouseUp == '') 
    		ss_divDragSavedMouseUp = self.document.onmouseup;
    self.document.onmousemove = ss_divDrag
    self.document.onmouseup = ss_divStopDrag

	if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo(null);
	ss_slidingTableMouseOverInfoDisabled = true;
	ss_draggingDragDiv = true;
		
    return false
}

function ss_divDrag(evt) {
    if (!evt) evt = window.event;
    if (ss_draggingDragDiv && ss_divDragObj) {
        if (ss_startingToDragDiv == 1) {
            if (evt.layerX) {
                if (isNSN || isNSN6 || isMoz5) {
                    ss_divOffsetX = evt.layerX;
                    ss_divOffsetY = evt.layerY;
                }
            }
            ss_startingToDragDiv = 0
        }
        var dObjLeft
        var dObjTop
        if (isNSN || isNSN6 || isMoz5) {
            dObjLeft = evt.pageX - ss_divOffsetX;
            dObjTop = evt.pageY - ss_divOffsetY;
        } else {
            dObjLeft = evt.clientX - ss_divOffsetX;
            dObjTop = evt.clientY - ss_divOffsetY;
    		//IE requires fix-up if wndow is scrolled
    		dObjTop += parseInt(self.document.body.scrollTop)
        }

		//Set the new height of the folder table
		var tableDivObj = document.getElementById(ss_folderTableId)
		var marginOffset = parseInt(parseInt(tableDivObj.style.marginTop) + 
		        parseInt(tableDivObj.style.marginBottom))
		if (parseInt(tableDivObj.style.borderTopWidth)) 
			marginOffset += parseInt(tableDivObj.style.borderTopWidth);
		if (parseInt(tableDivObj.style.borderTopWidth)) 
			marginOffset += parseInt(tableDivObj.style.borderBottomWidth);
	    //ss_debug('marginOffset='+marginOffset)
	    ss_folderDivHeight = parseInt(parseInt(dObjTop) - marginOffset + 
	    		ss_scrollbarHeight -
	    		parseInt(ss_getDivTop(ss_folderTableId)));
	    if (ss_folderDivHeight < 0) {
	    	//The initialization of the event was bad. Just stop the drag.
	    	//ss_debug('Bad ss_folderDivHeight = ' + ss_folderDivHeight)
	        //ss_debug('  dObjTop = '+dObjTop+', ss_getDivTop(ss_folderTableId) = '+ss_getDivTop(ss_folderTableId) +', ss_divOffsetY='+ss_divOffsetY)
	    	setTimeout('ss_divStopDrag()', 100);
	    	return false;
	    }
	    if (ss_folderDivHeight < ss_minFolderDivHeight) {
	        ss_folderDivHeight = ss_minFolderDivHeight;
	    }
	    ss_setObjectHeight(tableDivObj, ss_folderDivHeight);
	    
	    var lightBox = document.getElementById('ss_entry_light_box')
    	lightBox.style.width = parseInt(ss_getBodyWidth()) + 'px';
    	lightBox.style.height = parseInt(ss_getBodyHeight()) + 'px';
	    
        return false
    
    } else {
        return true
    }
}

function ss_divStopDrag(evt) {
	//ss_debug('stop drag')
	ss_startingToDragDiv = 0;
    if (!evt && window.event) evt = window.event;
    if (ss_divDragObj) {
        ss_slidingTableMouseOverInfoDisabled = false;;

	    self.document.onmousemove = ss_divDragSavedMouseMove;
	    self.document.onmouseup = ss_divDragSavedMouseUp;
        
        ss_draggingDragDiv = false;
        
		//Signal that the layout changed
		if (ssf_onLayoutChange) ssf_onLayoutChange();
    	
    	setTimeout("ss_entryClearDrag();",100);
    	
	    setTimeout("ss_saveEntryHeight(ss_folderDivHeight);", 500)
    }
    return false
}

function ss_entryClearDrag() {
	//ss_debug('ss_entryClearDrag')
	var lightBox = document.getElementById('ss_entry_light_box')
	if (lightBox != null) {
		//ss_debug('remove lightbox')
		dojo.style.setOpacity(lightBox, 1);
		lightBox.style.visibility = "hidden"
	}
	ss_slidingTableMouseOverInfoDisabled = false;
	self.document.onmousemove = ss_divDragSavedMouseMove;
	self.document.onmouseup = ss_divDragSavedMouseUp;
}

var ss_lastEntryHeight = -1;
function ss_saveEntryHeight(entryHeight) {
	ss_setupStatusMessageDiv()
	if (entryHeight == ss_lastEntryHeight) return;
	ss_lastEntryHeight = entryHeight;
    self.document.forms['ss_saveEntryHeightForm'].entry_height.value = entryHeight;
	var url = ss_saveEntryHeightUrl;
	var ajaxRequest = new AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addFormElements("ss_saveEntryHeightForm")
	ajaxRequest.setPostRequest(ss_postEntryHeightRequest);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_postEntryHeightRequest(obj) {
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	}
}
