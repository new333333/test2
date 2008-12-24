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
<% // Personal preferences %>
<%@ page import="org.kablink.teaming.ssfs.util.SsfsUtil" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<ssf:sidebarPanel title="sidebar.personalPref" id="ss_personal_sidebar" 
  divClass="ss_place_tags" initOpen="false" sticky="false">

  <c:if test="${!empty ssFolderActionsToolbar}">
	  <div class="ss_sidebarTitle"><ssf:nlt tag="sidebar.entryDisplay"/> 
		<div class="ss_sub_sidebarMenu">
		  	<ssf:toolbar toolbar="${ssFolderActionsToolbar}" style="ss_actions_bar4 ss_actions_bar" />
		</div>
	  </div>
  </c:if>	

  <% // configure entries per page %>
  <jsp:include page="/WEB-INF/jsp/sidebars/sidebar_configure_entriesPerPage.jsp" />

<c:if test="${!empty ss_toolbar_theme_ids}">
  <div class="ss_sidebarTitle"><ssf:nlt tag="sidebar.theme"/> 
	<div class="ss_sub_sidebarMenu">
	  	<jsp:include page="/WEB-INF/jsp/sidebars/sidebar_theme.jsp" /> 
	</div>
  </div>	
</c:if>

  <% if (SsfsUtil.supportAttachmentEdit()) { %>
	  <div class="ss_sidebarTitle"><ssf:nlt tag="sidebar.appConfig"/> 
		<div class="ss_sub_sidebarMenu">
		  	<jsp:include page="/WEB-INF/jsp/sidebars/sidebar_appConfig.jsp" /> 
		</div>
	  </div>	
  <% } %>
  
  <ssf:ifLoggedIn>
	<div class="ss_sidebarTitle"><ssf:nlt tag="accessible.accessibleMode"/>
	  <ssHelpSpot helpId="workspaces_folders/misc_tools/accessible_mode" offsetX="-22" 
      offsetY="-4" title="<ssf:nlt tag="helpSpot.accessibleMode"/>">
	  </ssHelpSpot>
	   <div class="ss_sub_sidebarMenu">
		 <ssf:ifaccessible>
		  <a href="${ss_accessibleUrl}">
		    <span class="ss_smallprint ss_light"><ssf:nlt tag="accessible.disableAccessibleMode"/></span>
		  </a>
		 </ssf:ifaccessible>
		 <ssf:ifnotaccessible>
		  <a href="${ss_accessibleUrl}"
		  onClick='return (confirm("<ssf:nlt tag="accessible.confirm" quoteDoubleQuote="true"/>"));'>
		    <span class="ss_smallprint ss_light"><ssf:nlt tag="accessible.enableAccessibleMode"/></span>
		  </a>
		 </ssf:ifnotaccessible>
	   </div>
	</div>
  </ssf:ifLoggedIn>

</ssf:sidebarPanel>
