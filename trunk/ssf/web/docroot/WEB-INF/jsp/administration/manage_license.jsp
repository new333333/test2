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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<ssf:ifadapter>
<body class="ss_style_body">
<div id="ss_pseudoAdministrationPortalDiv${renderResponse.namespace}">
</ssf:ifadapter>

<div class="ss_portlet_style ss_portlet">
  <c:if test='${! empty ssLicenseException}'>
    <div class="error"><span class="error">${ssLicenseException}</span></div>
  </c:if>
  
  <c:if test="${!empty ssLicense}">
  <span class="ss_largerprint ss_bold"><ssf:nlt tag="license.current"/></span>
  
  <div>
    <br/>
  <span><ssf:nlt tag="license.key.uid"/> ${ssLicenseKey}</span>
    <br/>
  <span><ssf:nlt tag="license.key.issued"/> ${ssLicenseIssued}</span>
    <br/>
  <span><ssf:nlt tag="license.key.issuer"/> ${ssLicenseIssuer}</span>
    <br/>
    <br/>
  <span><ssf:nlt tag="license.product.id"/> ${ssLicenseProductID}</span>
    <br/>
  <span><ssf:nlt tag="license.product.title"/> ${ssLicenseProductTitle}</span>
    <br/>
  <span><ssf:nlt tag="license.product.version"/> ${ssLicenseProductVersion}</span>
    <br/>
    <br/>
  <span><ssf:nlt tag="license.effective"/> ${ssLicenseEffective}</span>
    <br/>
    <br/>
  <c:if test="${ssLicenseUsers < 0}">
  <span><ssf:nlt tag="license.users.registered.unlimited"/></span>
  </c:if>
  <c:if test="${ssLicenseUsers >= 0}">
  <span><ssf:nlt tag="license.users.registered"/> ${ssLicenseUsers}</span>
  </c:if>
    <br/>
  <c:if test="${ssLicenseExternalUsers < 0}">
  <span><ssf:nlt tag="license.users.external.unlimited"/></span>
  </c:if>
  <c:if test="${ssLicenseExternalUsers >= 0}">
  <span><ssf:nlt tag="license.users.external"/> ${ssLicenseExternalUsers}</span>
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
  <span><ssf:nlt tag="license.contact"/> ${ssLicenseContact}</span>
    <br/>
	<hr shade=noshade size=1/>
	
  </div>
  </c:if>
  
  <div class="ss_style">
    <div class="ss_license">
	${ssLicense}
	</div>
<c:if test='${empty ssLicenseException}'>
  <ssf:ifLicenseExpired><div class="ss_warning ss_license_warning"><span class="ss_warning ss_license_warning"><ssf:nlt tag="license.expired.warning"/></span></div></ssf:ifLicenseExpired>
  <ssf:ifLicenseExpired invert="true">
    <ssf:ifLicenseExpired inThisManyDays="30"><div class="ss_warning ss_license_warning"><span class="ss_warning ss_license_warning"><ssf:nlt tag="license.expiring.soon.warning"/></span></div></ssf:ifLicenseExpired>
    <ssf:ifLicenseExpired inThisManyDays="30" invert="true">  
	  <ssf:ifLicenseOutOfCompliance><div class="ss_warning ss_license_warning"><span class="ss_warning ss_license_warning"><ssf:nlt tag="license.out.of.compliance"/></span></div></ssf:ifLicenseOutOfCompliance>
    </ssf:ifLicenseExpired>
  </ssf:ifLicenseExpired>
</c:if>
<br/>
<br/>
	<form class="ss_portlet_style ss_form" id="${ssNamespace}_btnForm" 
	  name="${ssNamespace}_btnForm" method="post"  
	  action="<ssf:url action="manage_license" actionUrl="true"/>">
		<input type="submit" class="ss_submit" name="updateBtn" value="<ssf:nlt tag="button.update_license"/>">
		<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
		  onClick="self.window.close();return false;"/>
	</form>
  </div>
</div>

<ssf:ifadapter>
</div>
<script type="text/javascript">
var ss_parentAdministrationNamespace${renderResponse.namespace} = "";
function ss_administration_showPseudoPortal${renderResponse.namespace}(obj) {
	//See if we are in an iframe inside a portlet 
	var windowName = self.window.name    
	if (windowName.indexOf("ss_administrationIframe") == 0) {
		//We are running inside a portlet iframe; set up for layout changes
		ss_parentAdministrationNamespace${renderResponse.namespace} = windowName.substr("ss_administrationIframe".length)
		ss_createOnResizeObj('ss_setParentAdministrationIframeSize${renderResponse.namespace}', ss_setParentAdministrationIframeSize${renderResponse.namespace});
		ss_createOnLayoutChangeObj('ss_setParentAdministrationIframeSize${renderResponse.namespace}', ss_setParentAdministrationIframeSize${renderResponse.namespace});
	} else {
		//Show the pseudo portal
		var divObj = self.document.getElementById('ss_pseudoAdministrationPortalDiv${renderResponse.namespace}');
		if (divObj != null) {
			divObj.className = "ss_pseudoPortal"
		}
		divObj = self.document.getElementById('ss_upperRightToolbar${renderResponse.namespace}');
		if (divObj != null) {
			divObj.style.display = "block"
			divObj.style.visibility = "visible"
		}
		divObj = self.document.getElementById('ss_administrationHeader_${renderResponse.namespace}');
		if (divObj != null) {
			divObj.style.display = "block"
			divObj.style.visibility = "visible"
		}
	}
}
ss_administration_showPseudoPortal${renderResponse.namespace}();
</script>
	</body>
</html>
</ssf:ifadapter>
