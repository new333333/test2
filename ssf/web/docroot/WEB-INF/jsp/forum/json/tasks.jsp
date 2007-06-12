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
tasks : [<%--
--%><c:forEach var="entry" items="${ssFolderEntries}" varStatus="status"><%--
  --%><jsp:useBean id="entry" type="java.util.HashMap" /><%--
  --%>{"title" : "<c:out value="${entry.title}" escapeXml="false"/>",
  		"id" : "${entry._docId}",
  		"dueDate" : "<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
					      value="<%= (java.util.Date)entry.get("start_end#EndDate") %>" type="both" 
						  timeStyle="short" dateStyle="short" />",
  		"dueDateObj" : {year : <fmt:formatDate value="<%= (java.util.Date)entry.get("start_end#EndDate") %>" pattern="yyyy" timeZone="${ssUser.timeZone.ID}"/>,
						month : <fmt:formatDate value="<%= (java.util.Date)entry.get("start_end#EndDate") %>" pattern="M" timeZone="${ssUser.timeZone.ID}"/>, 
						dayOfMonth : <fmt:formatDate value="<%= (java.util.Date)entry.get("start_end#EndDate") %>" pattern="d" timeZone="${ssUser.timeZone.ID}"/>,
						hour : <fmt:formatDate value="<%= (java.util.Date)entry.get("start_end#EndDate") %>" pattern="H" timeZone="${ssUser.timeZone.ID}"/>,
						minute : <fmt:formatDate value="<%= (java.util.Date)entry.get("start_end#EndDate") %>" pattern="m" timeZone="${ssUser.timeZone.ID}"/>},					  						  						  						  						 
		"status" : "${entry.status}",
		"completed" : "${entry.completed}",
		"priority" : "${entry.priority}",
		"completted" : "${entry.completed}",
		"assigned" : [<c:forEach var="user" items="<%= com.sitescape.team.util.ResolveIds.getPrincipals(entry.get("assignment")) %>" varStatus="assignedStatus">
						"${user.title}"<c:if test="${!assignedStatus.last}">,</c:if>
					</c:forEach>]
		}<c:if test="${!status.last}">,</c:if><%--
--%></c:forEach><%--
--%>]
}