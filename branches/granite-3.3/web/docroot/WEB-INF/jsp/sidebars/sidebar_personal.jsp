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
<% // Personal preferences %>
<%@ page import="org.kablink.teaming.ssfs.util.SsfsUtil" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="ss_sidebarStatusTitleInfo"><ssf:nlt tag="sidebar.personalPref.info"/></c:set>
<ssf:sidebarPanel title="sidebar.personalPref" id="ss_personal_sidebar" 
  divClass="ss_place_tags" initOpen="false" sticky="false" titleInfo="${ss_sidebarStatusTitleInfo}">

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
   <c:if test="${!empty ss_accessibleUrl}">
	<div class="ss_sidebarTitle"><ssf:nlt tag="accessible.accessibleMode"/>
	  <ssHelpSpot helpId="workspaces_folders/misc_tools/accessible_mode" offsetX="-22" 
      offsetY="-4" title="<ssf:nlt tag="helpSpot.accessibleMode"/>">
	  </ssHelpSpot>
	   <div class="ss_sub_sidebarMenu">
		 <ssf:ifaccessible simple_ui="true">
		  <a href="${ss_accessibleUrl}">
		    <span class="ss_smallprint ss_light"><ssf:nlt tag="accessible.disableAccessibleMode"/></span>
		  </a>
		 </ssf:ifaccessible>
		 <ssf:ifnotaccessible simple_ui="true">
		  <a href="${ss_accessibleUrl}"
		     onclick='return true;'>
		    <span class="ss_smallprint ss_light"><ssf:nlt tag="accessible.enableAccessibleMode"/></span>
		  </a>
		 </ssf:ifnotaccessible>
	   </div>
	</div>
   </c:if>

	<% /* Is the user looking at their own workspace? */ %>
	<c:if test="${!empty ssBinder && !empty ssUser && ssBinder.id == ssUser.workspaceId}">
		<% /* Yes */ %>
		<div class="ss_sidebarTitle">
			<ssf:nlt tag="sidebar.videoTutorial" />
			<div class="ss_sub_sidebarMenu">
				<!-- Create an anchor for the user to click on that will either show or hide the tutorial panel. -->
				<!-- The onclick event handler and the text for the anchor will be updated in JavaScript -->
				<!-- in showTutorialPanel() in tutorial_support_js.jsp. -->
				<a href="#" id="tutorialPanelPrefAnchor">
					<span id="tutorialPanelPrefSpan" class="ss_smallprint ss_light"></span>
				</a>
			</div>
		</div>
	</c:if>
  </ssf:ifLoggedIn>

</ssf:sidebarPanel>
