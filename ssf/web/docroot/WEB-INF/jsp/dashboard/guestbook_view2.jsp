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

<c:set var="hitCount" value="0"/>
<c:set var="componentId" value="${ssComponentId}"/>
<c:if test="${empty ssComponentId}">
<c:set var="componentId" value="${ssDashboard.ssComponentId}" />
</c:if>





<div class="ss_blog">

<c:forEach var="fileEntry" items="${ssDashboard.beans[componentId].ssSearchFormData.searchResults}" >

  <c:set var="hitCount" value="${hitCount + 1}"/>


  <table cellspacing="0" cellpadding="0" width="100%">
	  <tr>
		  <td valign="top"><span class="ss_bold ss_largerprint">
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
			<td class="ss_miniBusinessCard">
				<ssf:miniBusinessCard user="${fileEntry._principal}"/> 
			</td>		 	
			<td class="ss_guestbookContainer">
				<span class="ss_entryTitle">
					<c:if test="${empty fileEntry.title}">
				    	<i>(no title)</i>
				    </c:if>
					<c:out value="${fileEntry.title}" escapeXml="false"/>
				</span>
				<span class="ss_entrySignature"><fmt:formatDate timeZone="${fileEntry._principal.timeZone.ID}"
				      value="${fileEntry._modificationDate}" type="both" 
					  timeStyle="short" dateStyle="short" /></span>
				
				<c:if test="${!empty fileEntry._desc}">
				<div class="ss_entryContent ss_entryDescription">
					<span><c:out value="${fileEntry._desc}" escapeXml="false"/></span>
				</div>
				</c:if>
			</td>					 	
		</tr>
	</table>

</c:forEach>
</div>

<div>
  <table width="100%">
   <tr>
    <td>
<c:if test="${hitCount > 0}">
      <span class="ss_light ss_fineprint">
	    [<ssf:nlt tag="search.results">
	    <ssf:param name="value" value="${ss_pageNumber * 10 + 1}"/>
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
	      <a onClick="ss_moreDashboardSearchResults('${ssBinder.id}', '${ss_pageNumber - 1}', '10', '${ss_divId}', '${componentId}', 'guestbook'); return false;"
	        href="#" >&lt;&lt;&lt;&nbsp;<ssf:nlt tag="general.previousPage"/></a>&nbsp;&nbsp;&nbsp;
	    </span>
	  </c:if>
	  <c:if test="${(ss_pageNumber * 10 + hitCount) < ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchCount}">
	    <span>&nbsp;&nbsp;
	      <a onClick="ss_moreDashboardSearchResults('${ssBinder.id}', '${ss_pageNumber + 1}', '10', '${ss_divId}', '${componentId}', 'guestbook'); return false;"
	        href="#" ><ssf:nlt tag="general.nextPage"/>&nbsp;&gt;&gt;&gt;</a>
	    </span>
	  </c:if>
    </td>
   </tr>
  </table>
</div>
