<% //view a folder forum in accessible mode %>
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

<!-- Rounded box surrounding entire page (continuation of tabs metaphor) -->
<div class="ss_decor-round-corners-top1"><div><div></div></div></div>
	<div class="ss_decor-border3">
		<div class="ss_decor-border4">
			<div class="ss_rounden-content">
			  <div id="ss_tab_data_${ss_tabs.current_tab}">

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
function ss_showForumEntryInIframe(url) {
    self.location.href = url;
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

