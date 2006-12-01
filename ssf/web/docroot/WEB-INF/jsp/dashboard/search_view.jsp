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

<div>
<c:set var="hitCount" value="0"/>
<c:forEach var="fileEntry" items="${ssDashboard.beans[ssComponentId].ssSearchFormData.searchResults}" >
  <c:set var="hitCount" value="${hitCount + 1}"/>
  <div style="padding-bottom:6px;">
    <div>
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
    <span class="ss_bold ss_underline"><c:out value="${fileEntry.title}"/></span></a>

    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <span class="ss_smallprint">
      <c:out value="${fileEntry._principal.title}"/>,&nbsp;&nbsp;
	<fmt:formatDate timeZone="${fileEntry._principal.timeZone.ID}"
      value="${fileEntry._modificationDate}" type="both" 
	  timeStyle="short" dateStyle="short" /></span>
    
    
    &nbsp;&nbsp;&nbsp;
    <c:if test="${fileEntry._entityType == 'folderEntry' || 
      		fileEntry._entityType == 'reply'}">
      <a href="<ssf:url 
  		folderId="${fileEntry._binderId}" 
  		action="view_folder_listing">
    	<ssf:param name="binderId" value="${fileEntry._binderId}"/>
    	<ssf:param name="newTab" value="1"/>
    	</ssf:url>" 
    	onMouseover="ss_showObjInline('ss_folderName_${hitCount}');"
    	onMouseout="ss_hideObj('ss_folderName_${hitCount}');"
      >
      <c:if test="${empty ssBinderData[fileEntry._binderId].iconName}">
        <img src="<html:imagesPath/>icons/folder.gif"/>
      </c:if>
      <c:if test="${!empty ssBinderData[fileEntry._binderId].iconName}">
        <img src="<html:imagesPath/>${ssBinderData[fileEntry._binderId].iconName}" />
      </c:if>
       <div id="ss_folderName_${hitCount}" 
       style="position:absolute; display:none;">${ssBinderData[fileEntry._binderId].title}</div></a>
    </c:if>

    </div>
  
    <c:if test="${!empty fileEntry._desc}">
    <div class="ss_smallprint ss_indent_medium">  
      <c:out value="${fileEntry._desc}" escapeXml="false"/>&nbsp;&nbsp;
    </div>
    </c:if>
  
  </div>
</c:forEach>
  <div align="right">
    <span class="ss_light ss_fineprint">
	[<ssf:nlt tag="search.results">
	<ssf:param name="value" value="1"/>
	<ssf:param name="value" value="${hitCount}"/>
	<ssf:param name="value" value="${ssDashboard.beans[ssComponentId].ssSearchFormData.ssEntrySearchCount}"/>
	</ssf:nlt>]
	</span>
  </div>
</div>
