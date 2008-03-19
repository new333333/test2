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
<% //view a folder forum with folder on the left and the entry on the right in an iframe %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.team.domain.SeenMap" scope="request" />
<%
//Get the folder type of this definition (folder, file, or event)
String folderViewStyle = "folder";
Element folderViewTypeEle = (Element)ssConfigElement.selectSingleNode("properties/property[@name='type']");
if (folderViewTypeEle != null) folderViewStyle = folderViewTypeEle.attributeValue("value", "folder");
%>
<c:set var="ss_folderViewStyle" value="<%= folderViewStyle %>" scope="request" />


<div id="ss_showfolder${renderResponse.namespace}" class="ss_style ss_portlet ss_content_outer" 
  style="display:block;">

	<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>
<c:if test="${ss_displayType == 'ss_workarea' || ss_displayType == 'ss_forum'}">
	<%@ include file="/WEB-INF/jsp/forum/view_workarea_navbar.jsp" %>
</c:if>
<div class="ss_actions_bar1_pane" width="100%" style="height: 26px;">
<table cellspacing="0" cellpadding="0" width="100%">
<tr><td valign="middle">
<a href="javascript: ;" 
  onClick="ss_showHideSidebar('${renderResponse.namespace}');return false;"
><span style="padding-left:25px; display:none;"
  id="ss_sidebarHide${renderResponse.namespace}" 
  class="ss_bold"><ssf:nlt tag="toolbar.sidebar.show"/></span><span 
  style="padding-left:15px; display:block;"
  id="ss_sidebarShow${renderResponse.namespace}" 
  class="ss_bold"><ssf:nlt tag="toolbar.sidebar.hide"/></span></a>
</td><td valign="top">
<%@ include file="/WEB-INF/jsp/definition_elements/folder_toolbar.jsp" %>
</td></tr>
</table>
</div>
     <table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tbody>
    <tr>
    <td valign="top" class="ss_view_sidebar" id="ss_sidebarTd${renderResponse.namespace}">
    <div id="ss_sidebarDiv${renderResponse.namespace}" style="display:block;">

<c:if test="${ss_displayType != 'ss_workarea' && ss_displayType != 'ss_forum'}">
	<% // Navigation bar %>
	<jsp:include page="/WEB-INF/jsp/definition_elements/navbar.jsp" />
</c:if>

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

	</div>
	</td>
	<td valign="top" class="ss_view_info">
	    <div class="ss_style_color">
<c:if test="${ss_displayType != 'ss_workarea' && ss_displayType != 'ss_forum'}">
			<%@ include file="/WEB-INF/jsp/definition_elements/navigation_links.jsp" %>
</c:if>		
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
ss_createOnLoadObj('ss_initShowFolderDiv${renderResponse.namespace}', ss_initShowFolderDiv('${renderResponse.namespace}'));
</script>

<script type="text/javascript">
var ss_viewEntryPopupWidth = "<c:out value="${ss_entryWindowWidth}"/>px";
var ss_viewEntryPopupHeight = "<c:out value="${ss_entryWindowHeight}"/>px";
function ss_showForumEntryInIframe(url) {
    ss_debug('popup width = ' + ss_viewEntryPopupWidth)
    ss_debug('popup height = ' + ss_viewEntryPopupHeight)
    var wObj = self.document.getElementById('ss_showfolder')
    if (wObj == null) {
    	wObj = document.getElementsByTagName("body").item(0);
    }
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

