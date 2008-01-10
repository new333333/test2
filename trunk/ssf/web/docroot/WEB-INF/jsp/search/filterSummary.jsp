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
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ssNamespace" value="${renderResponse.namespace}"/>
	<script type="text/javascript">
	
	<c:if test="${!empty ssWorkflowDefinitionMap}">
		var ss_searchWorkflows = new Array();
		var ss_searchSteps = new Array();
		<c:forEach var="wf" items="${ssWorkflowDefinitionMap}">
			ss_searchWorkflows['${wf.id}'] = '<ssf:escapeJavaScript value="${wf.title}"/>';
			<c:forEach var="step" items="${wf.steps}">
				ss_searchSteps['${wf.id}-<ssf:escapeJavaScript value="${step.name}"/>'] = '<ssf:escapeJavaScript value="${step.title}"/>';
			</c:forEach>
		</c:forEach>
	</c:if>
	<c:if test="${!empty ssEntryDefinitionMap}">
		var ss_searchEntries = new Array();
		var ss_searchFields = new Array();
		var ss_searchFieldsTypes = new Array();
		<c:forEach var="entry" items="${ssEntryDefinitionMap}">
			ss_searchEntries['${entry.id}'] = '<ssf:escapeJavaScript value="${entry.title}"/>';
			<c:forEach var="field" items="${entry.fields}">
				ss_searchFields['${entry.id}-<ssf:escapeJavaScript value="${field.name}"/>'] = '<ssf:escapeJavaScript value="${field.title}"/>';
				ss_searchFieldsTypes['${entry.id}-<ssf:escapeJavaScript value="${field.name}"/>'] = '<ssf:escapeJavaScript value="${field.type}"/>';
			</c:forEach>
		</c:forEach>
	</c:if>
	</script>
<div id="ss_filterSummary_content">
	<h4><ssf:nlt tag="searchForm.summary.Title"/></h4>
	<c:if test="${! empty ss_filterMap.additionalFilters}">
		<c:if test="${!empty ss_filterMap.additionalFilters.workflow}">
			<c:forEach var="block" items="${ss_filterMap.additionalFilters.workflow}">
				<input type="hidden" name="searchWorkflow_hidden" value="${block.searchWorkflow}" />
				<p>
					<ssf:nlt tag="searchForm.label.workflow"/>:
					<script type="text/javascript">
						document.write(ss_searchWorkflows['${block.searchWorkflow}']);
					</script>
					<c:if test="${!empty block.filterWorkflowStateName}">
						-
					</c:if>
					<c:forEach var="step" items="${block.filterWorkflowStateName}" varStatus="status">
						<input type="hidden" name="searchWorkflowStep_${block.searchWorkflow}_step_hidden" value="${step}" />
						<script type="text/javascript">
							if (ss_searchSteps['${block.searchWorkflow}-<ssf:escapeJavaScript value="${step}"/>'])
								document.write(ss_searchSteps['${block.searchWorkflow}-<ssf:escapeJavaScript value="${step}"/>']<c:if test="${!status.last}"> + ", "</c:if>);
						</script>
					</c:forEach>
				</p>
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
					<ssf:nlt tag="searchForm.label.author"/>: ${block.authorTitle}
					<input type="hidden" name="searchAuthors_selected_hidden" value="${block.authorTitle}" />
					<input type="hidden" name="searchAuthors_hidden" value="${block.authorId}" />
				</p>
			</c:forEach>
		</c:if>
		<c:if test="${!empty ss_filterMap.additionalFilters.entry}">
			<c:forEach var="block" items="${ss_filterMap.additionalFilters.entry}">
				<input type="hidden" name="ss_entry_def_id_hidden" value="${block.entryType}" />
				<input type="hidden" name="elementName_${block.entryType}_hidden" value="${block.entryElement}" />
				<c:choose>
					<c:when test="${block.valueType == 'date' || block.valueType == 'event'}">
						<input type="hidden" name="elementValue_${block.entryType}_hidden" value="<fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${block.entryValuesNotFormatted}" pattern="yyyy-MM-dd"/>" />
					</c:when>
					<c:when test="${block.valueType == 'date_time'}">
						<input type="hidden" name="elementValue_${block.entryType}_hidden" value="<fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${block.entryValuesNotFormatted}" pattern="yyyy-MM-dd"/>" />
						<c:if test="${fn:length(block.entryValues) > 10}">
							<input type="hidden" name="elementValue0_${block.entryType}_hidden" value="<fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${block.entryValuesNotFormatted}" pattern="HH:mm"/>" />
						</c:if>
					</c:when>
					<c:otherwise>
						<input type="hidden" name="elementValue_${block.entryType}_hidden" value="${block.entryValuesNotFormatted}" />
					</c:otherwise>
				</c:choose>				
				
				
				<p><ssf:nlt tag="searchForm.label.entry"/>:
				<script type="text/javascript">
					document.write(ss_searchEntries['<ssf:escapeJavaScript value="${block.entryType}"/>']);
					if (ss_searchFields['<ssf:escapeJavaScript value="${block.entryType}"/>-<ssf:escapeJavaScript value="${block.entryElement}"/>']) {
						document.write(" - " + ss_searchFields['<ssf:escapeJavaScript value="${block.entryType}"/>-<ssf:escapeJavaScript value="${block.entryElement}"/>']);
					}
				</script>
				:
				<c:choose>
					<c:when test="${block.valueType == 'date' || block.valueType == 'event'}">
						<fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${block.entryValuesNotFormatted}" type="date" dateStyle="medium"/>
					</c:when>
					<c:when test="${block.valueType == 'date_time'}">
						<c:choose>
							<c:when test="${fn:length(block.entryValues) > 10}">
								<fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${block.entryValuesNotFormatted}" type="both" dateStyle="medium" timeStyle="short"/>
							</c:when>
							<c:otherwise>
								<fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${block.entryValuesNotFormatted}" type="date" dateStyle="medium"/>
							</c:otherwise>							
						</c:choose>
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
	<a href="javascript:;" class="ss_button" onClick="ss_showAdditionalOptions('ss_searchForm_additionalFilters', 'ss_search_more_options_txt_${ssNamespace}', '${ssNamespace}');"><ssf:nlt tag="searchForm.advanced.showMyForm"/></a>
</div>
<div class="ss_clear">
</div>