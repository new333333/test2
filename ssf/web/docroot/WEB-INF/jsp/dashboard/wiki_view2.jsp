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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="componentId" value="${ssComponentId}"/>
<c:if test="${empty ssComponentId}">
<c:set var="componentId" value="${ssDashboard.ssComponentId}" />
</c:if>

<c:set var="wikiEntry" value="${ssDashboard.beans[componentId].wikiHomepageEntry}" />
<div>
<span class="ss_entryTitle">
  <a style="text-decoration: none;" href="<ssf:url 
    folderId="${wikiEntry.parentFolder.id}" 
    action="view_folder_entry"
    entryId="${wikiEntry.id}">
	<ssf:param name="newTab" value="1"/>
    </ssf:url>">
<c:if test="${empty wikiEntry.title}">
  <span class="ss_light">--<ssf:nlt tag="entry.noTitle"/>--</span>
</c:if><c:out value="${wikiEntry.title}"/></a></span>
</div>

<c:if test="${!empty wikiEntry.description}">
<div class="ss_entryContent ss_entryDescription">
 <span><ssf:markup type="view" entity="${wikiEntry}"><c:out 
   value="${wikiEntry.description.text}" escapeXml="false"/></ssf:markup></span>
</div>
</c:if>

