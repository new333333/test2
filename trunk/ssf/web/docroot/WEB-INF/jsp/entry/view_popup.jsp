<% //view a folder forum with folder on the left and the entry on the right in an iframe %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.ef.domain.SeenMap" scope="request" />
<%
//Get the folder type of this definition (folder, file, or event)
String folderViewStyle = "folder";
Element folderViewTypeEle = (Element)ssConfigElement.selectSingleNode("properties/property[@name='type']");
if (folderViewTypeEle != null) folderViewStyle = folderViewTypeEle.attributeValue("value", "folder");
%>
<c:set var="ss_folderViewStyle" value="<%= folderViewStyle %>" scope="request" />

<div id="ss_showfolder" class="ss_style ss_portlet ss_content_outer" style="display:block; margin:2;">

<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>

<% // Navigation bar %>
<%@ include file="/WEB-INF/jsp/definition_elements/navbar.jsp" %>

<% // Tabs %>
<%@ include file="/WEB-INF/jsp/definition_elements/tabbar.jsp" %>
<div class="ss_clear"></div>

<div class="ss_tab_canvas">
<!-- Rounded box surrounding entire page (continuation of tabs metaphor) -->
<div class="ss_decor-round-corners-top1"><div><div></div></div></div>
	<div class="ss_decor-border3">
		<div class="ss_decor-border4">
			<div class="ss_rounden-content">
			  <div class="ss_style_color" id="ss_tab_data_${ss_tabs.current_tab}">

<% // Folder toolbar %>
<c:if test="${!empty ssFolderToolbar}">
<div class="ss_content_inner">
<ssf:toolbar toolbar="${ssFolderToolbar}" style="ss_actions_bar"/>
</div>
</c:if>

<div class="ss_content_inner">
<c:if test="${!ss_showSearchResults}">
<% // Navigation links %>
<%@ include file="/WEB-INF/jsp/definition_elements/navigation_links.jsp" %>
<br/>

<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${ssConfigElement}" 
  configJspStyle="${ssConfigJspStyle}" />
</c:if>
<c:if test="${ss_showSearchResults}">
<%@ include file="/WEB-INF/jsp/definition_elements/search_results_view.jsp" %>
</c:if>
</div>

<script type="text/javascript">
var ss_viewEntryPopupWidth = "<c:out value="${ss_entryWindowWidth}"/>px";
var ss_viewEntryPopupHeight = "<c:out value="${ss_entryWindowHeight}"/>px";
function ss_showForumEntryInIframe(url) {
    ss_debug('popup width = ' + ss_viewEntryPopupWidth)
    ss_debug('popup height = ' + ss_viewEntryPopupHeight)
    var wObj = self.document.getElementById('ss_showfolder')
	if (ss_viewEntryPopupWidth == "0px") ss_viewEntryPopupWidth = ss_getObjectWidth(wObj);
	if (ss_viewEntryPopupHeight == "0px") ss_viewEntryPopupHeight = parseInt(ss_getWindowHeight()) - 50;
    self.window.open(url, '_blank', 'width='+ss_viewEntryPopupWidth+',height='+ss_viewEntryPopupHeight+',resizable,scrollbars');
    return false;
}
</script>

			  </div>
			</div>
		</div>
	</div>
	<div class="ss_decor-round-corners-bottom1"><div><div></div></div></div>

<% // Footer toolbar %>
<%@ include file="/WEB-INF/jsp/definition_elements/footer_toolbar.jsp" %>

</div>
</div>


