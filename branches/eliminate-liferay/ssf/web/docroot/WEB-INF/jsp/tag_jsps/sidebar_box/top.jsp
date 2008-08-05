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
%><%--
--%><%@ include file="/WEB-INF/jsp/common/include.jsp" %><%--
--%><%@ page import="com.sitescape.util.ParamUtil" %><%--
--%><portletadapter:defineObjects1/><%--
--%><ssf:ifadapter><portletadapter:defineObjects2/></ssf:ifadapter><%--
--%><ssf:ifnotadapter><portlet:defineObjects/></ssf:ifnotadapter><%--
--%><%

String title = ParamUtil.get(request, "title", "");
String id = ParamUtil.get(request, "id", "");
String divClass = ParamUtil.get(request, "divClass", "");
Boolean initOpen = ParamUtil.getBoolean(request, "initOpen", true);
Boolean sticky = ParamUtil.getBoolean(request, "sticky", true);
%><%--
--%><c:set var="initOpen" value="<%= initOpen %>" /><%--
--%><c:set var="sticky" value="<%= sticky %>" /><%--
--%><c:set var="divId" value="<%= id %>" /><%--
--%><c:set var="divClass" value="<%= divClass %>" /><%--
--%><div class="ss_sidebarMenu"><%--
    --%><div <%--
        --%><c:if test="${initOpen}">class="ss_menuOpen"</c:if><%--
        --%><c:if test="${!initOpen}">class="ss_menuClosed"</c:if><%--
        --%> onClick="ss_showHideSidebarBox('${renderResponse.namespace}_${divId}', this, ${sticky}, '${divId}');"><%--
        --%><%= title %><%--
        --%><div><img alt="" src="<html:imagesPath/>pics/1pix.gif" height="1" width="180"/></div><%--
    --%></div><%--
--%><div id="${renderResponse.namespace}_${divId}" style="overflow: hidden; <%--
--%><c:if test="${initOpen}">visibility: visible; display: block;</c:if><%--
--%><c:if test="${!initOpen}">visibility: hidden; display: none;</c:if><%--
--%>"<%--
--%><c:if test="${!empty divClass}"> class="${divClass}"</c:if><%--
--%>>