<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%><%--
--%><%@ include file="/WEB-INF/jsp/common/include.jsp" %><%--
--%><%@ page import="org.kablink.util.ParamUtil" %><%--
--%><portletadapter:defineObjects1/><%--
--%><ssf:ifadapter><portletadapter:defineObjects2/></ssf:ifadapter><%--
--%><ssf:ifnotadapter><portlet:defineObjects/></ssf:ifnotadapter><%--
--%><%

String title = ParamUtil.get(request, "title", "");
String titleHTML = ParamUtil.get(request, "titleHTML", "");
String titleInfo = ParamUtil.get(request, "titleInfo", "");
String id = ParamUtil.get(request, "id", "");
String divClass = ParamUtil.get(request, "divClass", "");
Boolean initOpen = ParamUtil.getBoolean(request, "initOpen", true);
Boolean sticky = ParamUtil.getBoolean(request, "sticky", true);
Boolean noColorChange = ParamUtil.getBoolean(request, "noColorChange", true);
%><%--
--%><c:set var="title" value="<%= title %>" /><%--
--%><c:set var="titleHTML" value="<%= titleHTML %>" /><%--
--%><c:set var="titleInfo" value="<%= titleInfo %>" /><%--
--%><c:set var="noColorChange" value="<%= noColorChange %>" /><%--
--%><c:set var="initOpen" value="<%= initOpen %>" /><%--
--%><c:set var="sticky" value="<%= sticky %>" /><%--
--%><c:set var="divId" value="<%= id %>" /><%--
--%><c:set var="divClass" value="<%= divClass %>" /><%--
--%><div class="ss_sidebarMenu" <%--
--%><c:if test="${!noColorChange}">onmouseover="this.className='ss_mouseOver';" onmouseout="this.className='ss_sidebarMenu';"</c:if><%--
--%>><%--
    --%><a href="javascript: ;"  title="${titleInfo}" <%--
        --%><c:if test="${initOpen}">class="ss_menuOpen" </c:if><%--
        --%><c:if test="${!initOpen}">class="ss_menuClosed" </c:if><%--
        --%> onclick="ss_showHideSidebarBox('${renderResponse.namespace}_${divId}', this, ${sticky}, '${divId}');"><%--
        --%><span>${title}${titleHTML}</span><%--
    --%></a><div><img alt="" src="<html:imagesPath/>pics/1pix.gif" height="1" width="180"/></div><%--
--%><div id="${renderResponse.namespace}_${divId}" style="overflow: hidden; <%--
--%><c:if test="${initOpen}">visibility: visible; display: block;</c:if><%--
--%><c:if test="${!initOpen}">visibility: hidden; display: none;</c:if><%--
--%>"<%--
--%><c:if test="${!empty divClass}"> class="${divClass}"</c:if><%--
--%>>