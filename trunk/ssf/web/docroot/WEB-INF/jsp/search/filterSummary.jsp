<div id="ss_filterSummary_content">
	<h4><ssf:nlt tag="searchForm.summary.Title"/></h4>
<c:if test="${! empty filterMap.additionalFilters}">
	<c:if test="${!empty filterMap.additionalFilters.workflow}">
		<c:forEach var="block" items="${filterMap.additionalFilters.workflow}">
			<c:forEach var="step" items="${block.filterWorkflowStateName}">
				<p><ssf:nlt tag="searchForm.label.workflow"/>:
				<script type="text/javascript">
					document.write(ss_searchWorkflows['${block.searchWorkflow}']+" - ");
					if (ss_searchSteps['${block.searchWorkflow}-${step}'])
						document.write(ss_searchSteps['${block.searchWorkflow}-${step}']);
				</script>
				</p>
			</c:forEach>
			<c:if test="${empty block.filterWorkflowStateName}">
				<p><ssf:nlt tag="searchForm.label.workflow"/>:
				<script type="text/javascript">
					document.write(ss_searchWorkflows['${block.searchWorkflow}']);
				</script>
				</p>				
			</c:if>
		</c:forEach>
	</c:if>
	<c:if test="${!empty filterMap.additionalFilters.tag}">
		<c:forEach var="block" items="${filterMap.additionalFilters.tag}">
			<p><ssf:nlt tag="tags.communityTags"/>:${block.communityTag} <ssf:nlt tag="tags.personalTags"/>:${block.personalTag}</p>		
		</c:forEach>
	</c:if>
	<c:if test="${!empty filterMap.additionalFilters.creator_by_id}">
		<c:forEach var="block" items="${filterMap.additionalFilters.creator_by_id}">
			<p><ssf:nlt tag="searchForm.label.author"/>:${block.authorTitle} (${block.authorId})</p>
		</c:forEach>
	</c:if>
	<c:if test="${!empty filterMap.additionalFilters.entry}">
		<c:forEach var="block" items="${filterMap.additionalFilters.entry}">
			<p><ssf:nlt tag="searchForm.label.entry"/>:
			<script type="text/javascript">
				document.write(ss_searchEntries['${block.entryType}']+" - ");
				if (ss_searchFields['${block.entryType}-${block.entryElement}'])
					document.write(ss_searchFields['${block.entryType}-${block.entryElement}']);
			</script>
			: 
			<c:choose>
				<c:when test="${block.valueType == 'date' || block.valueType == 'event'}">
					<fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${block.entryValuesNotFormatted}" type="date" />
				</c:when>
				<c:otherwise>
					${block.entryValues}
				</c:otherwise>
			</c:choose>
			</p>
		</c:forEach>
	</c:if>
	<c:if test="${!empty filterMap.additionalFilters.creation_date}">
		<c:forEach var="block" items="${filterMap.additionalFilters.creation_date}">
			<p><ssf:nlt tag="searchForm.label.creationDate"/>: 
			<fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${block.startDateNotFormated}" type="date" /> - <fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${block.endDateNotFormated}" type="date" />
			</p>
		</c:forEach>
	</c:if>
	<c:if test="${!empty filterMap.additionalFilters.modification_date}">
		<c:forEach var="block" items="${filterMap.additionalFilters.modification_date}">
			<p><ssf:nlt tag="searchForm.label.modificationDate"/>: 
			<fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${block.startDateNotFormated}" type="date" /> - <fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${block.endDateNotFormated}" type="date" />
			</p>
		</c:forEach>
	</c:if>
</c:if>
</div>
<div id="ss_filterSummary_switch">
	<a href="#" class="ss_button" onClick="ss_showAdditionalOptions('ss_searchForm_additionalFilters');"><ssf:nlt tag="searchForm.advanced.showMyForm"/></a>
</div>
<div class="ss_clear">
</div>