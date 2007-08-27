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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.team.domain.SeenMap" scope="request" />
<%
	//Get the folder type of this definition (folder, file, or event)
	String folderViewStyle = "folder";
	Element folderViewTypeEle = (Element)ssConfigElement.selectSingleNode("properties/property[@name='type']");
	if (folderViewTypeEle != null) folderViewStyle = folderViewTypeEle.attributeValue("value", "folder");
%>
<c:set var="ss_folderViewStyle" value="<%= folderViewStyle %>" scope="request" />
<div id="ss_showfolder" class="ss_style ss_portlet ss_content_outer">

	<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>

	<script type="text/javascript">
		//Define the variables needed by the javascript routines
		var ss_iframe_box_div_name = '<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_iframe_box_div';
		
		<c:if test="${!empty ss_entryWindowTop && !empty ss_entryWindowLeft}">
			var ss_entryWindowTopOriginal = ${ss_entryWindowTop};
			var ss_entryWindowTop = ${ss_entryWindowTop};
			var ss_entryWindowLeft = ${ss_entryWindowLeft};
		</c:if>
		<c:if test="${empty ss_entryWindowTop || empty ss_entryWindowLeft}">
			var ss_entryWindowTopOriginal = -1;
			var ss_entryWindowTop = -1;
			var ss_entryWindowLeft = -1;
		</c:if>
		
		var ss_saveEntryWidthUrl = "<ssf:url 
			adapter="true" 
			portletName="ss_forum" 
			action="__ajax_request" 
			actionUrl="true" >
			<ssf:param name="operation" value="save_entry_width" />
			</ssf:url>"
		
		var ss_forumRefreshUrl = "<html:rootPath/>js/forum/refresh.html";
		<c:if test="${empty ss_entryWindowWidth}">
			var ss_entryWindowWidth = 0;
		</c:if>
		<c:if test="${!empty ss_entryWindowWidth}">
			var ss_entryWindowWidth = "${ss_entryWindowWidth}";
		</c:if>
		var ss_entryBackgroundColor = "${ss_style_background_color}";
	</script>
	<script type="text/javascript" src="<html:rootPath/>js/forum/view_iframe.js"></script>

	<div id="ss_showfolder" class="ss_style ss_portlet ss_content_outer">
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

	</div>


	</td>
	<td valign="top" class="ss_view_info">
		<div class="ss_style_color">	
			<div class="ss_content_inner">
				<% // Navigation links %>
				<%@ include file="/WEB-INF/jsp/definition_elements/navigation_links.jsp" %>
				<br/>
				<%@ include file="/WEB-INF/jsp/forum/list_team_members.jsp" %>

				<c:if test="${!empty ss_reloadUrl}">
					<div style="text-align: right; padding-right: 2px; padding-bottom: 2px;"><a class="ss_linkButton" href="<c:out value="${ss_reloadUrl}" />"><ssf:nlt tag="__return_to" /> <ssf:nlt tag="__folder_view" /></a></div>
				</c:if>
			</div>
 		</div>
		<% // Footer toolbar %>
		<jsp:include page="/WEB-INF/jsp/definition_elements/footer_toolbar.jsp" />
	</td>
	</tr>
	</tbody>
	</table>
</div>

<form class="ss_style ss_form" name="ss_saveEntryWidthForm" id="ss_saveEntryWidthForm" >
	<input type="hidden" name="entry_width">
	<input type="hidden" name="entry_top">
	<input type="hidden" name="entry_left">
</form>

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
