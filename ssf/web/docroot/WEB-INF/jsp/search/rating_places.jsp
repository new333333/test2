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
<% // Search results ratings of "places" %>
<ssf:sidebarPanel title="searchResult.ratigPlacesTitle" id="ss_rating_places" divClass="ss_rating_box_content"
    initOpen="true" sticky="false">

 <table>
	<tr><th><ssf:nlt tag="search.Rating"/></th><th><ssf:nlt tag="search.Places"/></th></tr>
	<c:forEach var="place" items="${ssFolderEntryPlaces}">
		<tr>
		<td class="ss_star"><img <ssf:alt/> class="${place.searchResultsRatingCSS}" 
			  src="<html:imagesPath/>pics/sym_m_star.gif"/></td>
		<td>

		<c:choose>
			<c:when test="${place.ssBinder.entityType == 'profiles'}">
				<a href="<ssf:url portletName="ss_forum" binderId="${place.ssBinder.id}" action="view_profile_listing" actionUrl="true">													
							</ssf:url>">${place.ssBinder.title}</a>
			</c:when>
			<c:otherwise>
				<a href="<portlet:renderURL><portlet:param 
						name="action" value="view_folder_listing"/><portlet:param 
						name="binderId" value="${place.ssBinder.id}"/><portlet:param 
						name="binderType" value="${place.ssBinder.entityType}"/></portlet:renderURL>"
					>${place.ssBinder.title}</a>
			</c:otherwise>
		</c:choose>
		</td>
		</tr>
	</c:forEach>
</table>
</ssf:sidebarPanel>