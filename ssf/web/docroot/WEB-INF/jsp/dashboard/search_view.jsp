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

<table class="ss_style" style="width:100%;">
<tr>
  <th align="left"><ssf:nlt tag="folder.column.Folder"/></th>
  <th align="left"><ssf:nlt tag="folder.column.Title"/></th>
  <th align="left"><ssf:nlt tag="folder.column.Author"/></th>
  <th align="left"><ssf:nlt tag="folder.column.Date"/></th>
</tr>
<c:forEach var="fileEntry" items="${ssDashboard.beans[ssComponentId].ssSearchFormData.searchResults}" >
<tr>
  <td valign="top" width="25%">
    <c:if test="${fileEntry._entityType == 'folderEntry' || 
      		fileEntry._entityType == 'reply'}">
      <a href="<ssf:url 
  		folderId="${fileEntry._binderId}" 
  		action="view_folder_listing">
    	<ssf:param name="binderId" value="${fileEntry._binderId}"/>
    	<ssf:param name="newTab" value="1"/>
    	</ssf:url>" 
       ><span
      <c:if test="${empty ssBinderData[fileEntry._binderId].iconName}">
        style="background:url(<html:imagesPath/>icons/folder.gif)  no-repeat left;
        padding-left:20px;"
      </c:if>
      <c:if test="${!empty ssBinderData[fileEntry._binderId].iconName}">
        style="background:url(<html:imagesPath/>${ssBinderData[fileEntry._binderId].iconName})  no-repeat left;
        padding-left:20px;"
      </c:if>
       >${ssBinderData[fileEntry._binderId].title}</span></a>
    </c:if>
    <c:if test="${fileEntry._entityType == 'user'}">
      <a href="<ssf:url 
  		folderId="${fileEntry._binderId}" 
  		action="view_profile_listing" >
    	<ssf:param name="binderId" value="${fileEntry._binderId}"/>
    	<ssf:param name="newTab" value="1"/>
    	</ssf:url>" 
       ><span>${ssBinderData[fileEntry._binderId].title}</span>
    </c:if>
  </td
  <td valign="top" width="25%">
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
  <td valign="top" width="20%">
    <c:out value="${fileEntry._principal.title}"/>&nbsp;&nbsp;
  </td>
  <td valign="top" width="25%">
    <c:out value="${fileEntry._modificationDate}"/>
  </td>
</tr>
</c:forEach>
</table>
