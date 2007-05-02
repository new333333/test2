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
<% // Search results rating of "people" %>
		<div class="ss_rating_box">
			<div class="ss_rating_box_title">
				<h4><ssf:nlt tag="searchResult.ratigPeopleTitle"/></h4>
				<img <ssf:alt tag="alt.expand"/> src="<html:imagesPath/>pics/flip_down16H.gif" 
				  onClick="ss_showHideRatingBox('ss_rating_people', this);" class="ss_toogler"/>
			</div>
			<div id="ss_rating_people" class="ss_rating_box_content" style="visibility:visible;display:block;">
				<table>
					<tr><th><ssf:nlt tag="search.Rating"/></th><th><ssf:nlt tag="search.People"/></th></tr>
					<c:forEach var="user" items="${ssFolderEntryPeople}">
						<tr>
							<td class="ss_star"><img class="${user.searchResultsRatingCSS}" 
							  <ssf:alt/> src="<html:imagesPath/>pics/sym_m_star.gif"/></td>
							<td><ssf:showUser user="${user.ssUser}" /></td>
						</tr>
					</c:forEach>
				</table>
			</div>
		</div>
