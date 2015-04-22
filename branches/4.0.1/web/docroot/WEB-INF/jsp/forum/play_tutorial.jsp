<%
/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html<% if (org.kablink.teaming.web.util.MiscUtil.isHtmlQuirksMode()) { %> PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"<% } %>>

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

			.sharedMessage 
			{
				margin: .25em;
			}
			
			.sharedMessagebox
			{
				display:block;
			}
			
			.sharedMessagebox *
			{
				display:block;
				height:1px;
				overflow:hidden;
				background:#f4f5bd;
			}
			
			.sharedMessagebox1
			{
				border-right:1px solid #f6f6d8;
				padding-right:1px;
				margin-right:3px;
				border-left:1px solid #f6f6d8;
				padding-left:1px;
				margin-left:3px;
				background:#f0f0bb;
			}
			
			.sharedMessagebox2
			{
				border-right:1px solid #fdfdf6;
				border-left:1px solid #fdfdf6;
				padding:0px 1px;
				background:#eeefb6;
				margin:0px 1px;
			}
			
			.sharedMessagebox3
			{
				border-right:1px solid #eeefb6;
				border-left:1px solid #eeefb6;
				margin:0px 1px;
			}
			
			.sharedMessagebox4
			{
				border-right:1px solid #f6f6d8;
				border-left:1px solid #f6f6d8;
			}
			
			.sharedMessagebox5
			{
				border-right:1px solid #f0f0bb;
				border-left:1px solid #f0f0bb;
			}
			
			.sharedMessagebox_content
			{
				padding:0 5px;
				background:#f4f5bd;
			} 
			
			.sharedMessagebox_content img
			{
				float: left; 
				padding-right: 0.5em; 
				padding-bottom: 0.5em; 
				padding-left: 0.2em;
			} 
			
			.sharedMessagebox_content .sharedMessageType
			{
				color: #9a4000;
				padding-right: 0.3em;
			}
			
			.sharedMessagebox_content .sharedWarnType
			{
				color: #9a4000;
				padding-right: 0.3em;
			}
			
			.sharedMessagebox_content .sharedErrorType
			{
				color: #d61e11;
				text-transform: uppercase;
				letter-spacing: 0.1em;
				padding-right: 0.3em;
			}
			
			.sharedMessagebox_content .sharedInfoType
			{
				color: #4d6d8b;
				padding-right: 0.3em;
			}
			
			.sharedMessagebox_content .sharedSuccessType
			{
				color: #117f12;
				padding-right: 0.3em;
			}
			
			.sharedMessagebox_content .sharedMessageText
			{
				font-size: 0.8em;
				font-weight: bold;
				padding: 0.75em 1.5em
			} 
				
			.sharedMessagebox_content .sharedSubtext
			{
				color: #424242; 
				font-size: 0.7em;
				background-color: #fff; 
				margin-top: 0.2em; 
				padding: 0.5em 1.5em 0.75em;
			} 	
		//-->
		</STYLE>

		<!-- Include the JavaScript used to interact with the flash plugin. -->
	    <script type="text/javascript" src="<html:rootPath/>js/swfobject/swfobject.js">
		</script>

	    <script type="text/javascript">
			var		m_selectedTutorial	= null;
			var		m_tutorialObjs		= null;	// Associative array of tutorial objects.
			var		m_swfObj			= null;	// swfobject used to interact with the flash plugin.

			var		REQUIRED_MIN_FLASH_PLUGIN_VERSION	= '9.0.115';

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
				var		fn;
					
				// Create an array to hold the tutorial objects.
				m_tutorialObjs = new Array();

				// Create a tutorial object for "what is teaming".
				tutorialObj = new Object();
				m_tutorialObjs['whatIsTeaming'] = tutorialObj;
				tutorialObj.td = document.getElementById( 'tutorial1TD' );
				tutorialObj.span = document.getElementById( 'tutorial1Span' );
				tutorialObj.img = document.getElementById( 'tutorial1SelectedImg' );
				tutorialObj.scriptUrl = 'biuhels.html'; 
				tutorialObj.type = 'application/x-shockwave-flash';
				tutorialObj.url = 'http://www.novell.com/documentation/teaming2/media/what_is_teaming_controller.swf';
//				tutorialObj.url = 'http://137.65.64.13/teaming-tutorials/Thayne_controller.swf';

				// Create a tutorial object for "Getting Started"
				tutorialObj = new Object();
				m_tutorialObjs['gettingStarted'] = tutorialObj;
				tutorialObj.td = document.getElementById( 'tutorial2TD' );
				tutorialObj.span = document.getElementById( 'tutorial2Span' );
				tutorialObj.img = document.getElementById( 'tutorial2SelectedImg' );
				tutorialObj.scriptUrl = 'biux9ad.html'; 
				tutorialObj.type = 'application/x-shockwave-flash';
				tutorialObj.url = 'http://www.novell.com/documentation/teaming2/media/getting-started_controller.swf';

				// Create a tutorial object for "Getting Informed"
				tutorialObj = new Object();
				m_tutorialObjs['gettingInformed'] = tutorialObj;
				tutorialObj.td = document.getElementById( 'tutorial3TD' );
				tutorialObj.span = document.getElementById( 'tutorial3Span' );
				tutorialObj.img = document.getElementById( 'tutorial3SelectedImg' );
				tutorialObj.scriptUrl = 'biuxe3o.html'; 
				tutorialObj.type = 'application/x-shockwave-flash';
				tutorialObj.url = 'http://www.novell.com/documentation/teaming2/media/getting-informed_controller.swf';

				// Create a tutorial object for "Getting Around"
				tutorialObj = new Object();
				m_tutorialObjs['navigation'] = tutorialObj;
				tutorialObj.td = document.getElementById( 'tutorial4TD' );
				tutorialObj.span = document.getElementById( 'tutorial4Span' );
				tutorialObj.img = document.getElementById( 'tutorial4SelectedImg' );
				tutorialObj.type = 'application/x-shockwave-flash';
				tutorialObj.scriptUrl = 'biuxihd.html'; 
				tutorialObj.url = 'http://www.novell.com/documentation/teaming2/media/navigation_controller.swf';

				// Create a tutorial object for "Customizing Teaming to Solve Business Problems"
				tutorialObj = new Object();
				m_tutorialObjs['customizingTeaming'] = tutorialObj;
				tutorialObj.td = document.getElementById( 'tutorial5TD' );
				tutorialObj.span = document.getElementById( 'tutorial5Span' );
				tutorialObj.img = document.getElementById( 'tutorial5SelectedImg' );
				tutorialObj.type = 'application/x-shockwave-flash';
				tutorialObj.scriptUrl = 'biuxlb3.html'; 
				tutorialObj.url = 'http://www.novell.com/documentation/teaming2/media/customizations_controller.swf';

				// Get the name of the tutorial we should start playing.
				tutorialName = '<ssf:escapeJavaScript>${ss_tutorial_name}</ssf:escapeJavaScript>';

				// Is the flash plugin installed?
				if ( !swfobject.hasFlashPlayerVersion( REQUIRED_MIN_FLASH_PLUGIN_VERSION ) )
				{
					var		span;
					var		div;

					// No
					// Display the text that tells the user how to install the plugin.
					div = document.getElementById( 'replaceMe' );
					div.style.width = '65%';
					span = document.getElementById( 'altContent' );
					span.style.display = '';
				}
				else
				{
					// Call the playTutorial() method after the dom is loaded.  We do this because we need to wait for
					// the dom to be loaded before we create the flash control.
					fn = function()
					{
						playTutorial( tutorialName );
					}
			      	swfobject.addDomLoadEvent( fn );
				}
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

				self.focus();

				// Is the flash plugin installed?
				if ( !swfobject.hasFlashPlayerVersion( REQUIRED_MIN_FLASH_PLUGIN_VERSION ) )
				{
					// Nothing to do.
					return;
				}

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

				// Create a new flash control.  On IE we can just reuse the swfobject we already have but on Firefox we can't.
				// As a result, we just create a new flash control every time.
		      	{
		        	var att;
		        	var par;
		        	var id;

		        	att = { data:m_selectedTutorial.url, width:'1024', height:'768' };
		        	par = { quality:'best', bgcolor:'#000000', allowfullscreen:'true', scale:'showall', allowscriptaccess:'always', flashvars:'autostart=false&thumb=FirstFrame.png&thumbscale=45&color=0x000000,0x000000' };
		        	id = 'replaceMe';

		        	// Create an <object> element for the flash control.
				    m_swfObj = swfobject.createSWF( att, par, id );
		      	};
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
			}// end unhighlightTutorial()


			/**
			 * View the tutorial script in the given format.
			 */
			function viewScript( format )
			{
				var		url;
				var		winHeight;
				var		winWidth;
				var		lang;

				lang = '${teamingLang}';
				url = 'http://www.novell.com';

				// If the language is not English we need to add the language code to the url.
				if ( 'en' != lang )
				{
					// Convert the language code into the expected format.
					if ( lang == 'de' || lang == 'fr' || lang == 'it' || lang == 'es' || lang == 'pl' || lang == 'nl' || lang == 'hu' || lang == 'ru' )
						lang += '-' + lang;
					url += '/' + lang;
				}

				if ( format == 'html' )
				{
					if ( m_selectedTutorial != null )
					{
						// Start the user at the begining of the script for the selected video.
						url += '/documentation/teaming2/team20_tutorials/data/' + m_selectedTutorial.scriptUrl;
					}
					else
					{
						// Since a tutorial is not selected open the script and start at the beginning.
						url += '/documentation/teaming2/team20_tutorials/data/biuhels.html';
					}
				}
				else
				{
					// What is the url to the pdf file?
					url += '/documentation/teaming2/pdfdoc/team20_tutorials/team20_tutorials.pdf#page=1';
				}

				winHeight = 720;
				winWidth = 720; 
				m_playTutorialWnd = window.open(
											url,
											'ViewTutorialScriptWindow',
											'height=' + winHeight + ',resizable,scrollbars,width=' + winWidth );
			}// end viewScript()
		</script>
	</head>

	<body onload="handleOnload()">
		<table width="100%">
			<tr>
				<td valign="middle" colspan="2" nowrap>
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
					<!-- The following <div> will be replaced with a flash player object if -->
					<!-- the correct flash player is installed.  Otherwise, the content in the div will be displayed -->
					<!-- telling the user they need to install/upgrade the flash plugin. -->
					<div id="replaceMe" >
						<div class="sharedMessage" id="altContent" style="display: none">
							<b class="sharedMessagebox">
								<b class="sharedMessagebox1">
									<b></b>
								</b>
								<b class="sharedMessagebox2">
									<b></b>
								</b>
								<b class="sharedMessagebox3"></b>
								<b class="sharedMessagebox4"></b>
								<b class="sharedMessagebox5"></b>
							</b>
							<div class="sharedMessagebox_content">
								<img	border="0"
										align="absmiddle"
										src="<html:imagesPath/>pics/warning.gif" />
								<div class="sharedMessageText">
									<ssf:nlt tag="playtutorial.pluginrequired" />
									<a href="http://www.adobe.com/go/getflashplayer">www.adobe.com/go/getflashplayer</a>
								</div>
							</div>
							<b class="sharedMessagebox">
								<b class="sharedMessagebox5"></b>
								<b class="sharedMessagebox4"></b>
								<b class="sharedMessagebox3"></b>
								<b class="sharedMessagebox2">
									<b></b>
								</b>
								<b class="sharedMessagebox1">
									<b></b>
								</b>
							</b>
						</div>
					</div>
				</td>
			</tr>
			<tr>
				<td>
				</td>
				<td valign="middle" nowrap>
					<div style="padding-top: 1em;">
						<span><ssf:nlt tag="playtutorial.viewscript" />&nbsp;</span>
						<a	href="#"
							onclick="viewScript( 'html' )"
							title="<ssf:nlt tag="playtutorial.alt.viewscripthtml" />">
							<span><ssf:nlt tag="playtutorial.viewscript.html" /></span>
						</a>
						<span>&nbsp;</span>
						<a	href="#"
							onclick="viewScript( 'pdf' )"
							title="<ssf:nlt tag="playtutorial.alt.viewscriptpdf" />">
							<span><ssf:nlt tag="playtutorial.viewscript.pdf" /></span>
						</a>
					</div>
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

