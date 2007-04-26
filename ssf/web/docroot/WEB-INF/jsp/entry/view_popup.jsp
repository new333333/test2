<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<% //view a folder forum with folder on the left and the entry on the right in an iframe %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.team.domain.SeenMap" scope="request" />
<%
//Get the folder type of this definition (folder, file, or event)
String folderViewStyle = "folder";
Element folderViewTypeEle = (Element)ssConfigElement.selectSingleNode("properties/property[@name='type']");
if (folderViewTypeEle != null) folderViewStyle = folderViewTypeEle.attributeValue("value", "folder");
%>
<c:set var="ss_folderViewStyle" value="<%= folderViewStyle %>" scope="request" />


<div id="ss_showfolder" class="ss_style ss_portlet ss_content_outer" style="display:block; margin:2px;">

	<c:if test="${ss_showSearchResults}">
		<%@ include file="/WEB-INF/jsp/search/search_result.jsp" %>
	</c:if>

	<c:if test="${!ss_showSearchResults}">
		<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>
		
		<% // Navigation bar %>
		<jsp:include page="/WEB-INF/jsp/definition_elements/navbar.jsp" />
		
		<% // Tabs %>
		<jsp:include page="/WEB-INF/jsp/definition_elements/tabbar.jsp" />
			
		<div class="ss_clear"></div>
		
		<div class="ss_tab_canvas">
			<!-- Rounded box surrounding entire page (continuation of tabs metaphor) -->
			<div class="ss_style_color" id="ss_tab_data_${ss_tabs.current_tab}">
			
				<% // Folder toolbar %>
				<div class="ss_content_inner">
					<ssf:toolbar toolbar="${ssFolderToolbar}" style="ss_actions_bar1 ss_actions_bar"/>
				</div>
	
				<div class="ss_content_inner">
					<% // Navigation links %>
					<%@ include file="/WEB-INF/jsp/definition_elements/navigation_links.jsp" %>
					
					<div align="right" width="100%">
						<%@ include file="/WEB-INF/jsp/definition_elements/tag_view.jsp" %>
					</div>
					
					<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
					  configElement="${ssConfigElement}" 
					  configJspStyle="${ssConfigJspStyle}" />
				</div>
			
			</div>
		</div>
	
		<% // Footer toolbar %>
		<jsp:include page="/WEB-INF/jsp/definition_elements/footer_toolbar.jsp" />
	</c:if>

</div>
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

<c:if test="${!empty ssEntryIdToBeShown && !empty ss_useDefaultViewEntryPopup}">
<script type="text/javascript">
function ss_showEntryToBeShown<portlet:namespace/>() {
    var url = "<ssf:url     
		adapter="true" 
		portletName="ss_forum" 
		folderId="${ssBinder.id}" 
		action="view_folder_entry" 
		entryId="${ssEntryIdToBeShown}" 
		actionUrl="true" />" 
	ss_showForumEntryInIframe(url);
}
ss_createOnLoadObj('ss_showEntryToBeShown<portlet:namespace/>', ss_showEntryToBeShown<portlet:namespace/>);
</script>
</c:if>

