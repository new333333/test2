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
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<c:set var="ssNamespace" value="${renderResponse.namespace}"/>
<c:if test="${!empty ssComponentId}">
<c:set var="ssNamespace" value="${ssNamespace}_${ssComponentId}"/>
</c:if>
<c:set var="ss_divId" value="ss_searchResults_${ssNamespace}"/>
<c:set var="ss_pageNumber" value="0"/>
<c:set var="componentId" value="${ssComponentId}"/>
<c:if test="${empty ssComponentId}">
<c:set var="componentId" value="${ssDashboard.ssComponentId}" />
</c:if>

<script type="text/javascript">

var ss_signGuestbookIframeOffset = 50;
function ss_showSignGuestbookIframe${ssNamespace}(obj) {
	var targetDiv = document.getElementById('${ssNamespace}_add_entry_from_iframe');
	var iframeDiv = document.getElementById('${ssNamespace}_new_guestbook_entry_iframe');
	if (window.frames['${ssNamespace}_new_guestbook_entry_iframe'] != null) {
		eval("var iframeHeight = parseInt(window.${ssNamespace}_new_guestbook_entry_iframe.document.body.scrollHeight);");
		if (iframeHeight > 0) {
			iframeDiv.style.height = iframeHeight + ss_signGuestbookIframeOffset + "px"
		}
	}
}
function ss_signGuestbook${ssNamespace}(obj) {

	var targetDiv = document.getElementById('${ssNamespace}_add_entry_from_iframe');
	if (targetDiv != null) {
		if (targetDiv.style.visibility == 'visible') {
			targetDiv.style.visibility = 'hidden';
			targetDiv.style.display = 'none';
			return;
		}
	}
	targetDiv.style.visibility = 'visible';
	targetDiv.style.display = 'block';
	var iframeDiv = document.getElementById('${ssNamespace}_new_guestbook_entry_iframe');
	iframeDiv.src = obj.href;
}

function ss_hideAddEntryIframe${ssNamespace}() {
	var targetDiv = document.getElementById('${ssNamespace}_add_entry_from_iframe');
	if (targetDiv != null) {
		targetDiv.style.visibility = 'hidden'
		targetDiv.style.display = 'none'
	}
}

</script>

<div style="text-align: right; margin: 5px; ">


<c:if test="${!empty ssComponentId && !empty ssDashboard.beans[componentId] && !empty ssDashboard.beans[componentId].ssSearchFormData && !empty ssDashboard.beans[componentId].ssSearchFormData.ssGuestbookBinder && !empty ssDashboard.beans[componentId].ssSearchFormData.ssGuestbookBinder.entryDefinitions[0]}">
<a href="<ssf:url adapter="true" portletName="ss_forum" 
		    action="add_folder_entry"
		    binderId="${ssDashboard.beans[componentId].ssSearchFormData.ssGuestbookBinder.id}">
		    <ssf:param name="entryType" value="${ssDashboard.beans[componentId].ssSearchFormData.ssGuestbookBinder.entryDefinitions[0].id}" />
    	    <ssf:param name="newTab" value="1"/>
    	    <ssf:param name="addEntryFromIFrame" value="1"/>
    	    <ssf:param name="namespace" value="${renderResponse.namespace}"/>    	        	    
			</ssf:url>" onClick="ss_signGuestbook${ssNamespace}(this);return false;">
<span class="ss_bold"><ssf:nlt tag="guestbook.addEntry"/></span>
</a>
</c:if>


</div>


<div id="${ssNamespace}_add_entry_from_iframe" style="display:none; visibility:hidden;">
<iframe id="${ssNamespace}_new_guestbook_entry_iframe"
  name="${ssNamespace}_new_guestbook_entry_iframe"
  onLoad="ss_showSignGuestbookIframe${ssNamespace}(this);" 
  width="100%">xxx</iframe>
</div>

<div id="ss_searchResults_${ssNamespace}">
<%@ include file="/WEB-INF/jsp/dashboard/guestbook_view2.jsp" %>
</div>
