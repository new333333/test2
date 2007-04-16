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

<c:set var="dashboardForm" value="1"/>
<div class="ss_style ss_portlet_style">

	<div class="ss_searchContainer">
		<div id="ss_searchForm_spacer"></div>

		<div id="ss_content">
			<%@ include file="/WEB-INF/jsp/search/advanced_search_form_common.jsp" %>
		</div>
	</div>
</div>
<script type="text/javascript">
// Init empty additional options
function init() {
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
	