<% //view a folder forum with folder on top and entry below %>

<%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>
<br>
<%
String iframeBoxId = renderResponse.getNamespace() + "_iframe_box_div";
%>

<div id="ss_showfolder" class="ss_portlet" style="display:block; margin:2;">
<ssf:displayConfiguration configDefinition="<%= ssConfigDefinition %>" 
  configElement="<%= ssConfigElement %>" 
  configJspStyle="<%= ssConfigJspStyle %>" />
</div>

<br>
<div id="ss_showentrydiv" class="ss_portlet" style="visibility:hidden; x:0; y:0;
  display:none; z-index:50;">
<%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>
<a href="#return_to_folder_list" onClick="scrollToSavedLocation();return false;">
Scroll up to the folder listing...
</a>
<br>
  <ssf:box>
    <ssf:param name="box_id" value="<%= iframeBoxId %>" />
    <ssf:param name="box_width" value="400" />
    <ssf:param name="box_show_close_icon" value="true" />
    <ssf:param name="box_show_close_routine" value="ss_hideEntryDiv()" />
  <iframe id="ss_showentryframe" name="ss_showentryframe" style="width:100%; display:block;"
    src="<html:rootPath/>js/forum/null.html" height="95%" width="100%" 
    frameBorder="no" >xxx</iframe>
  </ssf:box>
<br>
<a href="#return_to_folder_list" onClick="scrollToSavedLocation();return false;">
Scroll up to the folder listing...
</a>
<br>
</div>

<script language="javascript">
var ss_entryWindowWidth = <%= ss_entryWindowWidth %>;

function ss_showForumEntryInIframe(url) {
    //Keep a high water mark for the page so the scrolling doesn't bounce around
    setWindowHighWaterMark('ss_showentryhighwatermark');
    
	ss_positionEntryDiv();
    var wObj
    if (isNSN || isNSN6 || isMoz5) {
        wObj = self.document.getElementById('ss_showentryframe')
    } else {
        wObj = self.document.all['ss_showentryframe']
    }
    
    var wObj1 = null
    if (isNSN || isNSN6 || isMoz5) {
        wObj1 = self.document.getElementById('ss_showentrydiv')
     } else {
        wObj1 = self.document.all['ss_showentrydiv']
    }
    wObj1.style.display = "block";
    wObj1.style.visibility = "visible";

    if (wObj.src && wObj.src == url) {
    	wObj.src = "_blank";
    }
    wObj.style.height = 400 + "px";
    wObj.src = url

    //Keep a high water mark for the page so the scrolling doesn't bounce around
    setWindowHighWaterMark('ss_showentryhighwatermark');
    
    //Get the position of the div displaying the entry
    if (autoScroll == "true") {
	    var entryY = getDivTop('ss_showentrydiv')
	    var entryH = getDivHeight('ss_showentrydiv')
	    var bodyY = self.document.body.scrollTop
	    var windowH = getWindowHeight()
	    if (entryY >= bodyY) {
	    	if (entryY >= parseInt(bodyY + windowH)) {
	    		if (entryH > windowH) {
	    			smoothScroll(0,entryY)
	    		} else {
	    			var newY = parseInt(entryY - (windowH - entryH))
	    			smoothScroll(0,newY)
	    		}
	    	} else if (parseInt(entryY + entryH) > parseInt(bodyY + windowH)) {
	    		var overhang = parseInt((entryY + entryH) - (bodyY + windowH))
	    		var newY = parseInt(bodyY + overhang)
	    		if (newY > entryY) {newY = entryY}
	    		smoothScroll(0,newY)
	    	}
	    } else {
	    	smoothScroll(0,entryY)
	    }
	}

	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();

    return false;
}

function ss_positionEntryDiv() {
    var wObj = null
    if (isNSN || isNSN6 || isMoz5) {
        wObj = self.document.getElementById('ss_showfolder')
    } else {
        wObj = self.document.all['ss_showfolder']
    }
    var width = getObjectWidth(wObj);
    ss_entryWindowWidth = parseInt(width);
    var left = getObjectLeft(wObj);

    var wObj1 = null
    var wObj2 = null
    var wObj3 = null
    if (isNSN || isNSN6 || isMoz5) {
        wObj1 = self.document.getElementById('ss_showentrydiv')
        wObj2 = self.document.getElementById('<portlet:namespace/>_iframe_box_div')
        wObj3 = self.document.getElementById('ss_showentryframe')
    } else {
        wObj1 = self.document.all['ss_showentrydiv']
        wObj2 = self.document.all['<portlet:namespace/>_iframe_box_div']
        wObj3 = self.document.all['ss_showentryframe']
    }
    setObjectLeft(wObj1, left);
    setObjectWidth(wObj1, ss_entryWindowWidth);
    setObjectWidth(wObj2, ss_entryWindowWidth);
    //setObjectWidth(wObj3, ss_entryWindowWidth);
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
}

createOnLoadObj('ss_positionEntryDiv', ss_positionEntryDiv)
</script>
