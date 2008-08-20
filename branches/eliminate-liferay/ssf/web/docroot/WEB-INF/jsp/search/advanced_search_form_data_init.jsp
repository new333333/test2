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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.sitescape.team.util.CalendarHelper" %>

function ss_initSearchMainMask() {
	// fill the search mask form
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
				ss_addInitializedTag("<ssf:escapeJavaScript value="${block.communityTag}"/>", "<ssf:escapeJavaScript value="${block.personalTag}"/>");
			</c:forEach>
		</c:if>
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
		<c:if test="${!empty ss_filterMap.additionalFilters.workflow}">
			<c:forEach var="block" items="${ss_filterMap.additionalFilters.workflow}">
				ss_addInitializedWorkflow("<ssf:escapeJavaScript value="${block.searchWorkflow}"/>", [<%--
				--%><c:forEach var="step" items="${block.filterWorkflowStateName}" varStatus="loopStatus"><%--
					--%>"<ssf:escapeJavaScript value="${step}"/>"<c:if test="${!loopStatus.last}">, </c:if><%--
				--%></c:forEach><%--
				--%>]);<%--
			--%></c:forEach>
		</c:if>
		<c:if test="${!empty ss_filterMap.additionalFilters.entry}">
			<c:forEach var="block" items="${ss_filterMap.additionalFilters.entry}">
				ss_addInitializedEntry("<ssf:escapeJavaScript value="${block.entryType}"/>", "<ssf:escapeJavaScript value="${block.entryElement}"/>", "<ssf:escapeJavaScript value="${block.entryValuesNotFormatted}"/>", "<ssf:escapeJavaScript value="${block.entryValues}"/>");
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
		<c:if test="${empty ss_filterMap.additionalFilters.last_activity}">
			ss_addOption('last_activity');
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
		ss_addOption('last_activity');
	</c:if>		
	  ss_searchMoreInitialized = true;
}	

ss_initSearchMainMask();
