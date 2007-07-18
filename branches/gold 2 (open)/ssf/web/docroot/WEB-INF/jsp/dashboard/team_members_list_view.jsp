<%
// The dashboard "workspace tree" component
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
 //Don't include "include.jsp" directly 
%>
<%@ include file="/WEB-INF/jsp/dashboard/common_setup.jsp" %>

<c:set var="ssTeamMembersCount" value="${ssDashboard.beans[componentId].ssTeamMembersCount}" scope="request"/>
<c:set var="ssTeamMembers" value="${ssDashboard.beans[componentId].ssTeamMembers}"  scope="request"/>
<div id="${ss_divId}">
<%@ include file="/WEB-INF/jsp/forum/list_team_members.jsp" %>
</div>