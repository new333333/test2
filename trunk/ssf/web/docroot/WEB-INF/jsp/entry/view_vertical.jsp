<% //view a folder forum with the entry at the bottom in an iframe %>
<%
String iframeBoxId = renderResponse.getNamespace() + "_iframe_box_div";
%>
<a name="ss_top_of_folder"></a>
<div id="ss_showfolder" class="ss_style ss_portlet">
<div id="ss_folder">
  <ssf:displayConfiguration configDefinition="<%= ssConfigDefinition %>" 
    configElement="<%= ssConfigElement %>" 
    configJspStyle="<%= ssConfigJspStyle %>" />
</div>
<div id="ss_showfolder_slider" onMousedown="ss_startDragDiv();" width="100%"
 style="position:relative; margin:-1px 0px 3px 0px; padding:0px; top:-18px;">
  <table class="ss_style ss_bgmedgray" width="100%" cellpadding="0" cellspacing="0"
    style="border: 1px solid black;">
    <tr>
      <td align="center">^^^^^^^^^^^^^^^^^^</td>
    </tr>
  </table>
</div>

<div id="ss_showentrydiv" class="ss_style ss_portlet">
  <ssf:box>
    <ssf:param name="box_id" value="<%= iframeBoxId %>" />
    <ssf:param name="box_title" useBody="true">
      <div style="margin:0px; background-color: #cecece; border:solid #cccccc 1px;">
      <%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>
      </div>
    </ssf:param>
  <iframe id="ss_showentryframe" name="ss_showentryframe" style="width:100%; display:block;"
    src="<html:rootPath/>js/forum/null.html" height="200" width="100%" 
    frameBorder="no" >xxx</iframe>
  </ssf:box>
</div>
</div>
<div id="ss_showfolder_bottom" class="ss_style ss_portlet"></div>

<script type="text/javascript">
var ss_folderDivHeight = 400;
var ss_minFolderDivHeight = 150;
var ss_scrollTopOffset = 4;

var ss_marginLeft = 2
var ss_marginRight = 2
var ss_marginTop = 2
var ss_marginBottom = 2

function ss_positionEntryDiv() {
    var wObj = self.document.getElementById('ss_showfolder')
    var wObj1 = self.document.getElementById('ss_showentrydiv')
    var wObj2 = self.document.getElementById('<portlet:namespace/>_iframe_box_div')
    var wObj3 = self.document.getElementById('ss_showentryframe')

    var width = parseInt(parseInt(ss_getObjectWidth(wObj)) - ss_marginLeft - ss_marginRight);
	ss_setObjectWidth(wObj1, width);
	ss_setObjectWidth(wObj2, width);
}

ss_createOnLoadObj("ss_positionEntryDiv", ss_positionEntryDiv);

function ss_showForumEntryInIframe(url) {
	ss_positionEntryDiv();
    var wObj = self.document.getElementById('ss_showentryframe')

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
var ss_divDragSavedMouseMove = '';
var ss_divDragSavedMouseUp = '';
function ss_startDragDiv() {
	if (ss_clearMouseOverInfo) ss_clearMouseOverInfo(null);
	var sliderObj = document.getElementById('ss_showfolder_slider')
	if (!document.getElementById('ss_showfolder_slider_abs')) {
		var tempNode = sliderObj.cloneNode( true );
		tempNode.id = 'ss_showfolder_slider_abs';
		tempNode.style.position = 'absolute';
		tempNode.style.zIndex = 400;
		document.getElementsByTagName( "body" ).item(0).appendChild( tempNode );
	}
	ss_divDragObj = document.getElementById('ss_showfolder_slider_abs')
    ss_setObjectTop(ss_divDragObj, ss_getDivTop('ss_showfolder_slider'))
    ss_setObjectLeft(ss_divDragObj, ss_getDivLeft('ss_showfolder_slider'));
    ss_setObjectWidth(ss_divDragObj, ss_getDivWidth('ss_showfolder_slider'));
    ss_divDragObj.style.visibility = 'visible';
	
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
    	//Move the slider div
    	ss_setObjectTop(ss_divDragObj, dObjTop)
        return false
    
    } else {
        return true
    }
}

function ss_divStopDrag(evt) {
    if (!evt) evt = window.event;
    if (ss_divDragObj) {
    	ss_divDragObj.style.visibility = 'hidden';
        ss_divDragObj = null

	    self.document.onmousemove = ss_divDragSavedMouseMove;
	    self.document.onmouseup = ss_divDragSavedMouseUp;
        
        var dObjLeft
        var dObjTop
        if (isNSN || isNSN6 || isMoz5) {
            dObjLeft = evt.pageX - ss_divOffsetX;
            dObjTop = evt.pageY - ss_divOffsetY;
        } else {
            dObjLeft = evt.clientX - ss_divOffsetX;
            dObjTop = evt.clientY - ss_divOffsetY;
        }
		var tableDivObj = document.getElementById('<c:out value="${ss_slidingTableId}"/>')
	    ss_folderDivHeight = parseInt(parseInt(dObjTop) - parseInt(ss_getDivTop('<c:out value="${ss_slidingTableId}"/>')));
	    if (ss_folderDivHeight < ss_minFolderDivHeight) ss_folderDivHeight = ss_minFolderDivHeight;
	    ss_setObjectHeight(tableDivObj, ss_folderDivHeight);

		//Signal that the layout changed
		if (ssf_onLayoutChange) ssf_onLayoutChange();
    	
	    setTimeout("ss_saveEntryHeight(ss_folderDivHeight);", 500)
    }
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
	ajaxRequest.setPostRequest(ss_postEntryHeightRequest);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
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
