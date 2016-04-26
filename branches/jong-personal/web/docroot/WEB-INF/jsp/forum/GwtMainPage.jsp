<%
/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ page import="java.util.Locale" %>
<%@ page import="org.kablink.teaming.util.ReleaseInfo" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value="${ssProductTitle}" scope="request"/>
<c:set var="ss_skip_head_close" value="true" scope="request"/>
<c:set var="ss_GWT_main_page" value="true" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/common/initializeGWT.jsp" %>
<jsp:include page="/WEB-INF/jsp/sidebars/sidebar_appConfig.jsp" /> 

<% // The following javascript files are needed because the enhanced view widget on %>
<% // a landing page may display a calendar. %>
	<script type="text/javascript">
 		ss_loadJsFile( ss_rootPath, "js/common/ss_calendar.js" );
	</script>
	<script type="text/javascript" src="<html:rootPath/>js/datepicker/CalendarPopup.js"></script>
	<script type="text/javascript" src="<html:rootPath/>js/common/AnchorPosition.js"></script>
	<script type="text/javascript" src="<html:rootPath/>js/common/PopupWindow.js"></script>
	<script type="text/javascript" src="<html:rootPath/>js/datepicker/date.js"></script>
<% //------------------------------------------------------------------------------ %>

	<script type="text/javascript" src="<html:tinyMcePath/>tiny_mce.js?<%= ReleaseInfo.getContentVersion() %>"></script>

	<% // The GWT 'Tour' facility requires we bring in the hopscotch %>
	<% // JavaScript directly.                                       %>
	<script type="text/javascript" src="<html:rootPath/>js/gwt/gwtteaming/hopscotch-0.1.js"></script>

	<c:set var="gwtPage" value="main" scope="request"/>	
	<%@ include file="/WEB-INF/jsp/common/GwtRequestInfo.jsp" %>
    <%@ include file="/WEB-INF/jsp/common/tinymce_translations.jsp" %>
	
	<script type="text/javascript" language="javascript">
		// Are we being loaded in an iFrame?
		if ( self != window.top )
		{
			// GroupWise may run Vibe in an iframe.  That is ok.
			// Are we running inside GroupWise?
			if ( ${vibeProduct} != 1 && ${sessionCaptive} == false )
			{
				// No
				alert( '<ssf:nlt tag="error.webappCannotBeInIFrame"><ssf:param name="value" value="${productName}" /></ssf:nlt>' );
				window.top.location = self.location;
			}
		}

		// Initialize tinyMCE.  This must be done here so that tinyMCE can bring in the other
		// necessary js files. 
		tinyMCE.init(
		{
			paste_postprocess: function(pi,o){o.node.innerHTML=TinyMCEWebKitPasteFixup("paste_postprocess",o.node.innerHTML);},
			mode : "none",
			editor_selector : "mceEditable_standard",
			theme : "advanced", 
			onpageload : "ss_addLanguageChanges",
		    <c:choose><%--
		    --%><c:when test="${ssUser.locale == 'zh_TW'}">language: 'tw',</c:when><%--
		    --%><c:otherwise>language: '${ssUser.locale.language}',</c:otherwise><%--
			--%></c:choose>
			content_css : "<html:rootPath/>css/view_css_tinymce_editor.css",
			relative_urls : false, 
			remove_script_host : false,
			// document_base_url : "<ssf:fileUrl entity="${ssDefinitionEntry}" baseUrl="true"/>",
			width : "100%",
			accessibility_warnings : true,
			accessibility_focus : true,
			entities :  "39,#39,34,quot,38,amp,60,lt,62,gt",
			gecko_spellcheck : true,
			plugins : "pdw,table,preelementfix,ss_addimage,preview,paste,ss_wikilink,ss_youtube", 
			theme_advanced_toolbar_location : "top",
			theme_advanced_toolbar_align : "left", 
			theme_advanced_statusbar_location : "bottom", 
			theme_advanced_resizing: true, 
			convert_fonts_to_spans: true,
			theme_advanced_styles: "8px=ss_size_8px;9px=ss_size_9px;10px=ss_size_10px;11px=ss_size_11px;12px=ss_size_12px;13px=ss_size_13px;14px=ss_size_14px;15px=ss_size_15px;16px=ss_size_16px;18px=ss_size_18px;20px=ss_size_20px;24px=ss_size_24px;28px=ss_size_28px;32px=ss_size_32px",
			theme_advanced_buttons1_add: "",
			theme_advanced_buttons2_add: "|,ss_addimage,ss_wikilink,ss_youtube",
			theme_advanced_path: false,
			pdw_toggle_on : 1,
			pdw_toggle_toolbars : "2",
			// pdw_element_id : "ss_htmleditor_${element_name}",
			theme_advanced_resizing_use_cookie : true
		} );

		var ss_workareaIframeMinOffset = 12;
		function ss_setWorkareaIframeSize() {
			//ss_debug("**** "+ss_debugTrace());
			//If possible, try to directly set the size of the iframe
			//This may fail if the iframe is showing something in another domain
			//If so, the alternate method (via ss_communicationFrame) is used to set the window height
			try {
				var iframeDiv = document.getElementById('contentFlowPanel')
				var startOfContent = ss_getObjectTop(iframeDiv);
				var windowHeight = ss_getWindowHeight();
				var iframeMinimum = parseInt(windowHeight - startOfContent - ss_workareaIframeMinOffset);
				if (iframeMinimum < 100) iframeMinimum = 100;
				if (window.frames['gwtContentIframe'] != null) {
					var iframeHeight = window.gwtContentIframe.document.body.scrollHeight;
					if (parseInt(iframeDiv.style.height) != parseInt(iframeMinimum)) {
						//ss_debug("   ss_setWorkareaIframeSize: Setting iframe height to "+parseInt(iframeMinimum) + "px")
						iframeDiv.style.height = parseInt(iframeMinimum) + "px";
					}
				}
				//Also resize the entry iframe if needed
				ss_setEntryPopupIframeSize();
			} catch(e) {
				//alert('Error during frame resizing: ' + e)
			}
		}

		//Routine to set the size and position of the entry popup frame in the "newpage" mode
		var ss_entryPopupBottomMargin = 46;
		function ss_setEntryPopupIframeSize() {
			//ss_debug("**** "+ss_debugTrace());
			if (ss_isGwtUIActive && ss_getUserDisplayStyle() == "newpage") {
				try {
					var PANEL_PADDING = 8;
					var contentIframe = document.getElementById('contentFlowPanel');
					var startOfContent = ss_getObjectTop(contentIframe);
					var entryIframeBoxDiv = document.getElementById('ss_iframe_box_div');
					var entryIframeDiv = document.getElementById('ss_showentrydiv');
					var entryIframeFrame = document.getElementById('ss_showentryframe');
					if (entryIframeDiv == null || entryIframeFrame == null) return;
					var top   = (Number(ss_getObjectTop(contentIframe)) + PANEL_PADDING);
					var left  =  Number(ss_getObjectLeft(contentIframe));
					var width = (Number(contentIframe.offsetWidth)      - PANEL_PADDING);
					ss_setObjectTop(  entryIframeDiv,    top  );
					ss_setObjectLeft( entryIframeDiv,    left );
					ss_setObjectWidth(entryIframeBoxDiv, width);
					ss_setObjectWidth(entryIframeFrame,  width);
					var windowHeight = parseInt(ss_getWindowHeight());
					var iframeMinimum = (parseInt(windowHeight - startOfContent - ss_entryPopupBottomMargin) - PANEL_PADDING);
					if (iframeMinimum < 100) iframeMinimum = 100;
					if (window.frames['ss_showentryframe'] != null) {
						if (parseInt(entryIframeFrame.style.height) != parseInt(iframeMinimum)) {
							//ss_debug("   ss_showentryframe height: "+entryIframeFrame.style.height + ", set to: " + iframeMinimum)
							entryIframeFrame.style.height = parseInt(iframeMinimum) + "px";
						}
					}

					// Do we have an <IFRAME> within an <IFRAME> and
					// the inner <IFRAME> has content (i.e., it's not
					// null.html)?
					var popupFrame = window.frames['ss_showentryframe'].document.getElementById('ss_showpopupframe');
					if ((null != popupFrame) && (0 >= popupFrame.src.indexOf('null.html'))) {
						// Yes!  Scolling will be managed by the inner
						// <IFRAME>.  Turn it off on the host
						// <IFRAME>'s <BODY> tag.
						var bodies = window.frames['ss_showentryframe'].document.getElementsByTagName('body');
						for (var i = 0; i < bodies.length; i += 1) {
							bodies[i].style.overflow = "hidden";
						}
					}
				} catch(e) {
					//alert('Error during frame resizing: ' + e)
				}
			} else {
				ss_setCurrentIframeHeight();
			}
		}

		//Routine to hide the content frame if necessary
		function ss_hideContentFrame() {
			if (ss_isGwtUIActive && ss_getUserDisplayStyle() == "newpage") {
				var contentIframe = document.getElementById('contentControl');
				if (contentIframe != null) {
					contentIframe.style.visibility = "hidden";
				}
			}
		}

		// ss_wikiLinkUrl is used with the tinyMCE editor plugin that lets you insert a link to a Teaming page.
		var ss_wikiLinkUrl = "<ssf:url adapter="true" actionUrl="true" portletName="ss_forum" action="__ajax_request">
			  					<ssf:param name="operation" value="wikilink_form" />
			  					<ssf:param name="binderId" value="${wikiLinkBinderId}" />
			  					<ssf:param name="originalBinderId" value="${wikiLinkBinderId}" />
		    				   </ssf:url>";

		// ss_youTubeUrl and ss_invalidYouTubeUrl are used with the tinyMCE editor plugin that lets you add a youtube video.
		var ss_youTubeUrl = "<ssf:url adapter="true" actionUrl="true" portletName="ss_forum" action="__ajax_request">
			  					<ssf:param name="operation" value="youtube_form" />
		    				  </ssf:url>";
		var ss_invalidYouTubeUrl = "<%= NLT.get("__youTubeInvalidUrl").replaceAll("\"", "\\\\\"") %>";

		// The following variables are used by the tinyMCE editor plugin that lets you
		// upload an image and insert it into the tinyMCE editor.
		var ss_imageUploadError1 = "<ssf:nlt tag="imageUpload.badFile"/>"
		var ss_imageUploadUrl = "<ssf:url adapter="true" actionUrl="true" portletName="ss_forum" action="__ajax_request">
			  						<ssf:param name="operation" value="upload_image_file" />
		    					 </ssf:url>";

		/*
		 * Implementation method for GwtClientHelper.jsEvalString().
		 *
		 * Note:  The code contained here was originally inside that
		 *    native method but GWT's obfuscation used for our
		 *    production compile broke it.
		 */
		function jsEvalStringImpl(url, jsString) {
			// Setup an object to pass through the URL...
			var hrefObj = {href: url};
			
			// ...patch the JavaScript string...
			jsString = jsString.replace("this", "hrefObj");
			jsString = jsString.replace("return false;", "");
			jsString = ("window.top.gwtContentIframe." + jsString);
			
			// ...and evaluate it.
			eval(jsString);
		}

		// -- NOTE -- The following 3 ss_logoff*() functions were moved here from view_workarea_navbar.jsp
		
		/**
		 * 
		 */
		function ss_logoff() {
			var x = '<%= org.kablink.teaming.web.util.WebUrlUtil.getSsoProxyLogoffUrl(request) %>';
			if(x == null || x == "") {
				var logoutForm;
				var y = '<ssf:escapeJavaScript><%= org.kablink.teaming.web.util.WebUrlUtil.getServletRootURL(request) + org.kablink.teaming.web.WebKeys.SERVLET_LOGOUT %></ssf:escapeJavaScript>';

				//~JW:  (y);
				// Get the logout form.  We use a form so the logout request can be made with
				// a "post" instead of a "get".  This prevents logout spoofing.
				logoutForm = document.getElementById( 'logoutForm' );
				if ( logoutForm != null )
				{
					logoutForm.action = y;
					logoutForm.submit();
				}
				else
				{
					// This should never happen.
					alert( 'Could not find the logout form.' );
				}
			} else {
				//alert (x);
				var y = '<ssf:escapeJavaScript><%= org.kablink.teaming.web.util.WebUrlUtil.getServletRootURL(request) + org.kablink.teaming.web.WebKeys.SERVLET_LOGOUT %></ssf:escapeJavaScript>';
				ss_logoff_from_teaming_then_sso(y);
			}
		}
		
		/**
		 * 
		 */
		function ss_logoff_from_teaming_then_sso(logoutURL) {
			callbackRoutine = ss_logoff_from_sso
			var x;
		
			if (window.XMLHttpRequest) {
			x = new XMLHttpRequest();
			} else if (window.ActiveXObject) {
			x = new ActiveXObject("Microsoft.XMLHTTP");
			}
			
			x.open("POST", logoutURL, true);
			
			x.onreadystatechange = function() {
				if (x.readyState != 4) {
					return;
				}
				if (x.status == 200) {
					callbackRoutine(x.responseText)        	
				} else {		
					callbackRoutine(x.statusText)
				}
			}
			x.send(null);
			delete x;
		}
		
		/**
		 * 
		 */
		function ss_logoff_from_sso(s) {
			// Are we running the new GWT ui?
			if ( ss_isGwtUIActive )
			{
				// Yes, update the top window's href.
				window.top.location.href = '<%= org.kablink.teaming.web.util.WebUrlUtil.getSsoProxyLogoffUrl(request) %>';
			}
			else
				self.location.href='<%= org.kablink.teaming.web.util.WebUrlUtil.getSsoProxyLogoffUrl(request) %>';
		}
		
		// The following are used to persist task information across
		// refresh cycles.
		var ss_newTaskDisposition	= "";
		var ss_selectedTaskId		= "";
		var ss_showTaskGraphs		= false;
		
		// The following is used by the binder views to communicate
		// with the entry viewer about whether it should show the
		// next/previous buttons.
		var ss_allowNextPrevOnView = false;
		
		// The following is used to store the Window opened to a user
		// to authenticate to a Cloud Folder service.
		var ss_cloudFolderAuthenticationPopup = null;
	</script>
	
	<script type="text/javascript" src="<html:rootPath/>js/common/ss_common.js?<%= ReleaseInfo.getContentVersion() %>"></script>
	<script type="text/javascript" src="<html:rootPath/>js/forum/view_iframe.js?<%= ReleaseInfo.getContentVersion() %>"></script>
	<script type="text/javascript" language="javascript" src="<html:rootPath />js/gwt/gwtteaming/gwtteaming.nocache.js?<%= ReleaseInfo.getContentVersion() %>"></script>
	
  </head>

  <body>
    <c:set var="gwtUI" value="true" scope="request"/>

    <!-- Included for GWT based history support. -->
    <iframe src="javascript:''" id="__gwt_historyFrame" style="position:absolute;width:0;height:0;border:0"></iframe>

	<!-- This div will hold the content of the main Teaming page. -->
	<div id="gwtMainPageDiv">
	</div>

    <%@ include file="/WEB-INF/jsp/dashboard/portletsupport.jsp" %>
    
    <!-- This form is used for logging out. -->
    <!-- The value of the action attribute will be filled in at runtime. -->
	<form name="logoutForm" id="logoutForm" method="post" >
	</form> 
	
	<!--  This form is used for logging in. -->
	<form name="loginFormName" id="loginFormId" method="post" action="<c:out value="${ss_loginPostUrl}" escapeXml="true"/>" style="display: none;" accept-charset="UTF-8">
		<table cellspacing="4" class="dlgContent" style="margin: 10px;">
			<colgroup>
				<col>
			</colgroup>
			<tbody>
				<tr>
					<td><span id="userIdLabel"></span></td>
					<td>
						<input type="text" size="20" id="j_usernameId" name="j_username" class="gwt-TextBox">
					</td>
				</tr>
				<tr>
					<td><span id="pwdLabel"></span></td>
					<td>
						<input type="password" size="20" id="j_passwordId" name="j_password" class="gwt-PasswordTextBox">
					</td>
				</tr>
				<tr>
					<td align="right"></td>
					<td>
						<div class="gwt-Label margintop3 loginDlgKeyShieldPanel" style="display: none; width: 300px; white-space: normal !important;" id="loginDlgKeyShieldPanel">
							<span class="gwt-InlineLabel loginDlgKeyShieldErrorMessage" id="loginDlgKeyShieldErrorMessage"></span>
							<div class="gwt-Label loginDlgKeyShieldRefererPanel" id="loginDlgKeyShieldRefererPanel">
								<a class="gwt-Anchor loginDlgKeyShieldRefererLink" href="#" target="_top" id="loginDlgKeyShieldRefererLink"><ssf:nlt tag="loginDlg.keyShieldRefererLink"/></a>
							</div>
						</div>
					</td>
				</tr>
				<tr>
					<td align="right"></td>
					<td>
						<div class="gwt-Label loginFailedMsg" style="display: none; width: 250px;" id="loginFailedMsgDiv"></div>
					</td>
				</tr>
				<tr>
					<td align="right"></td>
					<td>
						<div class="gwt-Label loginAuthenticatingMsg" style="display: none;" id="authenticatingDiv"></div>
					</td>
				</tr>
			</tbody>
		</table>
		
		<!-- If needed, show the Text Verification controls. -->
		<%@ include file="/WEB-INF/jsp/definition_elements/textVerification.jsp" %>

		<table cellspacing="4" class="dlgContent" style="margin: 10px;">
			<colgroup>
				<col>
			</colgroup>
			<tbody>
				<tr>
					<td>
						<span class="gwt-InlineLabel margintop3 selfRegLink1 selfRegLink2" style="display: none;" id="forgottenPwdSpan"></span>
					</td>
					<td>
						<span class="gwt-InlineLabel margintop3 selfRegLink1 selfRegLink2" style="display: none;" id="createNewAccountSpan"></span>
					</td>
				</tr>
			</tbody>
		</table>

		<div class="teamingDlgBoxFooter" id="loginDlgFooterId" style="margin: 0px !important;">
			<button type="submit" class="gwt-Button teamingButton" id="loginOkBtn" ></button>
			<div id="termsBlock">
				<div id="termsContainer" class="gwt-CheckBox" style="display:none;float:left;">
					<div>
						<input id="acceptTermsCheckBox" type="checkbox" style="float:left;"/>
						<label id="acceptTermsAnchor" style="position:absolute; margin-top:-2px;">I accept <span style="cursor:pointer; color:#135c8f; text-decoration:underline;">terms and conditions</span></label>
					</div>
				</div>
				<button type="button" class="teamingButton" style="display: none;color:white;" id="loginRegisterBtn"></button>
			</div>
			<button type="button" class="gwt-Button teamingButton" style="display: none;" id="resetPwdBtn"></button>
			<button type="button" class="gwt-Button teamingButton" style="" id="loginCancelBtn"></button>
		</div>

	</form>

  </body>
</html>
