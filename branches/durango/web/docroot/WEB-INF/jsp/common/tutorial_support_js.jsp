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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page import="org.kablink.teaming.web.util.GwtUIHelper" %>

<% // Support routines for invoking the page that plays the tutorial videos. %>

<script type="text/javascript">
	// The m_playTutorialWnd variable holds a handle to the Play Tutorial window.
	var m_playTutorialWnd	= null;
	var m_inLandingPage		= '${ss_inLandingPage}';	// Have we been included as part of a landing page?
	var m_saveTutorialPanelState = true;

	// Global variables.
	window.TUTORIAL_PANEL_CLOSED	= 1;
	window.TUTORIAL_PANEL_EXPANDED	= 2;
	window.TUTORIAL_PANEL_COLLAPSED	= 3;

	/**
	 * This function will tell the user that closing the tutorial panel will permanently remove
	 * it and will ask the user if that is what they want to do.
	 */
	function confirmCloseTutorialPanel()
	{
		var	msg;

		// Tell the user they can restore the video tutorial panel from "Personal Preferences" in the sidebar.
		msg = '<ssf:escapeJavaScript><ssf:nlt tag="tutorial.confirmClose" /></ssf:escapeJavaScript>'; 
		alert( msg );

		// The user wants to permanently remove the tutorial panel.
		// Hide the tutorial panels.
		hideTutorialPanels();
	}// end confirmCloseTutorialPanel()


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

		// If we are running the GWT ui then bail.
		<% 	if ( GwtUIHelper.isGwtUIActive( request ) ) { %>
			return;
		<% } %>
		
		// Hide the collapsed tutorial table.
		table = document.getElementById( 'collapsedTutorialTable' );
		table.style.display = 'none';

		// Hide the expanded tutorial table.
		table = document.getElementById( 'expandedTutorialTable' );
		table.style.display = 'none';

		// Update the link in the "Video Tutorial Panel" preference in the sidebar.
		updateSidebarTutorialPanelPref( window.TUTORIAL_PANEL_CLOSED );

		// Do we need to save the tutorial panel state?
		if ( m_saveTutorialPanelState )
		{
			// Yes
			// Issue an ajax request to remember that the tutorial panel should not be displayed again.
			saveTutorialPanelState( window.TUTORIAL_PANEL_CLOSED );
		}
	}// end hideTutorialPanels()


	/**
	 * This function gets called after the page is loaded.
	 * This code will display the tutorial panel in its correct initial state, hidden, expanded or collapsed.
	 */
	function initTutorial()
	{
		var initialState;
		var lang;

		// If we are running the GWT ui then bail.
		<% 	if ( GwtUIHelper.isGwtUIActive( request ) ) { %>
			return;
		<% } %>
		
		lang = '${teamingLang}';

		// Is the user looking at a landing page or his own workspace?
		if ( isLandingPage() || isOwnWorkspace() )
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

		// The video tutorials are only in English.  If a user is running in a language other than English
		// we wan't to display the tutorial panel in its collapsed state.
		// Is the user running in a language other than English?
		if ( lang != 'en' )
		{
			// Yes
			// Is the video tutorial panel visible?
			if ( initialState != window.TUTORIAL_PANEL_CLOSED )
			{
				// Yes, set the tutorial panel as collapsed.
				initialState = window.TUTORIAL_PANEL_COLLAPSED;
			}
		}

		// If the user is looking at a landing page hide the "close tutorial" anchor.
		if ( isLandingPage() )
		{
			var		anchor;

			// Hide the "close tutorial" anchor on the collapsed tutorial table.
			anchor = document.getElementById( 'closeCollapsedTutorialTableAnchor' );
			anchor.style.display = 'none';

			// Hide the "close tutorial" anchor on the expanded tutorial table.
			anchor = document.getElementById( 'closeExpandedTutorialTableAnchor' );
			anchor.style.display = 'none';
		}

		// We don't want the tutorial panel state saved during initialization, only when the state changes.
		m_saveTutorialPanelState = false;
		
		// Is the tutorial panel supposed to be closed?
		if ( initialState == window.TUTORIAL_PANEL_CLOSED )
		{
			// Yes, hide the tutorial panel.
			hideTutorialPanels();
		}
		// Is the tutorial panel collapsed?
		else if ( initialState == window.TUTORIAL_PANEL_COLLAPSED )
		{
			// Yes, collapse the tutorial panel.
			showTutorialPanelCollapsed();
		}
		else
		{
			// The tutorial panel is expanded.
			// Show the expanded tutorial table.
			showTutorialPanelExpanded();
		}

		m_saveTutorialPanelState = true;
	}// end initTutorial()


	/**
	 * This function will return true if the user is looking at a landing page.
	 */
	function isLandingPage()
	{
		// If we are running the GWT ui then bail.
		<% 	if ( GwtUIHelper.isGwtUIActive( request ) ) { %>
			return;
		<% } %>
			
		if ( m_inLandingPage =='true' )
			return true;

		return false;
	}// end isLandingPage()

			
	/**
	 * This function will return true if the logged in user is looking at their own workspace.
	 */
	function isOwnWorkspace()
	{
		// If we are running the GWT ui then bail.
		<% 	if ( GwtUIHelper.isGwtUIActive( request ) ) { %>
			return;
		<% } %>
			
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

		// If we are running the GWT ui then bail.
		<% 	if ( GwtUIHelper.isGwtUIActive( request ) ) { %>
			return;
		<% } %>
		
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
	 * Show the tutorial panel in its collapsed state.
	 */
	function showTutorialPanelCollapsed()
	{
		var	table;

		// If we are running the GWT ui then bail.
		<% 	if ( GwtUIHelper.isGwtUIActive( request ) ) { %>
			return;
		<% } %>
		
		// Hide the expanded tutorial table.
		table = document.getElementById( 'expandedTutorialTable' );
		table.style.display = 'none';

		// Show the collapsed tutorial table.
		table = document.getElementById( 'collapsedTutorialTable' );
		table.style.display = '';

		// Update the link in the "Video Tutorial Panel" preference in the sidebar.
		updateSidebarTutorialPanelPref( window.TUTORIAL_PANEL_COLLAPSED );

		// Do we need to save the tutorial panel state?
		if ( m_saveTutorialPanelState )
		{
			// Yes
			// Issue an ajax request to remember that the tutorial panel is collapsed.
			saveTutorialPanelState( window.TUTORIAL_PANEL_COLLAPSED );
		}
	}// end showTutorialPanelCollapsed()

	
	/**
	 * Show the tutorial panel in its expanded state.
	 */
	function showTutorialPanelExpanded()
	{
		var	table;

		// If we are running the GWT ui then bail.
		<% 	if ( GwtUIHelper.isGwtUIActive( request ) ) { %>
			return;
		<% } %>
		
		// Hide the collapsed tutorial table.
		table = document.getElementById( 'collapsedTutorialTable' );
		table.style.display = 'none';

		// Show the expanded tutorial table.
		table = document.getElementById( 'expandedTutorialTable' );
		table.style.display = '';

		// Update the link in the "Video Tutorial Panel" preference in the sidebar.
		updateSidebarTutorialPanelPref( window.TUTORIAL_PANEL_EXPANDED );

		// Do we need to save the tutorial panel state?
		if ( m_saveTutorialPanelState )
		{
			// Yes
			// Issue an ajax request to remember that the tutorial panel is expanded.
			saveTutorialPanelState( window.TUTORIAL_PANEL_EXPANDED );
		}
	}// end showTutorialPanelExpanded()

	
	/**
	 * Open a window and start the given video tutorial.
	 */
	function startTutorial( tutorialName )
	{
		var		url
		var		winHeight;
		var		winWidth;

		// If we are running the GWT ui then bail.
		<% 	if ( GwtUIHelper.isGwtUIActive( request ) ) { %>
			return;
		<% } %>
		
		// Create the appropriate url based on the tutorial the user selected.
		url = 'http://www.brainstorminc.com/cbt/teaming/index.php?category=';
		if ( tutorialName == 'whatIsTeaming' )
			url += 'whatisteaming';
		else if ( tutorialName == 'gettingStarted' )
			url += 'gettingstarted';
		else if ( tutorialName == 'gettingInformed' )
			url += 'gettinginformed';
		else if ( tutorialName == 'navigation' )
			url += 'navigation';
		else if ( tutorialName == 'customizingTeaming' )
			url += 'customizations';
		else
		{
			// This should never happen.
			alert( 'Unknown tutorial: ' + tutorialName );
			return;
		}
			
		winHeight = 900;
		winWidth = 975; 
		m_playTutorialWnd = window.open(
									url,
									'PlayTutorialWindow',
									'height=' + winHeight + ',resizable,scrollbars,width=' + winWidth );

		// The following code is what we used before we started using Brainstorm's videos.
		// This code can be removed when we are satisfied with the Brainstorm videos.
		if ( false )
		{
			// See if the 'Play Tutorial Window' is already open.  If it is call its playTutorial() function.
			// Is the "Play Tutorial" window already open?
			if ( m_playTutorialWnd != null && ((typeof m_playTutorialWnd) != 'undefined' ) && !m_playTutorialWnd.closed )
			{
				// Yes.
				// Does the 'Play Tutorial' window have a playTutorial() function?
				if ( m_playTutorialWnd.playTutorial )
				{
					// Yes, call it.
					m_playTutorialWnd.playTutorial( tutorialName );
	
					// Nothing else to do.
					return;
				}
			}
	
			// As the fix for bug 481238, I removed the ssf:escapeJavaScript tag from the following line.
			url = '${ss_play_tutorial_base_url}';
			url += '&ss_tutorial_name=' + encodeURIComponent( tutorialName );
			winHeight = 900;
			winWidth = 1250; 
			m_playTutorialWnd = window.open(
										url,
										'PlayTutorialWindow',
										'height=' + winHeight + ',resizable,scrollbars,width=' + winWidth );
		}
	}// end startTutorial()


	/**
	 * Update the link in the "Video Tutorial Panel" section in the "Personal Preferences" section of the sidebar
	 */
	function updateSidebarTutorialPanelPref( panelState )
	{
		var anchor;
		var span;

		// If we are running the GWT ui then bail.
		<% 	if ( GwtUIHelper.isGwtUIActive( request ) ) { %>
			return;
		<% } %>
		
		anchor = document.getElementById( 'tutorialPanelPrefAnchor' );
		span = document.getElementById( 'tutorialPanelPrefSpan' );
		if ( anchor != null && span != null )
		{
			var text;

			// Is the tutorial panel closed?
			if ( panelState == window.TUTORIAL_PANEL_CLOSED )
			{
				// Yes
				// Update the link to be a "show" link.
				text = '<ssf:escapeJavaScript><ssf:nlt tag="sidebar.videoTutorial.show" /></ssf:escapeJavaScript>';

				anchor.onclick = showTutorialPanelExpanded;
			}
			else
			{
				// No
				// Update the link to be a "hide" link.
				text = '<ssf:escapeJavaScript><ssf:nlt tag="sidebar.videoTutorial.hide" /></ssf:escapeJavaScript>';

				anchor.onclick = hideTutorialPanels;
			}

			updateElementsTextNode( span, text );
		}
	}// end updateSidebarTutorialPanelPref()
</script>

