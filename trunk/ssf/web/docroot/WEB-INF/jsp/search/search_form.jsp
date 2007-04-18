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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<%@ include file="/WEB-INF/jsp/search/search_js.jsp" %>

<div id="ss_portlet_content" class="ss_style ss_portlet_style ss_content_outer">
	<% // Navigation bar %>
	<jsp:include page="/WEB-INF/jsp/definition_elements/navbar.jsp" />

	<% // Tabs %>
	<jsp:include page="/WEB-INF/jsp/definition_elements/tabbar.jsp" />
	<div class="ss_clear"></div>

	<div id="ss_tabs_container">

		<ul class="ss_actions_bar ss_actions_bar1"></ul>
	
		<% // Breadcrumbs %>
		<jsp:include page="/WEB-INF/jsp/definition_elements/navigation_links.jsp" />
	
		<div class="ss_clear"></div>
	
		<div id="ss_tab_content">
			<div class="ss_searchContainer">
				<div id="ss_searchForm_spacer"></div>
		
				<div id="ss_content">
				<c:if test="${quickSearch}">
					<!-- Quick search form -->
					<%@ include file="/WEB-INF/jsp/search/quick_search_form.jsp" %>
				</c:if>
				<c:if test="${!quickSearch}">
					<!-- Advanced search form -->
					<%@ include file="/WEB-INF/jsp/search/advanced_search_form.jsp" %>
				</c:if>
				</div>
			</div>
		</div>
	</div>
	
</div>
<script type="text/javascript">
// Init empty additional options
function ss_initSearchOptions() {
	if (!ss_searchMoreInitialized) {
		ss_addOption('workflow');
		ss_addOption('entry');
		ss_addOption('tag');
		ss_addOption('creation_date');
		ss_addOption('modification_date');
		ss_addOption('creator_by_id'); 
		ss_searchMoreInitialized = true;
	}
}
</script>
	