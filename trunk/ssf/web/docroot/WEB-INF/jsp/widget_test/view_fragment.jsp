<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<script type="text/javascript">

var baseUrl = "<portlet:renderURL/>";
function ss_showUrlInPortlet(params) {
	var url = baseUrl + params
	self.location.href = url;
	return false;
}


function ss_loadEntry(obj) {
	ss_showMessageInDiv("<ssf:nlt tag="Loading" text="Loading..."/>");
	ss_showForumEntry(obj.href, ss_showEntryInDiv);
	return false;
}

function ss_showMessageInDiv(str) {
	ss_showEntryInDiv(str)
}

function ss_showForumEntry(url, callbackRoutine) {
	ss_fetch_url(url, callbackRoutine);
}

function ss_showEntryInDiv(str) {
    var wObj1 = null
    var wObj2 = null
    wObj1 = self.document.getElementById('ss_showentrydiv')
    wObj2 = self.document.getElementById('ss_showentry')
    
    //If the entry div needs dynamic positioning, do it now
    ss_positionEntryDiv()
    
    wObj1.style.display = "block";
    wObj2.innerHTML = str;
    wObj1.style.visibility = "visible";
    
    //Get the position of the div displaying the entry
    if (autoScroll == "true") {
	    var entryY = ss_getDivTop('ss_showentrydiv')
	    var entryH = ss_getDivHeight('ss_showentrydiv')
	    var bodyY = ss_getScrollXY()[1]
	    var windowH = ss_getWindowHeight()
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
}

function ss_showForumEntryInIframe(url) {
	ss_positionEntryDiv();
    var wObj
    wObj = self.document.getElementById('ss_showentryframe')
    
    var wObj1 = null
    wObj1 = self.document.getElementById('ss_showentrydiv')
    wObj1.style.display = "block";
    wObj1.style.visibility = "visible";

    if (wObj.src && wObj.src == url) {
    	wObj.src = "_blank";
    	alert("iframe blanked")
    }
    //wObj.style.height = parseInt(wObj1.style.height) - 50 + "px";
    wObj.src = url
    return false;
}


function ss_showForumEntryInWindow(url) {
    self.window.open(url,"_blank",'width=400,height=250,resizable,scrollbars')
    return false;
}


var entryWindowWidth = 400;
function ss_positionEntryDiv() {
return
    var wObj1 = null
    wObj1 = self.document.getElementById('ss_showentrydiv')
    var top = parseInt(ss_getDivTop('showbutton'));
    if (top < parseInt(ss_getScrollXY()[1])) {top = parseInt(ss_getScrollXY()[1] + 4);} 
    var left = parseInt(ss_getDivWidth('showbutton') - entryWindowWidth - 14);
    var width = parseInt(entryWindowWidth);
    var height = parseInt(ss_getWindowHeight() + ss_getScrollXY()[1] - top );
    ss_setObjectTop(wObj1, top)
    ss_setObjectLeft(wObj1, left);
    ss_setObjectWidth(wObj1, width);
    //ss_setObjectHeight(wObj1, height);
    
    ss_setObjectHeight(wObj1, "");
    wObj1.style.background = "#ffffff"
}

function ss_hideEntryDiv() {
    var wObj1 = null
    wObj1 = self.document.getElementById('ss_showentrydiv')
    wObj1.style.visibility = "hidden";
}

ss_createOnLoadObj('ss_positionEntryDiv', ss_positionEntryDiv)

</script>

<span align="left">SiteScape Forum Widget Tester -  Fragment Widget</span>
<br />
<br />

<table class="ss_style"><tr><td valign="top">
<div id="showbutton" class="ss_style ss_portlet" style="display:block; margin:2;">
<a href="<ssf:url 
    adapter="true" 
    portletName="ss_widgettest" 
    action="fragment" 
    actionUrl="true" >
	<ssf:param name="operation" value="viewFragment" />
    </ssf:url>"
	onClick="ss_showForumEntryInIframe(this.href);return false;" 
 	>Show the fragment in an iframe</a><br>
<a href="<ssf:url 
    adapter="true" 
    portletName="ss_widgettest" 
    action="fragment" 
    actionUrl="true" >
	<ssf:param name="operation" value="viewFragment" />
    </ssf:url>"
	onClick="ss_showForumEntryInWindow(this.href);return false;" 
 	>Show the fragment in a new window</a>
</div>
</td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<td>
</td><td>
<div id="ss_showentrydiv" style="display:block; margin:2px; wwidth:400px; height:80%;">
  <ssf:box>
    <ssf:param name="box_width" value="400" />
    <ssf:param name="box_show_close_icon" value="true" />
    <ssf:param name="box_show_close_routine" value="ss_hideEntryDiv()" />
  <iframe id="ss_showentryframe" name="ss_showentryframe" 
    src="<html:rootPath/>js/forum/null.html" height="250" width="100%" 
    frameBorder="0" >xxx</iframe>
  </ssf:box>
</div>
</td></tr>
</table>


