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

<% /* Only display the tutorial panel if we are running the old ui. */ %>
<% 	if ( GwtUIHelper.isGwtUIActive( request ) == false ) { %>
<div style="width: 100%; background-image: url('<html:imagesPath/>pics/tutorial/bgVideoBanner.gif'); background-repeat: repeat-x; ">
	<% /* This table is displayed when the user collapses the video tutorial. */ %>
	<table id="collapsedTutorialTable" width="100%" style="padding: 4px; display: none;">
		<tr>
			<td width="90%" style="padding-left: 15px;">
				<span style="font-size: .8em; font-weight: bold; color: #906040;"><ssf:nlt tag="tutorial.heading" /></span>
			</td>
			<td width="10%" align="right" valign="top">
				<a 	href="#"
					id="closeCollapsedTutorialTableAnchor"
					onClick="confirmCloseTutorialPanel()">
					<img	border="0"
							src="<html:imagesPath/>pics/popup_close_box.gif"
							title="<ssf:nlt tag="tutorial.alt.closeTutorial"/>"
							alt="<ssf:nlt tag="tutorial.alt.closeTutorial"/>" />
				</a>
				<a 	href="#"
					onClick="showTutorialPanelExpanded()">
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
					title="<ssf:nlt tag="tutorial.alt.viewTutorial1" />"
					onClick="startTutorial( 'whatIsTeaming' )">
					<img	border="0"
							src="<html:imagesPath/>pics/tutorial/iconVideoWhatIsTeaming.png"
							title=""
							alt="" />
					<br /><span style="text-decoration: underline"><ssf:nlt tag="tutorial.tutorial1" /></span> 
				</a>
			</td>
			<td align="center">
				<a 	href="#"
					title="<ssf:nlt tag="tutorial.alt.viewTutorial2" />"
					onClick="startTutorial( 'gettingStarted' )">
					<img	border="0"
							src="<html:imagesPath/>pics/tutorial/iconVideoGettingAround.png"
							title=""
							alt="" />
					<br/><span style="text-decoration: underline"><ssf:nlt tag="tutorial.tutorial2" /></span> 
				</a>
			</td>
			<td align="center">
				<a 	href="#"
					title="<ssf:nlt tag="tutorial.alt.viewTutorial3" />"
					onClick="startTutorial( 'gettingInformed' )">
					<img	border="0"
							src="<html:imagesPath/>pics/tutorial/iconVideoImportingFiles.png"
							title=""
							alt="" />
					<br/><span style="text-decoration: underline"><ssf:nlt tag="tutorial.tutorial3" /></span> 
				</a>
			</td>
			<td align="center">
				<a 	href="#"
					title="<ssf:nlt tag="tutorial.alt.viewTutorial4" />"
					onClick="startTutorial( 'navigation' )">
					<img	border="0"
							src="<html:imagesPath/>pics/tutorial/iconVideoCustomization.png"
							title=""
							alt="" />
					<br/><span style="text-decoration: underline"><ssf:nlt tag="tutorial.tutorial4" /></span> 
				</a>
			</td>
			<td align="center">
				<a 	href="#"
					title="<ssf:nlt tag="tutorial.alt.viewTutorial5" />"
					onClick="startTutorial( 'customizingTeaming' )">
					<img	border="0"
							src="<html:imagesPath/>pics/tutorial/iconVideoBestPractices.png"
							title=""
							alt="" />
					<br/><span style="text-decoration: underline"><ssf:nlt tag="tutorial.tutorial5" /></span> 
				</a>
			</td>
			<td width="5%" align="right" valign="top">
				<a 	href="#"
					id="closeExpandedTutorialTableAnchor"
					onClick="confirmCloseTutorialPanel()">
					<img	border="0"
							src="<html:imagesPath/>pics/popup_close_box.gif"
							title="<ssf:nlt tag="tutorial.alt.closeTutorial"/>"
							alt="<ssf:nlt tag="tutorial.alt.closeTutorial"/>" />
				</a>
				<a 	href="#"
					onClick="showTutorialPanelCollapsed()">
					<img	border="0"
							src="<html:imagesPath/>pics/sym_s_collapse.gif"
							title="<ssf:nlt tag="tutorial.alt.collapseTutorial"/>"
							alt="<ssf:nlt tag="tutorial.alt.collapseTutorial"/>" />
				</a>
			</td>
		</tr>
	</table>
</div>
<% } %>
