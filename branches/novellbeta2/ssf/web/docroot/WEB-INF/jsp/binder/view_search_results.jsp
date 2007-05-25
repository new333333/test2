<%
// The search results page
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

<table class="ss_style" style="width:100%;">
<tr>
  <th align="left"><ssf:nlt tag="folder.column.Title"/></th>
  <th align="left"><ssf:nlt tag="folder.column.Author"/></th>
  <th align="left"><ssf:nlt tag="folder.column.Date"/></th>
</tr>
<c:forEach var="fileEntry" items="${ss_searchResults}" >
<tr>
  <td valign="top" width="35%">
  	<c:choose>
  	<c:when test="${fileEntry._entityType == 'folderEntry'}">
    <a target="_blank" href="<ssf:url action="view_folder_entry" 
    folderId="${fileEntry._binderId}"
    entryId="${fileEntry._docId}" />" >
    </c:when>
    <c:when test="${fileEntry._entityType == 'user' || fileEntry._entityType == 'group'}">
    <a target="_blank" href="<ssf:url action="view_profile_entry" 
    folderId="${fileEntry._binderId}"
    entryId="${fileEntry._docId}" />" >
    </c:when>
    <c:when test="${fileEntry._entityType == 'folder'}">
    <a target="_blank" href="<ssf:url action="view_folder_listing" 
    folderId="${fileEntry._docId}" />" >
    </c:when>
    <c:when test="${fileEntry._entityType == 'workspace'}">
    <a target="_blank" href="<ssf:url action="view_ws_listing" 
    folderId="${fileEntry._docId}" />" >
    </c:when>
    <c:when test="{fileEntry._entityType == 'profiles'}">
    <a target="_blank" href="<ssf:url action="view_profile_listing" 
    folderId="${fileEntry._docId}" />" >
    </c:when>
 	</c:choose>
    <c:if test="${empty fileEntry.title}">
    <span class="ss_fineprint"><i>(no title)</i></span>
    </c:if>
    <c:out value="${fileEntry.title}"/></a>
  </td>
  <td valign="top" width="30%">
    <c:out value="${fileEntry._principal.title}"/>&nbsp;&nbsp;
  </td>
  <td valign="top" width="35%">
    <c:out value="${fileEntry._modificationDate}"/>
  </td>
</tr>
</c:forEach>
</table>
