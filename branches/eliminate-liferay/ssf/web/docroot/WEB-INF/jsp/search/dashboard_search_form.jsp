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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<%@ include file="/WEB-INF/jsp/search/search_js.jsp" %>

<c:set var="ssNamespace" value="${renderResponse.namespace}"/>

<script type="text/javascript">

<c:if test="${!empty ssDashboard.beans[ssComponentId].ssSearchFormData.ssWorkflowDefinitionMap}">
	var ss_searchWorkflows = new Array();
	var ss_searchSteps = new Array();
	<c:forEach var="wf" items="${ssDashboard.beans[ssComponentId].ssSearchFormData.ssWorkflowDefinitionMap}">
		ss_searchWorkflows['${wf.id}'] = '<ssf:escapeJavaScript value="${wf.title}"/>';
		<c:forEach var="step" items="${wf.steps}">
			ss_searchSteps['${wf.id}-<ssf:escapeJavaScript value="${step.name}"/>'] = '<ssf:escapeJavaScript value="${step.title}"/>';
		</c:forEach>
	</c:forEach>
</c:if>
<c:if test="${!empty ssDashboard.beans[ssComponentId].ssSearchFormData.ssEntryDefinitionMap}">
	var ss_searchEntries = new Array();
	var ss_searchFields = new Array();
	var ss_searchFieldsTypes = new Array();
	<c:forEach var="entry" items="${ssDashboard.beans[ssComponentId].ssSearchFormData.ssEntryDefinitionMap}">
		ss_searchEntries['${entry.id}'] = '<ssf:escapeJavaScript value="${entry.title}"/>';
		<c:forEach var="field" items="${entry.fields}">
			ss_searchFields['${entry.id}-<ssf:escapeJavaScript value="${field.name}"/>'] = '<ssf:escapeJavaScript value="${field.title}"/>';
			ss_searchFieldsTypes['${entry.id}-<ssf:escapeJavaScript value="${field.name}"/>'] = '<ssf:escapeJavaScript value="${field.type}"/>';
		</c:forEach>
	</c:forEach>
</c:if>
</script>


<c:set var="summaryWordCount" value="${ssDashboard.dashboard.components[ssComponentId].data.summaryWordCount}"/>
<c:if test="${empty summaryWordCount}"><c:set var="summaryWordCount" value="20"/></c:if>
<c:set var="resultsCount" value="${ssDashboard.dashboard.components[ssComponentId].data.resultsCount}"/>
<c:if test="${empty resultsCount}"><c:set var="resultsCount" value="5"/></c:if>

<c:set var="ss_filterMap" value="${ssDashboard.beans[ssComponentId].ssSearchFormData.ss_filterMap}"/>

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