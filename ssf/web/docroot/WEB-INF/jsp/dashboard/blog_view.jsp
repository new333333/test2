<%
// The dashboard "search" component
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
  //this is used by penlets and portlets
 
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<c:set var="ssNamespace" value="${renderResponse.namespace}"/>
<c:if test="${!empty ssComponentId}">
<c:set var="ssNamespace" value="${ssNamespace}_${ssComponentId}"/>
</c:if>

<c:set var="ss_divId" value="ss_searchResults_${ssNamespace}"/>
<c:set var="ss_pageNumber" value="0"/>
<c:if test="${ssDashboard.scope == 'portlet'}">

<script type="text/javascript">
//generic url for ajax
	var ss_dashboardAjaxUrl = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="true"/>";
    	
function ${ss_divId}_blogurl(binderId, entryId) {
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
</c:if>
<div id="${ss_divId}">
<%@ include file="/WEB-INF/jsp/dashboard/blog_view2.jsp" %>
</div>
