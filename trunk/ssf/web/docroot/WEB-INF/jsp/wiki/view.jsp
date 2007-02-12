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

<script type="text/javascript">
//generic url for ajax
	var ss_dashboardAjaxUrl = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="true"/>";
    	
</script>
<span class="ss_bold"><ssf:nlt tag="portlet.forum.selected.folder"/>
	<a href="<portlet:renderURL><portlet:param 
		name="action" value="view_folder_listing"/><portlet:param 
		name="binderId" value="${ssBinder.id}"/></portlet:renderURL>">
		&nbsp;${ssBinder.title}</a></span><br/><br/>
<div class="ss_decor-border5">
  <div class="ss_decor-border6">
    <div class="ss_content_window">

<%@ include file="/WEB-INF/jsp/dashboard/wiki_view.jsp" %>

</div></div></div>