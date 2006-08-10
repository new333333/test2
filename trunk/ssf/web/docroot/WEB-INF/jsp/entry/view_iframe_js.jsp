<% //Supporting javascript routines for view_iframe.jsp %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<script type="text/javascript">
<c:if test="${!empty ss_entryWindowTop && !empty ss_entryWindowLeft}">
var ss_entryWindowTop = ${ss_entryWindowTop};
var ss_entryWindowLeft = ${ss_entryWindowLeft};
</c:if>
<c:if test="${empty ss_entryWindowTop || empty ss_entryWindowLeft}">
var ss_entryWindowTop = -1;
var ss_entryWindowLeft = -1;
</c:if>

var ss_entryWindowWidth = ${ss_entryWindowWidth};
var ss_minEntryWindowWidth = 200;
var ss_minEntryWindowHeight = 200;
var ss_entryWindowHeight = ss_minEntryWindowHeight;
var ss_scrollbarWidth = 25;
var ss_entryDivTopDelta = 25;
var ss_entryDivBottomDelta = 50;
var ss_scrollTopOffset = 4;
var ss_nextUrl = ""
var ss_scrollTopOffset = 4;
var ss_entryHeightHighWaterMark = 0
	//ss_debug("init: "+ss_entryWindowLeft)

function ss_setEntryDivHeight() {
	setTimeout("ss_positionEntryDiv();", 100);
}
function ss_showForumEntryInIframe(url) {
	//ss_debug('show url in frame = '+url)
	ss_positionEntryDiv();
    var wObj = self.document.getElementById('ss_showentryframe')
    var wObj1 = self.document.getElementById('ss_showentrydiv')

    ss_hideSpannedAreas();
    wObj1.style.display = "block";
    wObj1.style.zIndex = ssEntryZ;
    wObj1.style.visibility = "visible";
    //wObj.style.height = parseInt(wObj1.style.height) - ss_entryDivBottomDelta + "px";

    if (wObj.src && wObj.src == url) {
    	ss_nextUrl = url
    	wObj.src = "<html:rootPath/>js/forum/refresh.html";
    } else if (wObj.src && wObj.src == "<html:rootPath/>js/forum/refresh.html" && ss_nextUrl == url) {
    	wObj.src = "<html:rootPath/>js/forum/refresh.html";
    } else {
    	wObj.src = url
    }

	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();

    return false;
}

function ss_positionEntryDiv() {
	//ss_debug("ss_positionEntryDiv: "+ss_entryWindowLeft)
	var maxEntryWidth = parseInt(ss_getWindowWidth() - ss_scrollbarWidth);
	
    var wObj = self.document.getElementById('ss_showfolder')

    var width = ss_getObjectWidth(wObj);
    if (ss_entryWindowWidth == 0) {ss_entryWindowWidth = parseInt((width * 3) / 4);}
    //Make sure the entry width is within the window
    if (ss_entryWindowWidth > maxEntryWidth) ss_entryWindowWidth = maxEntryWidth;
    if (ss_entryWindowWidth < ss_minEntryWindowWidth) ss_entryWindowWidth = ss_minEntryWindowWidth;
    if (ss_entryWindowHeight < ss_minEntryWindowHeight) ss_entryWindowHeight = ss_minEntryWindowHeight;

    var wObj1 = self.document.getElementById('ss_showentrydiv')
    ss_moveObjectToBody(wObj1)
    var wObj2 = self.document.getElementById(ss_iframe_box_div_name)
    var wObj3 = self.document.getElementById('ss_showentryframe')

    if (ss_entryWindowTop <= 0 || ss_entryWindowLeft <= 0) {
    	//ss_debug("initial setting of top and left " + ss_entryWindowWidth)
    	ss_entryWindowTop = parseInt(ss_getDivTop('ss_showfolder') + ss_entryDivTopDelta);
    	ss_entryWindowLeft = parseInt(maxEntryWidth - ss_entryWindowWidth);
    }
	if (ss_entryWindowTop < parseInt(self.document.body.scrollTop)) {
		//ss_entryWindowTop = parseInt(self.document.body.scrollTop + ss_scrollTopOffset);
	}
    if (ss_entryWindowLeft < 0) ss_entryWindowLeft = 0;

    ss_setObjectTop(wObj1, ss_entryWindowTop)
    ss_setObjectLeft(wObj1, ss_entryWindowLeft);
    ss_setObjectWidth(wObj1, ss_entryWindowWidth);
    ss_setObjectWidth(wObj2, ss_entryWindowWidth);
    //ss_setObjectWidth(wObj3, ss_entryWindowWidth);
    
    wObj1.style.background = "${ss_style_background_color}"
    wObj1.style.visibility = "visible";

    //Allow the entry section to grow to as large as needed to show the entry
	if (window.ss_showentryframe && window.ss_showentryframe.document && 
			window.ss_showentryframe.document.body) {
	    var entryHeight = parseInt(window.ss_showentryframe.document.body.scrollHeight)
	    if (entryHeight < ss_minEntryWindowHeight) entryHeight = ss_minEntryWindowHeight;
	    if (entryHeight > ss_entryHeightHighWaterMark) {
		    //Only expand the height. Never shrink it. Otherwise the screen jumps around.
		    ss_entryHeightHighWaterMark = entryHeight;
			ss_setObjectHeight(wObj1, entryHeight);
		}
		ss_setObjectHeight(wObj3, entryHeight);
	}

}

function ss_hideEntryDiv() {
    var wObj1 = null
    if (isNSN || isNSN6 || isMoz5) {
        wObj1 = self.document.getElementById('ss_showentrydiv')
    } else {
        wObj1 = self.document.all['ss_showentrydiv']
    }
    wObj1.style.visibility = "hidden";
    ss_showSpannedAreas();
}

function ss_repositionEntryDiv() {
    //ss_debug('reposition div')
    var wObj1 = self.document.getElementById('ss_showentrydiv')
    if (wObj1.style.visibility == "visible") {
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
	if (ss_draggingDiv) return;
	ss_divDragMoveType = type;
	if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo(null);

    if (isNSN || isNSN6 || isMoz5) {
    } else {
        ss_divOffsetX = window.event.offsetX
        ss_divOffsetY = window.event.offsetY
    }
	ss_divDragObj = document.getElementById('ss_showentrydiv')
	
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
                if (isNSN || isNSN6 || isMoz5) {
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
			dojo.style.setOpacity(lightBox, 1);
		    lightBox.onclick = "ss_entryClearDrag();";
		    lightBox.style.top = 0;
		    lightBox.style.left = 0;
		    lightBox.style.width = ss_getBodyWidth();
		    lightBox.style.height = ss_getBodyHeight();
		    lightBox.style.display = "block";
		    lightBox.style.zIndex = parseInt(ssDragEntryZ - 1);
		    lightBox.style.visibility = "visible";			
			//ss_divDragObj.parentNode.removeChild(ss_divDragObj);
			//lightBox.appendChild(ss_divDragObj);
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
    		dObjLeft += parseInt(self.document.body.scrollLeft)
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
		        ss_positionEntryDiv()
		    }
		} else if (ss_divDragMoveType == 'move') {
	        ss_entryWindowTop = dObjTop;
	        ss_entryWindowLeft = dObjLeft;
	        ss_divDragObj.style.left = dObjLeft
	        ss_divDragObj.style.top = dObjTop
	        ss_positionEntryDiv()
		}
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
    setTimeout("ss_saveEntryWidth(ss_entryWindowWidth, ss_entryWindowTop, ss_entryWindowLeft);", 500)
    return false
}

function ss_entryClearDrag() {
	//ss_debug('clear drag')
	//ss_moveDivToBody('ss_showentrydiv')
	var lightBox = document.getElementById('ss_entry_light_box')
	if (lightBox != null) {
		//ss_debug('remove lightbox')
		lightBox.parentNode.removeChild(lightBox);
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
    self.document.forms['ss_saveEntryWidthForm'].entry_width.value = entryWidth;
    self.document.forms['ss_saveEntryWidthForm'].entry_top.value = entryTop;
    self.document.forms['ss_saveEntryWidthForm'].entry_left.value = entryLeft;
	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="true" >
		<ssf:param name="operation" value="save_entry_width" />
    	</ssf:url>"
	var ajaxRequest = new AjaxRequest(url); //Create AjaxRequest object
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
		alert("<ssf:nlt tag="general.notLoggedIn" text="Your session has timed out. Please log in again."/>");
	}
}

</script>
