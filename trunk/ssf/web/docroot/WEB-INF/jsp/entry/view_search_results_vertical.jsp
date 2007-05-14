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
<% //view a folder forum with the entry at the bottom in an iframe %>

<jsp:useBean id="ssSeenMap" type="com.sitescape.team.domain.SeenMap" scope="request" />
<%
	String iframeBoxId = renderResponse.getNamespace() + "_iframe_box_div";
	//int sliderDivHeight = 18;
	int sliderDivHeight = 7;
	String sliderDivOffset = "-20";

	//Get the folder type of this definition (folder, file, or event)
	String folderViewStyle = "folder";
	Element folderViewTypeEle = (Element)ssConfigElement.selectSingleNode("properties/property[@name='type']");
	if (folderViewTypeEle != null) folderViewStyle = folderViewTypeEle.attributeValue("value", "folder");
%>

<c:set var="ss_folderViewStyle" value="<%= folderViewStyle %>" scope="request" />
<a name="ss_top_of_folder"></a>
<div id="ss_showfolder" class="ss_style ss_portlet ss_content_outer">

	<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>

	<script type="text/javascript">
		//Set up variables needed by the javascript routines
		var ss_saveEntryHeightUrl = "<ssf:url 
			adapter="true" 
			portletName="ss_forum" 
			action="__ajax_request" 
			actionUrl="true" >
			<ssf:param name="operation" value="save_entry_height" />
			<ssf:param name="binderId" value="${ssFolder.id}" />
			</ssf:url>";
		var ss_folderTableId = 'ss_folder_table';
		var ss_iframe_box_div_name = '<portlet:namespace/>_iframe_box_div';
	</script>
	<script type="text/javascript" src="<html:rootPath/>js/forum/view_vertical.js"></script>

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
			<%@ include file="/WEB-INF/jsp/definition_elements/search/search_results_view.jsp" %>
		</div>

		<form class="ss_style ss_form" name="ss_saveEntryHeightForm" id="ss_saveEntryHeightForm" >
		<input type="hidden" name="entry_height">
		</form>

	</div>
</div>

<% // Footer toolbar %>
<jsp:include page="/WEB-INF/jsp/definition_elements/footer_toolbar.jsp" />

</div>
</div>

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

