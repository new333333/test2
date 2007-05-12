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

<script type="text/javascript">

<c:if test="${!empty ssDashboard.beans[ssComponentId].ssSearchFormData.ssWorkflowDefinitionMap}">
	var ss_searchWorkflows = new Array();
	var ss_searchSteps = new Array();
	<c:forEach var="wf" items="${ssDashboard.beans[ssComponentId].ssSearchFormData.ssWorkflowDefinitionMap}">
		ss_searchWorkflows['${wf.id}'] = '${wf.title}';
		<c:forEach var="step" items="${wf.steps}">
			ss_searchSteps['${wf.id}-${step.name}'] = '${step.title}';
		</c:forEach>
	</c:forEach>
</c:if>
<c:if test="${!empty ssDashboard.beans[ssComponentId].ssSearchFormData.ssEntryDefinitionMap}">
	var ss_searchEntries = new Array();
	var ss_searchFields = new Array();
	var ss_searchFieldsTypes = new Array();
	<c:forEach var="entry" items="${ssDashboard.beans[ssComponentId].ssSearchFormData.ssEntryDefinitionMap}">
		ss_searchEntries['${entry.id}'] = '${entry.title}';
		<c:forEach var="field" items="${entry.fields}">
			ss_searchFields['${entry.id}-${field.name}'] = '${field.title}';
			ss_searchFieldsTypes['${entry.id}-${field.name}'] = '${field.type}';
		</c:forEach>
	</c:forEach>
</c:if>
</script>


<c:set var="summaryWordCount" value="${ssDashboard.dashboard.components[ssComponentId].data.summaryWordCount[0]}"/>
<c:if test="${empty summaryWordCount}"><c:set var="summaryWordCount" value="20"/></c:if>
<c:set var="resultsCount" value="${ssDashboard.dashboard.components[ssComponentId].data.resultsCount[0]}"/>
<c:if test="${empty resultsCount}"><c:set var="resultsCount" value="5"/></c:if>

<c:set var="ss_filterMap" value="${ssDashboard.beans[ssComponentId].ssSearchFormData.ss_filterMap}"/>

<div class="ss_style ss_portlet_style">

	<div class="ss_searchContainer">
		<div id="ss_searchForm_spacer"></div>

		<div id="ss_content">
			<c:set var="disableSearchButton" value="1"/>
			<c:set var="activateDashboardFolder" value="true"/>		
			<%@ include file="/WEB-INF/jsp/search/advanced_search_form_common.jsp" %>
		
		</div>
	</div>
</div>
<script type="text/javascript">
ss_createOnSubmitObj('ss_prepareAdditionalSearchOptions', '${ss_dashboard_config_form_name}', ss_prepareAdditionalSearchOptions);
	
<%@ include file="/WEB-INF/jsp/search/advanced_search_form_data_init.jsp" %>

dojo.addOnLoad(function() {
	ss_showAdditionalOptions('ss_searchForm_additionalFilters');
});
</script>