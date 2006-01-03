<% //view a folder forum with folder on the left and the entry on the right %>

<%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>
<br>
<%
String ssfBoxId = renderResponse.getNamespace() + "_ssf_box_div";
%>

<div id="ss_showfolder" class="ss_portlet" style="display:block; margin:2;">
<ssf:displayConfiguration configDefinition="<%= ssConfigDefinition %>" 
  configElement="<%= ssConfigElement %>" 
  configJspStyle="<%= ssConfigJspStyle %>" />
</div>

<div id="ss_showentrydiv" style="position:absolute; visibility:hidden; x:0; y:0; z-index:50;
  width:<%= ss_entryWindowWidth %>; height:80%; display:none;">
  <ssf:box>
    <ssf:param name="box_id" value="<%= ssfBoxId %>" />
    <ssf:param name="box_width" value="400" />
    <ssf:param name="box_show_close_icon" value="true" />
    <ssf:param name="box_show_close_routine" value="ss_hideEntryDiv()" />
  <div id="ss_showentry" style="width:100%;">
  </div>
  </ssf:box>
</div>

<script language="javascript">
var ss_entryWindowWidth = <%= ss_entryWindowWidth %>;
var ss_originalEntryDivHtml = null;
if (isNSN || isNSN6 || isMoz5) {
    wObj = self.document.getElementById('ss_showentrydiv')
} else {
    wObj = self.document.all['ss_showentrydiv']
}
ss_originalEntryDivHtml = wObj.innerHTML

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
    if (isNSN || isNSN6 || isMoz5) {
        wObj1 = self.document.getElementById('ss_showentrydiv')
        wObj2 = self.document.getElementById('<portlet:namespace/>_ssf_box_div')
    } else {
        wObj1 = self.document.all['ss_showentrydiv']
        wObj2 = self.document.all['<portlet:namespace/>_ssf_box_div']
    }
    
    var top = parseInt(getDivTop('ss_showfolder') + 25);
    if (top < parseInt(self.document.body.scrollTop)) {top = parseInt(self.document.body.scrollTop + 4);} 
    var left = parseInt(getWindowWidth() - ss_entryWindowWidth - 14);
    var height = parseInt(getWindowHeight() + self.document.body.scrollTop - top - 25 );
    setObjectTop(wObj1, top)
    setObjectLeft(wObj1, left);
    setObjectWidth(wObj1, ss_entryWindowWidth);
    setObjectWidth(wObj2, ss_entryWindowWidth);
    setObjectHeight(wObj1, "");
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

</script>
