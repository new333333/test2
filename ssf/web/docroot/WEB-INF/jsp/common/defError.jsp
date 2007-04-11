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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<h1><spring:message code="exception.generalError.title"/></h1>

<p>${exception.localizedMessage == null ? exception : exception.localizedMessage }<br/>
<spring:message code="exception.contactAdmin"/></p>

<p>${exception.class}</p>

<p><% ((Exception)request.getAttribute("exception")).printStackTrace(); %>

<ssf:ifnotadapter>
<p style="text-align:center;"><a href="<portlet:renderURL portletMode="view" windowState="normal"/>">- <spring:message code="button.home"/> -</a></p>
</ssf:ifnotadapter>
