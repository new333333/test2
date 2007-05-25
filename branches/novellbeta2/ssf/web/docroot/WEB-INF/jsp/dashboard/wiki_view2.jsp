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
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<c:set var="componentId" value="${ssComponentId}"/>
<c:if test="${empty ssComponentId}">
<c:set var="componentId" value="${ssDashboard.ssComponentId}" />
</c:if>

<c:set var="wikiEntry" value="${ssDashboard.beans[componentId].wikiHomepageEntry}" />
<c:if test="${empty wikiEntry}">
  <span class="ss_light">--<ssf:nlt tag="entry.noWikiHomepageSet"/>--</span>
</c:if>
<c:if test="${!empty wikiEntry.title}">
<div>
<span class="ss_entryTitle">
  <a href="<ssf:url 
    folderId="${wikiEntry.parentFolder.id}" 
    action="view_folder_entry"
    entryId="${wikiEntry.id}">
	<ssf:param name="newTab" value="1"/>
    </ssf:url>"
	onClick="if (${ss_divId}_wikiurl) ${ss_divId}_wikiurl('${wikiEntry.parentFolder.id}','${wikiEntry.id}', '${wikiEntry.entityType}'); return false;">


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
</c:if>

