<%
/**
 * Copyright (c) 2005 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<script language="javascript">

var baseUrl = "<portlet:renderURL/>";
function ss_showUrlInPortlet(params) {
	var url = baseUrl + params
	self.location.href = url;
	return false;
}


function ss_loadEntry(obj) {
	ss_showMessageInDiv("Loading...");
	ss_showForumEntry(obj.href, ss_showEntryInDiv);
	return false;
}

function ss_showMessageInDiv(str) {
	ss_showEntryInDiv(str)
}

function ss_showForumEntry(url, callbackRoutine) {
	fetch_url(url, callbackRoutine);
}

function ss_showEntryInDiv(str) {
    var wObj1 = null
    var wObj2 = null
    if (isNSN || isNSN6 || isMoz5) {
        wObj1 = self.document.getElementById('ss_showentrydiv')
        wObj2 = self.document.getElementById('ss_showentry')
    } else {
        wObj1 = self.document.all['ss_showentrydiv']
        wObj2 = self.document.all['ss_showentry']
    }
    
    //If the entry div needs dynamic positioning, do it now
    ss_positionEntryDiv()
    
    wObj1.style.display = "block";
    wObj2.innerHTML = str;
    wObj1.style.visibility = "visible";
    
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
}

function ss_showForumEntryInIframe(url) {
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
    if (isNSN || isNSN6 || isMoz5) {
        wObj1 = self.document.getElementById('ss_showentrydiv')
    } else {
        wObj1 = self.document.all['ss_showentrydiv']
    }
    var top = parseInt(getDivTop('showbutton'));
    if (top < parseInt(self.document.body.scrollTop)) {top = parseInt(self.document.body.scrollTop + 4);} 
    var left = parseInt(getDivWidth('showbutton') - entryWindowWidth - 14);
    var width = parseInt(entryWindowWidth);
    var height = parseInt(getWindowHeight() + self.document.body.scrollTop - top );
    setObjectTop(wObj1, top)
    setObjectLeft(wObj1, left);
    setObjectWidth(wObj1, width);
    //setObjectHeight(wObj1, height);
    setObjectHeight(wObj1, "");
    wObj1.style.background = "#ffffff"
}

function hideEntryDiv() {
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

<span align="left">SiteScape Forum Widget Tester -  Fragment Widget</span>
<br />
<br />

<table><tr><td valign="top">
<div id="showbutton" class="ss_portlet" style="display:block; margin:2;">
<a href="<ssf:url 
    webPath="viewFragment" >
	<ssf:param name="operation" value="viewFragment" />
    </ssf:url>"
	onClick="ss_showForumEntryInIframe(this.href);return false;" 
 	>Show the fragment in an iframe</a><br>
<a href="<ssf:url 
    webPath="viewFragment" >
	<ssf:param name="operation" value="viewFragment" />
    </ssf:url>"
	onClick="ss_showForumEntryInWindow(this.href);return false;" 
 	>Show the fragment in a new window</a>
</div>
</td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<td>
</td><td>
<div id="ss_showentrydiv" style="display:block; margin:2; wwidth:400; height:80%;">
  <ssf:box top="/WEB-INF/jsp/box/box_top.jsp" bottom="/WEB-INF/jsp/box/box_bottom.jsp">
    <ssf:param name="box_width" value="400" />
    <ssf:param name="box_show_close_icon" value="true" />
    <ssf:param name="box_show_close_routine" value="hideEntryDiv()" />
  <iframe id="ss_showentryframe" name="ss_showentryframe" 
    src="<html:rootPath/>js/forum/null.html" height="250" width="100%" 
    frameBorder="no" >xxx</iframe>
  </ssf:box>
</div>
</td></tr>
</table>


