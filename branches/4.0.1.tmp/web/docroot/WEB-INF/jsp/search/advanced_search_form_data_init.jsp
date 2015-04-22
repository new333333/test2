<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.kablink.teaming.util.CalendarHelper" %>

function ss_initSearchOptions() {
	<c:if test="${! empty ss_filterMap.additionalFilters}">
	  <ssf:ifNotFilr>
		<c:if test="${!empty ss_filterMap.additionalFilters.tag}">
			<c:forEach var="block" items="${ss_filterMap.additionalFilters.tag}">
				ss_addInitializedTag("<ssf:escapeJavaScript value="${block.communityTag}"/>", "<ssf:escapeJavaScript value="${block.personalTag}"/>");
			</c:forEach>
		</c:if>
	  </ssf:ifNotFilr>
		<c:if test="${!empty ss_filterMap.additionalFilters.creator_by_id}">
			<c:forEach var="block" items="${ss_filterMap.additionalFilters.creator_by_id}">
				ss_addInitializedAuthor("${block.authorId}", "<ssf:escapeJavaScript value="${block.authorTitle}"/>");
			</c:forEach>
		</c:if>
		<c:if test="${!empty ss_filterMap.additionalFilters.last_activity}">
			<c:forEach var="block" items="${ss_filterMap.additionalFilters.last_activity}">
				ss_addInitializedLastActivity(${block.daysNumber});
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
		<ssf:ifNotFilr>
		<c:if test="${!empty ss_filterMap.additionalFilters.workflow}">
			<c:forEach var="block" items="${ss_filterMap.additionalFilters.workflow}">
				<c:set var="wf_blockId" value="${block.searchWorkflow}"/>
				<c:set var="wf_stepNamesChecked" value=""/>
				<c:set var="wf_stepNames" value=""/>
				<c:set var="wf_title" value=""/>
				<c:set var="wf_stepCaptions" value=""/>
				<c:forEach var="step" items="${block.filterWorkflowStateName}" varStatus="loopStatus">
					<c:set var="wf_stepNamesChecked">${wf_stepNamesChecked}<c:if test="${!loopStatus.first}">, </c:if>"<ssf:escapeJavaScript value="${step}"/>"</c:set>
				</c:forEach>
				<c:forEach var="wf" items="${ssWorkflowDefinitionMap}">
				  <c:if test="${wf.id == wf_blockId}">
				    <c:set var="wf_title" value="${wf.title}"/>
					<c:forEach var="step2" items="${wf.steps}" varStatus="loopStatus2">
						<c:set var="wf_stepCaptions">${wf_stepCaptions}<c:if test="${!loopStatus2.first}">, </c:if>"<ssf:escapeJavaScript value="${step2.title}"/>"</c:set>
					</c:forEach>
				  </c:if>
				</c:forEach>
				ss_addInitializedWorkflow("<ssf:escapeJavaScript value="${block.searchWorkflow}"/>", "<ssf:escapeJavaScript value="${wf_title}"/>", <%--
				--%>[${wf_stepNamesChecked}], [${wf_stepCaptions}]);
			</c:forEach>
		</c:if>
		</ssf:ifNotFilr>
		<ssf:ifNotFilr>
		<c:if test="${!empty ss_filterMap.additionalFilters.entry}">
			<c:forEach var="block" items="${ss_filterMap.additionalFilters.entry}">
				ss_addInitializedEntry("<ssf:escapeJavaScript value="${block.entryType}"/>", 
						"<ssf:escapeJavaScript value="${block.entryElement}"/>", 
						"<ssf:escapeJavaScript value="${block.entryValuesNotFormatted}"/>", 
						"<ssf:escapeJavaScript value="${block.entryValues}"/>", 
						"<ssf:escapeJavaScript value="${block.valueType}"/>", 
						"<ssf:escapeJavaScript value="${block.title}"/>",
						"<ssf:escapeJavaScript value="${block.entryType}"/>",
						"<ssf:escapeJavaScript value="${block.entryTypeTitle}"/>");
			</c:forEach>
		</c:if>
		</ssf:ifNotFilr>
		
		<ssf:ifNotFilr>
		<c:if test="${empty ss_filterMap.additionalFilters.workflow}">
			ss_addOption('workflow');
		</c:if>
		</ssf:ifNotFilr>
		<ssf:ifNotFilr>
		<c:if test="${empty ss_filterMap.additionalFilters.tag}">
			ss_addOption('tag');
		</c:if>
		</ssf:ifNotFilr>
		<c:if test="${empty ss_filterMap.additionalFilters.creation_date}">
			ss_addOption('creation_date');
		</c:if>
		<c:if test="${empty ss_filterMap.additionalFilters.modification_date}">
			ss_addOption('modification_date');
		</c:if>
		<c:if test="${empty ss_filterMap.additionalFilters.creator_by_id}">
			ss_addOption('creator_by_id');
		</c:if>
		<c:if test="${empty ss_filterMap.additionalFilters.last_activity}">
			ss_addOption('last_activity');
		</c:if>
		<ssf:ifNotFilr>
		<c:if test="${empty ss_filterMap.additionalFilters.entry}">
			ss_addOption('entry');
		</c:if>
		</ssf:ifNotFilr>
		
	</c:if>
	<c:if test="${empty ss_filterMap.additionalFilters}">
		ss_addOption('creation_date');
		ss_addOption('modification_date');
		<ssf:ifNotFilr>
		  ss_addOption('tag');
		</ssf:ifNotFilr>
		<ssf:ifNotFilr>
		  ss_addOption('workflow');
		</ssf:ifNotFilr>
		ss_addOption('creator_by_id');
		<ssf:ifNotFilr>
		  ss_addOption('entry');
		</ssf:ifNotFilr>
		ss_addOption('last_activity');
	</c:if>		
	  ss_searchMoreInitialized = true;
}	

