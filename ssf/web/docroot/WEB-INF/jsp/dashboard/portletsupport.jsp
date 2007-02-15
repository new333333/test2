<%
/**
 * Copyright (c) 2007 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
 */
%>
<script type="text/javascript">
//generic url for ajax
	var ss_dashboardAjaxUrl = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="true"/>";
    	
function ss_dashboardPorletUrlSupport(binderId, entryId, type) {
	//Build a url to go to
	var entryUrl = '<portlet:renderURL windowState="maximized"><portlet:param 
		name="action" value="ssActionPlaceHolder"/><portlet:param 
		name="binderId" value="ssBinderIdPlaceHolder"/><portlet:param 
		name="entryId" value="ssEntryIdPlaceHolder"/><portlet:param 
		name="newTab" value="1"/></portlet:renderURL>';
	var folderUrl = '<portlet:renderURL windowState="maximized"><portlet:param 
		name="action" value="ssActionPlaceHolder"/><portlet:param 
		name="binderId" value="ssBinderIdPlaceHolder"/><portlet:param 
		name="newTab" value="1"/></portlet:renderURL>';
	var url;
	if (type == 'folderEntry') {
		url = ss_replaceSubStr(entryUrl, "ssBinderIdPlaceHolder", binderId);
		url = ss_replaceSubStr(url, "ssEntryIdPlaceHolder", entryId);
		url = ss_replaceSubStr(url, "ssActionPlaceHolder", 'view_folder_entry');
	} else if (type == 'user') {
		url = ss_replaceSubStr(entryUrl, "ssBinderIdPlaceHolder", binderId);
		url = ss_replaceSubStr(url, "ssEntryIdPlaceHolder", entryId);
		url = ss_replaceSubStr(url, "ssActionPlaceHolder", 'view_ws_listing');
	} else if (type == 'folder') {
		url = ss_replaceSubStr(folderUrl, "ssBinderIdPlaceHolder", binderId);
		url = ss_replaceSubStr(url, "ssActionPlaceHolder", 'view_folder_listing');	
	} else if (type == 'workspace') {
		url = ss_replaceSubStr(folderUrl, "ssBinderIdPlaceHolder", binderId);
		url = ss_replaceSubStr(url, "ssActionPlaceHolder", 'view_ws_listing');	
	} else if (type == 'profiles') {
		url = ss_replaceSubStr(folderUrl, "ssBinderIdPlaceHolder", binderId);
		url = ss_replaceSubStr(url, "ssActionPlaceHolder", 'view_profile_listing');
	} 
	
	self.location.href = url;
	return false;
}
</script>
