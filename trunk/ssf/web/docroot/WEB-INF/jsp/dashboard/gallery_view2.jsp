<%
// The dashboard "gallery search" component
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
<c:set var="portletNamespace" value=""/>
<ssf:ifnotadapter>
<c:set var="portletNamespace" value="${renderResponse.namespace}"/>
</ssf:ifnotadapter>

<table><tr><td>
<c:choose>
<c:when test="${ssDashboard.dashboard.components[componentId].data.galleryImageSize == 'small'}">
<div class="ss_thumbnail_gallery ss_thumbnail_small"> 
</c:when>
<c:otherwise>
<div class="ss_thumbnail_gallery ss_thumbnail_big"> 
</c:otherwise>
</c:choose>
<c:set var="hitCount" value="0"/>
<c:set var="resultCount" value="0"/>
<c:forEach var="fileEntry" items="${ssDashboard.beans[componentId].ssSearchFormData.searchResults}" >
  <c:set var="resultCount" value="${resultCount + 1}"/>
  <c:if test="${not empty fileEntry._fileID}">

  <c:set var="hitCount" value="${hitCount + 1}"/>
    <div>
	  <a href="<ssf:url 
	    webPath="viewFile"
	    folderId="${fileEntry._binderId}"
	    entryId="${fileEntry._docId}" >
	    <ssf:param name="entityType" value="${fileEntry._entityType}"/>
	    <ssf:param name="fileId" value="${fileEntry._fileID}"/>
	    </ssf:url>"
		onClick="return ss_openUrlInWindow(this, '_blank');">
    <img <ssf:alt text="${fileEntry.title}"/> border="0" src="<ssf:url 
    webPath="viewFile"
    folderId="${fileEntry._binderId}"
    entryId="${fileEntry._docId}" >
	<ssf:param name="entityType" value="${fileEntry._entityType}" />
    <ssf:param name="fileId" value="${fileEntry._fileID}"/>
    <ssf:param name="viewType" value="thumbnail"/>
    </ssf:url>"></a>
    <br\>
  	<c:choose>
  	<c:when test="${fileEntry._entityType == 'folderEntry'}">
    <a href="<ssf:url adapter="true" portletName="ss_forum" 
		    action="view_permalink"
		    binderId="${fileEntry._binderId}"
		    entryId="${fileEntry._docId}">
		    <ssf:param name="entityType" value="${fileEntry._entityType}" />
    	    <ssf:param name="newTab" value="1"/>
			</ssf:url>"
		onClick="return ss_gotoPermalink('${fileEntry._binderId}','${fileEntry._docId}', '${fileEntry._entityType}', '${portletNamespace}', 'yes');">

    </c:when>
    <c:when test="${fileEntry._entityType == 'user'}">
    <a href="<ssf:url adapter="true" portletName="ss_forum" 
			action="view_permalink"
			binderId="${fileEntry._principal.workspaceId}">
			<ssf:param name="entityType" value="workspace" />
    	    <ssf:param name="newTab" value="1"/>
			</ssf:url>" 
		onClick="return ss_gotoPermalink('${fileEntry._binderId}','${fileEntry._docId}', '${fileEntry._entityType}', '${portletNamespace}', 'yes');">

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
			</ssf:url>" 
		onClick="return ss_gotoPermalink('${fileEntry._docId}','${fileEntry._docId}', '${fileEntry._entityType}', '${portletNamespace}', 'yes');">

    </c:when>
 	</c:choose>
    <c:out value="${fileEntry.title}"/></a></div>
 </c:if>

</c:forEach>
</div>
</table>

<div>
  <table width="100%">
   <tr>
    <td>
<c:if test="${hitCount > 0}">
      <span class="ss_light ss_fineprint">
	    [<ssf:nlt tag="folder.Results">
	    <ssf:param name="value" value="${ss_pageNumber * ss_pageSize + 1}"/>
	    <ssf:param name="value" value="${ss_pageNumber * ss_pageSize + hitCount}"/>
	    <ssf:param name="value" value="${ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchCount}"/>
	    </ssf:nlt>]
	  </span>
</c:if>
<c:if test="${hitCount == 0}">
    <span class="ss_light ss_fineprint">
	  [<ssf:nlt tag="dashboard.gallery.noneFound"/>]
	</span>
</c:if>
	</td>
	<td align="right">
	<c:if test="${ssDashboard.scope != 'portlet'}">
		<c:set var="binderId" value="${ssBinder.id}"/>
	</c:if>
	<c:if test="${ssDashboard.scope == 'portlet'}">
		<c:set var="binderId" value="${ssDashboardPortlet.id}"/>
	</c:if>
	  <c:if test="${ss_pageNumber > 0}">
	    <span>
	      <a onClick="ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber - 1}', '${ss_pageSize}', '${ss_divId}', '${componentId}', 'gallery'); return false;"
	        href="#" >&lt;&lt;&lt;&nbsp;<ssf:nlt tag="general.previousPage"/></a>&nbsp;&nbsp;&nbsp;
	    </span>
	  </c:if>
	  <c:if test="${(ss_pageNumber * ss_pageSize + resultCount) < ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchCount}">
	    <span>&nbsp;&nbsp;
	      <a onClick="ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber + 1}', '${ss_pageSize}', '${ss_divId}', '${componentId}', 'gallery'); return false;"
	        href="#" ><ssf:nlt tag="general.nextPage"/>&nbsp;&gt;&gt;&gt;</a>
	    </span>
	  </c:if>
    </td>
   </tr>
  </table>
</div>
