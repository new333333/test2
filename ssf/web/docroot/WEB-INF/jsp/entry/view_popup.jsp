<% //view a folder forum with folder on the left and the entry on the right in an iframe %>

<%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>

<div id="ss_showfolder" class="ss_portlet" style="display:block; margin:2;">
<ssf:displayConfiguration configDefinition="<%= ssConfigDefinition %>" 
  configElement="<%= ssConfigElement %>" 
  configJspStyle="<%= ssConfigJspStyle %>" />
</div>

<script language="javascript">

function ss_showForumEntryInIframe(url) {
    var wObj = null
    if (isNSN || isNSN6 || isMoz5) {
        wObj = self.document.getElementById('ss_showfolder')
    } else {
        wObj = self.document.all['ss_showfolder']
    }
    var width = getObjectWidth(wObj);
    var height = parseInt(getWindowHeight()) - 50;
    self.window.open(url, '_blank', 'width='+width+',height='+height+',resizable,scrollbars');
    return false;
}

</script>
