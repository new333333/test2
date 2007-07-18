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
<%@ page language="java" pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:if test="${!empty ssMeetingToken}">
{meetingToken : ${ssMeetingToken}}
</c:if>
<c:if test="${empty ssMeetingToken}">
{meetingError : "<ssf:escapeJavaScript value="${ssMeetingError}"/>"}
</c:if>
