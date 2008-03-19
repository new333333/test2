<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="portletadapter" uri="http://www.sitescape.com/tags-portletadapter" %>
<portletadapter:defineObjects1/>
<ssf:ifadapter><portletadapter:defineObjects2/></ssf:ifadapter>
<ssf:ifnotadapter><portlet:defineObjects/></ssf:ifnotadapter>

<c:if test="${ssConfigJspStyle != 'mobile'}">
<c:if test="${ss_showUserShowPresence}">
  <ssf:presenceInfo user="${ss_showUserUser}" showTitle="true" titleStyle="${ss_showUserTitleStyle}" /> 
</c:if>
<c:if test="${!ss_showUserShowPresence}">
<c:if test="${ss_showUserUser.active}">
	<ssf:ifadapter>
	  <c:if test="${!empty ss_showUserUser.workspaceId}">
	  <a href="<ssf:url adapter="true" portletName="ss_forum" 
	    action="view_permalink"
	    binderId="${ss_showUserUser.workspaceId}">
	    <ssf:param name="entityType" value="workspace" />
	    <ssf:param name="newTab" value="1" />
		</ssf:url>"
	  onClick="ss_openUrlInParentWorkarea(this.href, '${ss_showUserUser.workspaceId}', 'view_ws_listing');return false;"
	  ><span id="${ss_showUserUser.id}" 
	    class="${ss_showUserTitleStyle} ss_muster_users">${ss_showUserUser.title}</span></a>
	  </c:if>
	  <c:if test="${empty ss_showUserUser.workspaceId}">
	  <span id="${ss_showUserUser.id}" 
	    class="${ss_showUserTitleStyle} ss_muster_users">${ss_showUserUser.title}</span>
	  </c:if>
	</ssf:ifadapter>
	<ssf:ifnotadapter>
	  <c:if test="${!empty ss_showUserUser.workspaceId}">
	  <a href="<ssf:url windowState="maximized"><ssf:param 
	  	name="action" value="view_ws_listing"/><ssf:param 
	  	name="binderId" value="${ss_showUserUser.workspaceId}"/><ssf:param 
	  	name="newTab" value="1"/></ssf:url>"
	  onClick="ss_openUrlInWorkarea(this.href, '${ss_showUserUser.workspaceId}', 'view_ws_listing');return false;"
	  ><span id="${ss_showUserUser.id}"  
	    class="${ss_showUserTitleStyle} ss_muster_users">${ss_showUserUser.title}</span></a>
	  </c:if>
	  <c:if test="${empty ss_showUserUser.workspaceId}">
	  <span id="${ss_showUserUser.id}"  
	    class="${ss_showUserTitleStyle} ss_muster_users">${ss_showUserUser.title}</span>
	  </c:if>
	</ssf:ifnotadapter>
</c:if>
<c:if test="${!ss_showUserUser.active}">
  <span id="${ss_showUserUser.id}" 
    class="${ss_showUserTitleStyle} ss_muster_users">${ss_showUserUser.title}</span>
</c:if>
</c:if>
</c:if>
<c:if test="${ssConfigJspStyle == 'mobile'}">
  <c:if test="${!empty ss_showUserUser.workspaceId}">
  <a href="<ssf:url adapter="true" portletName="ss_forum" 
	    action="__ajax_mobile"
	    operation="mobile_show_workspace"
	    binderId="${ss_showUserUser.workspaceId}" />"
  ><span>${ss_showUserUser.title}</span></a>
  </c:if>
  <c:if test="${empty ss_showUserUser.workspaceId}">
  <span>${ss_showUserUser.title}</span>
  </c:if>
</c:if>
