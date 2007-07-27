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
<% // Search results saved queries %>
<div class="ss_rating_box">
	<div class="ss_rating_box_title">
		<h4><ssf:nlt tag="searchResult.savedSearchTitle"/></h4>
		<img <ssf:alt tag="alt.expand"/> src="<html:imagesPath/>pics/flip_down16H.gif" 
		onClick="ss_showHideRatingBox('ss_saved_searches', this);" class="ss_toogler"/>
	</div>
	<div id="ss_saved_searches" class="ss_rating_box_content" style="visibility:visible;display:block;">
		<c:if test="${!empty ss_filterMap}">
			<input class="ss_saveQueryNameUnactive" type="text" name="searchQueryName" id="searchQueryName" 
			  value="<ssf:nlt tag="searchResult.savedSearch.input.legend"/>" 
			onfocus="this.className='ss_saveQueryName'; this.value=''; this.focus();" />
			<a href="javascript: //;" onclick="ss_saveSearchQuery('searchQueryName', 'ss_saveQueryErrMsg');"><ssf:nlt tag="searchResult.savedSearch.save"/></a>
			<div id="ss_saveQueryErrMsg" style="visibility: hidden;"></div>
		</c:if>
		<ul id="ss_savedQueriesList">
			<c:if test="${empty ss_savedQueries}">
				<ssf:nlt tag="searchResult.savedSearch.noResults"/>
			</c:if>
			<c:forEach var="query" items="${ss_savedQueries}">
				<script type="text/javascript">
						ss_addToSaved('<ssf:escapeJavaScript value="${query}"/>');
						
				</script>
				<li>
					<a href="javascript: ;" 
					onclick="ss_removeSavedSearchQuery('<ssf:escapeJavaScript value="${query}"/>','ss_saveQueryErrMsg', this.parentNode)"><img src="<html:imagesPath/>pics/delete.gif"/></a>
					<a href="<portlet:actionURL windowState="maximized" 
						portletMode="view"><portlet:param 
						name="action" value="advanced_search"/><portlet:param 
						name="tabTitle" value="${query}"/><portlet:param 
						name="newTab" value="1"/><portlet:param 
						name="operation" value="ss_savedQuery"/><portlet:param 
						name="ss_queryName" value="${query}"/></portlet:actionURL>">${query}</a>
				</li>
			</c:forEach>
			
		</ul>
	</div>
</div>