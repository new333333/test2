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
