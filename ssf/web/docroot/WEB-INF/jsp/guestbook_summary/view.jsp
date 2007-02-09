<%
// The guestbook summary portlet
/**
 * Copyright (c) 2006 SiteScape, Inc. All rights reserved.
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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<span class="ss_bold"><ssf:nlt tag="portlet.forum.selected.folder"/>
	<a href="<portlet:renderURL><portlet:param 
		name="action" value="view_folder_listing"/><portlet:param 
		name="binderId" value="${ssBinder.id}"/></portlet:renderURL>">
		&nbsp;${ssBinder.title}</a></span><br/><br/>
<div class="ss_decor-border5">
  <div class="ss_decor-border6">
    <div class="ss_content_window">
<%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_js.jsp" %>
<c:set var="ssNamespace" value="${renderResponse.namespace}"/>
<c:set var="ss_divId" value="ss_searchResults_${ssNamespace}"/>

<script type="text/javascript">
function ${ss_divId}_guestbookUrl(binderId, entryId) {
	//Build a url to go to
	var url = '<portlet:renderURL windowState="maximized"><portlet:param 
		name="action" value="view_folder_entry"/><portlet:param 
		name="binderId" value="ssBinderIdPlaceHolder"/><portlet:param 
		name="entryId" value="ssEntryIdPlaceHolder"/><portlet:param 
		name="newTab" value="1"/></portlet:renderURL>';
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", binderId);
	url = ss_replaceSubStr(url, "ssEntryIdPlaceHolder", entryId);
	self.location.href = url;
	return false;
}
</script>

<%@ include file="/WEB-INF/jsp/dashboard/guestbook_view.jsp" %>

</div></div></div>