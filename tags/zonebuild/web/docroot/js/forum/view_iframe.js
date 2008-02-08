/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
//Routines that support the iframe folder style

var ss_minEntryWindowWidth = 300;
var ss_minEntryWindowHeight = 300;
var ss_entryWindowHeight = ss_minEntryWindowHeight;
var ss_scrollbarWidth = 30;
var ss_entryDivTopDelta = 25;
var ss_entryClickPositionDelta = 15;
var ss_entryDivBottomDelta = 50;
var ss_scrollTopOffset = 4;
var ss_nextUrl = ""
var ss_entryHeightHighWaterMark = 0
var ss_entryLastScrollTop = 0
	//ss_debug("init: "+ss_entryWindowLeft)

function ss_setEntryDivHeight() {
	setTimeout("ss_positionEntryDiv();", 100);
}
function ss_showForumEntryInIframe(url) {
	if (self.parent && typeof self.parent.ss_showForumEntryInIframe != "undefined") {
		self.parent.ss_showForumEntryInIframe(url);
		return
	}
		
	//ss_debug('show url in frame = '+url)
	ss_positionEntryDiv();
    var wObj = self.document.getElementById('ss_showentryframe')
    var wObj1 = self.document.getElementById('ss_showentrydiv')
	if (wObj1 == null) return true;
	
    ss_hideSpannedAreas();
    wObj1.style.display = "block";
    wObj1.style.zIndex = ssEntryZ;
    wObj1.style.visibility = "visible";
    //wObj.style.height = parseInt(wObj1.style.height) - ss_entryDivBottomDelta + "px";

    if (wObj.src && wObj.src == url) {
    	ss_nextUrl = url
    	wObj.src = ss_forumRefreshUrl;
    } else if (wObj.src && wObj.src == ss_forumRefreshUrl && ss_nextUrl == url) {
    	wObj.src = ss_forumRefreshUrl;
    } else {
    	wObj.src = url
    }

	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();

    return false;
}
// If you can't control the box model, you may need to set this to around 40.
var ss_scrollHeightFudge = 0;
function ss_positionEntryDiv() {
	//ss_debug("ss_positionEntryDiv: "+ss_entryWindowLeft)
	var maxEntryWidth = parseInt(ss_getWindowWidth() - ss_scrollbarWidth);
	
    var wObj = self.document.getElementById('ss_showfolder')
    if (wObj == null) {
    	wObj = document.getElementsByTagName("body").item(0);
    }
    var width = ss_getObjectWidth(wObj);
    if (ss_entryWindowWidth == 0) {ss_entryWindowWidth = parseInt((width * 3) / 4);}
    //Make sure the entry width is within the window
    if (ss_entryWindowWidth > maxEntryWidth) ss_entryWindowWidth = maxEntryWidth;
    if (ss_entryWindowWidth < ss_minEntryWindowWidth) ss_entryWindowWidth = ss_minEntryWindowWidth;
    if (ss_entryWindowHeight < ss_minEntryWindowHeight) ss_entryWindowHeight = ss_minEntryWindowHeight;

    var wObj1 = self.document.getElementById('ss_showentrydiv')
    if (wObj1 == null) return;
    ss_moveObjectToBody(wObj1)
    var wObj2 = self.document.getElementById(ss_iframe_box_div_name)
    var wObj3 = self.document.getElementById('ss_showentryframe')

    if (ss_entryWindowTop <= 0 || ss_entryWindowLeft <= 0) {
    	//ss_debug("initial setting of top and left " + ss_entryWindowWidth)
    	ss_entryWindowTop = parseInt(ss_getDivTop('ss_showfolder') + ss_entryDivTopDelta);
    	ss_entryWindowLeft = parseInt(maxEntryWidth - ss_entryWindowWidth);
    }
	if (ss_entryWindowTop < parseInt(ss_getScrollXY()[1])) {
		ss_entryWindowTop = parseInt(ss_getScrollXY()[1] + ss_scrollTopOffset);
	} else if (ss_entryWindowTop > parseInt(parseInt(ss_getScrollXY()[1]) + parseInt(ss_getWindowHeight()) - ss_scrollbarWidth)) {
		ss_entryWindowTop = ss_entryWindowTopOriginal;
		if (ss_entryWindowTop < parseInt(ss_getScrollXY()[1])) {
			ss_entryWindowTop = parseInt(ss_getScrollXY()[1] + ss_scrollTopOffset);
		} else if (ss_entryWindowTop > parseInt(parseInt(ss_getScrollXY()[1]) + parseInt(ss_getWindowHeight()) - ss_scrollbarWidth)) {
			ss_entryWindowTop = parseInt(ss_getScrollXY()[1] + ss_scrollTopOffset);
		}
	}
    if (ss_entryWindowLeft < 0) ss_entryWindowLeft = 0;

    ss_setObjectTop(wObj1, ss_entryWindowTop)
    ss_setObjectLeft(wObj1, ss_entryWindowLeft);
    ss_setObjectWidth(wObj1, ss_entryWindowWidth);
    ss_setObjectWidth(wObj2, ss_entryWindowWidth);
    //ss_setObjectWidth(wObj3, ss_entryWindowWidth);
    
    //Trying to set the property to "inherit" or something that is not a actual color values
    //causes JS to fail on IE so we are setting the background to a empty value, for the 
    //parent property to be inherited by the child.
    //wObj1.style.background = ss_entryBackgroundColor;
    wObj1.style.background = "";
    wObj1.style.visibility = "visible";

    //Allow the entry section to grow to as large as needed to show the entry
	try {
		if (window.ss_showentryframe && window.ss_showentryframe.document && 
				window.ss_showentryframe.document.body) {
		    var entryHeight = parseInt(window.ss_showentryframe.document.body.scrollHeight) + ss_scrollHeightFudge
		    if (entryHeight < ss_minEntryWindowHeight) entryHeight = ss_minEntryWindowHeight;
		    if (entryHeight > (ss_entryHeightHighWaterMark + ss_scrollHeightFudge)) {
			    //Only expand the height. Never shrink it. Otherwise the screen jumps around.
			    ss_entryHeightHighWaterMark = entryHeight;
			    
				ss_setObjectHeight(wObj1, entryHeight);
				ss_setObjectHeight(wObj3, entryHeight);
			}
			if (!ss_draggingDiv &&  ss_getScrollXY()[1] < ss_entryLastScrollTop) {
				//See if the entry runs off the bottom of the screen and should be moved up some
				if (ss_entryWindowTop + entryHeight > parseInt(ss_getScrollXY()[1]) + parseInt(ss_getWindowHeight())) {
					//Try to move the div up so it can be completely seen
					ss_entryWindowTop = parseInt(ss_getScrollXY()[1]) + parseInt(ss_getWindowHeight()) - entryHeight - ss_entryDivTopDelta - ss_entryDivBottomDelta;
					//But not higher than where the last click was
					if (ss_entryWindowTop < ss_getClickPositionY() + ss_entryClickPositionDelta) 
						ss_entryWindowTop = ss_getClickPositionY() + ss_entryClickPositionDelta;
				}
				if (ss_entryWindowTop < parseInt(ss_getScrollXY()[1] + ss_scrollTopOffset)) {
					ss_entryWindowTop = parseInt(ss_getScrollXY()[1] + ss_scrollTopOffset);
				}
				if (ss_entryWindowTop < parseInt(ss_getDivTop('ss_showfolder') + ss_entryDivTopDelta)) {
					//If it got moved too high, reset it to the minimum
					ss_entryWindowTop = parseInt(ss_getDivTop('ss_showfolder') + ss_entryDivTopDelta)
				}
	    		ss_setObjectTop(wObj1, ss_entryWindowTop)
	    	}
		}
	} catch(e) {}
	ss_entryLastScrollTop = ss_getScrollXY()[1];
}

function ss_hideEntryDiv() {
    var wObj1 = self.document.getElementById('ss_showentrydiv')
    if (wObj1 != null) {
    	wObj1.style.visibility = "hidden";
    	wObj1.style.display = "none";
    }
    ss_showSpannedAreas();
}

function ss_repositionEntryDiv() {
    //ss_debug('reposition div')
    var wObj1 = self.document.getElementById('ss_showentrydiv')
    if (wObj1 != null && wObj1.style.visibility == "visible") {
    	//The entry div is visible, so reposition it to the new size
    	ss_positionEntryDiv();
    }
}

//ss_createOnLoadObj('ss_positionEntryDiv', ss_positionEntryDiv)
ss_createOnResizeObj('ss_repositionEntryDiv', ss_repositionEntryDiv)

var ss_divDragObj = null
var ss_divOffsetX
var ss_divOffsetY

var ss_startingToDragDiv = null;
var ss_draggingDiv = false;
var ss_divDragMoveType = '';
var ss_divDragSavedMouseMove = '';
var ss_divDragSavedMouseUp = '';
var ss_divDragSavedMouseOut = '';

function ss_startDragDiv(type) {
	ss_debug('start drag')
	if (ss_draggingDiv) return;
	ss_divDragMoveType = type;
	if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo(null);

	ss_divDragObj = document.getElementById('ss_showentrydiv')
    if (ss_isNSN || ss_isNSN6 || ss_isMoz5) {
    } else {
        ss_divOffsetX = window.event.offsetX
        ss_divOffsetY = window.event.offsetY
        //Search the parent tree looking for 'ss_showentrydiv', summing the offsets along the way
        var offsetParent = window.event.srcElement.offsetParent
        while (offsetParent != null) {
        	if (offsetParent.id == 'ss_showentrydiv') break;
        	ss_divOffsetX += parseInt(offsetParent.offsetLeft)
        	offsetParent = offsetParent.offsetParent;
        }
    }
	
    ss_startingToDragDiv = 1;
    if (self.document.onmousemove) ss_divDragSavedMouseMove = self.document.onmousemove;
    if (self.document.onmouseup) ss_divDragSavedMouseUp = self.document.onmouseup;
    self.document.onmousemove = ss_divDrag
    self.document.onmouseup = ss_divStopDrag
    if (ss_divDragMoveType == 'move') {
    	if (self.document.onmouseout) ss_divDragSavedMouseOut = self.document.onmouseout;
    	self.document.onmouseout = ss_divDrag
    }
    ss_draggingDiv = true;

    return false
}

function ss_divDrag(evt) {
    if (!evt) evt = window.event;
    if (ss_divDragObj) {
        if (ss_startingToDragDiv == 1) {
            if (evt.layerX) {
                if (ss_isNSN || ss_isNSN6 || ss_isMoz5) {
                    ss_divOffsetX = evt.layerX;
                    ss_divOffsetY = evt.layerY;
                }
            }
            ss_startingToDragDiv = 0

			var lightBox = document.getElementById('ss_entry_light_box')
			if (!lightBox) {
				var bodyObj = document.getElementsByTagName("body").item(0)
				lightBox = document.createElement("div");
		        lightBox.setAttribute("id", "ss_entry_light_box");
		        lightBox.style.position = "absolute";
		        bodyObj.appendChild(lightBox);
			}
			lightBox.style.backgroundColor = "#ffffff";
			dojo.html.setOpacity(lightBox, .1);
		    lightBox.onclick = "ss_entryClearDrag();";
		    lightBox.style.display = "block";
		    lightBox.style.top = 0 + "px";
		    lightBox.style.left = 0 + "px";
		    lightBox.style.width = ss_getBodyWidth() + "px";
		    lightBox.style.height = ss_getBodyHeight()  + "px";
		    lightBox.style.zIndex = parseInt(ssDragEntryZ - 1);
		    lightBox.style.visibility = "visible";			
        }
        
        var dObjLeft
        var dObjTop
        if (ss_isNSN || ss_isNSN6 || ss_isMoz5) {
            dObjLeft = evt.pageX - ss_divOffsetX;
            dObjTop = evt.pageY - ss_divOffsetY;
        } else {
            dObjLeft = evt.clientX - ss_divOffsetX;
            dObjTop = evt.clientY - ss_divOffsetY;
    		//IE requires fix-up if window is scrolled
    		dObjTop += parseInt(ss_getScrollXY()[1])
    		dObjLeft += parseInt(ss_getScrollXY()[0])
        }
        ss_debug('left = ' + dObjLeft + ', top = '+dObjTop)
        if (dObjLeft <= 0) dObjLeft = 1;
        if (dObjTop <= 0) dObjTop = 1;
        if (ss_divDragMoveType == 'resize') {
	        var deltaW = parseInt(parseInt(dObjLeft) - parseInt(ss_divDragObj.style.left))
	        ss_entryWindowWidth = parseInt(parseInt(ss_divDragObj.style.width) - deltaW)
	        if (ss_entryWindowWidth >= ss_minEntryWindowWidth) {
		        ss_entryWindowLeft = dObjLeft;
		        ss_divDragObj.style.left = dObjLeft + "px";
		        ss_positionEntryDiv()
		    }
		} else if (ss_divDragMoveType == 'move') {
	        ss_entryWindowTop = dObjTop;
	        ss_entryWindowLeft = dObjLeft;
	        ss_divDragObj.style.left = dObjLeft  + "px";
	        ss_divDragObj.style.top = dObjTop  + "px";
	        ss_positionEntryDiv()
		}
	    var lightBox = document.getElementById('ss_entry_light_box')
    	//lightBox.style.width = ss_getBodyWidth()  + "px";
    	//lightBox.style.height = ss_getBodyHeight()  + "px";
	    
        return false
    
    } else {
        return true
    }
}

function ss_divStopDrag(evt) {
    if (!evt) evt = window.event;
    if (ss_divDragObj) {
        ss_divDragObj = null
    }
    self.document.onmousemove = ss_divDragSavedMouseMove;
    self.document.onmouseup = ss_divDragSavedMouseUp;
    if (ss_divDragMoveType == 'move') {
    	self.document.onmouseout = ss_divDragSavedMouseOut;
    }
    setTimeout("ss_entryClearDrag();",100);
    ss_draggingDiv = false;
    ss_entryWindowTopOriginal = ss_entryWindowTop;
    setTimeout("ss_saveEntryWidth(ss_entryWindowWidth, ss_entryWindowTop, ss_entryWindowLeft);", 500)
    return false
}

function ss_entryClearDrag() {
	//ss_debug('clear drag')
	//ss_moveDivToBody('ss_showentrydiv')
	var lightBox = document.getElementById('ss_entry_light_box')
	if (lightBox != null) {
		//ss_debug('remove lightbox')
		dojo.html.setOpacity(lightBox, 1);
		lightBox.style.visibility = "hidden"
	}
}

var ss_lastEntryWidth = -1;
var ss_lastEntryTop = -1;
var ss_lastEntryLeft = -1;
function ss_saveEntryWidth(entryWidth, entryTop, entryLeft) {
	ss_setupStatusMessageDiv()
	if (entryWidth == ss_lastEntryWidth && entryTop == ss_lastEntryTop && entryLeft == ss_lastEntryLeft) return;
	//ss_debug(entryWidth+', '+ss_lastEntryWidth+', '+entryTop+', '+ss_lastEntryTop+', '+entryLeft+', '+ss_lastEntryLeft) 
	ss_lastEntryWidth = entryWidth;
	ss_lastEntryTop = entryTop;
	ss_lastEntryLeft = entryLeft;
	formObj = document.getElementById('ss_saveEntryWidthForm');
    formObj.entry_width.value = entryWidth;
    formObj.entry_top.value = entryTop;
    formObj.entry_left.value = entryLeft;
 	    
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"save_entry_width"});
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addFormElements("ss_saveEntryWidthForm")
	//ajaxRequest.setEchoDebugInfo();
	//ajaxRequest.setPreRequest(ss_preRequest);
	ajaxRequest.setPostRequest(ss_postEntryWidthRequest);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_preRequest(obj) {
	//alert('preRequest: ' + obj.getQueryString());
}
function ss_postEntryWidthRequest(obj) {
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	}
}

