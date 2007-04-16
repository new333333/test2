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

<div class="ss_style ss_portlet_style">
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<%@ include file="/WEB-INF/jsp/search/search_js.jsp" %>

<script type="text/javascript">

<c:if test="${!empty ssWorkflowDefinitionMap}">
	var workflows = new Array();
	var steps = new Array();
	<c:forEach var="wf" items="${ssWorkflowDefinitionMap}">
		workflows['${wf.id}'] = '${wf.title}';
		<c:forEach var="step" items="${wf.steps}">
			steps['${wf.id}-${step.name}'] = '${step.title}';
		</c:forEach>
	</c:forEach>
</c:if>
<c:if test="${!empty ssEntryDefinitionMap}">
	var entries = new Array();
	var fields = new Array();
	var fieldsTypes = new Array();
	<c:forEach var="entry" items="${ssEntryDefinitionMap}">
		entries['${entry.id}'] = '${entry.title}';
		<c:forEach var="field" items="${entry.fields}">
			fields['${entry.id}-${field.name}'] = '${field.title}';
			fieldsTypes['${entry.id}-${field.name}'] = '${field.type}';
		</c:forEach>
	</c:forEach>
</c:if>
</script>


	<% // Navigation bar %>
	<jsp:include page="/WEB-INF/jsp/definition_elements/navbar.jsp" />

	<% // Tabs %>
	<jsp:include page="/WEB-INF/jsp/definition_elements/tabbar.jsp" />
	<div class="ss_clear"></div>

<div id="ss_tabs_container">
	<ssf:toolbar style="ss_actions_bar ss_actions_bar1"/>
	
	<div class="ss_clear"></div>

  <div id="ss_tab_content">
	<div id="ss_rankings">
		<!-- Places rating - Moved to the new file -->
		<%@ include file="/WEB-INF/jsp/search/rating_places.jsp" %>

		<!-- People rating - Moved to the new file -->
		<%@ include file="/WEB-INF/jsp/search/rating_people.jsp" %>

		<!-- Tags -->
		<%@ include file="/WEB-INF/jsp/search/tags.jsp" %>
		
		<!-- Saved searches -->
		<%@ include file="/WEB-INF/jsp/search/save_search.jsp" %>
		
	</div>
	<div id="ss_content_container" class="ss_searchContainer">		<div id="ss_searchForm_spacer"></div>

		<div id="ss_content">
		<c:if test="${quickSearch}">
			<!-- Quick search form -->
			<%@ include file="/WEB-INF/jsp/search/quick_search_form.jsp" %>
		</c:if>
		<c:if test="${!quickSearch}">
			<!-- Advanced search form -->
			<%@ include file="/WEB-INF/jsp/search/advanced_search_form.jsp" %>
		</c:if>		

		<!-- Search result header -->
		<%@ include file="/WEB-INF/jsp/search/result_header.jsp" %>
	
		<!-- Search result list -->
		<%@ include file="/WEB-INF/jsp/search/result_list.jsp" %>
		</div>
	</div>
	<div class="ss_clear"></div>
  </div>
</div>
</div>



<script type="text/javascript">

// TODO find the same method somewhere in common....
function ss_fillMask(id, value) { 
	if (document.getElementById(id)) document.getElementById(id).value = value;
}

<% /* fill the search mask form*/ %>
ss_fillMask("searchText", "<ssf:escapeJavaScript value="${filterMap.searchText}"/>");
ss_fillMask("searchAuthors", "<ssf:escapeJavaScript value="${filterMap.searchAuthors}"/>");
ss_fillMask("searchTags", "<ssf:escapeJavaScript value="${filterMap.searchTags}"/>");
// filterMap.searchJoinerAnd ${filterMap.searchJoinerAnd}
<c:if test="${!empty filterMap.searchJoinerAnd && filterMap.searchJoinerAnd}">
	if (document.getElementById("searchJoinerAnd")) document.getElementById("searchJoinerAnd").checked="true";
</c:if>
<c:if test="${empty filterMap.searchJoinerAnd || !filterMap.searchJoinerAnd}">
	if (document.getElementById("searchJoinerOr")) document.getElementById("searchJoinerOr").checked="true";
</c:if>


function init() {
  if (!ss_searchMoreInitialized) {
	<c:if test="${! empty filterMap.additionalFilters}">
		<c:if test="${!empty filterMap.additionalFilters.workflow}">
			<c:forEach var="block" items="${filterMap.additionalFilters.workflow}">
				<c:forEach var="step" items="${block.filterWorkflowStateName}">
					ss_addInitializedWorkflow("${block.searchWorkflow}", "${step}");
				</c:forEach>
			</c:forEach>
		</c:if>
		<c:if test="${!empty filterMap.additionalFilters.tag}">
			<c:forEach var="block" items="${filterMap.additionalFilters.tag}">
				ss_addInitializedTag("${block.communityTag}", "${block.personalTag}");
			</c:forEach>
		</c:if>
		<c:if test="${!empty filterMap.additionalFilters.creator_by_id}">
			<c:forEach var="block" items="${filterMap.additionalFilters.creator_by_id}">
				ss_addInitializedAuthor("${block.authorId}", "${block.authorTitle}");
			</c:forEach>
		</c:if>
		<c:if test="${!empty filterMap.additionalFilters.entry}">
			<c:forEach var="block" items="${filterMap.additionalFilters.entry}">
				ss_addInitializedEntry("${block.entryType}", "${block.entryElement}", "${block.entryValues}");
			</c:forEach>
		</c:if>
		<c:if test="${!empty filterMap.additionalFilters.creation_date}">
			<c:forEach var="block" items="${filterMap.additionalFilters.creation_date}">
				ss_addInitializedCreationDate("${block.startDate}", "${block.endDate}");
			</c:forEach>
		</c:if>
		<c:if test="${!empty filterMap.additionalFilters.modification_date}">
			<c:forEach var="block" items="${filterMap.additionalFilters.modification_date}">
				ss_addInitializedModificationDate("${block.startDate}", "${block.endDate}");
			</c:forEach>
		</c:if>
		<c:if test="${empty filterMap.additionalFilters.workflow}">
			ss_addOption('workflow');
		</c:if>
		<c:if test="${empty filterMap.additionalFilters.tag}">
			ss_addOption('tag');
		</c:if>
		<c:if test="${empty filterMap.additionalFilters.creation_date}">
			ss_addOption('creation_date');
		</c:if>
		<c:if test="${empty filterMap.additionalFilters.modification_date}">
			ss_addOption('modification_date');
		</c:if>
		<c:if test="${empty filterMap.additionalFilters.creator_by_id}">
			ss_addOption('creator_by_id');
		</c:if>
		<c:if test="${empty filterMap.additionalFilters.entry}">
			ss_addOption('entry');
		</c:if>
		
	</c:if>
  }
  ss_searchMoreInitialized = true;
}

// dojo.addOnLoad(function() {
//  init()
// });

</script>

