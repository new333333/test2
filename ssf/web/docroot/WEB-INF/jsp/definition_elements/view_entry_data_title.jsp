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
<% //Title view %>

<jsp:useBean id="ssUser" type="com.sitescape.team.domain.User" scope="request" />

<%
String displayStyle = ssUser.getDisplayStyle();
if (displayStyle == null || displayStyle.equals("")) {
	displayStyle = ObjectKeys.USER_DISPLAY_STYLE_IFRAME;
}
%>

<div class="ss_entryContent">
<span class="ss_entryTitle">
	<c:if test="${!empty ssDefinitionEntry.docNumber}">
	  <c:out value="${ssDefinitionEntry.docNumber}"/>.
	</c:if>
		<ssf:menuLink displayDiv="false" action="view_folder_entry" adapter="true" entryId="${ssDefinitionEntry.id}" 
		folderId="${ssDefinitionEntry.parentFolder.id}" binderId="${ssDefinitionEntry.parentFolder.id}" entityType="${ssDefinitionEntry.entityType}"
		imageId='menuimg_${ssDefinitionEntry.id}_${renderResponse.namespace}_${ssDefinitionEntry.id}' 
	    menuDivId="ss_emd_${renderResponse.namespace}_${ssDefinitionEntry.id}"
		linkMenuObjIdx="${renderResponse.namespace}_${ssDefinitionEntry.id}" 
		namespace="${renderResponse.namespace}_${ssDefinitionEntry.id}"
		entryCallbackRoutine="${showEntryCallbackRoutine}">
	
			<ssf:param name="url" useBody="true">
				<ssf:url adapter="true" portletName="ss_forum" folderId="${ssDefinitionEntry.parentFolder.id}" 
				action="view_folder_entry" entryId="${ssDefinitionEntry.id}" actionUrl="true" />
			</ssf:param>
	
			<c:if test="${empty ssDefinitionEntry.title}">
			  <span class="ss_light">
			    --<ssf:nlt tag="entry.noTitle"/>--
			  </span>
			</c:if>
	
			<c:out value="${ssDefinitionEntry.title}"/>
		</ssf:menuLink>

</span>

</div>

<ssf:menuLink displayDiv="true" menuDivId="ss_emd_${renderResponse.namespace}_${ssDefinitionEntry.id}" linkMenuObjIdx="${renderResponse.namespace}_${ssDefinitionEntry.id}" 
	namespace="${renderResponse.namespace}_${ssDefinitionEntry.id}">
</ssf:menuLink>

<script type="text/javascript">
var ss_displayStyle = "<%= displayStyle %>";

if (!ss_baseEntryUrl || !ss_baseBinderUrl) {
	var ss_baseEntryUrl = '';
	var ss_baseBinderUrl = '';
}

//This function just reloads the current link
function ss_showForumEntry(url, callbackRoutine, isDashboard) {
	//self.location.href = url;
	self.location.reload(true);
}

function ss_loadEntry() {
	self.location.reload(true);
}

var ss_viewEntryPopupWidth = "<c:out value="${ss_entryWindowWidth}"/>px";
var ss_viewEntryPopupHeight = "<c:out value="${ss_entryWindowHeight}"/>px";

function ss_showForumEntryInPopupWindow(definitionType) {
	var strAddWindowOpenParams = "";
	if (definitionType != null && (definitionType == 'folder' || definitionType == 'profiles' || 
		definitionType == 'user' || definitionType == 'group' || definitionType == 'workspace') ) {
		strAddWindowOpenParams = ",toolbar,menubar";
	}

    ss_debug('popup width = ' + ss_viewEntryPopupWidth)
    ss_debug('popup height = ' + ss_viewEntryPopupHeight)
    var wObj = self.document.getElementById('ss_showfolder')
	if (ss_viewEntryPopupWidth == "0px") ss_viewEntryPopupWidth = ss_getObjectWidth(wObj);
	if (ss_viewEntryPopupHeight == "0px") ss_viewEntryPopupHeight = parseInt(ss_getWindowHeight()) - 50;
	
    self.window.open(menuLinkAdapterURL, '_blank', 'width='+ss_viewEntryPopupWidth+',height='+ss_viewEntryPopupHeight+',resizable,scrollbars'+strAddWindowOpenParams);
    return false;
}
</script>