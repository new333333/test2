<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<% //view a folder forum with the entry at the bottom in an iframe %>

<jsp:useBean id="ssSeenMap" type="com.sitescape.team.domain.SeenMap" scope="request" />
<%
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
<div id="ss_showfolder${renderResponse.namespace}" class="ss_style ss_portlet ss_content_outer">

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
		var ss_iframe_box_div_name = 'ss_iframe_box_div';
	</script>
	<script type="text/javascript" src="<html:rootPath/>js/forum/view_vertical.js"></script>

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
		<ssf:tree treeName="sidebarWsTree${renderResponse.namespace}" 
		  treeDocument="${ssSidebarWsTree}" 
		  highlightNode="${ssBinder.id}" 
		  showIdRoutine="ss_treeShowId"
		  namespace="${renderResponse.namespace}"
		  rootOpen="true"
		  nowrap="true"/>
		</c:if>
	</ssf:sidebarPanel>

	</td>
	<td valign="top" class="ss_view_info">

		<%@ include file="/WEB-INF/jsp/definition_elements/folder_toolbar.jsp" %>
		<%@ include file="/WEB-INF/jsp/definition_elements/navigation_links.jsp" %>

		<div id="ss_folder">
			<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
			    configElement="${ssConfigElement}" 
			    configJspStyle="${ssConfigJspStyle}" />
		</div>
			
		<c:if test="${ss_folderViewStyle != 'blog' && ss_folderViewStyle != 'wiki' && ss_folderViewStyle != 'guestbook' && ss_folderViewStyle != 'search'}">
			<div id="ss_showfolder_slider" align="center" onMousedown="ss_startDragDiv();"
			  onMouseover="if (self.ss_clearMouseOverInfo) {ss_clearMouseOverInfo(null);}" 
			  style="position:relative; margin:0px 2px 0px 2px; padding:0px; 
			    border-top:1px solid #666666; 
			    background-color:${ss_style_background_color};
			    cursor:n-resize; top:<%= sliderDivOffset %>px;"
			><table cellspacing="0" cellpadding="0" style="width:95%">
			<tr>
			<td style="background:url(<html:imagesPath/>icons/resize_pane_vertical.gif) center no-repeat;"><img 
			  border="0" style="height:<%= String.valueOf(sliderDivHeight) %>px;" 
			  <ssf:alt/> src="<html:imagesPath/>pics/1pix.gif"></td>
			</tr></table></div>

			<div id="ss_showentrydiv" class="ss_style ss_portlet" 
			  onMouseover="if (self.ss_clearMouseOverInfo) {ss_clearMouseOverInfo(null);}"
			  style="position:relative; padding:0px 0px 20px 2px; width: 99.5%;
			    top:<%= sliderDivOffset %>px;">
			  <ssf:box>
			    <ssf:param name="box_id" value="${renderResponse.namespace}_iframe_box_div" />
			    <ssf:param name="box_class" value="ss_style" />
			    <ssf:param name="box_style" value="margin:0px;" />
			    <ssf:param name="box_color" value="${ss_folder_border_color}" />
			    <ssf:param name="box_canvas_color" value="${ss_style_background_color}" />
			    <ssf:param name="box_title" useBody="true">
			    <!-- Set width to 0 to indicate "100%" -->
			    <ssf:param name="box_width" value="0" />
			      <div style="position:relative;">
			      <c:set var="ss_history_bar_table_class" value="ss_title_bar_history_bar" scope="request"/>
			      <%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>
			      </div>
			    </ssf:param>
			  <iframe id="ss_showentryframe" name="ss_showentryframe" style="width:100%; 
			    display:block; position:relative; left:5px;"
			    src="<html:rootPath/>js/forum/null.html" height="100" width="100%" 
			    onLoad="if (self.ss_setEntryDivHeight) ss_setEntryDivHeight();" frameBorder="0" >xxx</iframe>
			  </ssf:box>
			</div>
			
		</c:if>

		<form class="ss_style ss_form" name="ss_saveEntryHeightForm" id="ss_saveEntryHeightForm" >
		<input type="hidden" name="entry_height">
		</form>

<% // Footer toolbar %>
<jsp:include page="/WEB-INF/jsp/definition_elements/footer_toolbar.jsp" />

	</td>
	</tr>
	</tbody>
	</table>

</div>
<script type="text/javascript">
ss_createOnLoadObj('ss_initShowFolderDiv${renderResponse.namespace}', ss_initShowFolderDiv('${renderResponse.namespace}'));
</script>

<c:if test="${!empty ssEntryIdToBeShown && !empty ss_useDefaultViewEntryPopup}">
<script type="text/javascript">
function ss_showEntryToBeShown<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>() {
    var url = "<ssf:url     
		adapter="true" 
		portletName="ss_forum" 
		folderId="${ssBinder.id}" 
		action="view_folder_entry" 
		entryId="${ssEntryIdToBeShown}" 
		actionUrl="true" />" 
	ss_showForumEntryInIframe(url);
}
ss_createOnLoadObj('ss_showEntryToBeShown<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>', ss_showEntryToBeShown<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>);
</script>
</c:if>

