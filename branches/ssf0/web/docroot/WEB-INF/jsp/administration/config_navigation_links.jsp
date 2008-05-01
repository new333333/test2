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
<% // Navigation links %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${empty ss_breadcrumbsShowIdRoutine}">
  <c:set var="ss_breadcrumbsShowIdRoutine" value="ss_treeShowId" scope="request" />
</c:if>
<c:if test="${empty ss_breadcrumbsTreeName}">
  <c:set var="ss_breadcrumbsTreeName" value="wsTree" scope="request" />
</c:if>
<div class="ss_breadcrumb">
<ul style="margin-left:-15px;">
<c:if test="${!empty ssBinderConfig.parentBinder}">
<c:set var="parentBinder" value="${ssBinderConfig.parentBinder}"/>
<jsp:useBean id="parentBinder" type="com.sitescape.team.domain.TemplateBinder" />
<%
	Stack parentTree = new Stack();
	while (parentBinder != null) {
		parentTree.push(parentBinder);
		parentBinder = (com.sitescape.team.domain.TemplateBinder)parentBinder.getParentBinder();
	}
	while (!parentTree.empty()) {
		com.sitescape.team.domain.TemplateBinder nextConfig = (com.sitescape.team.domain.TemplateBinder) parentTree.pop();
%>
<c:set var="nextConfig" value="<%= nextConfig %>"/>
<li style="float:left;">
<c:if test="${empty ssNavigationLinkTree[nextConfig.id]}">
<a
  href="<portlet:renderURL windowState="maximized"><portlet:param 
		name="action" value="configure_configuration"/><portlet:param 
		name="binderId" value="${nextConfig.id}"/></portlet:renderURL>" >"
  onClick="return(ss_navigation_goto(this.href));"
>
<c:if test="${empty nextConfig.templateTitle}" >
--<ssf:nlt tag="entry.noTitle" />--
</c:if>
<c:out value="${nextConfig.templateTitle}" /></a>
</c:if>
<c:if test="${!empty ssNavigationLinkTree[nextConfig.id]}">
<div style="display:inline">
<ssf:tree treeName="${ss_breadcrumbsTreeName}${nextConfig.id}" treeDocument="${ssNavigationLinkTree[nextConfig.id]}" 
  rootOpen="false" topId="${nextConfig.id}"
  showImages="false" showIdRoutine="${ss_breadcrumbsShowIdRoutine}" />
</div>
</c:if>
</li>
<li style="float:left; padding-top:2px;">&nbsp;&nbsp;//&nbsp;&nbsp;</li>
<%
	}
%>
</c:if>
<li style="float:left;">
<c:if test="${empty ssNavigationLinkTree[ssBinderConfig.id]}">
<a class="ss_bold"
  href="<portlet:renderURL windowState="maximized"><portlet:param 
		name="action" value="configure_configuration"/><portlet:param 
		name="binderId" value="${nextConfig.id}"/></portlet:renderURL>" >"

  onClick="return(ss_navigation_goto(this.href));"
>
<c:if test="${empty ssBinderConfig.templateTitle}" >
--<ssf:nlt tag="entry.noTitle" />--
</c:if>
<c:out value="${ssBinderConfig.templateTitle}" /><img <ssf:alt/> border="0"
  style="width:1px;height:14px;" src="<html:imagesPath/>pics/1pix.gif"/></a>
</c:if>
<c:if test="${!empty ssNavigationLinkTree[ssBinderConfig.id]}">
<div style="display:inline">
<ssf:tree treeName="${ss_breadcrumbsTreeName}${ssBinderConfig.id}" 
  treeDocument="${ssNavigationLinkTree[ssBinderConfig.id]}" 
  topId="${ssBinderConfig.id}" rootOpen="false" 
  showImages="false" showIdRoutine="${ss_breadcrumbsShowIdRoutine}" 
  highlightNode="${ssBinderConfig.id}" />
</div>
</c:if>
</li>
</ul>
</div>
<div class="ss_clear"></div>

