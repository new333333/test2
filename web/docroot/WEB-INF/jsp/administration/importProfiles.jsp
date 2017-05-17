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
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.import.profiles") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">

<script type="text/javascript">
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

</script>

<div class="ss_pseudoPortal">

<div class="ss_style ss_portlet">
<ssf:form titleTag="administration.import.profiles">
<script type="text/javascript">

//Create an onload handler that will look for errors passed to this page from a previous request.
ss_createOnLoadObj( 'onloadCheckForErrors', onloadCheckForErrors );

/**
 * This function gets called when the page is loaded.  It checks to see if an error returned from a previous
 * request to import a profile.
 */
function onloadCheckForErrors()
{
	// Did an error happen while importing a profile?
	<c:if test="${!empty ssException}">
		var errMsg;

		// Yes, tell the user about it.
		errMsg = '<ssf:escapeJavaScript>${ssException}</ssf:escapeJavaScript>';
		alert( errMsg );
	</c:if>
	 
}// end onloadCheckForErrors()


function ss_checkForFileSelected() {
	var formObj = document.forms['form1']
	if (formObj.profiles.value == '') {
		alert("<ssf:nlt tag="administration.import.profiles.selectFile"/>")
		return false;
	}
	return true;
}

</script>

	<div style="padding:10px;">
		<br>
		
		<c:if test="${!empty ssException}">
		  <font color="red">
		    <span class="ss_largerprint"><c:out value="${ssException}"/></span>
		  </font>
		  <br/>
		</c:if>
	
		<div style="text-align: left; margin: 0px 10px; border: 0pt none;" 
		  class="wg-tabs margintop3 marginbottom2">
		  <table>
		    <tr>
			  <td>
				  <div class="wg-tab roundcornerSM" >
					  <a href="<ssf:url action="add_profile_entry" actionUrl="true">
					    <ssf:param name="context" value="adminMenu" />
					  </ssf:url>"
					  ><ssf:nlt tag="administration.userAccounts.addUserAccount"/></a>
				  </div>
			  </td>
			  <td>
				  <div class="wg-tab roundcornerSM" >
					  <a href="<ssf:url action="manage_user_accounts" actionUrl="true"/>"
					  ><ssf:nlt tag="administration.userAccounts.disableUserAccount"/></a>
				  </div>
			  </td>
			  <td>
				  <div class="wg-tab roundcornerSM on" >
					  <a href="<ssf:url action="import_profiles" actionUrl="true"/>"
					  ><ssf:nlt tag="administration.import.profiles"/></a>
				  </div>
			  </td>
		    </tr>
		  </table>
		</div>
		<div class="ss_clear"></div>

<div id="manageIndexDiv" style="display:block;" class="wg-tab-content">
<form name="form1" class="ss_style ss_form" method="post" enctype="multipart/form-data" 
		  action="<ssf:url adapter="true" 
			portletName="ss_administration" 
			action="import_profiles" 
			actionUrl="true" ><ssf:param 
		    name="binderId" value="${ssBinder.id}"/></ssf:url>" >

	<label for="profiles"><span class="ss_bold"><ssf:nlt tag="administration.profiles.file"/></span></label>
	<table class="ss_style" border="0" cellpadding="5" cellspacing="0" width="95%">
		<tr><td>
			<input type="file" size="80" class="ss_text" name="profiles" id="profiles"><br>
		</td></tr>
	</table>

	<div class="ss_formBreak margintop3">
		<div class="ss_buttonBarLeft">
	
			<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />"
			  onclick="return ss_checkForFileSelected();"
			/>
			
			<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
			onClick="return handleCloseBtn();"/>
		
		</div>
	</div>
	<sec:csrfInput />

</form>
</div>

<div style="padding-top:20px;">
  <a href="<html:ssfPath/>html/sample_users.txt" target="_blank">
    <span><ssf:nlt tag="administration.import.profiles.viewSample"/></span>
  </a>
</div>

</div>
</ssf:form>
</div>
</div>
</body>
</html>
