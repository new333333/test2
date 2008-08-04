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
<% // Tabs %>
<%@ page import="com.sitescape.team.util.NLT" %>
<%@ page import="com.sitescape.team.util.SPropsUtil" %>
<%@ page import="com.sitescape.util.PropertyNotFoundException" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<c:set var="ss_urlWindowState" value="maximized"/>
<c:set var="ss_urlWindowState" value=""/>
<c:set var="numTabs" value="0"/>
<ssf:sidebarPanel title="sidebar.history" id="ss_history_box" initOpen="false" sticky="true">
<ul style="padding-top: 2px; padding-left: 5px;">
<c:forEach var="tab" items="${ss_tabs.tabList}">
<jsp:useBean id="tab" type="com.sitescape.team.web.util.Tabs.TabEntry" />
  <c:set var="numTabs" value="${numTabs + 1}"/>
  <c:if test="${numTabs < 6}">
  <li>
		<a id="ss_tabbar_td${tab.tabId}" 
		  <c:if test="${tab.type == 'binder'}">
		    href="<ssf:url 
  				binderId="${tab.binderId}" 
  				action="view_folder_listing"
  				windowState="${ss_urlWindowState}">
  				<ssf:param name="newTab" value="0"/>
  				</ssf:url>" 
		  </c:if>
		  <c:if test="${tab.type == 'workspace'}">
		    href="<ssf:url 
  				binderId="${tab.binderId}" 
  				action="view_ws_listing"
  				windowState="${ss_urlWindowState}">
  				<ssf:param name="newTab" value="0"/>
  				</ssf:url>" 
		  </c:if>
		  <c:if test="${tab.type == 'profiles'}">
		    href="<ssf:url 
  				binderId="${tab.binderId}" 
  				action="view_profile_listing"
  				windowState="${ss_urlWindowState}">
  				<ssf:param name="newTab" value="0"/>
  				</ssf:url>" 
		  </c:if>
		  <c:if test="${tab.type == 'search'}">
		    href="<ssf:url 
  				action="advanced_search"
  				windowState="${ss_urlWindowState}">
  				<ssf:param name="tabId" value="${tab.tabId}"/>
  				<ssf:param name="operation" value="viewPage"/>
  				</ssf:url>" 
		  </c:if>
		title="${tab.data.title}" >
<%
	// Truncate long tab titles to 30 characters
	int maxTitle = 30;

	try {
		maxTitle = SPropsUtil.getInt("history.max.title");
	} catch (PropertyNotFoundException e) {
	}

	String tabTitle = (String)tab.getData().get("title");
	if (tabTitle.length() > maxTitle) {
		tabTitle = tabTitle.substring(0, maxTitle) + "...";
	}
%>	
		   <span class="ss_tabs_title"><%= tabTitle %></span></a>
   </li>
   </c:if>
</c:forEach>
</ul>
</ssf:sidebarPanel>
