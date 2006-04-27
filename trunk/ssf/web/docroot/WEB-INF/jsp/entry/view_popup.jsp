<% //view a folder forum with folder on the left and the entry on the right in an iframe %>
<%
//Get the folder type of this definition (folder, file, or event)
String folderViewStyle = "folder";
Element folderViewTypeEle = (Element)ssConfigElement.selectSingleNode("properties/property[@name='type']");
if (folderViewTypeEle != null) folderViewStyle = folderViewTypeEle.attributeValue("value", "folder");
%>
<c:set var="ss_folderViewStyle" value="<%= folderViewStyle %>" scope="request" />

<div id="ss_showfolder" class="ss_style ss_portlet" style="display:block; margin:2;">
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${ssConfigElement}" 
  configJspStyle="${ssConfigJspStyle}" />
</div>

<script type="text/javascript">

var ss_viewEntryPopupWidth = "<c:out value="${ss_entryWindowWidth}"/>px";
var ss_viewEntryPopupHeight = "<c:out value="${ss_entryWindowHeight}"/>px";
function ss_showForumEntryInIframe(url) {
    var wObj = null
    if (isNSN || isNSN6 || isMoz5) {
        wObj = self.document.getElementById('ss_showfolder')
    } else {
        wObj = self.document.all['ss_showfolder']
    }
	if (ss_viewEntryPopupWidth == "0px") ss_viewEntryPopupWidth = ss_getObjectWidth(wObj);
	if (ss_viewEntryPopupHeight == "0px") ss_viewEntryPopupHeight = parseInt(ss_getWindowHeight()) - 50;
    self.window.open(url, '_blank', 'width='+ss_viewEntryPopupWidth+',height='+ss_viewEntryPopupHeight+',resizable,scrollbars');
    return false;
}

</script>
