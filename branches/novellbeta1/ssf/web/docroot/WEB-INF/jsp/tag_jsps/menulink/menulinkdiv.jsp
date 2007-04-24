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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page import="com.sitescape.util.ParamUtil" %>
<%
// General variables
String menuDivId = ParamUtil.get(request, "menuDivId", "");
String linkMenuIdx = ParamUtil.get(request, "linkMenuObjIdx", "");
String linkMenuObj = "ss_linkMenu_arr['"+ linkMenuIdx +"']";
String namespace = ParamUtil.get(request, "namespace", "");
String dashboardType = ParamUtil.get(request, "dashboardType", "");

String strNewWindowFunction = "newWindow()";
if (dashboardType.equals("portlet")) {
	strNewWindowFunction = "newWindowForPortlet()";
}
%>

<div id="<%= menuDivId %>" class="ss_title_menu_dd" style="width:175px;">
<ul id="ss_folderMenuShowFileLink_<%= namespace %>"><li><a href="#" onClick="<%= linkMenuObj %>.showFile(); return false;">
	<ssf:nlt tag="linkMenu.showFile"/></a></li></ul>
<ul id="ss_folderMenuShowEntryLink_<%= namespace %>"><li><a href="#" onClick="<%= linkMenuObj %>.showEntry(); return false;">
	<ssf:nlt tag="linkMenu.showEntry"/></a></li></ul>
<ul id="ss_folderMenuShowCurrentTab_<%= namespace %>" ><li><a href="#" onClick="<%= linkMenuObj %>.currentTab(); return false;">
	<ssf:nlt tag="linkMenu.currentTab"/></a></li></ul>
<ul id="ss_folderMenuShowNewTab_<%= namespace %>" ><li><a href="#" onClick="<%= linkMenuObj %>.newTab(); return false;">
	<ssf:nlt tag="linkMenu.newTab"/></a></li></ul>
<ul id="ss_folderMenuShowNewWindow_<%= namespace %>"><li><a href="#" onClick="<%= linkMenuObj %>.<%= strNewWindowFunction %>; return false;">
	<ssf:nlt tag="linkMenu.newWindow"/></a></li></ul>
</div>