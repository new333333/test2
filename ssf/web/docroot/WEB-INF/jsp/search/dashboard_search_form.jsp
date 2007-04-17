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
	var workflows = new Array();
	var steps = new Array();
	<c:forEach var="wf" items="${ssDashboard.beans[ssComponentId].ssSearchFormData.ssWorkflowDefinitionMap}">
		workflows['${wf.id}'] = '${wf.title}';
		<c:forEach var="step" items="${wf.steps}">
			steps['${wf.id}-${step.name}'] = '${step.title}';
		</c:forEach>
	</c:forEach>
</c:if>
<c:if test="${!empty ssDashboard.beans[ssComponentId].ssSearchFormData.ssEntryDefinitionMap}">
	var entries = new Array();
	var fields = new Array();
	var fieldsTypes = new Array();
	<c:forEach var="entry" items="${ssDashboard.beans[ssComponentId].ssSearchFormData.ssEntryDefinitionMap}">
		entries['${entry.id}'] = '${entry.title}';
		<c:forEach var="field" items="${entry.fields}">
			fields['${entry.id}-${field.name}'] = '${field.title}';
			fieldsTypes['${entry.id}-${field.name}'] = '${field.type}';
		</c:forEach>
	</c:forEach>
</c:if>
</script>

<c:set var="dashboardForm" value="1"/>
<c:set var="summaryWordCount" value="${ssDashboard.dashboard.components[ssComponentId].data.summaryWordCount[0]}"/>
<c:if test="${empty summaryWordCount}"><c:set var="summaryWordCount" value="20"/></c:if>
<c:set var="resultsCount" value="${ssDashboard.dashboard.components[ssComponentId].data.resultsCount[0]}"/>
<c:if test="${empty resultsCount}"><c:set var="resultsCount" value="5"/></c:if>

<div class="ss_style ss_portlet_style">

	<div class="ss_searchContainer">
		<div id="ss_searchForm_spacer"></div>

		<div id="ss_content">
		
			<%@ include file="/WEB-INF/jsp/search/advanced_search_form_common.jsp" %>
		
		</div>
	</div>
</div>
<script type="text/javascript">
ss_createOnSubmitObj('ss_prepareAdditionalSearchOptions', '${ss_dashboard_config_form_name}', ss_prepareAdditionalSearchOptions);

// TODO find the same method somewhere in common....
function ss_fillMask(id, value) { 
	if (document.getElementById(id)) document.getElementById(id).value = value;
}

<% /* fill the search mask form*/ %>
ss_fillMask("searchText", "<ssf:escapeJavaScript value="${ssDashboard.beans[ssComponentId].ssSearchFormData.filterMap.searchText}"/>");
ss_fillMask("searchAuthors", "<ssf:escapeJavaScript value="${ssDashboard.beans[ssComponentId].ssSearchFormData.filterMap.searchAuthors}"/>");
ss_fillMask("searchTags", "<ssf:escapeJavaScript value="${ssDashboard.beans[ssComponentId].ssSearchFormData.filterMap.searchTags}"/>");
// ssDashboard.beans[ssComponentId].ssSearchFormData.filterMap.searchJoinerAnd ${ssDashboard.beans[ssComponentId].ssSearchFormData.filterMap.searchJoinerAnd}
<c:if test="${!empty ssDashboard.beans[ssComponentId].ssSearchFormData.filterMap.searchJoinerAnd && ssDashboard.beans[ssComponentId].ssSearchFormData.filterMap.searchJoinerAnd}">
	if (document.getElementById("searchJoinerAnd")) document.getElementById("searchJoinerAnd").checked="true";
</c:if>
<c:if test="${empty ssDashboard.beans[ssComponentId].ssSearchFormData.filterMap.searchJoinerAnd || !ssDashboard.beans[ssComponentId].ssSearchFormData.filterMap.searchJoinerAnd}">
	if (document.getElementById("searchJoinerOr")) document.getElementById("searchJoinerOr").checked="true";
</c:if>


function ss_initSearchOptions() {
	<c:if test="${! empty ssDashboard.beans[ssComponentId].ssSearchFormData.filterMap.additionalFilters}">
		<c:if test="${!empty ssDashboard.beans[ssComponentId].ssSearchFormData.filterMap.additionalFilters.tag}">
			<c:forEach var="block" items="${ssDashboard.beans[ssComponentId].ssSearchFormData.filterMap.additionalFilters.tag}">
				ss_addInitializedTag("${block.communityTag}", "${block.personalTag}");
			</c:forEach>
		</c:if>
		<c:if test="${!empty ssDashboard.beans[ssComponentId].ssSearchFormData.filterMap.additionalFilters.creator_by_id}">
			<c:forEach var="block" items="${ssDashboard.beans[ssComponentId].ssSearchFormData.filterMap.additionalFilters.creator_by_id}">
				ss_addInitializedAuthor("${block.authorId}", "${block.authorTitle}");
			</c:forEach>
		</c:if>
		<c:if test="${!empty ssDashboard.beans[ssComponentId].ssSearchFormData.filterMap.additionalFilters.creation_date}">
			<c:forEach var="block" items="${ssDashboard.beans[ssComponentId].ssSearchFormData.filterMap.additionalFilters.creation_date}">
				ss_addInitializedCreationDate("${block.startDate}", "${block.endDate}");
			</c:forEach>
		</c:if>
		<c:if test="${!empty ssDashboard.beans[ssComponentId].ssSearchFormData.filterMap.additionalFilters.modification_date}">
			<c:forEach var="block" items="${ssDashboard.beans[ssComponentId].ssSearchFormData.filterMap.additionalFilters.modification_date}">
				ss_addInitializedModificationDate("${block.startDate}", "${block.endDate}");
			</c:forEach>
		</c:if>
		<c:if test="${!empty ssDashboard.beans[ssComponentId].ssSearchFormData.filterMap.additionalFilters.workflow}">
			<c:forEach var="block" items="${ssDashboard.beans[ssComponentId].ssSearchFormData.filterMap.additionalFilters.workflow}">
				<c:forEach var="step" items="${block.filterWorkflowStateName}">
					ss_addInitializedWorkflow("${block.searchWorkflow}", "${step}");
				</c:forEach>
			</c:forEach>
		</c:if>
		<c:if test="${!empty ssDashboard.beans[ssComponentId].ssSearchFormData.filterMap.additionalFilters.entry}">
			<c:forEach var="block" items="${ssDashboard.beans[ssComponentId].ssSearchFormData.filterMap.additionalFilters.entry}">
				ss_addInitializedEntry("${block.entryType}", "${block.entryElement}", "${block.entryValues}");
			</c:forEach>
		</c:if>
		<c:if test="${empty ssDashboard.beans[ssComponentId].ssSearchFormData.filterMap.additionalFilters.workflow}">
			ss_addOption('workflow');
		</c:if>
		<c:if test="${empty ssDashboard.beans[ssComponentId].ssSearchFormData.filterMap.additionalFilters.tag}">
			ss_addOption('tag');
		</c:if>
		<c:if test="${empty ssDashboard.beans[ssComponentId].ssSearchFormData.filterMap.additionalFilters.creation_date}">
			ss_addOption('creation_date');
		</c:if>
		<c:if test="${empty ssDashboard.beans[ssComponentId].ssSearchFormData.filterMap.additionalFilters.modification_date}">
			ss_addOption('modification_date');
		</c:if>
		<c:if test="${empty ssDashboard.beans[ssComponentId].ssSearchFormData.filterMap.additionalFilters.creator_by_id}">
			ss_addOption('creator_by_id');
		</c:if>
		<c:if test="${empty ssDashboard.beans[ssComponentId].ssSearchFormData.filterMap.additionalFilters.entry}">
			ss_addOption('entry');
		</c:if>
		
	</c:if>
	  ss_searchMoreInitialized = true;
}	

dojo.addOnLoad(function() {
	ss_showAdditionalOptions('ss_searchForm_additionalFilters');
});
</script>