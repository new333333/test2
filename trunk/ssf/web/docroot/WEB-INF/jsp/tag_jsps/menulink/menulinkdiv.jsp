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

<jsp:useBean id="ssUser" type="com.sitescape.team.domain.User" scope="request" />

<%
// General variables
String menuDivId = ParamUtil.get(request, "menuDivId", "");
String linkMenuIdx = ParamUtil.get(request, "linkMenuObjIdx", "");
String linkMenuObj = "ss_linkMenu_arr['"+ linkMenuIdx +"']";
String namespace = ParamUtil.get(request, "namespace", "");
String dashboardType = ParamUtil.get(request, "dashboardType", "");

String displayStyle = ssUser.getDisplayStyle();
if (displayStyle == null) displayStyle = "";

String strShowEntry = "showEntry()";
String strNewWindowFunction = "newWindow()";

if (dashboardType.equals("portlet")) {
	strNewWindowFunction = "newWindowForPortlet()";
	if (displayStyle.equals("popup")) {
		strShowEntry = "newWindowForPortlet()";
	}
}
%>

<div id="<%= menuDivId %>" name="<%= menuDivId %>" class="ss_title_menu_dd" style="width:175px;">
<ul id="ss_folderMenuShowFileLink_<%= linkMenuIdx %>" name="ss_folderMenuShowFileLink_<%= linkMenuIdx %>"><li><a href="javascript:;" onClick="<%= linkMenuObj %>.showFile(); return false;">
	<ssf:nlt tag="linkMenu.showFile"/></a></li></ul>
<ul id="ss_folderMenuShowEntryLink_<%= linkMenuIdx %>" name="ss_folderMenuShowEntryLink_<%= linkMenuIdx %>"><li><a href="javascript:;" onClick="<%= linkMenuObj %>.<%= strShowEntry %>; return false;">
	<ssf:nlt tag="linkMenu.showEntry"/></a></li></ul>
<ul id="ss_folderMenuShowCurrentTab_<%= linkMenuIdx %>" name="ss_folderMenuShowCurrentTab_<%= linkMenuIdx %>"><li><a href="javascript:;" onClick="<%= linkMenuObj %>.currentTab(); return false;">
	<ssf:nlt tag="linkMenu.currentTab"/></a></li></ul>
<ul id="ss_folderMenuShowNewTab_<%= linkMenuIdx %>" name="ss_folderMenuShowNewTab_<%= linkMenuIdx %>"><li><a href="javascript:;" onClick="<%= linkMenuObj %>.newTab(); return false;">
	<ssf:nlt tag="linkMenu.newTab"/></a></li></ul>
<ul id="ss_folderMenuShowNewWindow_<%= linkMenuIdx %>" name="ss_folderMenuShowNewWindow_<%= linkMenuIdx %>"><li><a href="javascript:;" onClick="<%= linkMenuObj %>.<%= strNewWindowFunction %>; return false;">
	<ssf:nlt tag="linkMenu.newWindow"/></a></li></ul>
</div>