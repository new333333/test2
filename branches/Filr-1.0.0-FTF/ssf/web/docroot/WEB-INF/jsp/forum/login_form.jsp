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

<%@ page import="org.kablink.teaming.web.util.GwtUIHelper" %>

<% if ( GwtUIHelper.isGwtUIActive( request ) ) { %>
	<script type="text/javascript">
		// Tell the Teaming GWT ui to handle login.
		if ( window.parent.ss_handlePageWithGWT )
			window.parent.ss_handlePageWithGWT( 'login-page' );
	</script>
<% }
   else { %>
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%  
String userAgents = org.kablink.teaming.util.SPropsUtil.getString("mobile.userAgents", "");
String tabletUserAgents = org.kablink.teaming.util.SPropsUtil.getString("tablet.userAgentRegexp", "");
Boolean testForAndroid = org.kablink.teaming.util.SPropsUtil.getBoolean("tablet.useDefaultTestForAndroidTablets", false);
boolean isMobile = (org.kablink.util.BrowserSniffer.is_mobile(request, userAgents) && 
	!org.kablink.util.BrowserSniffer.is_tablet(request, tabletUserAgents, testForAndroid));  
%>
<c:if test="${!empty ssAddUserAllowed}">
	<script type="text/javascript">
		/**
		 * This function gets called when the user clicks on the "Create new account" link.
		 * We will invoke the "Add User" page.
		 */
		function invokeCreateNewAccountPage()
		{
			var url = '<ssf:escapeJavaScript>${ssAddUserUrl}</ssf:escapeJavaScript>';
			ss_toolbarPopupUrl( url, '_blank', '', '' );
		}// end invokeCreateNewAccountPage()
	</script>
</c:if>

<script type="text/javascript">
	/**
	 * This function will display the details about a login failure.
	 */
	function showLoginFailureDetails()
	{
		var div;

		div = document.getElementById( 'login-failure-details' );
		if ( div != null )
			div.style.display = '';
	}// end showLoginFailureDetails()
</script>

<!-- Set some variables depending on whether we are running the Open or Enterprise version of Teaming. -->
<c:set var="topImage" value="login_novell_top.png" scope="request" />
<c:set var="copyrightNotice" value="login.copyright.Novell" scope="request" />
<c:set var="openEdition" value="<%= !org.kablink.teaming.util.ReleaseInfo.isLicenseRequiredEdition() %>"/>
<c:if test="${openEdition}">
	<c:set var="topImage" value="login_kablink_top.png" scope="request" />
	<c:set var="copyrightNotice" value="login.copyright.none" scope="request" />
</c:if>

<form name="loginForm" id="loginForm" method="post" action="${ss_loginPostUrl}" style="display:inline;"
  autocomplete="off">
	<table id="idLoginTable"
		   border="0"
		   cellpadding="3"
		   cellspacing="0"
		   style="margin-top: 5em; margin-bottom: 5em; width:27.5em; border: 1px solid black; background: #eceeec url(<html:imagesPath/>pics/Login/login_bk.png) no-repeat bottom right;">
		<!-- Add a row for the Novell/Kablink Teaming 2.0 image to live in. -->
		<tr style="background: #eceeec url(<html:imagesPath/>pics/Login/${topImage}) no-repeat top left;">
			<td align="right" style="color: #404040;">
				<img src="<html:imagesPath/>pics/Login/login_dotzero.gif" width="1" height="75">
				<table border="0" cellpadding="2" width="90%" style="margin: 0px 10px;">
					<tr>
						<td align="right" width="35%">
							<label for="j_username"><span><ssf:nlt tag="login.name"/></span></label>
						</td>
						<td align="left" style="padding: 0px !important;">
		          			<input type="text" class="ss_text_${ss_loginFormStyle}" size="40" name="j_username" id="j_username" style="width: 198px; padding-right: 0px !important; padding-left: 0px !important; margin-right: 0px !important;"/>
						</td>
					</tr>
					<tr>
						<td align="right">
		          			<label for="j_password"><span><ssf:nlt tag="login.password"/></span></label>
						</td>
						<td align="left" style="padding: 0px !important;">
		          			<input class="ss_text_${ss_loginFormStyle}" type="password" size="40" name="j_password" id="j_password" style="width: 198px; padding-right: 0px !important; padding-left: 0px !important; margin-right: 0px !important;"/>
						</td>
					</tr>

<!-- If there was an error logging in, show the error. -->
<c:if test="${!empty ss_loginError}">
		       		<tr>
				 		<td>&nbsp;</td>
		         		<td style="color: red;" colspan="2" align="right">
		           			<div id="errorcode.login.failed"><ssf:nlt tag="errorcode.login.failed"/></div>
	<c:if test="${!empty showLoginFailureDetails}">
							<a href="#"
							   onclick="showLoginFailureDetails();return false;"
							   title="<ssf:nlt tag="login.showLoginFailureDetails" />"
								<span><ssf:nlt tag="login.showLoginFailureDetails" /></span>
							</a>
	</c:if>
		         		</td>
		       		</tr>
	<c:if test="${!empty showLoginFailureDetails}">
					<!-- This row will hold the details of the login failure. -->
					<tr>
						<td style="color: red;" colspan="2">
							<div id="login-failure-details" style="display: none;">${ss_loginError}</div>
						</td>
					</tr>
	</c:if>
</c:if>
					<tr>
						<td align="right">&nbsp;</td>
						<td align="right" style="white-space: nowrap" >
							<div style="margin-top: 1.5em; margin-bottom: .25em;">
			    		  		<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>"/>
			    		  		<input type="reset" style="margin-left: 10px;" class="ss_submit" value="<ssf:nlt tag="button.reset"/>"/>
			<!-- If the user has rights to create a new user, add the "Create new account" link. -->
			<c:if test="${!empty ssAddUserAllowed}">
							  	<a style="margin-left: 2em;"
								   href="#"
								   onclick="invokeCreateNewAccountPage();return false;"
							       title="<ssf:nlt tag="login.createAccount" />" >
									<span><ssf:nlt tag="login.createAccount" /></span>
						  		</a>
			</c:if>
							</div>
						</td>
					</tr>
				</table>
			</td>
		</tr>

		<!-- This row holds the copyright information. -->
		<tr height="10" style="color: white; background-color: #999">
			<td style="font-size: 70%;" align="right">
				<span style="color: white;"><ssf:nlt tag="${copyrightNotice}"/></span>
			</td>
		</tr>
	</table>

<c:if test="${!empty ssUrl}">
	<input type="hidden" name="spring-security-redirect" value="${ssUrl}"/>
</c:if>
</form>

<script type="text/javascript">
	var formObj = self.document.getElementById('loginForm');
	formObj.j_username.focus();
</script>

<% } %>
