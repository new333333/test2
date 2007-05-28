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
		"title" : "<c:out value="${ssEntry.title}" escapeXml="false"/>",
  		"id" : "${ssEntry.id}",
  		"dueDate" : <c:forEach var="event" items="${ssEntry.events}" varStatus="loopStatus">
						<c:if test="${loopStatus.first}">"<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
					      value="${event.dtEnd.time}" type="both" 
						  timeStyle="short" dateStyle="short" />"</c:if>
					</c:forEach>,
		"status" : <c:forEach var="status" items="${ssEntry.customAttributes['status'].valueSet}" varStatus="loopStatus">
						<c:if test="${loopStatus.first}">"${status}"</c:if>
					</c:forEach>,
		"completed" : <c:forEach var="completed" items="${ssEntry.customAttributes['completed'].valueSet}" varStatus="loopStatus">
						<c:if test="${loopStatus.first}">"${completed}"</c:if>
					</c:forEach>,
		"priority" : <c:forEach var="priority" items="${ssEntry.customAttributes['priority'].valueSet}" varStatus="loopStatus">
						<c:if test="${loopStatus.first}">"${priority}"</c:if>
					</c:forEach>,
		"assigned" : [<c:forEach var="user" items="${assignedUsers}" varStatus="assignedStatus">
						"${user.title}"<c:if test="${!assignedStatus.last}">,</c:if>
					</c:forEach>]
}
