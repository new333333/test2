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
<% // The main workspace view  %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value="${ssBinder.title}" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="showWorkspacePage" value="true"/>

<body class="ss_style_body tundra" onload="bodyOnloadHandler()">
<div id="ss_pseudoPortalDiv${renderResponse.namespace}">
<ssf:ifLoggedIn><c:if test="${empty ss_noEnableAccessibleLink && !empty ss_accessibleUrl && (empty ss_displayStyle || ss_displayStyle != 'accessible')}">
  <a class="ss_skiplink" href="${ss_accessibleUrl}"><img border="0"
    <ssf:alt tag="accessible.enableAccessibleMode"/> 
    src="<html:imagesPath/>pics/1pix.gif" /></a><%--
		--%></c:if></ssf:ifLoggedIn>
<jsp:include page="/WEB-INF/jsp/common/presence_support.jsp" />

<c:if test="${!empty ssReloadUrl}">
	<script type="text/javascript">
		//Open the current url in the opener window
		ss_reloadOpener('<c:out value="${ssReloadUrl}" escapeXml="false"/>')
	</script>
</c:if>

<c:if test="${empty ssReloadUrl}">
<script type="text/javascript">
if (self.parent) {
	//We are in an iframe inside a portlet (maybe?)
	var windowName = self.window.name    
	if (windowName.indexOf("ss_workareaIframe") == 0) {
		//We are running inside an iframe, get the namespace name of that iframe's owning portlet
		var namespace = windowName.substr("ss_workareaIframe".length)
		//alert('namespace = '+namespace+', binderId = ${ssBinder.id}, entityType = ${ssBinder.entityType}')
		var url = "<ssf:url
					adapter="true"
					portletName="ss_forum" 
					action="__ajax_request" 
					binderId="${ssBinder.id}">
				  <ssf:param name="entityType" value="${ssBinder.entityType}"/>
				  <ssf:param name="namespace" value="ss_namespacePlaceholder" />
				  <ssf:param name="operation" value="set_last_viewed_binder" />
				  <ssf:param name="rn" value="ss_randomNumberPlaceholder"/>
				  </ssf:url>"
		url = ss_replaceSubStr(url, 'ss_namespacePlaceholder', namespace);
		url = ss_replaceSubStr(url, 'ss_randomNumberPlaceholder', ss_random++);
		ss_fetch_url(url);
	}
}

function ss_workarea_showId${renderResponse.namespace}(id, action, entryId) {
	if (typeof entryId == "undefined") entryId = "";
	//Build a url to go to
	var url = "<ssf:url     
	    		  adapter="true" 
	    		  portletName="ss_forum" 
	    		  binderId="ssBinderIdPlaceHolder" 
    			  entryId="ssEntryIdPlaceHolder" 
	    		  action="ssActionPlaceHolder" 
	    		  actionUrl="false" >
	    	   <ssf:param name="namespace" value="${renderResponse.namespace}"/>
	           </ssf:url>"
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", id);
	url = ss_replaceSubStr(url, "ssEntryIdPlaceHolder", entryId);
	url = ss_replaceSubStr(url, "ssActionPlaceHolder", action);
	self.location.href = url;
	return false;
}
if (typeof ss_workarea_showId == "undefined") 
	ss_workarea_showId = ss_workarea_showId${renderResponse.namespace};
</script>

<% /* The following javascript is used by the 'Tutorial' ui. */ %>
<script type="text/javascript">
	// Global variables.
	window.TUTORIAL_PANEL_CLOSED	= 1;
	window.TUTORIAL_PANEL_EXPANDED	= 2;
	window.TUTORIAL_PANEL_COLLAPSED	= 3;

	/**
	 * This function is the onload event handler for the <body> tag.
	 * This code will display the tutorial panel in its correct initial state, hidden, expanded or collapsed.
	 */
	function bodyOnloadHandler()
	{
		var	initialState;
		var	table;

		// Is the user looking at his own workspace?
		if ( isOwnWorkspace() )
		{
			// Yes
			// Get the initial state of the tutorial panel.
			initialState = '${ss_tutorial_panel_state}';

			// Do we have a tutorial panel state?
			if ( initialState == null || initialState.length == 0 )
			{
				// No
				initialState = window.TUTORIAL_PANEL_EXPANDED;
			}
		}
		else
		{
			// No, the user is looking at someone elses workspace.  Don't show the tutorial panel.
			initialState = window.TUTORIAL_PANEL_CLOSED;
		}
		
		// Is the tutorial panel state valid?
		if ( initialState < window.TUTORIAL_PANEL_CLOSED || initialState > window.TUTORIAL_PANEL_COLLAPSED )
		{
			// No
			initialState = window.TUTORIAL_PANEL_EXPANDED;
		}

		// Is the tutorial panel supposed to be closed?
		if ( initialState == window.TUTORIAL_PANEL_CLOSED )
		{
			// Yes
			// Hide the expanded tutorial table.
			table = document.getElementById( 'expandedTutorialTable' );
			table.style.display = 'none';

			// Hide the collapsed tutorial table.
			table = document.getElementById( 'collapsedTutorialTable' );
			table.style.display = 'none';
		}
		// Is the tutorial panel collapsed?
		else if ( initialState == window.TUTORIAL_PANEL_COLLAPSED )
		{
			// Yes
			// Hide the expanded tutorial table.
			table = document.getElementById( 'expandedTutorialTable' );
			table.style.display = 'none';

			// Show the collapsed tutorial table.
			table = document.getElementById( 'collapsedTutorialTable' );
			table.style.display = '';
		}
		else
		{
			// The tutorial panel is expanded.
			// Show the expanded tutorial table.
			table = document.getElementById( 'expandedTutorialTable' );
			table.style.display = '';

			// Hide the collapsed tutorial table.
			table = document.getElementById( 'collapsedTutorialTable' );
			table.style.display = 'none';
		}
	}// end bodyOnloadHandler()

	
	/**
	 * This function will collapse the tutorial ui.
	 */
	function collapseTutorialPanel()
	{
		var	table;

		// Hide the expanded tutorial table.
		table = document.getElementById( 'expandedTutorialTable' );
		table.style.display = 'none';

		// Show the collapsed tutorial table.
		table = document.getElementById( 'collapsedTutorialTable' );
		table.style.display = '';

		// Issue an ajax request to remember that the tutorial panel is collapsed.
		saveTutorialPanelState( window.TUTORIAL_PANEL_COLLAPSED );
	}// end collapseTutorialPanel()


	/**
	 * This function will tell the user that closing the tutorial panel will permanently remove
	 * it and will ask the user if that is what they want to do.
	 */
	function confirmCloseTutorialPanel()
	{
		var	msg;

		// Ask the user if they want to permanently remove the tutorial panel.
		msg = '<ssf:escapeJavaScript><ssf:nlt tag="tutorial.confirmClose" /></ssf:escapeJavaScript>'; 
		if ( window.confirm( msg ) )
		{
			// The user wants to permanently remove the tutorial panel.
			// Hide the tutorial panels.
			hideTutorialPanels();
			
			// Issue an ajax request to remember that the tutorial panel should not be displayed again.
			saveTutorialPanelState( window.TUTORIAL_PANEL_CLOSED );
		}
	}// end configCloseTutorialPanel()


	/**
	 * This function will expand the tutorial ui.
	 */
	function expandTutorialPanel()
	{
		var	table;

		// Hide the collapsed tutorial table.
		table = document.getElementById( 'collapsedTutorialTable' );
		table.style.display = 'none';

		// Show the expanded tutorial table.
		table = document.getElementById( 'expandedTutorialTable' );
		table.style.display = '';

		// Issue an ajax request to remember that the tutorial panel is expanded.
		saveTutorialPanelState( window.TUTORIAL_PANEL_EXPANDED );
	}// end expandTutorialPanel()


	/**
	 * This function gets called when we get the response to the ajax request to save the tutorial panel state.
	 */
	function handleSaveTutorialPanelStateAjaxResults( responseData )
	{
		// Nothing to do.
	}// end handleSaveTutorialPanelStateAjaxResults()

	
	/**
	 * There are two tables that make up the tutorial ui, an expanded panel and a collapsed panel.
	 * Hide both of them.
	 */
	function hideTutorialPanels()
	{
		var	table;

		// Hide the collapsed tutorial table.
		table = document.getElementById( 'collapsedTutorialTable' );
		table.style.display = 'none';

		// Hide the expanded tutorial table.
		table = document.getElementById( 'expandedTutorialTable' );
		table.style.display = 'none';
	}// end hideTutorialPanels()


	/**
	 * This function will return true if the logged in user is looking at their own workspace.
	 */
	function isOwnWorkspace()
	{
		// Is the id of the workspace we are looking at the same as the id of the user's workspace?
		if ( ${ssBinder.id} == ${ssUser.workspaceId} )
		{
			// Yes
			return true;
		}

		// The user is looking at someone elses workspace.
		return false; 
	}// end isOwnWorkspace()


	/**
	 * Issue an ajax request to save the state of the tutorial panel.
	 */
	function saveTutorialPanelState( panelState )
	{
		var	url;
		var	obj;

		// Set up the object that will be used in the ajax request.
		obj = new Object();
		obj.operation = 'save_user_tutorial_panel_state';
		obj.tutorialPanelState = panelState;

		// Build the url used in the ajax request.
		url = ss_buildAdapterUrl( ss_AjaxBaseUrl, obj );
		
		// Issue the ajax request.  The function handleSaveTutorialPanelStateAjaxResults() will be called
		// when we get the response to the request.
		ss_get_url( url, handleSaveTutorialPanelStateAjaxResults );
	}// end saveTutorialPanelState()


	/**
	 * Open a window and start the given video tutorial.
	 */
	function startTutorial( tutorialName )
	{
		alert( 'Not yet implemented' );
	}// end startTutorial()
</script>

<c:if test="${showWorkspacePage}">
	<jsp:useBean id="ssUserProperties" type="java.util.Map" scope="request" />
	<jsp:useBean id="ssUser" type="org.kablink.teaming.domain.User" scope="request" />
	<script type="text/javascript">
		var ss_reloadUrl = "${ss_reloadUrl}";
		var ss_reloadUrl${ssBinder.id} = ss_reloadUrl;
	</script>
<div id="ss_showfolder${renderResponse.namespace}" class="ss_style ss_portlet ss_content_outer">
<jsp:include page="/WEB-INF/jsp/forum/view_workarea_navbar.jsp" />
<c:set var="ss_sidebarVisibility" value="${ssUserProperties.sidebarVisibility}" scope="request"/>
<c:if test="${empty ss_sidebarVisibility}"><c:set var="ss_sidebarVisibility" value="block" scope="request"/></c:if>
<c:if test="${ss_sidebarVisibility == 'none'}">
  <c:set var="ss_sidebarVisibilityShow" value="block"/>
  <c:set var="ss_sidebarVisibilityHide" value="none"/>
  <c:set var="ss_sidebarTdStyle" value=""/>
</c:if>
<c:if test="${ss_sidebarVisibility != 'none'}">
  <c:set var="ss_sidebarVisibilityShow" value="none"/>
  <c:set var="ss_sidebarVisibilityHide" value="block"/>
  <c:set var="ss_sidebarTdStyle" value="ss_view_sidebar"/>
</c:if>
<div class="ss_actions_bar1_pane ss_sidebarImage">
<table cellspacing="0" cellpadding="0">
<tbody>
<tr>
<ssf:ifnotaccessible>
<c:if test="${!ss_mashupHideSidebar}">
<td valign="middle">
<a href="javascript: ;" 
  onClick="ss_showHideSidebar('${renderResponse.namespace}');return false;"
><span style="padding-left:12px; display:${ss_sidebarVisibilityShow};"
  id="ss_sidebarHide${renderResponse.namespace}" 
  class="ss_fineprint ss_sidebarSlidesm ss_sidebarSlidetext"><ssf:nlt tag="toolbar.sidebar.show"/></span>
  <span style="padding-left:12px; display:${ss_sidebarVisibilityHide};"
  id="ss_sidebarShow${renderResponse.namespace}" 
  class="ss_fineprint ss_sidebarSlide ss_sidebarSlidetext"><ssf:nlt tag="toolbar.sidebar.hide"/></span></a>
</td>
</c:if>
</ssf:ifnotaccessible>

<c:if test="${!ss_mashupHideToolbar}">
<td valign="middle">
	<ssf:toolbar style="ss_actions_bar1 ss_actions_bar">			
	<% // Workspace toolbar %>
	<jsp:include page="/WEB-INF/jsp/definition_elements/folder_toolbar.jsp" />
	</ssf:toolbar>
</td>
</c:if>
</tr></tbody>
</table>
</div>
    <table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tbody>
    <tr>
<ssf:ifnotaccessible>
    <c:if test="${!ss_mashupHideSidebar}">
      <td valign="top" class="${ss_sidebarTdStyle}" id="ss_sidebarTd${renderResponse.namespace}">
		<jsp:include page="/WEB-INF/jsp/sidebars/sidebar.jsp" />
	  </td>
	</c:if>
</ssf:ifnotaccessible>
	<td valign="top" class="ss_view_info">
		<div style="width: 100%; background-image: url('<html:imagesPath/>pics/tutorial/bgVideoBanner.gif'); background-repeat: repeat-x; ">
			<% /* This table is displayed when the user collapses the video tutorial. */ %>
			<table id="collapsedTutorialTable" width="100%" style="padding: 4px; display: none;">
				<tr>
					<td width="90%" style="padding-left: 15px;">
						<span style="font-size: .8em; font-weight: bold; color: #906040;"><ssf:nlt tag="tutorial.heading" /></span>
					</td>
					<td width="10%" align="right" valign="top">
						<a 	href="#"
							onClick="confirmCloseTutorialPanel()">
							<img	border="0"
									src="<html:imagesPath/>pics/popup_close_box.gif"
									title="<ssf:nlt tag="tutorial.alt.closeTutorial"/>"
									alt="<ssf:nlt tag="tutorial.alt.closeTutorial"/>" />
						</a>
						<a 	href="#"
							onClick="expandTutorialPanel()">
							<img	border="0"
									src="<html:imagesPath/>pics/sym_s_expand.gif"
									title="<ssf:nlt tag="tutorial.alt.expandTutorial"/>"
									alt="<ssf:nlt tag="tutorial.alt.expandTutorial"/>" />
						</a>
					</td>
				</tr>
			</table>

			<% /* This table is displayed when the video tutorial is not collapsed. */ %>
			<table id="expandedTutorialTable" width="100%" style="padding: 4px; display: none;">
				<tr>
					<td style="padding-left: 15px;">
						<span style="font-size: 1.25em; font-weight: bold; color: #906040;"><ssf:nlt tag="tutorial.heading" /></span>
					</td>
					<td align="center">
						<a 	href="#"
							title="<ssf:nlt tag="tutorial.alt.viewWhatIsTeaming" />"
							onClick="startTutorial( 'whatIsTeaming' )">
							<img	border="0"
									src="<html:imagesPath/>pics/tutorial/iconVideoWhatIsTeaming.png"
									title=""
									alt="" />
							<br /><span style="text-decoration: underline"><ssf:nlt tag="tutorial.whatsTeaming" /></span> 
						</a>
					</td>
					<td align="center">
						<a 	href="#"
							title="<ssf:nlt tag="tutorial.alt.viewGettingAround" />"
							onClick="startTutorial( 'gettingAround' )">
							<img	border="0"
									src="<html:imagesPath/>pics/tutorial/iconVideoGettingAround.png"
									title=""
									alt="" />
							<br/><span style="text-decoration: underline"><ssf:nlt tag="tutorial.gettingAround" /></span> 
						</a>
					</td>
					<td align="center">
						<a 	href="#"
							title="<ssf:nlt tag="tutorial.alt.viewImportingFiles" />"
							onClick="startTutorial( 'importingFiles' )">
							<img	border="0"
									src="<html:imagesPath/>pics/tutorial/iconVideoImportingFiles.png"
									title=""
									alt="" />
							<br/><span style="text-decoration: underline"><ssf:nlt tag="tutorial.importingFiles" /></span> 
						</a>
					</td>
					<td align="center">
						<a 	href="#"
							title="<ssf:nlt tag="tutorial.alt.viewCustomization" />"
							onClick="startTutorial( 'customization' )">
							<img	border="0"
									src="<html:imagesPath/>pics/tutorial/iconVideoCustomization.png"
									title=""
									alt="" />
							<br/><span style="text-decoration: underline"><ssf:nlt tag="tutorial.customization" /></span> 
						</a>
					</td>
					<td align="center">
						<a 	href="#"
							title="<ssf:nlt tag="tutorial.alt.viewBestPractices" />"
							onClick="startTutorial( 'bestPractices' )">
							<img	border="0"
									src="<html:imagesPath/>pics/tutorial/iconVideoBestPractices.png"
									title=""
									alt="" />
							<br/><span style="text-decoration: underline"><ssf:nlt tag="tutorial.bestPractices" /></span> 
						</a>
					</td>
					<td width="5%" align="right" valign="top">
						<a 	href="#"
							onClick="confirmCloseTutorialPanel()">
							<img	border="0"
									src="<html:imagesPath/>pics/popup_close_box.gif"
									title="<ssf:nlt tag="tutorial.alt.closeTutorial"/>"
									alt="<ssf:nlt tag="tutorial.alt.closeTutorial"/>" />
						</a>
						<a 	href="#"
							onClick="collapseTutorialPanel()">
							<img	border="0"
									src="<html:imagesPath/>pics/sym_s_collapse.gif"
									title="<ssf:nlt tag="tutorial.alt.collapseTutorial"/>"
									alt="<ssf:nlt tag="tutorial.alt.collapseTutorial"/>" />
						</a>
					</td>
				</tr>
			</table>
		</div>
		<div class="ss_tab_canvas">
			<!-- Rounded box surrounding entire page (continuation of tabs metaphor) -->
				<div class="ss_style_color">				
					<div class="ss_content_inner">
					  	<c:choose>
					  		<c:when test="${ss_showTeamMembers}">
								<% // Navigation links %>
								<%@ include file="/WEB-INF/jsp/forum/list_team_members.jsp" %>
								
								<c:if test="${!empty ss_reloadUrl}">
									<div style="text-align: right; "><a href="<c:out value="${ss_reloadUrl}" />"><ssf:nlt tag="__return_to" /> <ssf:nlt tag="__workspace_view" /></a></div>
								</c:if>
								
							</c:when>
							<c:otherwise>

								<div id="ss_whatsNewDiv${ss_namespace}">
								<c:if test="${ss_type == 'whatsNew' || ss_type == 'unseen'}">
								<jsp:include page="/WEB-INF/jsp/forum/whats_new_page.jsp" />
								</c:if>
								</div>

								<% // Show the workspace according to its definition %>
								<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
								  processThisItem="true"
								  configElement="${ssConfigElement}" 
								  configJspStyle="${ssConfigJspStyle}"
								  entry="${ssBinder}" />
							</c:otherwise>
						</c:choose>
					</div>

				</div>

		  <% // Footer toolbar %>
		  <c:if test="${!ss_mashupHideFooter}">
		    <jsp:include page="/WEB-INF/jsp/definition_elements/footer_toolbar.jsp" />
		  </c:if>
		  <c:if test="${ss_mashupHideToolbar && ss_mashupShowAlternateToolbar}">
			<div class="ss_actions_bar1_pane ss_sidebarImage">
			<table cellspacing="0" cellpadding="0">
			<tbody>
			<tr>
			<td valign="middle">
				<ssf:toolbar style="ss_actions_bar1 ss_actions_bar">			
				<% // Workspace toolbar %>
				<jsp:include page="/WEB-INF/jsp/definition_elements/folder_toolbar.jsp" />
				</ssf:toolbar>
			</td>
			</tr>
			</tbody>
			</table>
			</div>
		  </c:if>
			
		</div>
	</td>
	</tr>
	</tbody>
	</table>
<ssf:ifaccessible>
  <div>
	<jsp:include page="/WEB-INF/jsp/sidebars/sidebar.jsp" />
  </div>
</ssf:ifaccessible>
  </div>
<script type="text/javascript">
ss_createOnLoadObj('ss_initShowFolderDiv${renderResponse.namespace}', ss_initShowFolderDiv('${renderResponse.namespace}'));
</script>
</c:if>
	
</c:if>

<ssf:ifadapter>
</div>
	</body>
</html>
</ssf:ifadapter>
