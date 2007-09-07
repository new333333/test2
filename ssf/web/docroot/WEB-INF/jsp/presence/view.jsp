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

<div class="ss_portlet_style ss_portlet">
<c:if test="${ss_windowState == 'maximized'}">
<% // Navigation bar %>
<jsp:include page="/WEB-INF/jsp/definition_elements/navbar.jsp" />
</c:if>
<div class="ss_style" style="padding:4px;">
<ssHelpSpot helpId="portlets/buddy_list_portlet" offsetX="0" offsetY="-10" 
			    title="<ssf:nlt tag="helpSpot.buddyListPortlet"/>"></ssHelpSpot>
<table class="ss_style" width="100%"><tr><td>
<div>
  <div>
    <div class="ss_content_window">
<%@ include file="/WEB-INF/jsp/dashboard/buddy_list_view_init.jsp" %>
<%@ include file="/WEB-INF/jsp/dashboard/buddy_list_view.jsp" %>

</div></div></div>
</td></tr></table>
<div align="right">
  <a class="ss_linkButton" href="<portlet:renderURL 
      portletMode="edit" 
      windowState="maximized" />">
    <span><ssf:nlt tag="presence.addBuddy"/></span>
  </a>
</div>

</div>
</div>
