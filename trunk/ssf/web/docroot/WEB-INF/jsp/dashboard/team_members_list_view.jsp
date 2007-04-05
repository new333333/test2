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
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<c:set var="ssNamespace" value="${renderResponse.namespace}"/>
<c:if test="${!empty ssComponentId}">
<c:set var="ssNamespace" value="${ssNamespace}_${ssComponentId}"/>
</c:if>
<c:set var="componentId" value="${ssComponentId}"/>
<c:if test="${empty ssComponentId}">
<c:set var="componentId" value="${ssDashboard.ssComponentId}" />
</c:if>
<c:set var="portletNamespace" value=""/>
<ssf:ifnotadapter>
<c:set var="portletNamespace" value="${renderResponse.namespace}"/>
</ssf:ifnotadapter>

<c:if test="${ssDashboard.scope == 'portlet'}">
<%@ include file="/WEB-INF/jsp/dashboard/portletsupport.jsp" %>
<script type="text/javascript">    	
function ${ssNamespace}_user_url(binderId, entryId, type) {
	return ss_gotoPermalink(binderId, entryId, type, '${portletNamespace}');
}
</script>
</c:if>

<c:if test="${ssConfigJspStyle == 'template'}">
<script type="text/javascript">
function ${ssNamespace}_user_url(binderId, entryId, type) {
	return false;
}
</script>
</c:if>




<c:set var="ssTeamMembersCount" value="${ssDashboard.beans[componentId].ssTeamMembersCount}" />
<c:set var="ssTeamMembers" value="${ssDashboard.beans[componentId].ssTeamMembers}" />

<%@ include file="/WEB-INF/jsp/forum/list_team_members.jsp" %>
