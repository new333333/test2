<%
// The dashboard "post-it note" component
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
<div style="padding: 5px; background-color: ${ssDashboard.dashboard.components[ssComponentId].data.noteColor}">
<c:out value="${ssDashboard.dashboard.components[ssComponentId].data.note}" escapeXml="false"/>
</div>
