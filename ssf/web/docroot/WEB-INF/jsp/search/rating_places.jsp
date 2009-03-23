<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:if test="${ss_searchResultsPage}">
	<% // Search results ratings of "places" %>
	<ssf:sidebarPanel title="searchResult.ratigPlacesTitle" id="ss_rating_places" divClass="ss_rating_box_content"
	    initOpen="true" sticky="false">
	
	 <table>
		<tr><th><ssf:nlt tag="search.Rating"/></th><th><ssf:nlt tag="search.Places"/></th></tr>
		<c:forEach var="place" items="${ssFolderEntryPlaces}">
			<tr>
			<td class="ss_star"><img <ssf:alt/> class="${place.searchResultsRatingCSS}" 
				  src="<html:imagesPath/>pics/sym_m_star.gif" <ssf:alt tag=""/> /></td>
			<td>
	
			<c:choose>
				<c:when test="${place.ssBinder.entityType == 'profiles'}">
					<a href="<ssf:url portletName="ss_forum" binderId="${place.ssBinder.id}" action="view_profile_listing" actionUrl="true">													
								</ssf:url>">${place.ssBinder.title}</a>
				</c:when>
				<c:otherwise>
					<a href="<ssf:url action="view_folder_listing"><ssf:param 
							name="binderId" value="${place.ssBinder.id}"/><ssf:param 
							name="binderType" value="${place.ssBinder.entityType}"/></ssf:url>"
						title="${place.ssBinder.pathName}"
					>${place.ssBinder.title}</a>
				</c:otherwise>
			</c:choose>
			</td>
			</tr>
		</c:forEach>
	</table>
	</ssf:sidebarPanel>
</c:if>
