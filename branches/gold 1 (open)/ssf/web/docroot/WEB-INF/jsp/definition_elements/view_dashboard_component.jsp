<%
// View a dashboard component
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
<%@ include file="/WEB-INF/jsp/common/snippet.include.jsp" %>
<c:set var="ssf_support_files_loaded" value="1" scope="request"/>

<ssf:dashboard id="${ssDashboard.ssComponentId}"
  type="viewData" configuration="${ssDashboard}"/>
