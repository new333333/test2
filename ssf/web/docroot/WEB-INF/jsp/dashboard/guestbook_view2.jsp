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
 
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<c:set var="hitCount" value="0"/>
<c:set var="componentId" value="${ssComponentId}"/>
<c:if test="${empty ssComponentId}">
<c:set var="componentId" value="${ssDashboard.ssComponentId}" />
</c:if>
<c:set var="portletNamespace" value=""/>
<ssf:ifnotadapter>
<c:set var="portletNamespace" value="${renderResponse.namespace}"/>
</ssf:ifnotadapter>

<div class="ss_blog">

<c:forEach var="fileEntry" items="${ssDashboard.beans[componentId].ssSearchFormData.searchResults}" >

  <c:set var="hitCount" value="${hitCount + 1}"/>


  <table class="ss_guestbook" cellspacing="0" cellpadding="0" width="100%">
	  <tr> 	
			<td class="ss_miniBusinessCard" style="padding-bottom: 5px;" valign="top">
				<ssf:miniBusinessCard user="${fileEntry._principal}"/> 
			</td>		 	
			<td class="ss_guestbookContainer" valign="top">
			
    <a href="<ssf:url adapter="true" portletName="ss_forum" 
		    action="view_permalink"
		    binderId="${fileEntry._binderId}"
		    entryId="${fileEntry._docId}">
		    <ssf:param name="entityType" value="${fileEntry._entityType}" />
    	    <ssf:param name="newTab" value="1"/>
			</ssf:url>"
			onClick="return ss_gotoPermalink('${fileEntry._binderId}','${fileEntry._docId}', '${fileEntry._entityType}', '${portletNamespace}');">
		
				<span class="ss_entryTitle ss_normalprint">
					<c:if test="${empty fileEntry.title}">
				    	${fileEntry._principal.title} <ssf:nlt tag="guestbook.author.wrote"/>: 
				    </c:if>
					<c:out value="${fileEntry.title}" escapeXml="false"/>
				</span></a>
				<span class="ss_att_meta"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				      value="${fileEntry._modificationDate}" type="both" 
					  timeStyle="short" dateStyle="short" /></span>
				
				<c:if test="${!empty fileEntry._desc}">
				<div class="ss_blockquote_watermark"></div>
				<div class="ss_blockquote_watermark_content">
					<span><ssf:markup type="view" binderId="${fileEntry._binderId}" 
					  entryId="${fileEntry._docId}"><c:out value="${fileEntry._desc}" escapeXml="false"/></ssf:markup></span>
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
    <td valign="top">
<c:if test="${hitCount > 0}">
      <span class="ss_light ss_fineprint">
	    [<ssf:nlt tag="search.results">
	    <ssf:param name="value" value="${ss_pageNumber * ss_pageSize + 1}"/>
	    <ssf:param name="value" value="${ss_pageNumber * ss_pageSize + hitCount}"/>
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
	<c:if test="${ssDashboard.scope != 'portlet'}">
		<c:set var="binderId" value="${ssBinder.id}"/>
	</c:if>
	<c:if test="${ssDashboard.scope == 'portlet'}">
		<c:set var="binderId" value="${ssDashboardPortlet.id}"/>
	</c:if>
	  <c:if test="${ss_pageNumber > 0}">
	    <span>
	      <a onClick="ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber - 1}', '${ss_pageSize}', '${ss_divId}', '${componentId}', 'guestbook'); return false;"
	        href="#" >&lt;&lt;&lt;&nbsp;<ssf:nlt tag="general.previousPage"/></a>&nbsp;&nbsp;&nbsp;
	    </span>
	  </c:if>
	  <c:if test="${(ss_pageNumber * ss_pageSize + hitCount) < ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchCount}">
	    <span>&nbsp;&nbsp;
	      <a onClick="ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber + 1}', '${ss_pageSize}', '${ss_divId}', '${componentId}', 'guestbook'); return false;"
	        href="#" ><ssf:nlt tag="general.nextPage"/>&nbsp;&gt;&gt;&gt;</a>
	    </span>
	  </c:if>
    </td>
   </tr>
  </table>
</div>
