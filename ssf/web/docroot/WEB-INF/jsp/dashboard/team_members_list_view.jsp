<%
// The dashboard "workspace tree" component
/**
 * Copyright (c) 2006 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
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
