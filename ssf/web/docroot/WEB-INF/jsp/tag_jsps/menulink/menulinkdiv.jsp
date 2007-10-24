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
<%@ page import="com.sitescape.util.ParamUtil" %>

<jsp:useBean id="ssUser" type="com.sitescape.team.domain.User" scope="request" />

<%
// General variables
String menuDivId = ParamUtil.get(request, "menuDivId", "");
String linkMenuIdx = ParamUtil.get(request, "linkMenuObjIdx", "");
String linkMenuObj = "ss_linkMenu_arr['"+ linkMenuIdx +"']";
String namespace = ParamUtil.get(request, "namespace", "");
String dashboardType = ParamUtil.get(request, "dashboardType", "");

String strShowEntry = "showEntry()";
String strNewWindowFunction = "newWindow()";

if (dashboardType.equals("portlet")) {
	strNewWindowFunction = "newWindowForPortlet()";
	if ("popup".equals(ssUser.getDisplayStyle())) {
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