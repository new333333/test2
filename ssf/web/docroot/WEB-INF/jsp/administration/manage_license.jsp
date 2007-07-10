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
  <span><ssf:nlt tag="license.current"/></span>
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
	  name="${ssNamespace}_btnForm" method="post"  action="<portlet:actionURL><portlet:param 
		name="action" value="manage_license"/></portlet:actionURL>">
		<input type="submit" class="ss_submit" name="updateBtn" value="<ssf:nlt tag="button.update_license"/>">
		<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>">
	</form>
  </div>
</div>
