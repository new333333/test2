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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/xml; charset=UTF-8" %>
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">

	<taconite-replace contextNodeID="${ssNamespace}_refreshDate" 
	parseInBrowser="true"><div id="${ssNamespace}_refreshDate">
<span class="ss_smallprint ss_light"><ssf:nlt 
tag="presence.last.refresh"/> <fmt:formatDate timeZone="${ssUser.timeZone.ID}"
value="<%= new java.util.Date() %>" type="time" /></span>
</div></taconite-replace>

<c:forEach var="user" items="${ssUsers}">
<jsp:useBean id="user" type="com.sitescape.team.domain.User" />

	<taconite-replace contextNodeID="${ssNamespace}_user_${user.id}" 
	parseInBrowser="true"><span id="${ssNamespace}_user_${user.id}"
	><ssf:presenceInfo user="<%=user%>" componentId="${ssNamespace}" 
	/></span></taconite-replace>
</c:forEach>
</c:if>
</taconite-root>
