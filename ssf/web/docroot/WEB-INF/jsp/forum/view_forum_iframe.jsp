<% //view a folder forum with folder on the left and the entry on the right in an iframe %>

<%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>
<%
String iframeBoxId = renderResponse.getNamespace() + "_iframe_box_div";
%>

<div id="ss_showfolder" class="ss_portlet" style="display:block; margin:2;">
<ssf:displayConfiguration configDefinition="<%= ssConfigDefinition %>" 
  configElement="<%= ssConfigElement %>" 
  configJspStyle="<%= ssConfigJspStyle %>" />
</div>

<div id="ss_showentrydiv" style="position:absolute; visibility:hidden; x:0; y:0;
  width:600; height:80%; display:none; z-index:10;">
  <ssf:box top="/WEB-INF/jsp/box/box_top.jsp" bottom="/WEB-INF/jsp/box/box_bottom.jsp">
    <ssf:param name="box_id" value="<%= iframeBoxId %>" />
    <ssf:param name="box_width" value="400" />
    <ssf:param name="box_show_close_icon" value="true" />
    <ssf:param name="box_show_close_routine" value="hideEntryDiv()" />
  <iframe id="ss_showentryframe" name="ss_showentryframe" style="width:100%; display:block;"
    src="<html:rootPath/>js/forum/null.html" height="95%" width="100%" 
    frameBorder="no" >xxx</iframe>
  </ssf:box>
</div>

<script language="javascript">
var ss_entryWindowWidth = <%= ss_entryWindowWidth %>;

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
    wObj.style.height = parseInt(wObj1.style.height) - 50 + "px";
    wObj.src = url
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
    if (ss_entryWindowWidth == 0) {ss_entryWindowWidth = parseInt((width * 3) / 4);}

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
    var top = parseInt(getDivTop('ss_showfolder') + 25);
    if (top < parseInt(self.document.body.scrollTop)) {top = parseInt(self.document.body.scrollTop + 4);} 
    var left = parseInt(getWindowWidth() - ss_entryWindowWidth - 14);
    var height = parseInt(getWindowHeight() + self.document.body.scrollTop - top - 25 );
    setObjectTop(wObj1, top)
    setObjectLeft(wObj1, left);
    setObjectWidth(wObj1, ss_entryWindowWidth);
    setObjectWidth(wObj2, ss_entryWindowWidth);
    setObjectWidth(wObj3, ss_entryWindowWidth);
    setObjectHeight(wObj1, height);
    wObj1.style.background = "#ffffff"
    wObj1.style.visibility = "visible";
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
