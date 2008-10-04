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
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%
boolean isIE = BrowserSniffer.is_ie(request);
%>
<% // BEGIN SIDEBAR LAYOUT  %>
<div id="ss_sidebarDiv${renderResponse.namespace}" style="display:${ss_sidebarVisibility};">
	<div id="ss_sideNav_wrap"> <% // new sidebar format %>

	<% // Status %>
	<jsp:include page="/WEB-INF/jsp/sidebars/sidebar_status.jsp" />	
	
	<% // Track %>
	<jsp:include page="/WEB-INF/jsp/sidebars/sidebar_track.jsp" />
	
	<% // Share %>
  	<jsp:include page="/WEB-INF/jsp/sidebars/sidebar_share.jsp" />

	<% // Email %>
	<jsp:include page="/WEB-INF/jsp/sidebars/sidebar_email.jsp" />  	
		
	<% // Clipboard %>
	<jsp:include page="/WEB-INF/jsp/sidebars/sidebar_clipboard.jsp" />
		
	<% // RSS Feed %>
	<jsp:include page="/WEB-INF/jsp/sidebars/sidebar_rss.jsp" />
<%--
* The following line is used to call customer supplied customizations.
* Jsp files added to the custom_jsps directory will not be overwritten during upgrades
--%><jsp:include page="/WEB-INF/jsp/custom_jsps/ss_call_out_sidebar_top.jsp" />

	<% // Recent Places %>
	<jsp:include page="/WEB-INF/jsp/sidebars/sidebar_recent_places.jsp" />

	<% // Workspace Tree %>    
	<jsp:include page="/WEB-INF/jsp/sidebars/sidebar_workspace_tree.jsp" />

<%--
* The following line is used to call customer supplied customizations.
* Jsp files added to the custom_jsps directory will not be overwritten during upgrades
--%><jsp:include page="/WEB-INF/jsp/custom_jsps/ss_call_out_sidebar_middle.jsp" />

	<% // Folder Tools %>
	<jsp:include page="/WEB-INF/jsp/sidebars/sidebar_dispatch.jsp" />

	<!-- Saved searches -->
	<jsp:include page="/WEB-INF/jsp/search/save_search.jsp" />
							
	<!-- Search Places rating  -->
	<jsp:include page="/WEB-INF/jsp/search/rating_places.jsp" />

	<!-- Search People rating  -->
	<jsp:include page="/WEB-INF/jsp/search/rating_people.jsp" />

	<!-- Search Tag cloud -->
	<jsp:include page="/WEB-INF/jsp/search/tags.jsp" />
	
	<% // Team Sidebar %>
	<jsp:include page="/WEB-INF/jsp/sidebars/sidebar_team.jsp" />

	<% // Folder or Workspace Tags %>
	<jsp:include page="/WEB-INF/jsp/sidebars/sidebar_workspace_tags.jsp" />
	<jsp:include page="/WEB-INF/jsp/sidebars/sidebar_folder_tags.jsp" />

	<% // Meet %>
	<jsp:include page="/WEB-INF/jsp/sidebars/sidebar_meet.jsp" />
	
	<% // Personal Preferences %>
	<jsp:include page="/WEB-INF/jsp/sidebars/sidebar_personal.jsp" />
	
<%--
* The following line is used to call customer supplied customizations.
* Jsp files added to the custom_jsps directory will not be overwritten during upgrades
--%><jsp:include page="/WEB-INF/jsp/custom_jsps/ss_call_out_sidebar_bottom.jsp" />

	</div> <% // end of new sidebar format %>
</div> <% // end of ss_sidebarDiv %>
