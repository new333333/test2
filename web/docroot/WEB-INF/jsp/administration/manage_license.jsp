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
<%@ page import="org.kablink.teaming.web.util.GwtUIHelper" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.manage.license") %>' scope="request"/>
<c:set var="helpGuideName" value="install" scope="request" />
<c:set var="helpPageId" value="license" scope="request" />
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
			return true;
	<%	} %>
	
	}// end handleCloseBtn()
</script>

<script type="text/JavaScript">

	// Schedule checkForErrors() to be called after the page loads.
	ss_createOnLoadObj( 'licence_check_for_errors', checkForErrors );
	
	/**
	 * This function is an onload event handler that will check to see if an error.
	 * occurred parsing the license files.
	 */
	function checkForErrors()
	{
		// Was there an error?
		<c:if test='${! empty ssLicenseException}'>
			var		msg;

			// Yes, tell the user about the problem.
			msg = '<ssf:escapeJavaScript>${ssLicenseException}</ssf:escapeJavaScript>';
			alert( msg );
		</c:if>
	}// end checkForErrors()
	
</script>

<div class="ss_pseudoPortal">

<div class="ss_style ss_portlet">
<ssf:form titleTag="administration.manage.license">

  <c:if test='${! empty ssLicenseException}'>
    <div class="error"><span class="error">${ssLicenseException}</span></div>
  </c:if>
  
  <c:if test="${!empty ssLicense}">
  <span class="ss_largerprint ss_bold"><ssf:nlt tag="license.current"/></span>
  
  <div>
    <br/>
  <span><ssf:nlt tag="license.key.uid"/>&nbsp;${ssLicenseKey}</span>
    <br/>
  <span><ssf:nlt tag="license.key.issued"/>&nbsp;${ssLicenseIssued}</span>
    <br/>
  <span><ssf:nlt tag="license.key.issuer"/>&nbsp;${ssLicenseIssuer}</span>
    <br/>
    <br/>
  <span><ssf:nlt tag="license.product.id"/>&nbsp;${ssLicenseProductID}</span>
    <br/>
  <span><ssf:nlt tag="license.product.title"/>&nbsp;${ssLicenseProductTitle}</span>
    <br/>
  <span><ssf:nlt tag="license.product.version"/>&nbsp;${ssLicenseProductVersion}</span>
    <br/>
    <br/>
  <span><ssf:nlt tag="license.effective"/>&nbsp;${ssLicenseEffective}</span>
    <br/>
    <br/>

  <!-- Allowed number of registered users -->
  <ssf:nlt tag="license.users.registered"/>&nbsp;
  <c:if test="${ssLicenseUsers < 0}">
  	<span><ssf:nlt tag="license.users.registered.unlimited"/></span>
  </c:if>
  <c:if test="${ssLicenseUsers >= 0}">
  	${ssLicenseUsers}
  </c:if>
    <br/>

  <!-- Allowed number of external users -->
  <ssf:nlt tag="license.users.external"/>&nbsp;
  <c:if test="${ssLicenseExternalUsers < 0}">
  	<ssf:nlt tag="license.users.external.unlimited"/>
  </c:if>
  <c:if test="${ssLicenseExternalUsers >= 0}">
  	<ssf:nlt tag="license.users.external"/> ${ssLicenseExternalUsers}
  </c:if>
    <br/>
    <br/>

  <c:if test="${!empty ssLicenseOptionsList}">
	<span><ssf:nlt tag="license.options"/><span>
	 <br/>
	<c:forEach var="option" items="${ssLicenseOptionsList}">
 	 <span style="padding-left:20px;">${option}</span>
	 <br/>
  	</c:forEach>
  	<br/>
  </c:if>
  <c:if test="${!empty ssLicenseExternalAccessList}">
	<span><ssf:nlt tag="license.externalaccess"/></span>
	 <br/>
	<c:forEach var="prop" items="${ssLicenseExternalAccessList}">
 	 <span style="padding-left:20px;">${prop}</span>
	 <br/>
  	</c:forEach>
  	<br/>
  </c:if>
  <c:if test="${!empty ssLicenseContact}">
    <span><ssf:nlt tag="license.contact"/> ${ssLicenseContact}</span>
    <br/>
  </c:if>	
  </div>
  </c:if>
  
  <div class="ss_style" style="margin-bottom: 100px; border-top: 1px solid #e0e0e0; padding-top: 10px; margin-top: 25px;">
<c:if test='${empty ssLicenseException}'>
  <ssf:ifLicenseExpired><div class="ss_warning ss_license_warning"><span class="ss_warning ss_license_warning"><ssf:nlt tag="license.expired.warning"/></span></div></ssf:ifLicenseExpired>
  <ssf:ifLicenseExpired invert="true">
    <ssf:ifLicenseExpired inThisManyDays="30"><div class="ss_warning ss_license_warning"><span class="ss_warning ss_license_warning"><ssf:nlt tag="license.expiring.soon.warning"/></span></div></ssf:ifLicenseExpired>
    <ssf:ifLicenseExpired inThisManyDays="30" invert="true">  
	  <ssf:ifLicenseOutOfCompliance><div class="ss_warning ss_license_warning"><span class="ss_warning ss_license_warning"><ssf:nlt tag="license.out.of.compliance"/></span></div></ssf:ifLicenseOutOfCompliance>
    </ssf:ifLicenseExpired>
  </ssf:ifLicenseExpired>
</c:if>
	<form class="ss_portlet_style ss_form" id="${ssNamespace}_btnForm" 
	  name="${ssNamespace}_btnForm" method="post"  
	  action="<ssf:url action="manage_license" actionUrl="true"/>">
		<input type="submit" class="ss_submit" name="updateBtn" value="<ssf:nlt tag="button.update_license"/>">
		<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
		  onClick="return handleCloseBtn();"/>
	</form>
  </div>
</ssf:form>
</div>

</div>
</body>
</html>