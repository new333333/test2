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
	<%@ include file="/WEB-INF/jsp/search/search_result.jsp" %>
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

