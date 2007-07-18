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
 //this is used by penlets and portlets
 //Don't include "include.jsp" directly 
%>
<%@ include file="/WEB-INF/jsp/dashboard/common_setup.jsp" %>

<c:if test="${!empty ssDashboard.beans[componentId].ssBinder}">
<c:set var="folder" value="${ssDashboard.beans[componentId].ssBinder}"/>

<table class="ss_style" cellspacing="0" cellpadding="0">
<tr>
  <td>
<c:if test="${ssConfigJspStyle != 'template'}">
    <a href="javascript: ;"
		onClick="return ss_gotoPermalink('${folder.parentBinder.id}', '${folder.parentBinder.id}', '${folder.parentBinder.entityIdentifier.entityType}', '${ss_namespace}', 'yes');"
		>${folder.parentBinder.title}</a> // 
    <a href="javascript: ;"
		onClick="return ss_gotoPermalink('${folder.id}', '${folder.id}', 'folder', '${ss_namespace}', 'yes');"
		><span class="ss_bold">${folder.title}</span></a>
</c:if>
<c:if test="${ssConfigJspStyle == 'template'}">
    ${folder.parentBinder.title} // <span class="ss_bold">${folder.title}</span>
</c:if>
</td></tr>
</table>
<br/>

</c:if>


<div id="${ss_divId}">

<c:set var="wikiEntry" value="${ssDashboard.beans[componentId].wikiHomepageEntry}" />
<c:if test="${empty wikiEntry}">
  <span class="ss_light">--<ssf:nlt tag="entry.noWikiHomepageSet"/>--</span>
</c:if>

<c:if test="${!empty wikiEntry}">
<div>
<span class="ss_entryTitle">
  <a href="<ssf:url 
    folderId="${wikiEntry.parentFolder.id}" 
    action="view_folder_entry"
    entryId="${wikiEntry.id}">
	<ssf:param name="newTab" value="1"/>
    </ssf:url>"
   <c:if test="${ssConfigJspStyle != 'template'}">
	onClick="return ss_gotoPermalink('${wikiEntry.parentFolder.id}','${wikiEntry.id}', '${wikiEntry.entityType}', '${ss_namespace}', 'yes'); ">
	</c:if>
	<c:if test="${ssConfigJspStyle == 'template'}"> 
	onClick="return false;">
	</c:if>

<c:if test="${!empty wikiEntry.title}">
<c:out value="${wikiEntry.title}"/>
</c:if>
<c:if test="${empty wikiEntry.title}">
  <span class="ss_light">--<ssf:nlt tag="entry.noTitle"/>--</span>
</c:if>
</a></span>
</div>

<c:if test="${!empty wikiEntry.description}">
<div class="ss_entryContent ss_entryDescription">
 <span><ssf:markup type="view" entity="${wikiEntry}"><c:out 
   value="${wikiEntry.description.text}" escapeXml="false"/></ssf:markup></span>
</div>
</c:if> 

</c:if> 
</div>
