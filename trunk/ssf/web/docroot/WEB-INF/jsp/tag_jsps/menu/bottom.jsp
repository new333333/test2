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
		<a href="javascript: ;" <ssf:title tag="title.closeMenu"/> onClick="ss_hideAccessibleMenu('<%= menuTagDivId %>${renderResponse.namespace}');">
		<span><ssf:nlt tag="button.close"/></span></a>
	</div>
	
</c:if>	
	
</div>

<span id="parent_<%= menuTagDivId %>${renderResponse.namespace}" style="display:inline;"
   ><a class="<%= titleClass %>"
id="<%= titleId %>" href="javascript: ;" <ssf:title tag="title.showMenu" />

<c:if test="${isAccessible == 'false'}">
	onClick="ss_activateMenuLayerClone('<%= menuTagDivId %>${renderResponse.namespace}', 'parent_<%= menuTagDivId %>${renderResponse.namespace}', '<%= offsetLeft %>', '<%= offsetTop %>', '<%= openStyle %>');"
</c:if>

<c:if test="${isAccessible == 'true'}">
	onClick="ss_showAccessibleMenu('<%= menuTagDivId %>${renderResponse.namespace}');"
</c:if>
	  
><%= title %>
<c:if test='<%= !("".equals(menuImage)) %>'>
	<img src='<html:imagesPath/><%= menuImage %>' <ssf:alt tag="alt.showMenu"/>/>
</c:if>
</a></span>