<% //view a folder forum with folder on the left and the entry on the right %>

<%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>

<div id="showfolder" class="ss_portlet" style="display:block; margin:2;">
<ssf:displayConfiguration configDefinition="<%= ssConfigDefinition %>" 
  configElement="<%= ssConfigElement %>" 
  configJspStyle="<%= ssConfigJspStyle %>" />
</div>

<div id="showentrydiv" style="position:absolute; visibility:hidden; x:0; y:0;
  width:<%= ss_entryWindowWidth %>; height:80%; display:none;">
  <div id="showentry" style="width:100%;">
  </div>
</div>

<script language="javascript">
var entryWindowWidth = <%= ss_entryWindowWidth %>;
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
    //setObjectHeight(wObj1, height);
    setObjectHeight(wObj1, "");
    wObj1.style.background = "#ffffff"
}
createOnLoadObj('positionEntryDiv', positionEntryDiv)
</script>
