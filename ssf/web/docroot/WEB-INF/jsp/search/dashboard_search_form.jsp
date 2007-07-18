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

<c:set var="ssNamespace" value="${renderResponse.namespace}"/>

<script type="text/javascript">

<c:if test="${!empty ssDashboard.beans[ssComponentId].ssSearchFormData.ssWorkflowDefinitionMap}">
	var ss_searchWorkflows = new Array();
	var ss_searchSteps = new Array();
	<c:forEach var="wf" items="${ssDashboard.beans[ssComponentId].ssSearchFormData.ssWorkflowDefinitionMap}">
		ss_searchWorkflows['${wf.id}'] = '<ssf:escapeJavaScript value="${wf.title}"/>';
		<c:forEach var="step" items="<ssf:escapeJavaScript value="${wf.steps}"/>">
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
		<c:forEach var="field" items="<ssf:escapeJavaScript value="${entry.fields}"/>">
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