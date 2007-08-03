<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

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
	  action="<portlet:actionURL windowState="maximized"><portlet:param 
		name="action" value="manage_license"/></portlet:actionURL>">
		<input type="submit" class="ss_submit" name="updateBtn" value="<ssf:nlt tag="button.update_license"/>">
		<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>">
	</form>
  </div>
</div>
