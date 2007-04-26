
function ss_initSearchMainMask() {
	// fill the search mask form
	ss_fillSearchMask("searchText_adv", "<ssf:escapeJavaScript value="${ss_filterMap.searchText}"/>");
	ss_fillSearchMask("searchAuthors", "<ssf:escapeJavaScript value="${ss_filterMap.searchAuthors}"/>");
	ss_fillSearchMask("searchTags", "<ssf:escapeJavaScript value="${ss_filterMap.searchTags}"/>");
	<c:if test="${!empty ss_filterMap.searchJoinerAnd && ss_filterMap.searchJoinerAnd}">
		if (document.getElementById("searchJoinerAnd")) document.getElementById("searchJoinerAnd").checked="true";
	</c:if>
	<c:if test="${empty ss_filterMap.searchJoinerAnd || !ss_filterMap.searchJoinerAnd}">
		if (document.getElementById("searchJoinerOr")) document.getElementById("searchJoinerOr").checked="true";
	</c:if>
}

function ss_initSearchOptions() {
	<c:if test="${! empty ss_filterMap.additionalFilters}">
		<c:if test="${!empty ss_filterMap.additionalFilters.tag}">
			<c:forEach var="block" items="${ss_filterMap.additionalFilters.tag}">
				ss_addInitializedTag("${block.communityTag}", "${block.personalTag}");
			</c:forEach>
		</c:if>
		<c:if test="${!empty ss_filterMap.additionalFilters.creator_by_id}">
			<c:forEach var="block" items="${ss_filterMap.additionalFilters.creator_by_id}">
				ss_addInitializedAuthor("${block.authorId}", "${block.authorTitle}");
			</c:forEach>
		</c:if>
		<c:if test="${!empty ss_filterMap.additionalFilters.creation_date}">
			<c:forEach var="block" items="${ss_filterMap.additionalFilters.creation_date}">
				ss_addInitializedCreationDate("${block.startDate}", "${block.endDate}");
			</c:forEach>
		</c:if>
		<c:if test="${!empty ss_filterMap.additionalFilters.modification_date}">
			<c:forEach var="block" items="${ss_filterMap.additionalFilters.modification_date}">
				ss_addInitializedModificationDate("${block.startDate}", "${block.endDate}");
			</c:forEach>
		</c:if>
		<c:if test="${!empty ss_filterMap.additionalFilters.workflow}">
			<c:forEach var="block" items="${ss_filterMap.additionalFilters.workflow}">
				<c:forEach var="step" items="${block.filterWorkflowStateName}">
					ss_addInitializedWorkflow("${block.searchWorkflow}", "${step}");
				</c:forEach>
				<c:if test="${empty block.filterWorkflowStateName}">
					ss_addInitializedWorkflow("${block.searchWorkflow}", "");				
				</c:if>				
			</c:forEach>
		</c:if>
		<c:if test="${!empty ss_filterMap.additionalFilters.entry}">
			<c:forEach var="block" items="${ss_filterMap.additionalFilters.entry}">
				ss_addInitializedEntry("${block.entryType}", "${block.entryElement}", "${block.entryValuesNotFormatted}", "${block.entryValues}");
			</c:forEach>
		</c:if>
		
		<c:if test="${empty ss_filterMap.additionalFilters.workflow}">
			ss_addOption('workflow');
		</c:if>
		<c:if test="${empty ss_filterMap.additionalFilters.tag}">
			ss_addOption('tag');
		</c:if>
		<c:if test="${empty ss_filterMap.additionalFilters.creation_date}">
			ss_addOption('creation_date');
		</c:if>
		<c:if test="${empty ss_filterMap.additionalFilters.modification_date}">
			ss_addOption('modification_date');
		</c:if>
		<c:if test="${empty ss_filterMap.additionalFilters.creator_by_id}">
			ss_addOption('creator_by_id');
		</c:if>
		<c:if test="${empty ss_filterMap.additionalFilters.entry}">
			ss_addOption('entry');
		</c:if>
	</c:if>
	<c:if test="${empty ss_filterMap.additionalFilters}">
		ss_addOption('creation_date');
		ss_addOption('modification_date');
		ss_addOption('tag');
		ss_addOption('workflow');
		ss_addOption('creator_by_id');
		ss_addOption('entry');
	</c:if>		
	  ss_searchMoreInitialized = true;
}	

ss_initSearchMainMask();
