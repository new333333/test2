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
				tutorialObj.td = document.getElementById( 'tutorial1TD' );
				tutorialObj.span = document.getElementById( 'tutorial1Span' );
				tutorialObj.img = document.getElementById( 'tutorial1SelectedImg' );
				tutorialObj.type = 'application/x-shockwave-flash';
				tutorialObj.url = 'http://www.youtube.com/v/8YqsFwmgMms&hl=en&fs=1';

				// Create a tutorial object for "Getting Started"
				tutorialObj = new Object();
				m_tutorialObjs['gettingStarted'] = tutorialObj;
				tutorialObj.td = document.getElementById( 'tutorial2TD' );
				tutorialObj.span = document.getElementById( 'tutorial2Span' );
				tutorialObj.img = document.getElementById( 'tutorial2SelectedImg' );
				tutorialObj.type = 'application/x-shockwave-flash';
				tutorialObj.url = 'http://www.youtube.com/v/yuI6XfKoDiA&hl=en&fs=1';

				// Create a tutorial object for "Getting Informed"
				tutorialObj = new Object();
				m_tutorialObjs['gettingInformed'] = tutorialObj;
				tutorialObj.td = document.getElementById( 'tutorial3TD' );
				tutorialObj.span = document.getElementById( 'tutorial3Span' );
				tutorialObj.img = document.getElementById( 'tutorial3SelectedImg' );
				tutorialObj.type = 'application/x-shockwave-flash';
				tutorialObj.url = 'http://www.youtube.com/v/VJTQr4BPurc&hl=en&fs=1';

				// Create a tutorial object for "Navigation"
				tutorialObj = new Object();
				m_tutorialObjs['navigation'] = tutorialObj;
				tutorialObj.td = document.getElementById( 'tutorial4TD' );
				tutorialObj.span = document.getElementById( 'tutorial4Span' );
				tutorialObj.img = document.getElementById( 'tutorial4SelectedImg' );
				tutorialObj.type = '';
				tutorialObj.url = 'http://137.65.64.13/funnies/Lucky-4.wmv';

				// Create a tutorial object for "Customizing Teaming to Solve Business Problems"
				tutorialObj = new Object();
				m_tutorialObjs['customizingTeaming'] = tutorialObj;
				tutorialObj.td = document.getElementById( 'tutorial5TD' );
				tutorialObj.span = document.getElementById( 'tutorial5Span' );
				tutorialObj.img = document.getElementById( 'tutorial5SelectedImg' );
				tutorialObj.type = '';
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
				tutorialObj.td.style.backgroundImage = "url('<html:imagesPath/>pics/tutorial/bgSelectedVideo.gif')";
				tutorialObj.td.style.backgroundRepeat = 'repeat-x';

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
				embed.type = tutorialObj.type;
				embed.src = tutorialObj.url;

				td.appendChild( embed );
			}// end playTutorial()


			/**
			 * Unhighlight the selected tutorial.
			 */
			function unhighlightTutorial( tutorialObj )
			{
				// Set the background image of the tutorial text to nothing.
				tutorialObj.td.style.backgroundImage = '';
				tutorialObj.td.style.backgroundRepeat = '';

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
							<td nowrap width="1%" id="tutorial1TD">
								<div id="tutorial1Div" class="tutorialItem">
									<a 	href="#"
										onclick="playTutorial( 'whatIsTeaming' )"
										style="text-decoration: none;"
										title="<ssf:nlt tag="tutorial.alt.viewTutorial1" />" >
										<img	border="0"
												align="absmiddle"
												src="<html:imagesPath/>pics/tutorial/iconWhatIsTeaming.png"
												title="<ssf:nlt tag="tutorial.alt.viewTutorial1" />"
												alt="<ssf:nlt tag="tutorial.alt.viewTutorial1" />" />
										<!-- We want the text in this column to line up.  That's why we have an image here. -->
										<img	border="0" width="5" src="<html:imagesPath/>pics/1pix.gif" title="" alt="" />
										<span class="tutorialUnhighlightedText" id="tutorial1Span"><ssf:nlt tag="tutorial.tutorial1" /></span>
									</a>
								</div>
							</td>
							<td nowrap width="1%">
								<img id="tutorial1SelectedImg" border="0" align="absmiddle" style="" src="<html:imagesPath/>pics/1pix.gif" title="" alt="" />
							</td>
						</tr>
						<tr>
							<td nowrap width="1%" id="tutorial2TD">
								<div id="tutorial2Div" class="tutorialItem">
									<a	href="#"
										onclick="playTutorial( 'gettingStarted' )"
										style="text-decoration: none;"
										title="<ssf:nlt tag="tutorial.alt.viewTutorial2" />" >
										<img	border="0"
												align="absmiddle"
												src="<html:imagesPath/>pics/tutorial/iconGettingAround.png"
												title="<ssf:nlt tag="tutorial.alt.viewTutorial2" />"
												alt="<ssf:nlt tag="tutorial.alt.viewTutorial2" />" />
										<!-- We want the text in this column to line up.  That's why we have an image here. -->
										<img	border="0" width="4" src="<html:imagesPath/>pics/1pix.gif" title="" alt="" />
										<span class="tutorialUnhighlightedText" id="tutorial2Span"><ssf:nlt tag="tutorial.tutorial2" /></span>
									</a>
								</div>
							</td>
							<td nowrap width="1%">
								<img id="tutorial2SelectedImg" border="0" align="absmiddle" style="" src="<html:imagesPath/>pics/1pix.gif" title="" alt="" />
							</td>
						</tr>
						<tr>
							<td nowrap width="1%" id="tutorial3TD">
								<div id="tutorial3Div" class="tutorialItem">
									<a	href="#"
										onclick="playTutorial( 'gettingInformed' )"
										style="text-decoration: none;"
										title="<ssf:nlt tag="tutorial.alt.viewTutorial3" />" >
										<img	border="0"
												align="absmiddle"
												src="<html:imagesPath/>pics/tutorial/iconImportingFiles.png"
												title="<ssf:nlt tag="tutorial.alt.viewTutorial3" />"
												alt="<ssf:nlt tag="tutorial.alt.viewTutorial3" />" />
										<!-- We want the text in this column to line up.  That's why we have an image here. -->
										<img	border="0" width="12" src="<html:imagesPath/>pics/1pix.gif" title="" alt="" />
										<span class="tutorialUnhighlightedText" id="tutorial3Span"><ssf:nlt tag="tutorial.tutorial3" /></span>
									</a>
								</div>
							</td>
							<td nowrap width="1%">
								<img id="tutorial3SelectedImg" border="0" align="absmiddle" style="" src="<html:imagesPath/>pics/1pix.gif" title="" alt="" />
							</td>
						</tr>
						<tr>
							<td nowrap width="1%" id="tutorial4TD">
								<div id="tutorial4Div" class="tutorialItem">
									<a	href="#"
										onclick="playTutorial( 'navigation' )"
										style="text-decoration: none;"
										title="<ssf:nlt tag="tutorial.alt.viewTutorial4" />" >
										<img	border="0"
												align="absmiddle"
												src="<html:imagesPath/>pics/tutorial/iconCustomization.png"
												title="<ssf:nlt tag="tutorial.alt.viewTutorial4" />"
												alt="<ssf:nlt tag="tutorial.alt.viewTutorial4" />" />
										<!-- We want the text in this column to line up.  That's why we have an image here. -->
										<img	border="0" width="8" src="<html:imagesPath/>pics/1pix.gif" title="" alt="" />
										<span class="tutorialUnhighlightedText" id="tutorial4Span"><ssf:nlt tag="tutorial.tutorial4" /></span>
									</a>
								</div>
							</td>
							<td nowrap width="1%">
								<img id="tutorial4SelectedImg" border="0" align="absmiddle" style="" src="<html:imagesPath/>pics/1pix.gif" title="" alt="" />
							</td>
						</tr>
						<tr>
							<td nowrap width="1%" id="tutorial5TD">
								<div id="tutorial5Div" class="tutorialItem">
									<a	href="#"
										onclick="playTutorial( 'customizingTeaming' )"
										style="text-decoration: none;"
										title="<ssf:nlt tag="tutorial.alt.viewTutorial5" />" >
										<img	border="0"
												align="absmiddle"
												src="<html:imagesPath/>pics/tutorial/iconBestPractices.png"
												title="<ssf:nlt tag="tutorial.alt.viewTutorial5" />"
												alt="<ssf:nlt tag="tutorial.alt.viewTutorial5" />" />
										<!-- We want the text in this column to line up.  That's why we have an image here. -->
										<img	border="0" width="8" src="<html:imagesPath/>pics/1pix.gif" title="" alt="" />
										<span class="tutorialUnhighlightedText" id="tutorial5Span"><ssf:nlt tag="tutorial.tutorial5" /></span>
									</a>
								</div>
							</td>
							<td nowrap width="1%">
								<img id="tutorial5SelectedImg" border="0" align="absmiddle" style="" src="<html:imagesPath/>pics/1pix.gif" title="" alt="" />
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

