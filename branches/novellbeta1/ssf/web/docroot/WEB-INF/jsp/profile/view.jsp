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
<c:if test="${empty ss_portletInitialization}">
<div class="ss_portlet_style ss_portlet">
<ssf:toolbar toolbar="${ss_toolbar}" style="ss_actions_bar2 ss_actions_bar" />

<table width="100%">
<tr><td>
 <a href="<portlet:renderURL windowState="maximized"><portlet:param 
 		name="action" value="view_ws_listing"/><portlet:param 
 		name="binderId" value="${ssUser.parentBinder.id}"/><portlet:param 
 		name="entryId" value="${ssUser.id}"/></portlet:renderURL>">

<c:out value="${ssUser.title}"/></a></td></tr>
</table>
</div>
</c:if>
