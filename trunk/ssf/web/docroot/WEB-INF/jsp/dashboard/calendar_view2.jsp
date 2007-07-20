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

<c:set var="isDashboard" value="true" />
<%@ include file="/WEB-INF/jsp/definition_elements/calendar/calendar_view_content.jsp" %>
<script type="text/javascript">
	ss_callDashboardEvent("${ssComponentId}", "onAfterShow");
</script>

