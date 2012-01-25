<%
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
<%@ include file="/WEB-INF/jsp/search/search_js.jsp" %>

<c:set var="ssNamespace" value="${renderResponse.namespace}"/>

<c:set var="summaryWordCount" value="${ssDashboard.dashboard.components[ssComponentId].data.summaryWordCount}"/>
<c:if test="${empty summaryWordCount}"><c:set var="summaryWordCount" value="20"/></c:if>
<c:set var="resultsCount" value="${ssDashboard.dashboard.components[ssComponentId].data.resultsCount}"/>
<c:if test="${empty resultsCount}"><c:set var="resultsCount" value="5"/></c:if>

<c:set var="ss_filterMap" value="${ssDashboard.beans[ssComponentId].ssSearchFormData.ss_filterMap}"/>
<c:set var="ssWorkflowDefinitionMap" value="${ssDashboard.beans[ssComponentId].ssSearchFormData.ssWorkflowDefinitionMap}"/>
<c:set var="ssEntryDefinitionMap" value="${ssDashboard.beans[ssComponentId].ssSearchFormData.ssEntryDefinitionMap}"/>

<div class="ss_style ss_portlet_style">

	<div class="ss_searchContainer">
		<div id="ss_content">
			<c:set var="disableSearchButton" value="1"/>
			<c:set var="activateDashboardFolder" value="true"/>
			<c:if test="${ssDashboard.scope == 'portlet'}">
				<c:set var="activateDashboardFolder" value="false"/>
			</c:if>	
			<%@ include file="/WEB-INF/jsp/search/advanced_search_form_common.jsp" %>
		
		</div>
	</div>
</div>
<script type="text/javascript">
ss_createOnSubmitObj('ss_prepareAdditionalSearchOptions', '<ssf:escapeJavaScript value="${ss_dashboard_config_form_name}"/>', ss_prepareAdditionalSearchOptions);
	
<%@ include file="/WEB-INF/jsp/search/advanced_search_form_data_init.jsp" %>

dojo.addOnLoad(function() {
	ss_showAdditionalOptions('ss_searchForm_additionalFilters', 'ss_search_more_options_txt_${ssNamespace}', '${ssNamespace}');
});
</script>