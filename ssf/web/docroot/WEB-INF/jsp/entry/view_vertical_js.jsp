<% //Supporting javascript routines for view_vertical.jsp %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%
int sliderDivHeight = 22;
int sliderDivArrowHeight = 17;    //This is the height of pics/sym_s_arrows_northsouth.gif
int sliderDivBlankHeight = sliderDivHeight - sliderDivArrowHeight;
String sliderDivOffset = "-" + String.valueOf(sliderDivHeight);
%>

<script type="text/javascript">
var ss_folderDivHeight = 400;
var ss_bottomHeight = 100;
var ss_minFolderDivHeight = 100;
var ss_minEntryDivHeight = 100;
var ss_scrollTopOffset = 4;
var ss_positioningEntryDiv = 0;
var ss_marginLeft = 2
var ss_marginRight = 2
var ss_folderDivMarginOffset = 6

function ss_setEntryDivHeight() {
	if (window.ss_showentryframe && window.ss_showentryframe.document && 
			window.ss_showentryframe.document.body) {
	    var wObj3 = self.document.getElementById('ss_showentryframe')
	    var entryHeight = parseInt(window.ss_showentryframe.document.body.scrollHeight)
	    var entryHeightPlus2 = parseInt(entryHeight + 2)
		ss_setObjectHeight(wObj3, ss_minEntryDivHeight);
		setTimeout("ss_setEntryDivHeight2();", 100);
	}
}
function ss_setEntryDivHeight2() {
	if (window.ss_showentryframe && window.ss_showentryframe.document && 
			window.ss_showentryframe.document.body) {
	    var wObj3 = self.document.getElementById('ss_showentryframe')
	    var entryHeight = parseInt(window.ss_showentryframe.document.body.scrollHeight)
	    var entryHeightPlus2 = parseInt(entryHeight + 2)
		ss_setObjectHeight(wObj3, entryHeightPlus2);
		setTimeout("ss_positionEntryDiv();", 100);
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
    var wObj4 = self.document.getElementById('ss_showentrydiv_place_holder')
        
    var width = parseInt(parseInt(ss_getObjectWidth(wObj)) - ss_marginLeft - ss_marginRight);
	ss_setObjectWidth(wObj1, width);
	ss_setObjectWidth(wObj2, width);

    ss_setObjectTop(wObj1, parseInt(parseInt(ss_getDivTop('ss_showfolder_slider')) + <%= sliderDivHeight %>))
    
    //Keep the entry within the confines of the main window
    //var entryHeight = parseInt(ss_getWindowHeight() - ss_getDivTop('ss_showfolder_slider') - ss_bottomHeight)
    
    //Allow the entry section to grow to as large as needed to show the entry
	if (window.ss_showentryframe && window.ss_showentryframe.document && 
			window.ss_showentryframe.document.body) {
	    var entryHeight = parseInt(window.ss_showentryframe.document.body.scrollHeight)
	    
	    if (entryHeight < ss_minEntryDivHeight) entryHeight = ss_minEntryDivHeight;
		ss_setObjectHeight(wObj1, entryHeight);
		ss_setObjectHeight(wObj3, entryHeight);
		ss_setObjectHeight(wObj4, entryHeight);
	}
	
	ss_positioningEntryDiv = 0
}

var ss_savedSliderClassName = ""
var ss_savedSliderBorder = ""
function ss_showEntryDiv() {
    var wObj1 = self.document.getElementById('ss_showentrydiv')
    wObj1.style.visibility = "visible";
    wObj1.style.display = "block";

    var wObj2 = self.document.getElementById('ss_showfolder_slider')
    if (ss_savedSliderClassName != "") wObj2.className = ss_savedSliderClassName;
    if (ss_savedSliderBorder != "") wObj2.style.border = ss_savedSliderBorder;
}

function ss_hideEntryDiv() {
	//Mark that we are positioning the entry div so dragging doesn't do layout changes
	ss_positioningEntryDiv = 1
	
    var wObj1 = self.document.getElementById('ss_showentrydiv')
    wObj1.style.visibility = "hidden";
    var wObj2 = self.document.getElementById('ss_showfolder_slider')
    if (ss_savedSliderClassName == "") ss_savedSliderClassName = wObj2.className;
    wObj2.className = "ss_style ss_bgwhite";
    if (ss_savedSliderBorder == "") ss_savedSliderBorder = wObj2.style.border;
    wObj2.style.border = "1px solid white";
}

var ss_lastLayoutEntryHeight = 0;
function ss_checkLayoutChange() {
	//Reposition entry div, but only if not in the process of doing it
	if (ss_positioningEntryDiv != 1) ss_positionEntryDiv();
}

ss_createOnLoadObj("ss_positionEntryDiv", ss_positionEntryDiv);
ss_createOnResizeObj('ss_positionEntryDiv', ss_positionEntryDiv);
ss_createOnLayoutChangeObj('ss_checkLayoutChange', ss_checkLayoutChange);

function ss_showForumEntryInIframe(url) {
	ss_positionEntryDiv();
    var wObj = self.document.getElementById('ss_showentryframe')

 	ss_setObjectHeight(wObj, ss_minEntryDivHeight);

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
	if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo(null);
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
    ss_setObjectWidth(ss_divDragObj, parseInt(ss_getDivWidth('ss_showfolder_slider') - ss_marginRight));
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

	//Hide the entry divs so dragging doesn't do lots of layout changes
	ss_hideEntryDiv();
	if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo(null);
		
    return false
}

function ss_divDrag(evt) {
    if (!evt) evt = window.event;
    if (ss_divDragObj) {
		//Hide the entry div so dragging doesn't do lots of layout changes
		ss_hideEntryDiv();
		
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
		var tableDivObj = document.getElementById('<c:out value="${ss_folderTableId}"/>')
	    ss_folderDivHeight = parseInt(parseInt(dObjTop) - 
	    		parseInt("<%= sliderDivOffset %>") - ss_folderDivMarginOffset - 
	    		parseInt(ss_getDivTop('<c:out value="${ss_folderTableId}"/>')));
	    if (ss_folderDivHeight < ss_minFolderDivHeight) ss_folderDivHeight = ss_minFolderDivHeight;
	    ss_setObjectHeight(tableDivObj, ss_folderDivHeight);

		//Reposition the entry div to fit in the new space
		ss_positionEntryDiv()

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
		<ssf:param name="binderId" value="${ssFolder.id}" />
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
		alert("<ssf:nlt tag="general.notLoggedIn" text="Your session has timed out. Please log in again."/>");
	}
}

</script>
