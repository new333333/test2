<c:set var="ssNamespace" value="${renderResponse.namespace}"/>

<div id="ss_filterSummary_content">
	<h4><ssf:nlt tag="searchForm.summary.Title"/></h4>
	<c:if test="${! empty ss_filterMap.additionalFilters}">
		<c:if test="${!empty ss_filterMap.additionalFilters.workflow}">
			<c:forEach var="block" items="${ss_filterMap.additionalFilters.workflow}">
				<input type="hidden" name="searchWorkflow_hidden" value="${block.searchWorkflow}" />
				<c:forEach var="step" items="${block.filterWorkflowStateName}">
					<input type="hidden" name="searchWorkflowStep_${block.searchWorkflow}_step_hidden" value="${step}" />
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
		<c:if test="${!empty ss_filterMap.additionalFilters.tag}">
			<c:forEach var="block" items="${ss_filterMap.additionalFilters.tag}">
				<p>
					<c:if test="${!empty block.communityTag}">
						<ssf:nlt tag="tags.communityTags"/>: ${block.communityTag} 
						<input type="hidden" name="searchCommunityTags_hidden" value="${block.communityTag}" />
					</c:if>
					
					<c:if test="${!empty block.personalTag}">
						<ssf:nlt tag="tags.personalTags"/>:${block.personalTag}
						<input type="hidden" name="searchPersonalTags_hidden" value="${block.personalTag}" />
					</c:if>
				</p>
			</c:forEach>
		</c:if>
		<c:if test="${!empty ss_filterMap.additionalFilters.creator_by_id}">
			<c:forEach var="block" items="${ss_filterMap.additionalFilters.creator_by_id}">
				<p>
					<ssf:nlt tag="searchForm.label.author"/>: ${block.authorTitle} (${block.authorId})
					<input type="hidden" name="searchAuthors_selected_hidden" value="${block.authorTitle}" />
					<input type="hidden" name="searchAuthors_hidden" value="${block.authorId}" />
				</p>
			</c:forEach>
		</c:if>
		<c:if test="${!empty ss_filterMap.additionalFilters.entry}">
			<c:forEach var="block" items="${ss_filterMap.additionalFilters.entry}">
				<input type="hidden" name="ss_entry_def_id_hidden" value="${block.entryType}" />
				<input type="hidden" name="elementName_${block.entryType}_hidden" value="${block.entryElement}" />
				<input type="hidden" name="elementValue_${block.entryType}_hidden" value="${block.entryValuesNotFormatted}" />
				
				<p><ssf:nlt tag="searchForm.label.entry"/>:
				<script type="text/javascript">
					document.write(ss_searchEntries['${block.entryType}']);
					if (ss_searchFields['${block.entryType}-${block.entryElement}']) {
						document.write(" - " + ss_searchFields['${block.entryType}-${block.entryElement}']);
					}
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
		<c:if test="${!empty ss_filterMap.additionalFilters.last_activity}">
			<c:forEach var="block" items="${ss_filterMap.additionalFilters.last_activity}">
				<% /* There is only one value on the list. */ %>
				<input type="hidden" name="searchDaysNumber_hidden" value="${block.daysNumber}" />
				<p>
					<ssf:nlt tag="searchForm.label.lastActivityInDays"/>: ${block.daysNumber}
				</p>
			</c:forEach>
		</c:if>
		<c:if test="${!empty ss_filterMap.additionalFilters.creation_date}">
			<c:forEach var="block" items="${ss_filterMap.additionalFilters.creation_date}" varStatus="status">
				<input type="hidden" name="creation_date_searchStartDate_${status.index}_hidden" value="<fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${block.startDateNotFormated}" pattern="yyyy-MM-dd" />" />
				<input type="hidden" name="creation_date_searchEndDate_${status.index}_hidden" value="<fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${block.endDateNotFormated}" pattern="yyyy-MM-dd" />" />
				<p><ssf:nlt tag="searchForm.label.creationDate"/>: 
				<fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${block.startDateNotFormated}" type="date" /> - <fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${block.endDateNotFormated}" type="date" />
				</p>
				<c:if test="${status.last}">
					<input type="hidden" name="creation_date_length" value="${status.count}" />
				</c:if>
			</c:forEach>
		</c:if>
		<c:if test="${!empty ss_filterMap.additionalFilters.modification_date}">
			<c:forEach var="block" items="${ss_filterMap.additionalFilters.modification_date}" varStatus="status">
				<input type="hidden" name="modification_date_searchStartDate_${status.index}_hidden" value="<fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${block.startDateNotFormated}" pattern="yyyy-MM-dd" />" />
				<input type="hidden" name="modification_date_searchEndDate_${status.index}_hidden" value="<fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${block.endDateNotFormated}" pattern="yyyy-MM-dd" />" />
				<p><ssf:nlt tag="searchForm.label.modificationDate"/>: 
				<fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${block.startDateNotFormated}" type="date" /> - <fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${block.endDateNotFormated}" type="date" />
				</p>
				<c:if test="${status.last}">
					<input type="hidden" name="modification_date_length" value="${status.count}" />
				</c:if>
			</c:forEach>
		</c:if>
		<c:if test="${!empty ss_filterMap.additionalFilters.item_types}">
			<p>
				<ssf:nlt tag="searchForm.sectionTitle.ItemType"/>: 
				<c:forEach var="type" items="${ss_filterMap.additionalFilters.item_types}" varStatus="loopStatus">
					<c:if test="${type.value}">
						<ssf:nlt tag="searchForm.itemType.${type.key}"/><c:if test="${!loopStatus.last}">, </c:if>
						<input type="hidden" name="searchItemType_hidden" value="${type.key}" />
					</c:if>
				</c:forEach>
			</p>
		</c:if>
	</c:if>
</div>
<div id="ss_filterSummary_switch">
	<a href="#" class="ss_button" onClick="ss_showAdditionalOptions('ss_searchForm_additionalFilters', 'ss_search_more_options_txt_${ssNamespace}', '${ssNamespace}');"><ssf:nlt tag="searchForm.advanced.showMyForm"/></a>
</div>
<div class="ss_clear">
</div>