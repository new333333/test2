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
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page import="java.util.Date" %>
/*
<c:choose>
	<c:when test="${ss_ajaxStatus.ss_ajaxNotLoggedIn}">
		{notLoggedIn : ${ss_ajaxStatus.ss_ajaxNotLoggedIn}}
	</c:when>
	<c:when test="${!empty ss_ajaxStatus.ss_operation_denied}">
		{denied : "<c:out value="${ss_ajaxStatus.ss_operation_denied}" escapeXml="false"/>"}
	</c:when>	
	<c:otherwise>
		<jsp:useBean id="ssEntry" type="com.sitescape.team.domain.FolderEntry" scope="request"/>
		
		<%	
			boolean overdue = false;
			if (ssEntry.getEvents() != null && !ssEntry.getEvents().isEmpty()) {
				overdue = com.sitescape.team.util.DateComparer.isOverdue((Date)((com.sitescape.team.domain.Event)ssEntry.getEvents().iterator().next()).getDtEnd().getTime());
			}
			
		%>
		<c:set var="overdue" value="<%= overdue %>"/>

		
		<% // This is JSON type AJAX response  %>
		{
			"title" : "<ssf:escapeJavaScript value="${ssEntry.title}" />",
	  		"id" : "${ssEntry.id}",
	  		"dueDate" : "<c:forEach var="event" items="${ssEntry.events}" varStatus="loopStatus"><%--
							--%><c:if test="${loopStatus.first}"><%--
							  --%><c:choose><%--
									--%><c:when test="${!empty event.timeZone}"><%--
										--%><fmt:formatDate 
												timeZone="${ssUser.timeZone.ID}"
												value="${event.dtEnd.time}" type="both" 
												dateStyle="short" timeStyle="short" /><%--					
									--%></c:when><%--
									--%><c:otherwise><%--
										--%><fmt:formatDate 
												timeZone="${ssUser.timeZone.ID}"
												value="${event.dtEnd.time}" type="date" 
												dateStyle="short"/><%--
									--%></c:otherwise><%--
								--%></c:choose><%--
							  --%></c:if><%--
						--%></c:forEach>",
			"overdue" : ${overdue},
			"status" : <c:forEach var="status" items="${ssEntry.customAttributes['status'].valueSet}" varStatus="loopStatus">
							<c:if test="${loopStatus.first}">"<ssf:escapeJavaScript value="${status}" />"</c:if>
						</c:forEach>,
			"completed" : <c:forEach var="completed" items="${ssEntry.customAttributes['completed'].valueSet}" varStatus="loopStatus">
							<c:if test="${loopStatus.first}">"<ssf:escapeJavaScript value="${completed}" />"</c:if>
						</c:forEach>,
			"priority" : <c:forEach var="priority" items="${ssEntry.customAttributes['priority'].valueSet}" varStatus="loopStatus">
							<c:if test="${loopStatus.first}">"<ssf:escapeJavaScript value="${priority}" />"</c:if>
						</c:forEach>,
			"assigned" : [<c:forEach var="user" items='<%= com.sitescape.team.util.ResolveIds.getPrincipals(ssEntry.getCustomAttribute("assignment")) %>' varStatus="assignedStatus">
							"<ssf:escapeJavaScript value="${user.title}" />"<c:if test="${!assignedStatus.last}">,</c:if>
						</c:forEach>],
			statuses : 
						[
							<c:forEach var="status" items="${ssEntryDefinitionElementData['status'].values}" varStatus="loopStatus">
							{
								value : "<ssf:escapeJavaScript value="${status.value}" />",
								key : "<ssf:escapeJavaScript value="${status.key}" />"
							}<c:if test="${!loopStatus.last}">,</c:if>
							</c:forEach>
						],
			priorities : 
			[
				<c:forEach var="priority" items="${ssEntryDefinitionElementData['priority'].values}" varStatus="loopStatus">
				{
					value : "<ssf:escapeJavaScript value="${priority.value}" />",
					key : "<ssf:escapeJavaScript value="${priority.key}" />"
				}<c:if test="${!loopStatus.last}">,</c:if>
				</c:forEach>
			],
			
			completedValues : 
			{
				<c:forEach var="completed" items="${ssEntryDefinitionElementData['completed'].values}" varStatus="loopStatus">
					"<ssf:escapeJavaScript value="${completed.key}" />" : "<ssf:escapeJavaScript value="${completed.value}" />"
					<c:if test="${!loopStatus.last}">,</c:if>
				</c:forEach>
			}
		}
			
	</c:otherwise>
</c:choose>
*/