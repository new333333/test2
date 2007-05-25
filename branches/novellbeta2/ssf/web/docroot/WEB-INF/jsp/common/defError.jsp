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
<%@page import="java.io.PrintWriter" %>

<h1><spring:message code="exception.generalError.title"/></h1>

<p><spring:message code="exception.contactAdmin"/></p>


<ssf:ifadapter>
<input type="button" value="<ssf:nlt tag="button.returnToForm"/>" onclick="history.go(-1);"/><input type="button" value="<ssf:nlt tag="button.close"/>" onclick="window.close();"/>
</ssf:ifadapter>
<ssf:ifnotadapter>
<p style="text-align:center;">
<a href="<portlet:renderURL portletMode="view" windowState="normal"/>">- <spring:message code="button.home"/> -</a>
</p>
</ssf:ifnotadapter>

<p>
${exception.class}<br/>
${exception.localizedMessage == null ? exception : exception.localizedMessage }
</p>

<input type="button" id="ss_show" value="<ssf:nlt tag="button.showDetails"/>" onclick="ss_details.style.display='block'; ss_hide.style.display='inline'; this.style.display='none'"/>
<input type="button" id="ss_hide" value="<ssf:nlt tag="button.hideDetails"/>" onclick="ss_details.style.display='none'; ss_show.style.display='inline'; this.style.display='none'" style="display:none;"/>
<div id="ss_details" style="display:none;">
<%((Exception)request.getAttribute("exception")).printStackTrace(new PrintWriter(out)); %>
</div>

<ssf:ifnotadapter>
<p style="text-align:center;"><a href="<portlet:renderURL portletMode="view" windowState="normal"/>">- <spring:message code="button.home"/> -</a></p>
</ssf:ifnotadapter>
