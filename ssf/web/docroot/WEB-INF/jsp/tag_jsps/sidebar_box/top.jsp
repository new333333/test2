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
--%><div class="ss_sidebar_panel"><%--
    --%><div class="ss_base_title_bar"><%--
        --%><img <ssf:alt tag="alt.expand"/> src="<%--
        --%><c:if test="${initOpen}"><html:imagesPath/>pics/flip_down16H.gif</c:if><%--
        --%><c:if test="${!initOpen}"><html:imagesPath/>pics/flip_up16H.gif</c:if><%--
        --%>" onClick="ss_showHideSidebarBox('<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_${divId}', this, ${sticky}, '${divId}');" class="ss_toggler"/><%--
        --%><%= title %><%--
    --%></div><%--
--%><div id="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_${divId}" style="overflow: hidden; <%--
--%><c:if test="${initOpen}">visibility: visible; display: block;</c:if><%--
--%><c:if test="${!initOpen}">visibility: hidden; display: none;</c:if><%--
--%>"<%--
--%><c:if test="${!empty divClass}"> class="${divClass}"</c:if><%--
--%>>