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


function loadEntry(obj) {
	showEntryMessageInDiv("Loading...");
	showForumEntry(obj.href, showEntryInDiv);
	return false;
}

function showMessageInDiv(str) {
	showEntryInDiv(str)
}

function showForumEntry(url, callbackRoutine) {
	fetch_url(url, callbackRoutine);
}

function showEntryInDiv(str) {
    var wObj1 = null
    var wObj2 = null
    if (isNSN || isNSN6 || isMoz5) {
        wObj1 = self.document.getElementById('showentrydiv')
        wObj2 = self.document.getElementById('showentry')
    } else {
        wObj1 = self.document.all['showentrydiv']
        wObj2 = self.document.all['showentry']
    }
    
    //If the entry div needs dynamic positioning, do it now
    positionEntryDiv()
    
    wObj1.style.display = "block";
    wObj2.innerHTML = str;
    wObj1.style.visibility = "visible";
    
    //Get the position of the div displaying the entry
    if (autoScroll == "true") {
	    var entryY = getDivTop('showentrydiv')
	    var entryH = getDivHeight('showentrydiv')
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

function showForumEntryInIframe(url) {
	positionEntryDiv();
    var wObj
    if (isNSN || isNSN6 || isMoz5) {
        wObj = self.document.getElementById('showentryframe')
    } else {
        wObj = self.document.all['showentryframe']
    }
    
    var wObj1 = null
    if (isNSN || isNSN6 || isMoz5) {
        wObj1 = self.document.getElementById('showentrydiv')
    } else {
        wObj1 = self.document.all['showentrydiv']
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


var entryWindowWidth = 400;
function positionEntryDiv() {
return
    var wObj1 = null
    if (isNSN || isNSN6 || isMoz5) {
        wObj1 = self.document.getElementById('showentrydiv')
    } else {
        wObj1 = self.document.all['showentrydiv']
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
createOnLoadObj('positionEntryDiv', positionEntryDiv)

</script>

<span align="left">SiteScape Forum Widget Tester -  Fragment Widget</span>
<br />
<br />

<table><tr><td>
<div id="showbutton" class="ss_portlet" style="display:block; margin:2;">
<a href="<portlet:renderURL>
	<portlet:param name="action" value="fragment" />
	<portlet:param name="operation" value="showFragment" />
	</portlet:renderURL>" 
	onClick="showForumEntryInIframe(this.href);return false;" 
 	>Show the fragment</a>
</div>
</td><td>
<div id="showentrydiv" style="display:block; margin:2; width:400; height:80%;">
  <div style="width:90%;">
    <table cellspacing="0" cellpadding="0" width="100%">
      <tr>
        <td align="right">
          <a href="javascript: ;" onClick="hideEntryDiv();return false;">Close</a>
        </td>
      </tr>
    </table>
  </div>
  <iframe id="showentryframe" name="showentryframe" 
    src="<html:rootPath/>js/forum/null.html" height="250" width="400" 
    frameBorder="no" >xxx</iframe>
</di</td></tr>
</table>


