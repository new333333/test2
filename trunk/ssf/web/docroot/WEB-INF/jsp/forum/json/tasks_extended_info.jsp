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
<% // This is JSON type AJAX response  %>
{
	statuses : 
		[
			<c:forEach var="status" items="${ssEntryDefinitionElementData['status'].values}" varStatus="loopStatus">
			{
				value : "<c:out value="${status.value}" escapeXml="false"/>",
				key : "<c:out value="${status.key}" escapeXml="false"/>"
			}<c:if test="${!loopStatus.last}">,</c:if>
			</c:forEach>
		],
		
	priorities : 
	[
		<c:forEach var="priority" items="${ssEntryDefinitionElementData['priority'].values}" varStatus="loopStatus">
		{
			value : "<c:out value="${priority.value}" escapeXml="false"/>",
			key : "<c:out value="${priority.key}" escapeXml="false"/>"
		}<c:if test="${!loopStatus.last}">,</c:if>
		</c:forEach>
	],
	
	completed : 
	{
		<c:forEach var="completed" items="${ssEntryDefinitionElementData['completed'].values}" varStatus="loopStatus">
			"<c:out value="${completed.key}" escapeXml="false"/>" : "<c:out value="${completed.value}" escapeXml="false"/>"
			<c:if test="${!loopStatus.last}">,</c:if>
		</c:forEach>
	}
}

