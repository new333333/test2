<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<% // Search results saved queries %>
<c:if test="${empty ss_namespace}">
	<c:set var="ss_namespace" value="${renderResponse.namespace}" />
</c:if>

<ssf:sidebarPanel title="searchResult.savedSearchTitle" id="ss_saved_searches" divClass="ss_rating_box_content"
    initOpen="true" sticky="false">

<c:if test="${!empty ss_filterMap}">
   <input class="ss_saveQueryNameUnactive" type="text" name="searchQueryName" id="${ss_namespace}searchQueryName" 
		  value="<ssf:nlt tag="searchResult.savedSearch.input.legend"/>" 
	      onfocus="this.className='ss_saveQueryName'; if (this.value == '<ssf:nlt tag="searchResult.savedSearch.input.legend"/>') this.value = ''; " 
	      onblur="if (this.value == '') this.value='<ssf:nlt tag="searchResult.savedSearch.input.legend"/>'"/>
	<a href="javascript: //;" onclick="ss_saveSearchQuery('${ss_namespace}searchQueryName', 'ss_saveQueryErrMsg');"><ssf:nlt tag="searchResult.savedSearch.save"/></a>
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
		<a href="<ssf:url action="advanced_search" actionUrl="true"><ssf:param 
					name="tabTitle" value="${query}"/><ssf:param 
					name="newTab" value="1"/><ssf:param 
					name="operation" value="ss_savedQuery"/><ssf:param 
					name="ss_queryName" value="${query}"/></ssf:url>">${query}</a>
	</li>
</c:forEach>
</ul>
</ssf:sidebarPanel>