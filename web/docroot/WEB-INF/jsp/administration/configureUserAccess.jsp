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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ page import="org.kablink.teaming.web.util.GwtUIHelper" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.configure_ldap") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">

<script type="text/javascript">
	var m_guestRightsMsg			= null;
	var m_guestHasSufficientRights	= null;

	ss_createOnLoadObj( 'configureUserAccess', onLoadEventHandler );
	
	/**
	 * 
	 */
	function handleCloseBtn()
	{
	<% 	if ( GwtUIHelper.isGwtUIActive( request ) ) { %>
			// Tell the Teaming GWT ui to close the administration content panel.
			if ( window.parent.ss_closeAdministrationContentPanel ) {
				window.parent.ss_closeAdministrationContentPanel();
			} else {
				ss_cancelButtonCloseWindow();
			}

			return false;
	<% 	}
		else { %>
			ss_cancelButtonCloseWindow();
			return false;
	<%	} %>
		
	}// end handleCloseBtn()

	/**
	 * This function gets called when the page is loaded.
	 */
	function onLoadEventHandler()
	{
		var form;
		
		// Load the string that we may use to tell the user they need to give the guest user "participant" rights to the
		// "personal workspaces" folder in order for the "allow people to create their own accounts" to work.
		m_guestRightsMsg = '<ssf:escapeJavaScript><ssf:nlt tag="administration.configure.userAccess.necessaryRightsForSelfReg" /></ssf:escapeJavaScript>';
		
		// Get the value that tells us whether the guest user has sufficient rights to create a new account.
		m_guestHasSufficientRights = false;
		<c:if test="${guestUserHasAddRightsToProfileBinder}">
			m_guestHasSufficientRights = true;
		</c:if>
		
		form = document.forms[0];
		form.onsubmit = onSubmitEventHandler;

	}// end onLoadEventHandler()

	
	/**
	 * This function gets called when the user clicks the "apply" button.
	 * If the user has enabled the "Allow people to create their own accounts" and the guest user does not
	 * have sufficient rights to create a new account we will pop up a message telling the user what they
	 * need to do.
	 */
	function onSubmitEventHandler()
	{
		 var	ckbox;

		 // Did the user enable the "Allow people to create their own accounts" setting?
		 ckbox = document.getElementById( 'allowSelfRegistration' );
		 if ( ckbox != null && ckbox.checked )
		 {
			 // Yes, does the guest user have sufficient rights to create a new account?
			 if ( m_guestHasSufficientRights == false )
			 {
				 // No, tell the user what they need to do so the "Allow people to create their own accounts" will work.
				 alert( m_guestRightsMsg );
			 }
		 }
	
		 return true;
	}// end onSubmitEventHandler()

</script>

<div class="ss_pseudoPortal">
<div class="ss_style ss_portlet">
<c:set var="licensedEdition" value="<%= org.kablink.teaming.util.ReleaseInfo.isLicenseRequiredEdition() %>"/>
<c:set var="titleTag" value='<%= NLT.get("administration.configure_userAccess") %>'/>
<c:if test="${licensedEdition}">
  <c:set var="titleTag" value='<%= NLT.get("administration.configure_userAccessOnly", NLT.get("administration.configure_userAccess")) %>'/>
</c:if>
<ssf:form title="${titleTag}" >

	<c:if test="${!empty ssException}">
	<span class="ss_largerprint"><ssf:nlt tag="administration.errors"/> (<c:out value="${ssException}"/>)</span><br>
	</c:if>

	<form class="ss_style ss_form" name="${renderResponse.namespace}fm" method="post" 
	  action="<ssf:url action="configure_user_access" actionUrl="true"/>">
		<div class="ss_buttonBarRight">
		<br/>
		<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
			<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
				  onClick="return handleCloseBtn();"/>
		</div>
	  <div>
	    <div>
	    	<c:set var="showGuestOption" value="<%= Boolean.FALSE %>"/>
	    	<c:set var="openEdition" value="<%= !org.kablink.teaming.util.ReleaseInfo.isLicenseRequiredEdition() %>"/>
	    	<c:if test="${openEdition}">
	    		<c:set var="showGuestOption" value="<%= Boolean.TRUE %>"/>
	    	</c:if>
	    	<c:if test="${!openEdition}">
		    	<ssf:ifAuthorizedByLicense featureName="com.novell.teaming.GuestAccess">
		    		<c:set var="showGuestOption" value="<%= Boolean.TRUE %>"/>
		    	</ssf:ifAuthorizedByLicense>
	    	</c:if>
	    	<c:if test="${showGuestOption}">
				<input type="checkbox" id="allowAnonymous" name="allowAnonymous" <c:if test="${ssAuthenticationConfig.allowAnonymousAccess}">checked</c:if>/>
					<label for="allowAnonymous"><span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.config.allowAnonymous"/></span><br/></label>
			</c:if>
	    	<c:if test="${!showGuestOption}">
	    	  <div>
	    	    <span><ssf:nlt tag="administration.configure_userAccessNotLicensed"/></span>
	    	  </div>
			</c:if>

			<!-- Are we running the open version of Teaming? -->
			<c:if test="${openEdition}">
				<!-- Yes, add the "Allow people to create their own accounts" option. -->
				<input type="checkbox" id="allowSelfRegistration" name="allowSelfRegistration" <c:if test="${ssAuthenticationConfig.allowSelfRegistration}">checked</c:if>/>
				<label for="allowSelfRegistration"><span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.config.allowSelfRegistration"/></span></label>
			</c:if>

			<br/>
			<br/>
			<br/>
	    </div>
	  </div>
	<br/>
	<div class="ss_buttonBarLeft">
		<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
		<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
			  onClick="return handleCloseBtn();"/>
	</div>
	</form>
</ssf:form>
</div>
</div>
</body>
</html>