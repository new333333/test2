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
//Routines that support the iframe folder style

var ss_minEntryWindowWidth = 300;
var ss_minEntryWindowHeight = 300;
var ss_entryWindowHeight = ss_minEntryWindowHeight;
var ss_scrollbarWidth = 30;
var ss_entryDivTopDelta = 25;
var ss_entryClickPositionDelta = 15;
var ss_entryDivBottomDelta = 50;
var ss_scrollTopOffset = 4;
var ss_nextUrl = "";
var ss_entryHeightHighWaterMarkArray = new Array();
var ss_entryLastScrollTop = 0

var ss_selectedDiv = null;
var ss_selectedInternalDiv = null;
var ss_selectedIframe = null;
var ss_box_iframe_name = null;
var ss_selectedPortletNamespace = null;
var ss_selectedIframeForm = null;

//ss_debug("init: "+ss_entryWindowLeft)

function ss_setEntryDivHeight() {
	setTimeout("ss_positionEntryDiv(true);", 100);
}
function ss_showForumEntryInIframe(url) {
	//ss_debug('show url in frame = '+url)
	ss_positionEntryDiv(true);
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
    wObj.focus();

	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();

    return false;
}

function ss_getDivTopForPortlet(obj) {
    while (1) {
        if (!obj) {break}
        top += parseInt(obj.offsetTop)
        if (obj == obj.offsetParent) {break}
        obj = obj.offsetParent
    }
    
    return parseInt(top);
}

// If you can't control the box model, you may need to set this to around 40.
var ss_scrollHeightFudge = 0;
function ss_positionEntryDiv(moveTop) {
	if (typeof moveTop == 'undefined') moveTop = false;

	//alert("ss_entryWindowTop: " + ss_entryWindowTop + "\n ss_entryWindowLeft: " + ss_entryWindowLeft + "\n ss_entryWindowWidth: "+ss_entryWindowWidth);

	//ss_debug("ss_positionEntryDiv: "+ss_entryWindowLeft)
	var maxEntryWidth = parseInt(ss_getWindowWidth() - ss_scrollbarWidth);
	
    //When entry opens up, we will set it to standard width of 600
    if (ss_entryWindowWidth == 0) {
    	ss_entryWindowWidth = ss_entryWindowWidth = 600;
    }
    
    //Make sure the entry width is within the window
    if (ss_entryWindowWidth > maxEntryWidth) ss_entryWindowWidth = maxEntryWidth;
    if (ss_entryWindowWidth < ss_minEntryWindowWidth) ss_entryWindowWidth = ss_minEntryWindowWidth;
    if (ss_entryWindowHeight < ss_minEntryWindowHeight) ss_entryWindowHeight = ss_minEntryWindowHeight;

    var wObj1 = ss_selectedDiv;
    if (wObj1 == null) return;
    ss_moveObjectToBody(wObj1)
    var wObj2 = ss_selectedInternalDiv;
    var wObj3 = ss_selectedIframe;

    if (ss_entryWindowTop <= 0 || ss_entryWindowLeft <= 0) {
    	//ss_debug("initial setting of top and left " + ss_entryWindowWidth)
    	ss_entryWindowTop = parseInt(ss_getDivTop('ss_showfolder') + ss_entryDivTopDelta);
    	ss_entryWindowLeft = parseInt(maxEntryWidth - ss_entryWindowWidth);
    }
	if (moveTop) {
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
	    ss_setObjectTop(wObj1, ss_entryWindowTop)
	}
    if (ss_entryWindowLeft < 0) ss_entryWindowLeft = 0;
	
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
		var iframeScrollHeightString = "window."+ss_box_iframe_name+".document.body.scrollHeight";
		//alert("iframeScrollHeightString: "+iframeScrollHeightString);
		if (eval(iframeScrollHeightString)) {
		    var iframeScrollHeight = eval(iframeScrollHeightString);
		    var entryHeight = parseInt(iframeScrollHeight) + ss_scrollHeightFudge;
		    if (entryHeight < ss_minEntryWindowHeight) entryHeight = ss_minEntryWindowHeight;

		    var ss_selWaterMark = ss_entryHeightHighWaterMarkArray[ss_selectedPortletNamespace];
		    if (ss_selWaterMark == null) {
		    	ss_selWaterMark = 0;
		    }
		    if (entryHeight >= (ss_selWaterMark + ss_scrollHeightFudge)) {
			    //Only expand the height. Never shrink it. Otherwise the screen jumps around.
			    //ss_entryHeightHighWaterMark = entryHeight;

			    ss_entryHeightHighWaterMarkArray[ss_selectedPortletNamespace] = entryHeight;
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

function ss_hideEntryDiv(divToClose) {
    var wObj1 = self.document.getElementById(divToClose);
    if (wObj1 != null) {
    	wObj1.style.visibility = "hidden";
    	wObj1.style.display = "none";
    }
    ss_showSpannedAreas();
}

function ss_repositionEntryDiv() {
    //ss_debug('reposition div')
    //var wObj1 = self.document.getElementById('ss_showentrydiv')
    var wObj1 = ss_selectedDiv;
    if (wObj1 != null && wObj1.style.visibility == "visible") {
    	//The entry div is visible, so reposition it to the new size
    	ss_positionEntryDiv(true);
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

function ss_startDragDiv(type, divId) {
	ss_debug('start drag')
	if (ss_draggingDiv) return;
	ss_divDragMoveType = type;
	if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo(null);

	ss_divDragObj = document.getElementById(divId)
	//ss_divDragObj = ss_selectedDiv;
		
    if (ss_isNSN || ss_isNSN6 || ss_isMoz5) {
    } else {
        ss_divOffsetX = window.event.offsetX
        ss_divOffsetY = window.event.offsetY
        //Search the parent tree looking for 'ss_showentrydiv'/divId, summing the offsets along the way
        var offsetParent = window.event.srcElement.offsetParent
        while (offsetParent != null) {
        	if (offsetParent.id == divId) break;
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
			ss_setOpacity(lightBox, .1);
		    lightBox.onclick = "ss_entryClearDrag();";
		    lightBox.style.display = "block";
		    lightBox.style.top = 0;
		    lightBox.style.left = 0;
		    lightBox.style.width = ss_getBodyWidth();
		    lightBox.style.height = ss_getBodyHeight();
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
        //ss_debug('left = ' + dObjLeft + ', top = '+dObjTop)
        if (dObjLeft <= 0) dObjLeft = 1;
        if (dObjTop <= 0) dObjTop = 1;
        if (ss_divDragMoveType == 'resize') {
	        var deltaW = parseInt(parseInt(dObjLeft) - parseInt(ss_divDragObj.style.left))
	        ss_entryWindowWidth = parseInt(parseInt(ss_divDragObj.style.width) - deltaW)
	        if (ss_entryWindowWidth >= ss_minEntryWindowWidth) {
		        ss_entryWindowLeft = dObjLeft;
		        ss_divDragObj.style.left = dObjLeft
		        ss_positionEntryDiv(true)
		    }
		} else if (ss_divDragMoveType == 'move') {
	        ss_entryWindowTop = dObjTop;
	        ss_entryWindowLeft = dObjLeft;
	        ss_divDragObj.style.left = dObjLeft
	        ss_divDragObj.style.top = dObjTop
	        ss_positionEntryDiv(true)
		}
	    var lightBox = document.getElementById('ss_entry_light_box')
    	lightBox.style.width = ss_getBodyWidth();
    	lightBox.style.height = ss_getBodyHeight();
	    
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
		ss_setOpacity(lightBox, 1);
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
//    self.document.forms['ss_saveEntryWidthForm'].entry_width.value = entryWidth;
//    self.document.forms['ss_saveEntryWidthForm'].entry_top.value = entryTop;
//    self.document.forms['ss_saveEntryWidthForm'].entry_left.value = entryLeft;

    self.document.forms[ss_selectedIframeForm].entry_width.value = entryWidth;
    self.document.forms[ss_selectedIframeForm].entry_top.value = entryTop;
    self.document.forms[ss_selectedIframeForm].entry_left.value = entryLeft;
    
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"save_entry_width"});
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addFormElements(ss_selectedIframeForm)
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