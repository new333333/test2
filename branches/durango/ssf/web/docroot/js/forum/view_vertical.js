/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
//Javascript routines for viewing vertical style folders

var ss_folderDivHeight = 300;
var ss_folderDivHeightLast = 300;
var ss_folderDivHeightMinMove = 2;
var ss_bottomHeight = 100;
var ss_minFolderDivHeight = 100;
var ss_minEntryDivHeight = 100;
var ss_scrollTopOffset = 4;
var ss_scrollbarHeight = 20;
var ss_positioningEntryDiv = 0;
var ss_entryHeightHighWaterMark = 0

function ss_setEntryDivHeight() {
	try {
		if (window.ss_showentryframe && window.ss_showentryframe.document && 
				window.ss_showentryframe.document.body) {
		    var wObj = self.document.getElementById('ss_showentryframe')
			if (ss_minEntryDivHeight > ss_entryHeightHighWaterMark) {
				ss_entryHeightHighWaterMark = ss_minEntryDivHeight;
				ss_setObjectHeight(wObj, ss_minEntryDivHeight);
			}
			setTimeout("ss_positionEntryDiv(true);", 100);
		}
	} catch(e) {
		ss_debug('ss_setEntryDivHeight failed: ' + e.message)
	}
}

function ss_positionEntryDiv(true) {
	ss_positioningEntryDiv = 1
	ss_showEntryDiv()

    var wObj1 = self.document.getElementById('ss_showentrydiv')
    var wObj2 = self.document.getElementById('ss_showentryframe')

    //Allow the entry section to grow to as large as needed to show the entry
    try {
		if (window.ss_showentryframe && window.ss_showentryframe.document && 
				window.ss_showentryframe.document.body) {
		    var entryHeight = parseInt(window.ss_showentryframe.document.body.scrollHeight)
		    if (entryHeight < ss_minEntryDivHeight) entryHeight = ss_minEntryDivHeight;
		    if (entryHeight > ss_entryHeightHighWaterMark) {
			    //Only expand the height. Never shrink it. Otherwise the screen jumps around.
			    ss_entryHeightHighWaterMark = entryHeight;
				ss_setObjectHeight(wObj1, entryHeight);
			}
			ss_setObjectHeight(wObj2, entryHeight);
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
    if (wObj1 != null) {
    	wObj1.style.visibility = "visible";
    	wObj1.style.display = "block";
    	wObj1.focus();
    }
}

function ss_checkLayoutChange() {
	//Reposition entry div, but only if not in the process of doing it
	if (ss_positioningEntryDiv != 1) {
		ss_positionEntryDiv(true);
	}
}

ss_createOnLoadObj("ss_positionEntryDiv", ss_positionEntryDiv);
ss_createOnResizeObj('ss_positionEntryDiv', ss_positionEntryDiv);
ss_createOnLayoutChangeObj('ss_checkLayoutChange', ss_checkLayoutChange);

function ss_showForumEntryInIframe(url) {
	if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo(null);
	ss_positionEntryDiv(true);
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
	ss_setOpacity(lightBox, .1);
    lightBox.onclick = "ss_divStopDrag();";
    lightBox.style.visibility = "hidden";			
    lightBox.style.display = "block";
    //ss_debug('start drag block')
    lightBox.style.top = 0;
    lightBox.style.left = 0;
    lightBox.style.width = parseInt(ss_getBodyWidth()) + 'px';
    lightBox.style.height = parseInt(ss_getBodyHeight()) + 'px';
    lightBox.style.zIndex = ssDragEntryZ;
    lightBox.style.visibility = "visible";			

	ss_divDragObj = document.getElementById('ss_showfolder_slider');
	
    if (ss_isNSN || ss_isNSN6 || ss_isMoz5) {
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
    //ss_debug('dragging')
    if (!evt) evt = window.event;
    if (ss_draggingDragDiv && ss_divDragObj) {
        if (ss_startingToDragDiv == 1) {
            ss_divOffsetX = 0;
            ss_divOffsetY = 0;
            ss_startingToDragDiv = 0
        }
        var dObjLeft
        var dObjTop
        if (ss_isNSN || ss_isNSN6 || ss_isMoz5) {
            dObjLeft = evt.pageX - ss_divOffsetX;
            dObjTop = evt.pageY - ss_divOffsetY;
        } else {
            dObjLeft = evt.clientX - ss_divOffsetX;
            dObjTop = evt.clientY - ss_divOffsetY;
    		//IE requires fix-up if wndow is scrolled
    		dObjTop += parseInt(ss_getScrollXY()[1])
        }

		//Set the new height of the folder table
		var tableDivObj = document.getElementById(ss_folderTableId)
		var marginOffset = parseInt(parseInt(tableDivObj.style.marginTop) + 
		        parseInt(tableDivObj.style.marginBottom))
		if (parseInt(tableDivObj.style.borderTopWidth)) 
			marginOffset += parseInt(tableDivObj.style.borderTopWidth);
		if (parseInt(tableDivObj.style.borderTopWidth)) 
			marginOffset += parseInt(tableDivObj.style.borderBottomWidth);
	    var oldFolderDivHeight = ss_folderDivHeight;
	    ss_folderDivHeight = parseInt(parseInt(dObjTop) - marginOffset + 
	    		ss_scrollbarHeight - parseInt(ss_getDivTop(ss_folderTableId)));
	    if (ss_folderDivHeight < 0) {
	    	//The initialization of the event was bad. Just stop the drag.
	    	//ss_debug('Bad ss_folderDivHeight = ' + ss_folderDivHeight)
	        //ss_debug('  dObjTop = '+dObjTop+', ss_getDivTop(ss_folderTableId) = '+ss_getDivTop(ss_folderTableId) +', ss_divOffsetY='+ss_divOffsetY)
	    	ss_folderDivHeight = oldFolderDivHeight;
		    if (ss_folderDivHeight < ss_minFolderDivHeight) {
		        ss_folderDivHeight = ss_minFolderDivHeight;
		    }
	    	setTimeout('ss_divStopDrag()', 100);
	    	return false;
	    }
	    if (ss_folderDivHeight < ss_minFolderDivHeight) {
	        ss_folderDivHeight = ss_minFolderDivHeight;
	    }
	    var change = parseInt(ss_folderDivHeight - ss_folderDivHeightLast);
	    if ((change > 0 && change > ss_folderDivHeightMinMove) || 
	    		(change < 0 && parseInt(change + ss_folderDivHeightMinMove) < 0)) {
	    	//ss_debug('ss_folderDivHeight = ' +ss_folderDivHeight)
	    	ss_setObjectHeight(tableDivObj, ss_folderDivHeight);
	    	ss_folderDivHeightLast = ss_folderDivHeight;
	    }
	    
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
		var tableDivObj = document.getElementById(ss_folderTableId)
	    if (ss_folderDivHeight < ss_minFolderDivHeight) {
	        ss_folderDivHeight = ss_minFolderDivHeight;
	    }
	    ss_setObjectHeight(tableDivObj, ss_folderDivHeight);
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
		ss_setOpacity(lightBox, 1);
		lightBox.style.visibility = "hidden"
	}
	ss_slidingTableMouseOverInfoDisabled = false;
	self.document.onmousemove = ss_divDragSavedMouseMove;
	self.document.onmouseup = ss_divDragSavedMouseUp;
}

function ss_setFolderDivHeight(height) {
	if (height != null && height != '') ss_folderDivHeight = height;
	var tableDivObj = document.getElementById(ss_folderTableId)
	ss_setObjectHeight(tableDivObj, ss_folderDivHeight);
}

var ss_lastEntryHeight = -1;
function ss_saveEntryHeight(entryHeight) {
	ss_setupStatusMessageDiv()
	if (entryHeight == ss_lastEntryHeight) return;
	ss_lastEntryHeight = entryHeight;
    self.document.forms['ss_saveEntryHeightForm'].entry_height.value = entryHeight;
	var url = ss_saveEntryHeightUrl;
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
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
