<%
// The dashboard "gallery search" component
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

<div>
<table><tr><td>
<c:choose>
<c:when test="${ssDashboard.dashboard.components[ssComponentId].data.galleryImageSize[0] == 'small'}">
<div class="ss_thumbnail_gallery ss_thumbnail_small"> 
</c:when>
<c:otherwise>
<div class="ss_thumbnail_gallery ss_thumbnail_big"> 
</c:otherwise>
</c:choose>
<c:set var="hitCount" value="0"/>
<c:forEach var="fileEntry" items="${ssDashboard.beans[ssComponentId].ssSearchFormData.searchResults}" >
 <c:if test="${not empty fileEntry._fileID}">

  <c:set var="hitCount" value="${hitCount + 1}"/>
    <div>
  	<c:choose>
  	<c:when test="${fileEntry._entityType == 'folderEntry'}">
    <a href="<ssf:url adapter="true" portletName="ss_forum" 
		    action="view_permalink"
		    binderId="${fileEntry._binderId}"
		    entryId="${fileEntry._docId}">
		    <ssf:param name="entityType" value="${fileEntry._entityType}" />
    	    <ssf:param name="newTab" value="1"/>
			</ssf:url>">
    </c:when>
    <c:when test="${fileEntry._entityType == 'user'}">
    <a href="<ssf:url adapter="true" portletName="ss_forum" 
			action="view_permalink"
			binderId="${fileEntry._principal.workspaceId}">
			<ssf:param name="entityType" value="workspace" />
    	    <ssf:param name="newTab" value="1"/>
			</ssf:url>" >
    </c:when>
    <c:when test="${fileEntry._entityType == 'group'}">
    <a target="_blank" href="<ssf:url action="view_profile_entry" 
    		folderId="${fileEntry._binderId}"
    		entryId="${fileEntry._docId}" />" >
    </c:when>
    <c:when test="${fileEntry._entityType == 'folder' || fileEntry._entityType == 'workspace' || fileEntry._entityType == 'profiles'}">
    <a href="<ssf:url adapter="true" portletName="ss_forum" 
		    action="view_permalink"
		    binderId="${fileEntry._docId}">
		    <ssf:param name="entityType" value="${fileEntry._entityType}" />
    	    <ssf:param name="newTab" value="1"/>
			</ssf:url>" >
    </c:when>
 	</c:choose>
    <img border="0" src="<ssf:url 
    webPath="viewFile"
    folderId="${fileEntry._binderId}"
    entryId="${fileEntry._docId}" >
    <ssf:param name="fileId" value="${fileEntry._fileID}"/>
    <ssf:param name="viewType" value="thumbnail"/>
    </ssf:url>"><br\>
    <c:out value="${fileEntry.title}"/></a></div>
 </c:if>

</c:forEach>
</div>
</table>

<c:if test="${hitCount > 0}">
  <div align="right">
    <span class="ss_light ss_fineprint">
	[<ssf:nlt tag="search.results">
	<ssf:param name="value" value="1"/>
	<ssf:param name="value" value="${hitCount}"/>
	<ssf:param name="value" value="${ssDashboard.beans[ssComponentId].ssSearchFormData.ssEntrySearchCount}"/>
	</ssf:nlt>]
	</span>
  </div>
</c:if>
<c:if test="${hitCount == 0}">
  <div>
    <span class="ss_light ss_fineprint">
	  [<ssf:nlt tag="search.noneFound"/>]
	</span>
  </div>
</c:if>
</div>
