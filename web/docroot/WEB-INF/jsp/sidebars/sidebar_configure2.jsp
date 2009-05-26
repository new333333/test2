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
<% // Former Folder Tools for Blogs, Photo Albums, and Wikis %>
	<% // folder views and folder actions, (no configure columns, themes, or entries per page) %>

<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${ssDefinitionEntry.entityType == 'folder'}">
<c:set var="ss_sidebarStatusTitleInfo"><ssf:nlt tag="sidebar.configureInfo"/></c:set>
  <ssf:sidebarPanel title="sidebar.configure" id="ss_tooltags_sidebar" divClass="ss_place_tags" 
    initOpen="false" sticky="false" titleInfo="${ss_sidebarStatusTitleInfo}">

		<c:if test="${!empty ssFolderViewsToolbar || !empty ssCalendarImportToolbar}">
		  <div class="ss_sidebarTitle"><ssf:nlt tag="sidebar.folderConfiguration"/> 
			<div class="ss_sub_sidebarMenu">
			  <c:if test="${!empty ssFolderViewsToolbar}">
			    <ssf:toolbar toolbar="${ssFolderViewsToolbar}" style="ss_actions_bar4 ss_actions_bar" />
			  </c:if>
			  <c:if test="${!empty ssCalendarImportToolbar}">
			    <ssf:toolbar toolbar="${ssCalendarImportToolbar}" style="ss_actions_bar4 ss_actions_bar" />
			  </c:if>
			</div>
		  </div>	
		</c:if>
   
  </ssf:sidebarPanel>
</c:if>

