<%
// The task summary portlet
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
<div class="ss_portlet_style ss_portlet">
<c:if test="${ss_windowState == 'maximized'}">
<% // Navigation bar %>
<jsp:include page="/WEB-INF/jsp/definition_elements/navbar.jsp" />
</c:if>
<div class="ss_style" style="padding:4px;">

<script type="text/javascript">
//Define the url of this page in case the "add entry" operation needs to reload this page
var ss_reloadUrl = "<portlet:renderURL/>";
</script>
<table class="ss_style" width="100%"><tr><td>
<div class="ss_decor-border5">
  <div class="ss_decor-border6">
    <div class="ss_content_window">
<%@ include file="/WEB-INF/jsp/dashboard/task_view.jsp" %>
</div></div></div>
</td></tr></table>
<div align="right">
  <a class="ss_linkButton" href="<portlet:renderURL 
      portletMode="edit" 
      windowState="maximized" />">
    <span><ssf:nlt tag="button.configure"/></span>
  </a>
</div>
</div>
</div>
</table>