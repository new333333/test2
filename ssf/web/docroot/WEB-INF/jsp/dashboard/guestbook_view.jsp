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
<c:set var="portletNamespace" value=""/>
<ssf:ifnotadapter>
<c:set var="portletNamespace" value="${renderResponse.namespace}"/>
</ssf:ifnotadapter>

<c:set var="ss_divId" value="ss_searchResults_${ssNamespace}"/>
<c:set var="ss_pageNumber" value="0"/>

<c:set var="componentId" value="${ssComponentId}"/>
<c:if test="${empty ssComponentId}">
<c:set var="componentId" value="${ssDashboard.ssComponentId}" />
</c:if>
<c:if test="${ssDashboard.scope == 'portlet'}">
<%@ include file="/WEB-INF/jsp/dashboard/portletsupport.jsp" %>
<script type="text/javascript">    	
function ${ss_divId}_guestbookurl(binderId, entryId, type) {
	return ss_gotoPermalink(binderId, entryId, type, '${portletNamespace}');
}
</script>
</c:if>

<c:if test="${ssConfigJspStyle == 'template'}">
<script type="text/javascript">
function ${ss_divId}_guestbookurl(binderId, entryId, type) {
	return false;
}
</script>
</c:if>

<script type="text/javascript" src="<html:rootPath/>js/common/guestbook.js"></script>


<div style="text-align: right; margin: 5px; ">
	<c:if test="${!empty ssComponentId && !empty ssDashboard.beans[componentId] && !empty ssDashboard.beans[componentId].ssSearchFormData && !empty ssDashboard.beans[componentId].ssSearchFormData.ssGuestbookBinder && !empty ssDashboard.beans[componentId].ssSearchFormData.ssGuestbookBinder.entryDefinitions[0]}">
	<a href="<ssf:url adapter="true" portletName="ss_forum" 
			    action="add_folder_entry"
			    binderId="${ssDashboard.beans[componentId].ssSearchFormData.ssGuestbookBinder.id}">
			    <ssf:param name="entryType" value="${ssDashboard.beans[componentId].ssSearchFormData.ssGuestbookBinder.entryDefinitions[0].id}" />
	    	    <ssf:param name="newTab" value="1"/>
	    	    <ssf:param name="addEntryFromIFrame" value="1"/>
	    	    <ssf:param name="namespace" value="${renderResponse.namespace}"/>    	        	    
				</ssf:url>" onClick="ss_signGuestbook('${ssNamespace}', this);return false;">
	<span class="ss_bold"><ssf:nlt tag="guestbook.addEntry"/></span>
	</a>
	</c:if>
</div>


<div id="${ssNamespace}_add_entry_from_iframe" style="display:none; visibility:hidden;">
<iframe id="${ssNamespace}_new_guestbook_entry_iframe"
  name="${ssNamespace}_new_guestbook_entry_iframe"
  onLoad="ss_showSignGuestbookIframe('${ssNamespace}', this);" 
  width="100%">xxx</iframe>
</div>

<div id="${ss_divId}">
<%@ include file="/WEB-INF/jsp/dashboard/guestbook_view2.jsp" %>
</div>

