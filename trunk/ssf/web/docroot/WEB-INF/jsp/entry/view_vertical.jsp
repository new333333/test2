<% //view a folder forum with the entry at the bottom in an iframe %>
<%
String iframeBoxId = renderResponse.getNamespace() + "_iframe_box_div";
%>
<a name="ss_top_of_folder"></a>
<div id="ss_showfolder" class="ss_style ss_portlet">
<ssf:displayConfiguration configDefinition="<%= ssConfigDefinition %>" 
  configElement="<%= ssConfigElement %>" 
  configJspStyle="<%= ssConfigJspStyle %>" />
</div>

<div id="ss_showentrydiv" style="position:absolute; visibility:hidden; x:0; y:0;
  width:600; height:400px; display:none; z-index:50;">
  <ssf:box>
    <ssf:param name="box_id" value="<%= iframeBoxId %>" />
    <ssf:param name="box_width" value="400" />
    <ssf:param name="box_title" useBody="true">
<div style="margin:0px; background-color: #cecece; border:solid #cccccc 1px;">
<%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>
</div>
    </ssf:param>
    <ssf:param name="box_show_close_icon" value="true" />
    <ssf:param name="box_show_close_routine" value="ss_hideEntryDiv()" />
  <iframe id="ss_showentryframe" name="ss_showentryframe" style="width:100%; display:block;"
    src="<html:rootPath/>js/forum/null.html" height="95%" width="100%" 
    frameBorder="no" >xxx</iframe>
  </ssf:box>
</div>
<div id="ss_showfolder_bottom" class="ss_style ss_portlet">bbbbbbbbbbb</div>
<div id="ss_showfolder_slider" onMousedown="ss_startDragDiv()"
 style="position:absolute; margin:4px; top:4px;" width="100%">
  <div style="border: 1px solid gray; margin:3px; 
    background-color:silver;"
    width="100%" align="center">
  ^^^^^^^^^^^^^^^^^^
  </div>
</div>

<script type="text/javascript">
var ss_entryWindowWidth = <%= ss_entryWindowWidth %>;
var ss_minEntryWindowWidth = 200;
var ss_scrollbarWidth = 25;
//var ss_entryWindowHeight = <c:out value="${ss_entryWindowHeight}"/>;
var ss_entryWindowHeight = 400;
var ss_minEntryWindowHeight = 200;
var ss_scrollbarHeight = 25;
var ss_entryDivTopDelta = 25;
var ss_entryDivBottomDelta = 50;
var ss_scrollTopOffset = 4;

function ss_positionSliderDiv() {
	var sliderObj = document.getElementById("ss_showfolder_slider");
	setObjectTop(sliderObj, parseInt(getDivTop("ss_showfolder_bottom")));
	sliderObj.style.visibility = "visible";
}

ss_createOnLoadObj("ss_positionSliderDiv", ss_positionSliderDiv);

function ss_showForumEntryInIframe(url) {
	ss_positionEntryDiv();
    var wObj = self.document.getElementById('ss_showentryframe')
    var wObj1 = self.document.getElementById('ss_showentrydiv')

    wObj1.style.display = "block";
    wObj1.style.visibility = "visible";

    if (wObj.src && wObj.src == url) {
    	wObj.src = "_blank";
    }
    wObj.style.height = parseInt(wObj1.style.height) - ss_entryDivBottomDelta + "px";
    wObj.src = url

    //Scroll to the bottom of the window
	smoothScrollInTime(0 , parseInt(ss_getWindowHeight()), 8)

	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();

    return false;
}

function ss_positionEntryDiv() {
    var wObj = self.document.getElementById('ss_showfolder')

	var marginLeft = 2
	var marginRight = 2
	var marginTop = 2
	var marginBottom = 2
	
    var width = parseInt(parseInt(ss_getObjectWidth(wObj)) - marginLeft - marginRight);
    var height = parseInt(parseInt(ss_getObjectHeight(wObj)) - marginTop - marginBottom);
    ss_entryWindowWidth = parseInt(width);
    ss_entryWindowHeight = parseInt(height);

    var wObj1 = self.document.getElementById('ss_showentrydiv')
    var wObj2 = self.document.getElementById('<portlet:namespace/>_iframe_box_div')
    var wObj3 = self.document.getElementById('ss_showentryframe')

    var top = parseInt(getDivTop('ss_showentrydiv'));
    var left = parseInt(parseInt(getDivLeft('ss_showfolder')) + marginLeft);
    var height = parseInt(ss_getWindowHeight() - ss_entryDivBottomDelta);
    setObjectTop(wObj1, top)
    setObjectLeft(wObj1, left);
    setObjectWidth(wObj1, ss_entryWindowWidth);
    setObjectWidth(wObj2, ss_entryWindowWidth);
    setObjectHeight(wObj1, ss_entryWindowHeight);
    setObjectHeight(wObj2, ss_entryWindowHeight);

    setObjectHeight(wObj1, height);
    wObj1.style.background = "#ffffff"
    wObj1.style.display = "block";
    wObj1.style.visibility = "visible";
}

function ss_hideEntryDiv() {
    var wObj1 = self.document.getElementById('ss_showentrydiv')
    wObj1.style.display = "none";
    wObj1.style.visibility = "hidden";
}

function ss_repositionEntryDiv() {
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
var ss_divDragSavedMouseMove = '';
var ss_divDragSavedMouseUp = '';
function ss_startDragDiv() {
	ss_divDragObj = document.getElementById('ss_showfolder_slider')
    if (isNSN || isNSN6 || isMoz5) {
    } else {
        ss_divOffsetX = window.event.offsetX
        ss_divOffsetY = window.event.offsetY
    }
    ss_startingToDragDiv = 1;
    if (self.document.onmousemove) ss_divDragSavedMouseMove = self.document.onmousemove;
    if (self.document.onmouseup) ss_divDragSavedMouseUp = self.document.onmouseup;
    self.document.onmousemove = ss_divDrag
    self.document.onmouseup = ss_divStopDrag

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
        }
        var dObjLeft
        var dObjTop
        if (isNSN || isNSN6 || isMoz5) {
            dObjLeft = evt.pageX - ss_divOffsetX;
            dObjTop = evt.pageY - ss_divOffsetY;
        } else {
            dObjLeft = evt.clientX - ss_divOffsetX;
            dObjTop = evt.clientY - ss_divOffsetY;
        }
        var deltaW = parseInt(parseInt(dObjLeft) - parseInt(ss_divDragObj.style.left))
        var deltaH = parseInt(parseInt(dObjTop) - parseInt(ss_divDragObj.style.top))
        ss_entryWindowWidth = parseInt(parseInt(ss_divDragObj.style.width) - deltaW)
        ss_entryWindowHeight = parseInt(parseInt(ss_divDragObj.style.height) - deltaH)
	    setObjectTop(ss_divDragObj, dObjTop)
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
    setTimeout("ss_saveEntryHeight(ss_entryWindowHeight);", 500)
    return false
}

var ss_lastEntryHeight = -1;
function ss_saveEntryHeight(entryHeight) {
	if (entryHeight == ss_lastEntryHeight) return;
	ss_lastEntryHeight = entryHeight;
    self.document.forms['ss_saveEntryHeightForm'].entry_height.value = entryHeight;
	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="true" >
		<ssf:param name="operation" value="save_entry_height" />
    	</ssf:url>"
	var ajaxRequest = new AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addFormElements("ss_saveEntryHeightForm")
	//ajaxRequest.setEchoDebugInfo();
	//ajaxRequest.setPreRequest(ss_preRequest);
	ajaxRequest.setPostRequest(ss_postEntryHeightRequest);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_preRequest(obj) {
	//alert('preRequest: ' + obj.getQueryString());
}
function ss_postEntryHeightRequest(obj) {
	//See if there was an error
	if (self.document.getElementById("ss_entry_height_status_message").innerHTML == "error") {
		alert("<ssf:nlt tag="forum.unseenCounts.notLoggedIn" text="Your session has timed out. Please log in again."/>");
	}
}

</script>
<form class="ss_style" name="ss_saveEntryHeightForm" id="ss_saveEntryHeightForm" >
<input type="hidden" name="entry_height">
</form>
<div id="ss_entry_height_status_message" style="visibility:hidden; display:none;"></div>
