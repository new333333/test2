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
	<% // Search results tags %>
	<ssf:sidebarPanel title="searchResult.tagsTitle" id="ss_rating_tags" divClass="ss_rating_box_content"
	    initOpen="true" sticky="false">
	<h5><ssf:nlt tag="tags.community"/></h5>
	<div class="ss_tags_cloud">
	<c:forEach var="tag" items="${ssFolderEntryCommunityTags}">
		<span class="${tag.searchResultsRatingCSS}">
			<a href="<ssf:url action="advanced_search" actionUrl="true"><ssf:param 
					name="searchCommunityTags_hidden" value="${tag.ssTag}"/><ssf:param 
					name="operation" value="ss_searchResults"/><ssf:param 
					name="tabTitle" value="ss_tagPlaceHolder"/><ssf:param 
					name="newTab" value="1"/></ssf:url>">${tag.ssTag}</a>
		</span>
	</c:forEach>
	</div>
	
	<h5><ssf:nlt tag="tags.personal"/></h5>
	<div class="ss_tags_cloud">
	<c:forEach var="tag" items="${ssFolderEntryPersonalTags}">
		<span class="${tag.searchResultsRatingCSS}"><a 
				href="<ssf:url action="advanced_search" actionUrl="true"><ssf:param 
				name="searchPersonalTags_hidden" value="${tag.ssTag}"/><ssf:param 
				name="operation" value="ss_searchResults"/><ssf:param 
				name="tabTitle" value="ss_tagPlaceHolder"/><ssf:param 
				name="newTab" value="1"/></ssf:url>">${tag.ssTag}</a></span>
	</c:forEach>
	</div>
	</ssf:sidebarPanel>
</c:if>

