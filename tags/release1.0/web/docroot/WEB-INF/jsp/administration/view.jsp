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
<c:if test="${empty ss_portletInitialization}">
<c:set var="adminTreeName" value="${renderResponse.namespace}_adminDomTree"/>
  <div class="ss_portlet_style ss_portlet">
  <div class="ss_style">
    <c:out value="${releaseInfo}"/>
<ssf:ifLicenseExpired><div class="ss_warning ss_license_warning"><span class="ss_warning ss_license_warning"><ssf:nlt tag="license.expired.warning"/></span></div></ssf:ifLicenseExpired>
<ssf:ifLicenseExpired invert="true">
  <ssf:ifLicenseExpired inThisManyDays="30"><div class="ss_warning ss_license_warning"><span class="ss_warning ss_license_warning"><ssf:nlt tag="license.expiring.soon.warning"/></span></div></ssf:ifLicenseExpired>
  <ssf:ifLicenseExpired inThisManyDays="30" invert="true">  
	  <ssf:ifLicenseOutOfCompliance><div class="ss_warning ss_license_warning"><span class="ss_warning ss_license_warning"><ssf:nlt tag="license.out.of.compliance"/></span></div></ssf:ifLicenseOutOfCompliance>
  </ssf:ifLicenseExpired>
</ssf:ifLicenseExpired>

	<table border="0" width="100%">
	<tr>
	  <td>
<c:if test="${ss_isSiteAdmin}">
	    <ssHelpSpot helpId="portlets/admin/admin_portlet_site" 
	      title="<ssf:nlt tag="helpSpot.adminPortletSite"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt>" 
	      offsetY="5" offsetX="-13">
	    </ssHelpSpot>
</c:if>
<c:if test="${!ss_isSiteAdmin}">
	    <ssHelpSpot helpId="portlets/admin/admin_portlet" 
	      title="<ssf:nlt tag="helpSpot.adminPortlet"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt>" 
	      offsetY="5" offsetX="-13">
	    </ssHelpSpot>
</c:if>
	  </td>
	</tr>
	<tr>
		<td>
			<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td>
					<div>
						<c:if test="${ssUser.displayStyle != 'accessible'}" >
						  <ssf:tree treeName="${adminTreeName}" 
						    treeDocument="${ssAdminDomTree}" 
						    rootOpen="true" />
						</c:if>
						<c:if test="${ssUser.displayStyle == 'accessible'}" >
						<ssf:tree treeName="${adminTreeName}" 
						  treeDocument="${ssAdminDomTree}" 
						  flat="true"
						  rootOpen="true" />
						</c:if>
					</div>
				</td>
			</tr>
			</table>
		</td>
		<td align="right" width="30" valign="top">
		<a href="javascript:;" onClick="ss_helpSystem.run();return false;"><img border="0" 
  		  src="<html:imagesPath/>icons/help.png" 
  		  alt="<ssf:nlt tag="navigation.help" text="Help"/>" /></a>
		</td>
	</tr>
	</table>
  </div>
  </div>
<jsp:include page="/WEB-INF/jsp/common/help_welcome.jsp" />
</c:if>
