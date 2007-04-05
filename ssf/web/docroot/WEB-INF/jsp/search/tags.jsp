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
		<div class="ss_rating_box">
			<div class="ss_rating_box_title">
				<h4><ssf:nlt tag="searchResult.tagsTitle"/></h4>
				<img src="<html:imagesPath/>pics/flip_down16H.gif" onClick="ss_showHideRatingBox('ss_rating_tags', this);" class="ss_toogler"/>
			</div>
			<div id="ss_rating_tags" class="ss_rating_box_content" style="visibility:visible;display:block;">

				<h5><ssf:nlt tag="tags.community"/></h5>
				<p class="ss_tags_cloud">
					<c:forEach var="tag" items="${ssFolderEntryCommunityTags}">
						<span class="${tag.searchResultsRatingCSS}">${tag.ssTag}</span>
					</c:forEach>
				</p>

				<h5><ssf:nlt tag="tags.personal"/></h5>
				<p class="ss_tags_cloud">
					<c:forEach var="tag" items="${ssFolderEntryPersonalTags}">
						<span class="${tag.searchResultsRatingCSS}">${tag.ssTag}</span>
					</c:forEach>
				</p>

			</div>
		</div>
		