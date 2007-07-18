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
[
	<c:if test="${!empty ssPagePrevious || !empty ssPageNext}">	
		{
		<c:if test="${!empty ssPagePrevious}">
			_prev: {text: "<ssf:nlt tag="widget.prev"/>", pgStr: "${ssPagePrevious.start};${ssPagePrevious.end}"}
		</c:if>
		<c:if test="${!empty ssPageNext}">
			<c:if test="${!empty ssPagePrevious}">,</c:if>
			_next: {text: "<ssf:nlt tag="widget.next"/>", pgStr: "${ssPageNext.start};${ssPageNext.end}"}
		</c:if>
		},
	</c:if>
<c:forEach var="user" items="${ssUsers}" varStatus="status">
	["<ssf:escapeJavaScript value="${user.title}"/>", "<ssf:escapeJavaScript value="${user._docId}"/>"]<c:if test="${!status.last}">,</c:if>
</c:forEach>
]