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
<% // This is JSON type AJAX response with IOFrame  %>
<html>
	<head></head>
	<body>
	<textarea>{
	<c:choose>
		<c:when test="${ss_ajaxStatus.ss_ajaxNotLoggedIn}">
			notLoggedIn : ${ss_ajaxStatus.ss_ajaxNotLoggedIn} 
		</c:when>
		<c:otherwise>
			entriesAmountMsg: "<c:choose><c:when test="${entriesAmount == 1}"><ssf:nlt tag="calendar.import.status.message.single" /></c:when><c:otherwise><ssf:nlt tag="calendar.import.status.message.plural">
								<ssf:param name="value" value="${entriesAmount}"/>
							</ssf:nlt></c:otherwise></c:choose>"
		</c:otherwise>
	</c:choose>
	}</textarea>
	</body>
</html>