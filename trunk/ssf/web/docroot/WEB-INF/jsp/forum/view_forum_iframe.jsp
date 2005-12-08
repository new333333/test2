<% //view a folder forum with folder on the left and the entry on the right in an iframe %>

<%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>

<div id="showfolder" class="ss_portlet" style="display:block; margin:2;">
<ssf:displayConfiguration configDefinition="<%= ssConfigDefinition %>" 
  configElement="<%= ssConfigElement %>" 
  configJspStyle="<%= ssConfigJspStyle %>" />
</div>

<div id="showentrydiv" style="position:absolute; visibility:hidden; x:0; y:0;
  width:<%= ss_entryWindowWidth %>; height:80%; display:none;">
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
    src="<html:rootPath/>js/null.html" height="95%" width="100%" 
    frameBorder="no" >xxx</iframe>
</div>


<script language="javascript">
var entryWindowWidth = <%= ss_entryWindowWidth %>;

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
    wObj.style.height = parseInt(wObj1.style.height) - 50 + "px";
    wObj.src = url
    return false;
}

function positionEntryDiv() {
    var wObj1 = null
    if (isNSN || isNSN6 || isMoz5) {
        wObj1 = self.document.getElementById('showentrydiv')
    } else {
        wObj1 = self.document.all['showentrydiv']
    }
    var top = parseInt(getDivTop('showfolder'));
    if (top < parseInt(self.document.body.scrollTop)) {top = parseInt(self.document.body.scrollTop + 4);} 
    var left = parseInt(getDivWidth('showfolder') - entryWindowWidth - 14);
    var width = parseInt(entryWindowWidth);
    var height = parseInt(getWindowHeight() + self.document.body.scrollTop - top );
    setObjectTop(wObj1, top)
    setObjectLeft(wObj1, left);
    setObjectWidth(wObj1, width);
    setObjectHeight(wObj1, height);
    wObj1.style.background = "#ffffff"
    wObj1.style.visibility = "visible";
}

function hideEntryDiv() {
    var wObj1 = null
    if (isNSN || isNSN6 || isMoz5) {
        wObj1 = self.document.getElementById('showentrydiv')
    } else {
        wObj1 = self.document.all['showentrydiv']
    }
    wObj1.style.visibility = "hidden";
}

createOnLoadObj('positionEntryDiv', positionEntryDiv)
</script>
