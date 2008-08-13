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

<% // Former Folder Tools %>
	<% // folder views, folder actions, themes, configure columns, and entries per page %>
	
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${ssDefinitionEntry.entityType == 'folder'}">

<ssf:sidebarPanel title="sidebar.configure" id="ss_folderTags_sidebar" divClass="ss_place_tags" 
  initOpen="false" sticky="false">

<c:if test="${!empty ssFolderViewsToolbar}">
  <div class="ss_sidebarTitle"><ssf:nlt tag="sidebar.folderConfiguration"/> 
	<div class="ss_sub_sidebarMenu">
	  <ssf:toolbar toolbar="${ssFolderViewsToolbar}" style="ss_actions_bar4 ss_actions_bar" />
	</div>
  </div>	
</c:if>
   
  <% // configure columns area %>
  <jsp:include page="/WEB-INF/jsp/sidebars/sidebar_configure_columns.jsp" />
 
  <% // configure entries per page %>
  <jsp:include page="/WEB-INF/jsp/sidebars/sidebar_configure_entriesPerPage.jsp" />
 
</ssf:sidebarPanel>
</c:if>
