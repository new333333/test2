<% //view a folder forum with folder on the left and the entry on the right in an iframe %>
<%
String iframeBoxId = renderResponse.getNamespace() + "_iframe_box_div";

//Get the folder type of this definition (folder, file, or event)
String folderViewStyle = "folder";
Element folderViewTypeEle = (Element)ssConfigElement.selectSingleNode("properties/property[@name='type']");
if (folderViewTypeEle != null) folderViewStyle = folderViewTypeEle.attributeValue("value", "folder");
%>
<c:set var="ss_folderViewStyle" value="<%= folderViewStyle %>" scope="request" />
<div id="ss_showfolder" class="ss_style ss_portlet">

<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${ssConfigElement}" 
  configJspStyle="${ssConfigJspStyle}" />
</div>

<div id="ss_showentrydiv" onMouseover="if (self.ss_clearMouseOverInfo) {ss_clearMouseOverInfo(null);}"
  style="position:absolute; visibility:hidden;
  width:600; height:80%; display:none; z-index:50;">
  <ssf:box>
    <ssf:param name="box_id" value="<%= iframeBoxId %>" />
    <ssf:param name="box_width" value="400" />
    <ssf:param name="box_color" value="${ss_entry_border_color}" />
    <ssf:param name="box_canvas_color" value="${ss_style_background_color}" />
    <ssf:param name="box_title" useBody="true">
<div class="ss_entry_border" style="margin:0px;">
<%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>
</div>
    </ssf:param>
    <ssf:param name="box_show_resize_icon" value="true" />
    <ssf:param name="box_show_resize_routine" value="ss_startDragDiv()" />
    <ssf:param name="box_show_resize_gif" value="box/resize_east_west.gif" />
    <ssf:param name="box_show_close_icon" value="true" />
    <ssf:param name="box_show_close_routine" value="ss_hideEntryDiv()" />
  <iframe id="ss_showentryframe" name="ss_showentryframe" style="width:100%; display:block;"
    src="<html:rootPath/>js/forum/null.html" height="95%" width="100%" 
    frameBorder="no" >xxx</iframe>
  </ssf:box>
</div>

<script type="text/javascript">
var ss_entryWindowWidth = <%= ss_entryWindowWidth %>;
var ss_minEntryWindowWidth = 200;
var ss_scrollbarWidth = 25;
var ss_entryDivTopDelta = 25;
var ss_entryDivBottomDelta = 50;
var ss_scrollTopOffset = 4;
var ss_nextUrl = ""

function ss_showForumEntryInIframe(url) {
	ss_positionEntryDiv();
    var wObj = self.document.getElementById('ss_showentryframe')
    var wObj1 = self.document.getElementById('ss_showentrydiv')

    ss_hideSpannedAreas();
    wObj1.style.display = "block";
    wObj1.style.visibility = "visible";
    wObj.style.height = parseInt(wObj1.style.height) - ss_entryDivBottomDelta + "px";

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
	var maxEntryWidth = parseInt(ss_getWindowWidth() - ss_scrollbarWidth);
	
    var wObj = self.document.getElementById('ss_showfolder')

    var width = ss_getObjectWidth(wObj);
    if (ss_entryWindowWidth == 0) {ss_entryWindowWidth = parseInt((width * 3) / 4);}
    //Make sure the entry width is within the window
    if (ss_entryWindowWidth > maxEntryWidth) ss_entryWindowWidth = maxEntryWidth;
    if (ss_entryWindowWidth < ss_minEntryWindowWidth) ss_entryWindowWidth = ss_minEntryWindowWidth;

    var wObj1 = self.document.getElementById('ss_showentrydiv')
    var wObj2 = self.document.getElementById('<portlet:namespace/>_iframe_box_div')
    var wObj3 = self.document.getElementById('ss_showentryframe')

    var top = parseInt(ss_getDivTop('ss_showfolder') + ss_entryDivTopDelta);
    if (top < parseInt(self.document.body.scrollTop)) {top = parseInt(self.document.body.scrollTop + ss_scrollTopOffset);} 
    var left = parseInt(maxEntryWidth - ss_entryWindowWidth);
    if (left < 0) left = 0;
    var height = parseInt(ss_getWindowHeight() + self.document.body.scrollTop - top - ss_entryDivTopDelta );
    ss_setObjectTop(wObj1, top)
    ss_setObjectLeft(wObj1, left);
    ss_setObjectWidth(wObj1, ss_entryWindowWidth);
    ss_setObjectWidth(wObj2, ss_entryWindowWidth);
    //ss_setObjectWidth(wObj3, ss_entryWindowWidth);
    ss_setObjectHeight(wObj1, height);
    wObj1.style.background = "#ffffff"
    wObj1.style.visibility = "visible";
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
	if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo(null);
	
	ss_divDragObj = document.getElementById('ss_showentrydiv')
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
        if (isNSN || isNSN6 || isMoz5) {
            dObjLeft = evt.pageX - ss_divOffsetX;
        } else {
            dObjLeft = evt.clientX - ss_divOffsetX;
        }
        var deltaW = parseInt(parseInt(dObjLeft) - parseInt(ss_divDragObj.style.left))
        ss_entryWindowWidth = parseInt(parseInt(ss_divDragObj.style.width) - deltaW)
        if (ss_entryWindowWidth >= ss_minEntryWindowWidth) {
	        ss_divDragObj.style.left = dObjLeft
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
    setTimeout("ss_saveEntryWidth(ss_entryWindowWidth);", 500)
    return false
}

var ss_lastEntryWidth = -1;
function ss_saveEntryWidth(entryWidth) {
	if (entryWidth == ss_lastEntryWidth) return;
	ss_lastEntryWidth = entryWidth;
    self.document.forms['ss_saveEntryWidthForm'].entry_width.value = entryWidth;
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
	if (self.document.getElementById("ss_entry_width_status_message").innerHTML == "error") {
		alert("<ssf:nlt tag="general.notLoggedIn" text="Your session has timed out. Please log in again."/>");
	}
}

</script>
<form class="ss_style ss_form" name="ss_saveEntryWidthForm" id="ss_saveEntryWidthForm" >
<input type="hidden" name="entry_width">
</form>
<div id="ss_entry_width_status_message" style="visibility:hidden; display:none;"></div>
