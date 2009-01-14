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
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%
try
{
%>
<html>
	<head>
		<title><ssf:nlt tag="playtutorial.title" /></title>

		<% /* Styles for this page. */ %>
		<STYLE media="screen" type="text/css">
		<!--
			.headingDiv
			{
				background-color: #ffecce;
				border: solid thin #000000;
			}
			
			.headingText
			{
				color: gray;
				font-family: arial,sans-serif;
				font-size: 1.5em;
				margin: 15px;
			}
			
			.tutorialHighlightedText
			{
				color: white;
				font-family: arial,sans-serif;
				font-size: .85em;
				font-weight: bold;
			}
			
			.tutorialUnhighlightedText
			{
				color: black;
				font-family: arial,sans-serif;
				font-size: .85em;
				font-weight: bold;
			}
			
			.tutorialItem
			{
				padding-left: 5px;
				padding-right: 5px;
				padding-top: 5px;
				padding-bottom: 5px;
			}
		//-->
		</STYLE>

		<script type="text/javascript">
			var		m_selectedTutorial	= null;
			var		m_tutorialObjs	= null;	// Associative array of tutorial objects.

			/**
			 * Return a tutorial object for the given tutorial name.
			 */
			function getTutorialFromName( tutorialName )
			{
				if ( m_tutorialObjs == null )
				{
					alert( 'In getTutorialFromName(), m_tutorialObjs is null' );
					return null;
				}

				return m_tutorialObjs[tutorialName];
			}// end getTutorialFromName()

			
			/**
			 * This function is the onload event handler for the <body> object.  It will create
			 * all of the tutorial objects needed.
			 */ 
			function handleOnload()
			{
				var		tutorialObj;
				var		tutorialName;

				// Create an array to hold the tutorial objects.
				m_tutorialObjs = new Array();

				// Create a tutorial object for "what is teaming".
				tutorialObj = new Object();
				m_tutorialObjs['whatIsTeaming'] = tutorialObj;
				tutorialObj.div = document.getElementById( 'whatIsTeamingDiv' );
				tutorialObj.span = document.getElementById( 'whatIsTeamingSpan' );
				tutorialObj.img = document.getElementById( 'whatIsTeamingSelectedImg' );
				tutorialObj.url = 'http://137.65.64.13/funnies/Lucky-1.wmv';

				tutorialObj = new Object();
				m_tutorialObjs['gettingAround'] = tutorialObj;
				tutorialObj.div = document.getElementById( 'gettingAroundDiv' );
				tutorialObj.span = document.getElementById( 'gettingAroundSpan' );
				tutorialObj.img = document.getElementById( 'gettingAroundSelectedImg' );
				tutorialObj.url = 'http://137.65.64.13/funnies/Lucky-2.wmv';

				tutorialObj = new Object();
				m_tutorialObjs['importingFiles'] = tutorialObj;
				tutorialObj.div = document.getElementById( 'importingFilesDiv' );
				tutorialObj.span = document.getElementById( 'importingFilesSpan' );
				tutorialObj.img = document.getElementById( 'importingFilesSelectedImg' );
				tutorialObj.url = 'http://137.65.64.13/funnies/Lucky-3.wmv';

				tutorialObj = new Object();
				m_tutorialObjs['customization'] = tutorialObj;
				tutorialObj.div = document.getElementById( 'customizationDiv' );
				tutorialObj.span = document.getElementById( 'customizationSpan' );
				tutorialObj.img = document.getElementById( 'customizationSelectedImg' );
				tutorialObj.url = 'http://137.65.64.13/funnies/Lucky-4.wmv';

				tutorialObj = new Object();
				m_tutorialObjs['bestPractices'] = tutorialObj;
				tutorialObj.div = document.getElementById( 'bestPracticesDiv' );
				tutorialObj.span = document.getElementById( 'bestPracticesSpan' );
				tutorialObj.img = document.getElementById( 'bestPracticesSelectedImg' );
				tutorialObj.url = 'http://137.65.64.13/funnies/Lucky-5.wmv';

				// Get the name of the tutorial we should start playing.
				tutorialName = '<ssf:escapeJavaScript>${ss_tutorial_name}</ssf:escapeJavaScript>';

				playTutorial( tutorialName );
			}// end handleOnload()

				 
			/**
			 * Highlight the selected tutorial.
			 */
			function highlightTutorial( tutorialObj )
			{
				// Change the background image of the tutorial text.
				tutorialObj.div.style.backgroundImage = "url('<html:imagesPath/>pics/tutorial/bgSelectedVideo.gif')";
				tutorialObj.div.style.backgroundRepeat = 'repeat-x';

				tutorialObj.span.className = 'tutorialHighlightedText';

				tutorialObj.img.src = '<html:imagesPath/>pics/tutorial/selectedVideoArrow.png';
			}// end highlightTutorial()


			/**
			 * This function will play the given tutorial.
			 */
			function playTutorial( tutorialName )
			{
				var		ctrl;
				var		tutorialObj;
				var		embed;
				var		td;

				self.focus();

				// Get the tutorial object for the given tutorial.
				tutorialObj = getTutorialFromName( tutorialName );

				if ( tutorialObj == null )
				{
					// This should never happen.
					alert( 'In playTutorial(), unknown tutorial name: ' + tutorialName );
					return;
				}

				// Unhighlight the previously selected tutorial.
				if ( m_selectedTutorial != null )
					unhighlightTutorial( m_selectedTutorial );
					
				// Highlight the name of the given tutorial.
				highlightTutorial( tutorialObj );

				m_selectedTutorial = tutorialObj;

				// Get the td that will holds the media player.
				td = document.getElementById( 'watchThisTutorialTD' );

				// Delete the current <embed> element if it exists.
				embed = document.getElementById( 'mediaPlayer' );
				if ( embed != null )
					embed.parentNode.removeChild( embed );

				// Create a new <embed> element.
				embed = document.createElement( 'embed' );
				embed.id = 'mediaPlayer';
				embed.allowscriptaccess = 'always';
				embed.allowfullscreen = 'true';
				embed.autoplay = 'true';
				embed.quality = 'high';
				embed.bgcolor = '#000000';
				embed.name = 'medialPlayer';
				embed.width = '480';
				embed.height = '385';
//!!!				embed.type = 'application/x-shockwave-flash';
				embed.src = tutorialObj.url;

				td.appendChild( embed );
			}// end playTutorial()


			/**
			 * Unhighlight the selected tutorial.
			 */
			function unhighlightTutorial( tutorialObj )
			{
				// Set the background image of the tutorial text to nothing.
				tutorialObj.div.style.backgroundImage = '';
				tutorialObj.div.style.backgroundRepeat = '';

				tutorialObj.span.className = 'tutorialUnhighlightedText';

				tutorialObj.img.src = '<html:imagesPath/>pics/1pix.gif';
			}// end highlightTutorial()
		</script>
	</head>

	<body onload="handleOnload()">
		<table width="100%">
			<tr>
				<td valign="middle" colspan="3" nowrap>
					<div class="headingDiv">
						<span class="headingText"><ssf:nlt tag="playtutorial.heading" /></span>
					</div>
				</td>
			</tr>
			<tr>
				<td nowrap width="1%" valign="top">
					<table width="100%" cellpadding="0" cellspacing="0">
						<tr>
							<td nowrap width="1%">
								<div id="whatIsTeamingDiv" class="tutorialItem">
									<a 	href="#"
										onclick="playTutorial( 'whatIsTeaming' )"
										style="text-decoration: none;"
										title="<ssf:nlt tag="tutorial.alt.viewWhatIsTeaming" />" >
										<img	border="0"
												align="absmiddle"
												src="<html:imagesPath/>pics/tutorial/iconWhatIsTeaming.png"
												title="<ssf:nlt tag="tutorial.alt.viewWhatIsTeaming" />"
												alt="<ssf:nlt tag="tutorial.alt.viewWhatIsTeaming" />" />
										<!-- We want the text in this column to line up.  That's why we have an image here. -->
										<img	border="0" width="5" src="<html:imagesPath/>pics/1pix.gif" title="" alt="" />
										<span class="tutorialUnhighlightedText" id="whatIsTeamingSpan"><ssf:nlt tag="tutorial.whatsTeaming" /></span>
									</a>
								</div>
							</td>
							<td nowrap width="1%">
								<img id="whatIsTeamingSelectedImg" border="0" height="36" align="absmiddle" style="" src="<html:imagesPath/>pics/1pix.gif" title="" alt="" />
							</td>
						</tr>
						<tr>
							<td nowrap width="1%">
								<div id="gettingAroundDiv" class="tutorialItem">
									<a	href="#"
										onclick="playTutorial( 'gettingAround' )"
										style="text-decoration: none;"
										title="<ssf:nlt tag="tutorial.alt.viewGettingAround" />" >
										<img	border="0"
												align="absmiddle"
												src="<html:imagesPath/>pics/tutorial/iconGettingAround.png"
												title="<ssf:nlt tag="tutorial.alt.viewGettingAround" />"
												alt="<ssf:nlt tag="tutorial.alt.viewGettingAround" />" />
										<!-- We want the text in this column to line up.  That's why we have an image here. -->
										<img	border="0" width="4" src="<html:imagesPath/>pics/1pix.gif" title="" alt="" />
										<span class="tutorialUnhighlightedText" id="gettingAroundSpan"><ssf:nlt tag="tutorial.gettingAround" /></span>
									</a>
								</div>
							</td>
							<td nowrap width="1%">
								<img id="gettingAroundSelectedImg" border="0" height="36" align="absmiddle" style="" src="<html:imagesPath/>pics/1pix.gif" title="" alt="" />
							</td>
						</tr>
						<tr>
							<td nowrap width="1%">
								<div id="importingFilesDiv" class="tutorialItem">
									<a	href="#"
										onclick="playTutorial( 'importingFiles' )"
										style="text-decoration: none;"
										title="<ssf:nlt tag="tutorial.alt.viewImportingFiles" />" >
										<img	border="0"
												align="absmiddle"
												src="<html:imagesPath/>pics/tutorial/iconImportingFiles.png"
												title="<ssf:nlt tag="tutorial.alt.viewImportingFiles" />"
												alt="<ssf:nlt tag="tutorial.alt.viewImportingFiles" />" />
										<!-- We want the text in this column to line up.  That's why we have an image here. -->
										<img	border="0" width="12" src="<html:imagesPath/>pics/1pix.gif" title="" alt="" />
										<span class="tutorialUnhighlightedText" id="importingFilesSpan"><ssf:nlt tag="tutorial.importingFiles" /></span>
									</a>
								</div>
							</td>
							<td nowrap width="1%">
								<img id="importingFilesSelectedImg" border="0" height="36" align="absmiddle" style="" src="<html:imagesPath/>pics/1pix.gif" title="" alt="" />
							</td>
						</tr>
						<tr>
							<td nowrap width="1%">
								<div id="customizationDiv" class="tutorialItem">
									<a	href="#"
										onclick="playTutorial( 'customization' )"
										style="text-decoration: none;"
										title="<ssf:nlt tag="tutorial.alt.viewCustomization" />" >
										<img	border="0"
												align="absmiddle"
												src="<html:imagesPath/>pics/tutorial/iconCustomization.png"
												title="<ssf:nlt tag="tutorial.alt.viewCustomization" />"
												alt="<ssf:nlt tag="tutorial.alt.viewCustomization" />" />
										<!-- We want the text in this column to line up.  That's why we have an image here. -->
										<img	border="0" width="8" src="<html:imagesPath/>pics/1pix.gif" title="" alt="" />
										<span class="tutorialUnhighlightedText" id="customizationSpan"><ssf:nlt tag="tutorial.customization" /></span>
									</a>
								</div>
							</td>
							<td nowrap width="1%">
								<img id="customizationSelectedImg" border="0" height="36" align="absmiddle" style="" src="<html:imagesPath/>pics/1pix.gif" title="" alt="" />
							</td>
						</tr>
						<tr>
							<td nowrap width="1%">
								<div id="bestPracticesDiv" class="tutorialItem">
									<a	href="#"
										onclick="playTutorial( 'bestPractices' )"
										style="text-decoration: none;"
										title="<ssf:nlt tag="tutorial.alt.viewBestPractices" />" >
										<img	border="0"
												align="absmiddle"
												src="<html:imagesPath/>pics/tutorial/iconBestPractices.png"
												title="<ssf:nlt tag="tutorial.alt.viewBestPractices" />"
												alt="<ssf:nlt tag="tutorial.alt.viewBestPractices" />" />
										<!-- We want the text in this column to line up.  That's why we have an image here. -->
										<img	border="0" width="8" src="<html:imagesPath/>pics/1pix.gif" title="" alt="" />
										<span class="tutorialUnhighlightedText" id="bestPracticesSpan"><ssf:nlt tag="tutorial.bestPractices" /></span>
									</a>
								</div>
							</td>
							<td nowrap width="1%">
								<img id="bestPracticesSelectedImg" border="0" height="36" align="absmiddle" style="" src="<html:imagesPath/>pics/1pix.gif" title="" alt="" />
							</td>
						</tr>
					</table>
				</td>
				<td width="*" id="watchThisTutorialTD">
				</td>
			</tr>
		</table>
	</body>
</html>
<%
}
catch (Exception e)
{
	e.printStackTrace();
%>
	An exception occured processing play_tutorial.jsp.
<%
}// end catch
%>

						<% /* * * * * * * */ %>
						<% /* End of File */ %>
						<% /* * * * * * * */ %>

