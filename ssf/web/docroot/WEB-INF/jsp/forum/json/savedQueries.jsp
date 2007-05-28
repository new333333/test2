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
[<%--
--%><c:forEach var="query" items="${ss_UserQueries}" varStatus="status"><%--
  --%>"${query.key}"<c:if test="${!status.last}">,</c:if><%--
--%></c:forEach><%--
--%>]