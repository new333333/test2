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
  <th align="left">Title</th>
  <th align="left">Author</th>
  <th align="left">Date</th>
</tr>
<c:forEach var="fileEntry" items="${ssDashboard.beans[ssDashboardId].ssSearchFormData.searchResults}" >
<tr id="folderLine_<c:out value="${fileEntry._docId}"/>">
  <td valign="top" width="35%">
    <a target="_blank" href="<ssf:url action="view_folder_entry" 
    folderId="${fileEntry._binderId}"
    entryId="${fileEntry._docId}" />" 
    >
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
