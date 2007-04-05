<%
// The dashboard "iframe" component
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
<iframe src="${ssDashboard.dashboard.components[ssComponentId].data.url[0]}"
  style="width: 99%;
  height: ${ssDashboard.dashboard.components[ssComponentId].data.height[0]};
  margin:0px; padding:0px;" frameBorder="0" >xxx</iframe>
