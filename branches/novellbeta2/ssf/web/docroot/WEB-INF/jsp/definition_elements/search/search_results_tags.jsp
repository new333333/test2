<%
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
<% // Search results listing of "tags" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div id="ss_tags_table" class="ss_search_results_pane">
 <div style="padding-left: 20px;">
  <span class="ss_bold"><ssf:nlt tag="tags.community"/>:</span>
  <p>
   <c:forEach var="tag" items="${ssFolderEntryCommunityTags}">
   
   	<a href="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
		name="action" value="search"/><portlet:param 
		name="searchText" value="${ss_tab_search_text}"/><portlet:param 
		name="searchCommunityTags" value="${tag.ssTagSearchText}"/><portlet:param 
		name="searchPersonalTags" value="${ss_tab_personal_tag_search_text}"/><portlet:param 
		name="searchTags" value="addToSearchText"/><portlet:param 
		name="tabId" value="${tabId}"/></portlet:actionURL>" 
	  class="${tag.searchResultsRatingCSS}">${tag.ssTagSign}</a><a 
	  href="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
		name="action" value="search"/><portlet:param 
		name="searchCommunityTags" value="${tag.ssTag}"/><portlet:param 
		name="searchTags" value="tagOnlySearch"/><portlet:param 
		name="tabId" value="${tabId}"/></portlet:actionURL>" 
	  class="${tag.searchResultsRatingCSS}">&nbsp;${tag.ssTag}</a>&nbsp;&nbsp;
   	
   </c:forEach>
<p>
<span class="ss_bold"><ssf:nlt tag="tags.personal"/>:</span>
<p>
   <c:forEach var="tag" items="${ssFolderEntryPersonalTags}">

   	<a href="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
				name="action" value="search"/><portlet:param 
				name="searchText" value="${ss_tab_search_text}"/><portlet:param 
				name="searchPersonalTags" value="${tag.ssTagSearchText}"/><portlet:param 
				name="searchCommunityTags" value="${ss_tab_community_tag_search_text}"/><portlet:param 
				name="searchTags" value="addToSearchText"/><portlet:param 
				name="tabId" value="${tabId}"/></portlet:actionURL>" class="${tag.searchResultsRatingCSS}">${tag.ssTagSign}</a><a href="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
				name="action" value="search"/><portlet:param 
				name="searchPersonalTags" value="${tag.ssTag}"/><portlet:param 
				name="searchTags" value="tagOnlySearch"/><portlet:param 
				name="tabId" value="${tabId}"/></portlet:actionURL>" class="${tag.searchResultsRatingCSS}">&nbsp;${tag.ssTag}</a>&nbsp;&nbsp;
				
   </c:forEach>
 </div>
</div>