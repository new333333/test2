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
%>
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ page import="org.kablink.teaming.util.SPropsUtil" %>
<%@ page import="org.kablink.util.PropertyNotFoundException" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("sidebar.history") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/mobile/mobile_init.jsp" %>

<c:set var="ss_pageTitle" value='<%= NLT.get("sidebar.history") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/mobile/masthead.jsp" %>

<div class="content">

<%@ include file="/WEB-INF/jsp/mobile/action_bar.jsp" %>

  <div class="folders">
    <div class="folder-content">
      <div class="folder-head"><ssf:nlt tag="sidebar.history"/></div>
	  <c:forEach var="tab" items="${ss_tabs.tabList}">
			<jsp:useBean id="tab" type="org.kablink.teaming.web.util.Tabs.TabEntry" />
			  <c:set var="numTabs" value="${numTabs + 1}"/>
			  <c:if test="${numTabs < 6}">
			    <div class="folder-item">
					<a id="ss_tabbar_td${tab.tabId}" 
					  <c:if test="${tab.type == 'binder'}">
					    href="<ssf:url adapter="true" portletName="ss_forum" 
							folderId="${tab.binderId}" 
							action="__ajax_mobile" operation="mobile_show_folder" actionUrl="false" />" 
					  </c:if>
					  <c:if test="${tab.type == 'workspace'}">
					    href="<ssf:url adapter="true" portletName="ss_forum" 
							folderId="${tab.binderId}" 
							action="__ajax_mobile" operation="mobile_show_workspace" actionUrl="false" />" 
					  </c:if>
					  <c:if test="${tab.type == 'profiles'}">
					    href="<ssf:url adapter="true" portletName="ss_forum" 
							folderId="${tab.binderId}" 
							action="__ajax_mobile" operation="mobile_show_workspace" actionUrl="false" />" 
					  </c:if>
					  <c:if test="${tab.type == 'search'}">
					    href="<ssf:url 
			  				action="__ajax_mobile" >
			  				<ssf:param name="tabId" value="${tab.tabId}"/>
			  				<ssf:param name="operation" value="mobile_show_search_results"/>
			  				<ssf:param name="operation" value="mobile_show_search_results"/>
			  				</ssf:url>" 
					  </c:if>
					title="${tab.data.path}" >
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
			     </div>
			   </c:if>
	  </c:forEach>
	</div>
  </div>
</div>
</body>
</html>
