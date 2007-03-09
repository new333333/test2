<%
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
var url = "";
<c:forEach var="tab" items="${ss_tabs.tablist}">
  <c:if test="${tab.tabId == ss_tabs.current_tab}">
	<c:if test="${tab.type == 'binder'}">
		    url = "<ssf:url 
  				folderId="${tab.binderId}" 
  				action="view_folder_listing">
  				<ssf:param name="binderId" value="${tab.binderId}"/>
  				<ssf:param name="tabId" value="${tab.tabId}"/>
  				</ssf:url>" 
	</c:if>
	<c:if test="${tab.type == 'workspace'}">
		    url = "<ssf:url 
  				folderId="${tab.binderId}" 
  				action="view_ws_listing">
  				<ssf:param name="binderId" value="${tab.binderId}"/>
  				<ssf:param name="tabId" value="${tab.tabId}"/>
  				</ssf:url>" 
	</c:if>
	<c:if test="${tab.type == 'entry'}">
		    url = "<ssf:url 
  				folderId="${tab.binderId}" 
  				entryId="${tab.entryId}" 
  				action="view_folder_entry">
   				<ssf:param name="entryId" value="${tab.entryId}"/>
  				<ssf:param name="binderId" value="${tab.binderId}"/>
  				<ssf:param name="tabId" value="${tab.tabId}"/>
  				</ssf:url>" 
	</c:if>
	<c:if test="${tab.type == 'user'}">
		    url = "<ssf:url 
  				folderId="${tab.binderId}" 
  				entryId="${tab.entryId}" 
  				action="view_profile_entry">
   				<ssf:param name="entryId" value="${tab.entryId}"/>
   				<ssf:param name="binderId" value="${tab.binderId}"/>
  				<ssf:param name="tabId" value="${tab.tabId}"/>
  				</ssf:url>" 
	</c:if>
	<c:if test="${tab.type == 'profiles'}">
		    url = "<ssf:url 
  				folderId="${tab.binderId}" 
  				action="view_profile_listing">
  				<ssf:param name="binderId" value="${tab.binderId}"/>
  				<ssf:param name="tabId" value="${tab.tabId}"/>
  				</ssf:url>" 
	</c:if>
	<c:if test="${tab.type == 'query'}">
		    url = "<ssf:url 
  				action="view_search_results_listing">
  				<ssf:param name="tabId" value="${tab.tabId}"/>
  				</ssf:url>" 
	</c:if>
	setTimeout("self.location.replace('"+url+"');", 300);
</script>
<span class="ss_bold ss_italic"><ssf:nlt tag="Loading"/></span>
  </c:if>
</c:forEach>
