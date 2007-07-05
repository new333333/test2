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
  //this is used by penlets and portlets
  //Don't include "include.jsp" directly 
%>
<%@ include file="/WEB-INF/jsp/dashboard/common_setup.jsp" %>

<c:set var="ss_pageNumber" value="0"/>

<c:if test="${ssConfigJspStyle != 'template'}">
<c:if test="${!empty ssDashboard.beans[componentId].ssFolderList}">
<table class="ss_style" cellspacing="0" cellpadding="0" width="100%">
<c:forEach var="folder" items="${ssDashboard.beans[componentId].ssFolderList}">
<tr>
  <td>
    <a href="javascript: ;"
		onClick="return ss_gotoPermalink('${folder.parentBinder.id}', '${folder.parentBinder.id}', '${folder.parentBinder.entityIdentifier.entityType}', '${ss_namespace}', 'yes');"
		>${folder.parentBinder.title}</a> // 
    <a href="javascript: ;"
		onClick="return ss_gotoPermalink('${folder.id}', '${folder.id}', 'folder', '${ss_namespace}', 'yes');"
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
		    	    <ssf:param name="namespace" value="${ss_namespace}"/>    	        	    
					</ssf:url>" onClick="ss_signGuestbook('${ss_namespace}', this);return false;">
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
<div id="${ss_namespace}_add_entry_from_iframe" style="display:none; visibility:hidden;">
<iframe id="${ss_namespace}_new_guestbook_entry_iframe"
  name="${ss_namespace}_new_guestbook_entry_iframe"
  onLoad="ss_showSignGuestbookIframe('${ss_namespace}', this);" 
  width="100%" frameBorder="0">xxx</iframe>
</div>

<div id="${ss_divId}">
<%@ include file="/WEB-INF/jsp/dashboard/guestbook_view2.jsp" %>
</div>

</c:if>

<c:if test="${ssConfigJspStyle == 'template'}">
<table class="ss_style" cellspacing="0" cellpadding="0">
<c:forEach var="folder" items="${ssDashboard.beans[componentId].ssFolderList}">
<tr>
  <td>
    ${folder.parentBinder.title} // <span class="ss_bold">${folder.title}</span>
   </td>
</tr>
</c:forEach>
</table>
<br/>
</c:if>
