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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<% // Support routines for invoking the page that plays the tutorial videos. %>

<script type="text/javascript">
	// The m_playTutorialWnd variable holds a handle to the Play Tutorial window.
	var		m_playTutorialWnd	= null;
	var		m_inLandingPage		= '${ss_inLandingPage}';	// Have we been included as part of a landing page?

	// Global variables.
	window.TUTORIAL_PANEL_CLOSED	= 1;
	window.TUTORIAL_PANEL_EXPANDED	= 2;
	window.TUTORIAL_PANEL_COLLAPSED	= 3;

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
	 * This function gets called after the page is loaded.
	 * This code will display the tutorial panel in its correct initial state, hidden, expanded or collapsed.
	 */
	function initTutorial()
	{
		var	initialState;
		var	table;

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
	}// end initTutorial()


	/**
	 * This function will return true if the user is looking at a landing page.
	 */
	function isLandingPage()
	{
		if ( m_inLandingPage =='true' )
			return true;

		return false;
	}// end isLandingPage()

			
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
		var		url
		var		winHeight;
		var		winWidth;

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
			
		url = '<ssf:escapeJavaScript>${ss_play_tutorial_base_url}</ssf:escapeJavaScript>';
		url += '&ss_tutorial_name=' + encodeURIComponent( tutorialName );
		winHeight = 520;
		winWidth = 720; 
		m_playTutorialWnd = window.open(
									url,
									'PlayTutorialWindow',
									'height=' + winHeight + ',resizable,scrollbars,width=' + winWidth );
	}// end startTutorial()
</script>

