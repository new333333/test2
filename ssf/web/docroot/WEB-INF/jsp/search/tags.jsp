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
		