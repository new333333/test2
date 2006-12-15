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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="hitCount" value="0"/>
<c:set var="componentId" value="${ssComponentId}"/>
<c:if test="${empty ssComponentId}">
<c:set var="componentId" value="${ssDashboard.ssComponentId}" />
</c:if>
<c:forEach var="fileEntry" items="${ssDashboard.beans[componentId].ssSearchFormData.searchResults}" >
  <c:set var="hitCount" value="${hitCount + 1}"/>
  <div style="padding-bottom:6px;">
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
    <c:if test="${empty fileEntry.title}">
    <span class="ss_fineprint"><i>(<ssf:nlt tag="entry.noTitle"/>)</i></span>
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
      <a href="<ssf:url adapter="true" portletName="ss_forum" 
		    action="view_permalink"
		    binderId="${fileEntry._binderId}">
		    <ssf:param name="entityType" value="folder" />
    	    <ssf:param name="newTab" value="1"/>
			</ssf:url>" 
    	onMouseover="ss_showObjInline('ss_folderName_${hitCount}_${componentId}_<portlet:namespace/>');"
    	onMouseout="ss_hideObj('ss_folderName_${hitCount}_${componentId}_<portlet:namespace/>');"
      >
      <c:if test="${empty ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[fileEntry._binderId].iconName}">
        <img border="0" src="<html:imagesPath/>icons/folder.gif"/>
      </c:if>
      <c:if test="${!empty ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[fileEntry._binderId].iconName}">
        <img border="0" src="<html:imagesPath/>${ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[fileEntry._binderId].iconName}" />
      </c:if>
       <div id="ss_folderName_${hitCount}_${componentId}_<portlet:namespace/>" 
       style="position:absolute; display:none;">${ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[fileEntry._binderId].title}</div></a>
    </c:if>

    </div>
  
    <div class="ss_smallprint ss_indent_medium">  
      <c:out value="${fileEntry._desc}" escapeXml="false"/>
    </div>
  </div>
</c:forEach>
  <div>
    <table width="100%"><tr>
    <td>
<c:if test="${hitCount > 0}">
      <span class="ss_light ss_fineprint">
	    [<ssf:nlt tag="search.results">
	    <ssf:param name="value" value="${ss_pageNumber * 10}"/>
	    <ssf:param name="value" value="${ss_pageNumber * 10 + hitCount}"/>
	    <ssf:param name="value" value="${ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchCount}"/>
	    </ssf:nlt>]
	  </span>
</c:if>
<c:if test="${hitCount == 0}">
    <span class="ss_light ss_fineprint">
	  [<ssf:nlt tag="search.noneFound"/>]
	</span>
</c:if>
	</td>
	<td align="right">
	  <c:if test="${ss_pageNumber > 0}">
	    <span>
	      <a onClick="ss_moreDashboardSearchResults('${ssBinder.id}', '${ss_pageNumber - 1}', '10', '${ss_divId}', '${componentId}'); return false;"
	        href="#" >&lt;&lt;&lt;&nbsp;<ssf:nlt tag="general.previousPage"/></a>&nbsp;&nbsp;&nbsp;
	    </span>
	  </c:if>
	  <c:if test="${(ss_pageNumber * 10 + hitCount) < ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchCount}">
	    <span>&nbsp;&nbsp;
	      <a onClick="ss_moreDashboardSearchResults('${ssBinder.id}', '${ss_pageNumber + 1}', '10', '${ss_divId}', '${componentId}'); return false;"
	        href="#" ><ssf:nlt tag="general.nextPage"/>&nbsp;&gt;&gt;&gt;</a>
	    </span>
	  </c:if>
    </td></tr></table>
  </div>
