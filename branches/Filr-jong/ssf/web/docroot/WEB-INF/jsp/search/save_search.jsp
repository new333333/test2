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
	<% // Search results saved queries %>
	<c:if test="${empty ss_namespace}">
		<c:set var="ss_namespace" value="${renderResponse.namespace}" />
	</c:if>
	
	<ssf:sidebarPanel title="searchResult.savedSearchTitle" id="ss_saved_searches" divClass="ss_rating_box_content"
	    initOpen="true" sticky="false">
	
	<c:if test="${!empty ss_filterMap}">
	
	   <label for="${ss_namespace}searchQueryName"><span style="display:none;"><ssf:nlt tag="label.queryName"/></span></label>
	
	   <input class="ss_saveQueryNameUnactive" type="text" name="searchQueryName" id="${ss_namespace}searchQueryName" 
			  value="<ssf:escapeQuotes><ssf:nlt tag="searchResult.savedSearch.input.legend"/></ssf:escapeQuotes>" 
		      onfocus="this.className='ss_saveQueryName'; if (this.value == '<ssf:escapeJavaScript><ssf:nlt tag="searchResult.savedSearch.input.legend"/></ssf:escapeJavaScript>') this.value = ''; " 
		      onblur="if (this.value == '') this.value='<ssf:escapeJavaScript><ssf:nlt tag="searchResult.savedSearch.input.legend"/></ssf:escapeJavaScript>'"/>
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
					onclick="ss_removeSavedSearchQuery('<ssf:escapeJavaScript value="${query}"/>','ss_saveQueryErrMsg', this.parentNode)">
					<img src="<html:imagesPath/>pics/delete.gif" <ssf:alt tag=""/> />
			</a>
			<a href="<ssf:url action="advanced_search" actionUrl="true"><ssf:param 
						name="tabTitle" value="${query}"/><ssf:param 
						name="newTab" value="1"/><ssf:param 
						name="operation" value="ss_savedQuery"/><ssf:param 
						name="ss_queryName" value="${query}"/></ssf:url>">${query}</a>
		</li>
	</c:forEach>
	</ul>
	</ssf:sidebarPanel>
</c:if>
