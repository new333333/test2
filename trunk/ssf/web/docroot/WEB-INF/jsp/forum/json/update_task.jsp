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
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<c:choose>
	<c:when test="${ss_ajaxStatus.ss_ajaxNotLoggedIn}">
		{notLoggedIn : ${ss_ajaxStatus.ss_ajaxNotLoggedIn}}
	</c:when>
	<c:when test="${!empty ss_ajaxStatus.ss_operation_denied}">
		{denied : "<c:out value="${ss_ajaxStatus.ss_operation_denied}" escapeXml="false"/>"}
	</c:when>	
	<c:otherwise>
		<jsp:useBean id="ssEntry" type="com.sitescape.team.domain.FolderEntry" scope="request"/>
		<% // This is JSON type AJAX response  %>
		{
			"title" : "<ssf:escapeJavaScript value="${ssEntry.title}" />",
	  		"id" : "${ssEntry.id}",
	  		"dueDate" : "<c:forEach var="event" items="${ssEntry.events}" varStatus="loopStatus"><%--
							--%><c:if test="${loopStatus.first}"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
						      value="${event.dtEnd.time}" type="both" 
							  dateStyle="medium" timeStyle="short" /></c:if><%--
						--%></c:forEach>",
			"status" : <c:forEach var="status" items="${ssEntry.customAttributes['status'].valueSet}" varStatus="loopStatus">
							<c:if test="${loopStatus.first}">"<ssf:escapeJavaScript value="${status}" />"</c:if>
						</c:forEach>,
			"completed" : <c:forEach var="completed" items="${ssEntry.customAttributes['completed'].valueSet}" varStatus="loopStatus">
							<c:if test="${loopStatus.first}">"<ssf:escapeJavaScript value="${completed}" />"</c:if>
						</c:forEach>,
			"priority" : <c:forEach var="priority" items="${ssEntry.customAttributes['priority'].valueSet}" varStatus="loopStatus">
							<c:if test="${loopStatus.first}">"<ssf:escapeJavaScript value="${priority}" />"</c:if>
						</c:forEach>,
			"assigned" : [<c:forEach var="user" items="<%= com.sitescape.team.util.ResolveIds.getPrincipals(ssEntry.getCustomAttribute("assignment")) %>" varStatus="assignedStatus">
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



