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
<% // The default workspace view  %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>
<ssf:ifadapter>
<body class="ss_style_body tundra">
</ssf:ifadapter>

<jsp:useBean id="ssUserProperties" type="java.util.Map" scope="request" />
<jsp:useBean id="ssUser" type="org.kablink.teaming.domain.User" scope="request" />

<div class="ss_style ss_portlet">

<% // Navigation bar %>
<jsp:include page="/WEB-INF/jsp/definition_elements/navbar.jsp" />

<% // BEGIN SIDEBAR LAYOUT  %>
<ssf:ifnotaccessible>
    <table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tbody>
    <tr>
    <td valign="top" class="${ss_sidebarTdStyle}" id="ss_sidebarTd${renderResponse.namespace}">
     <div id="ss_sidebarDiv${renderResponse.namespace}" style="display:${ss_sidebarVisibility};">
	
	  <div id="ss_sideNav_wrap"> <% // new sidebar format %>

		<% // Status %>
		<jsp:include page="/WEB-INF/jsp/sidebars/sidebar_status.jsp" />	
	
		<% // "It" Bars %>
		<jsp:include page="/WEB-INF/jsp/sidebars/sidebar_action_dispatch.jsp" />

		<% // Recent Places %>
		<jsp:include page="/WEB-INF/jsp/sidebars/sidebar_recent_places.jsp" />

		<% // Folder Sidebar %>
    	<jsp:include page="/WEB-INF/jsp/sidebars/sidebar_dispatch.jsp" />

		<% // Workspace Tree %>    
    	<jsp:include page="/WEB-INF/jsp/sidebars/sidebar_workspace_tree.jsp" />

	  </div> <% // end of new sidebar format %>
	 </div> <% // end of ss_sidebarDiv %>
	</td>

	<td valign="top" class="ss_view_info">
</ssf:ifnotaccessible>

<% // Navigation links %>
<% if (!(GwtUIHelper.isGwtUIActive(request))) { %>
<jsp:include page="/WEB-INF/jsp/definition_elements/navigation_links.jsp" />
<% } %>

<% // Toolbar %>
<ssf:toolbar toolbar="${ssFolderToolbar}" style="ss_actions_bar1 ss_actions_bar" />

<% // Show the workspace default parts %>

<% // Title %>
<%@ include file="/WEB-INF/jsp/definition_elements/view_workspace_title.jsp" %>

<% // List of workspaces and folders %>
<%@ include file="/WEB-INF/jsp/definition_elements/workspace_binder_list.jsp" %>

</div>

<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>

