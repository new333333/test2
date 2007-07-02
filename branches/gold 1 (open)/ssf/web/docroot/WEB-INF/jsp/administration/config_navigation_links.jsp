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
  href="<portlet:renderURL><portlet:param 
		name="action" value="configure_configuration"/>
		<portlet:param  name="binderId" value="${nextConfig.id}"/></portlet:renderURL>" >"
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
  href="<portlet:renderURL><portlet:param 
		name="action" value="configure_configuration"/>
		<portlet:param  name="binderId" value="${nextConfig.id}"/></portlet:renderURL>" >"

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

