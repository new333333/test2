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
<% // Search results tags %>
<ssf:sidebarPanel title="searchResult.tagsTitle" id="ss_rating_tags" divClass="ss_rating_box_content"
    initOpen="true" sticky="false">
<h5><ssf:nlt tag="tags.community"/></h5>
<p class="ss_tags_cloud">
<c:forEach var="tag" items="${ssFolderEntryCommunityTags}">
	<span class="${tag.searchResultsRatingCSS}">
		<a href="<portlet:actionURL windowState="maximized" 
				portletMode="view"><portlet:param 
				name="action" value="advanced_search"/><portlet:param 
				name="searchCommunityTags_hidden" value="${tag.ssTag}"/><portlet:param 
				name="operation" value="ss_searchResults"/><portlet:param 
				name="tabTitle" value="ss_tagPlaceHolder"/><portlet:param 
				name="newTab" value="1"/></portlet:actionURL>">${tag.ssTag}</a>
	</span>
</c:forEach>
</p>

<h5><ssf:nlt tag="tags.personal"/></h5>
<p class="ss_tags_cloud">
<c:forEach var="tag" items="${ssFolderEntryPersonalTags}">
	<span class="${tag.searchResultsRatingCSS}"><a 
			href="<portlet:actionURL windowState="maximized" 
			portletMode="view"><portlet:param 
			name="action" value="advanced_search"/><portlet:param 
			name="searchPersonalTags_hidden" value="${tag.ssTag}"/><portlet:param 
			name="operation" value="ss_searchResults"/><portlet:param 
			name="tabTitle" value="ss_tagPlaceHolder"/><portlet:param 
			name="newTab" value="1"/></portlet:actionURL>">${tag.ssTag}</a></span>
</c:forEach>
</p>
</ssf:sidebarPanel>