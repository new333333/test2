<%
// The dashboard "search" component
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
  //this is used by penlets and portlets
 //Don't include "include.jsp" directly 
%>
<%@ include file="/WEB-INF/jsp/dashboard/common_setup.jsp" %>

<c:set var="prefix" value="${ssComponentId}${renderResponse.namespace}" />

<script type="text/javascript">

<c:set var="binderIds" value="" />
<c:forEach var="folder" items="${ssDashboard.beans[componentId].ssFolderList}" varStatus="status">
	<c:choose>
		<c:when test="${status.first}">
			<c:set var="binderIds" value="binderIds=${folder.id}" />
		</c:when>
		<c:otherwise>
			<c:set var="binderIds" value="${binderIds}&binderIds=${folder.id}" />
		</c:otherwise>
	</c:choose>
</c:forEach>

	var ss_findEventsUrl${prefix} = "<ssf:url 
		    	adapter="true" 
		    	portletName="ss_forum" 
		    	action="__ajax_request" 
		    	actionUrl="true" >
					<ssf:param name="binderId" value="${ssBinder.id}" />
					<ssf:param name="operation" value="find_calendar_events" />
		    	</ssf:url><c:if test="${!empty binderIds}">&${binderIds}</c:if><c:if test="${!empty ssDashboard}">&ssDashboardRequest=true</c:if>";
</script>

<%@ include file="/WEB-INF/jsp/definition_elements/calendar/calendar_view_content.jsp" %>


