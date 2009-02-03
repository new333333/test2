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

<c:if test="${empty ss_tutorial_support_loaded}">
	<script type="text/javascript">
		// The m_playTutorialWnd variable holds a handle to the Play Tutorial window.
		var		m_playTutorialWnd	= null;

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
	
	<c:set var="ss_tutorial_support_loaded" value="1" scope="request" />
</c:if>

