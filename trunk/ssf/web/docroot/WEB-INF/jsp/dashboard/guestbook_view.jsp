<%
// The dashboard "search" component
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
</c:if>
<script type="text/javascript">    	
function ${ss_divId}_guestbookurl(binderId, entryId, type) {
	return ss_gotoPermalink(binderId, entryId, type, '${portletNamespace}', 'yes');
}
</script>

<c:if test="${ssConfigJspStyle == 'template'}">
<script type="text/javascript">
function ${ss_divId}_guestbookurl(binderId, entryId, type) {
	return false;
}
</script>
</c:if>

<script type="text/javascript" src="<html:rootPath/>js/common/guestbook.js"></script>

<c:if test="${!empty ssDashboard.beans[ssComponentId].ssFolderList}">
<table class="ss_style" cellspacing="0" cellpadding="0" width="100%">
<c:forEach var="folder" items="${ssDashboard.beans[ssComponentId].ssFolderList}">
<tr>
  <td>
    <a href="javascript: ;"
		onClick="return ${ss_divId}_guestbookurl('${folder.parentBinder.id}', '${folder.parentBinder.id}', '${folder.parentBinder.entityIdentifier.entityType}');"
		>${folder.parentBinder.title}</a> // 
    <a href="javascript: ;"
		onClick="return ${ss_divId}_guestbookurl('${folder.id}', '${folder.id}', 'folder');"
		><span class="ss_bold">${folder.title}</span></a>
  </td>
  <td valign="top" align="right">
	<div style="text-align: right; margin: 5px; ">
		<c:if test="${!empty ssDashboard.beans[componentId].ssBinder && !empty ssDashboard.beans[componentId].ssBinder.entryDefinitions[0]}">
		<a class="ss_linkButton" href="<ssf:url adapter="true" portletName="ss_forum" 
				    action="add_folder_entry"
				    binderId="${ssDashboard.beans[componentId].ssBinder.id}">
				    <ssf:param name="entryType" value="${ssDashboard.beans[componentId].ssBinder.entryDefinitions[0].id}" />
		    	    <ssf:param name="newTab" value="1"/>
		    	    <ssf:param name="addEntryFromIFrame" value="1"/>
		    	    <ssf:param name="namespace" value="${renderResponse.namespace}"/>    	        	    
					</ssf:url>" onClick="<c:if test="${ssConfigJspStyle != 'template'}">ss_signGuestbook('${ssNamespace}', this);</c:if>return false;">
		<span class="ss_bold"><ssf:nlt tag="guestbook.addEntry"/></span>
		</a>
		</c:if>
	</div>
  </td>
</tr>
</c:forEach>
</table>
<br/>
</c:if>



<div id="${ssNamespace}_add_entry_from_iframe" style="display:none; visibility:hidden;">
<iframe id="${ssNamespace}_new_guestbook_entry_iframe"
  name="${ssNamespace}_new_guestbook_entry_iframe"
  onLoad="ss_showSignGuestbookIframe('${ssNamespace}', this);" 
  width="100%">xxx</iframe>
</div>

<div id="${ss_divId}">
<%@ include file="/WEB-INF/jsp/dashboard/guestbook_view2.jsp" %>
</div>

