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
<%@ page session="false" %>
<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ssf" uri="http://www.sitescape.com/tags-ssf" %>
<%@ taglib prefix="portletadapter" uri="http://www.sitescape.com/tags-portletadapter" %>
<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>
<%@ page import="com.sitescape.util.ParamUtil" %>
<portletadapter:defineObjects1/>
<ssf:ifadapter><portletadapter:defineObjects2/></ssf:ifadapter>
<ssf:ifnotadapter><portlet:defineObjects/></ssf:ifnotadapter>
<%

// General variables
Integer nameCount = (Integer) renderRequest.getAttribute("ss_menu_tag_name_count");
String menuTagDivId = "ss_menuTagDiv" + nameCount.toString();
String title = ParamUtil.get(request, "title", "");
String titleId = ParamUtil.get(request, "titleId", "");
String titleClass = ParamUtil.get(request, "titleClass", "ss_toolbar_item");
String openStyle = ParamUtil.get(request, "openStyle", "");
String anchor = ParamUtil.get(request, "anchor", "");
String offsetTop = ParamUtil.get(request, "offsetTop", "");
String offsetLeft = ParamUtil.get(request, "offsetLeft", "");
String menuImage = ParamUtil.get(request, "menuImage", "");
String isAccessible = ParamUtil.get(request, "isAccessible", "false");
%>
<c:set var="isAccessible" value="<%= isAccessible %>"/>

<c:if test="${isAccessible == 'true'}">

	<div align="center" style="margin:10px 0px 0px 0px;">
		<a href="javascript: ;" <ssf:title tag="title.closeMenu"/> onClick="ss_hideAccessibleMenu('<%= menuTagDivId %><portlet:namespace/>');">
		<span><ssf:nlt tag="button.close"/></span></a>
	</div>
	
</c:if>	
	
</div>

<span id="parent_<%= menuTagDivId %><portlet:namespace/>" style="display:inline;"
   ><a class="<%= titleClass %>"
id="<%= titleId %>" href="javascript: ;" <ssf:title tag="title.showMenu" />

<c:if test="${isAccessible == 'false'}">
	onClick="ss_activateMenuLayerClone('<%= menuTagDivId %><portlet:namespace/>', 'parent_<%= menuTagDivId %><portlet:namespace/>', '<%= offsetLeft %>', '<%= offsetTop %>', '<%= openStyle %>');"
</c:if>

<c:if test="${isAccessible == 'true'}">
	onClick="ss_showAccessibleMenu('<%= menuTagDivId %><portlet:namespace/>');"
</c:if>
	  
><%= title %>
<c:if test="<%= !("".equals(menuImage)) %>">
	<img src='<html:imagesPath/><%= menuImage %>' <ssf:alt tag="alt.showMenu"/>/>
</c:if>
</a></span>