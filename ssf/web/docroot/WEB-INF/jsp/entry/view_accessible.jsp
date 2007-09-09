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
<% //view a folder forum in accessible mode %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.team.domain.SeenMap" scope="request" />

<%
//Get the folder type of this definition (folder, file, or event)
String folderViewStyle = "folder";
Element folderViewTypeEle = (Element)ssConfigElement.selectSingleNode("properties/property[@name='type']");
if (folderViewTypeEle != null) folderViewStyle = folderViewTypeEle.attributeValue("value", "folder");
%>
<c:set var="ss_folderViewStyle" value="<%= folderViewStyle %>" scope="request" />

<div id="ss_showfolder" class="ss_style ss_portlet ss_content_outer" style="display:block; margin:2px;">

	<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>
    <table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tbody>
    <tr>
    <td valign="top" class="ss_view_sidebar">

	<% // Navigation bar %>
	<jsp:include page="/WEB-INF/jsp/definition_elements/navbar.jsp" />

	<% // Tabs %>
	<jsp:include page="/WEB-INF/jsp/definition_elements/tabbar.jsp" />

	<% // Folder Sidebar %>

    <%@ include file="/WEB-INF/jsp/sidebars/sidebar_dispatch.jsp" %>


    <ssf:sidebarPanel title="__definition_default_workspace" id="ss_workspace_sidebar"
        initOpen="true" sticky="true">
		<c:if test="${!empty ssSidebarWsTree}">
		<ssf:tree treeName="sidebarWsTree" 
		  treeDocument="${ssSidebarWsTree}" 
		  highlightNode="${ssBinder.id}" 
		  showIdRoutine="ss_tree_showId"
		  rootOpen="true"
		  nowrap="true"/>
		</c:if>
	</ssf:sidebarPanel>

	</td>
	<td valign="top" class="ss_view_info">
	    <div class="ss_style_color" id="ss_tab_data_${ss_tabs.current_tab}">
			<%@ include file="/WEB-INF/jsp/definition_elements/folder_toolbar.jsp" %>
			<%@ include file="/WEB-INF/jsp/definition_elements/navigation_links.jsp" %>
		
			<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
					  configElement="${ssConfigElement}" 
					  configJspStyle="${ssConfigJspStyle}" />
		</div>
		<% // Footer toolbar %>
		<jsp:include page="/WEB-INF/jsp/definition_elements/footer_toolbar.jsp" />
	</td>
	</tr>
	</tbody>
	</table>

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
function ss_showEntryToBeShown<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>() {
    var url = "<ssf:url     
		folderId="${ssBinder.id}" 
		action="view_folder_entry" 
		entryId="${ssEntryIdToBeShown}" 
		actionUrl="true" />" 
	self.location.href = url;
}
ss_createOnLoadObj('ss_showEntryToBeShown<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>', ss_showEntryToBeShown<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>);
</script>
</c:if>

