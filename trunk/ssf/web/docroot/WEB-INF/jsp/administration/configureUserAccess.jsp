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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.configure_ldap") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">
<div class="ss_pseudoPortal">
<div class="ss_style ss_portlet">
<ssf:form titleTag="administration.configure.userAccess.title">

	<c:if test="${!empty ssException}">
	<span class="ss_largerprint"><ssf:nlt tag="administration.errors"/> (<c:out value="${ssException}"/>)</span></br>
	</c:if>

	<form class="ss_style ss_form" name="${renderResponse.namespace}fm" method="post" 
	  action="<ssf:url action="configure_user_access" actionUrl="true"/>">
		<div class="ss_buttonBarRight">
		<br/>
		<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
			<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
				  onClick="self.window.close();return false;"/>
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
			<input type="checkbox" id="allowLocalLogin" name="allowLocalLogin" <c:if test="${ssAuthenticationConfig.allowLocalLogin}">checked</c:if>/>
				<label for="allowLocalLogin"><span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.config.allowLocalLogin"/></span><br/></label>
			<input type="checkbox" id="allowSelfRegistration" name="allowSelfRegistration" <c:if test="${ssAuthenticationConfig.allowSelfRegistration}">checked</c:if>/>
				<label for="allowSelfRegistration"><span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.config.allowSelfRegistration"/></span></label>
			<br/>
			<br/>
			<br/>
	    </div>
	  </div>
	<br/>
	<div class="ss_buttonBarLeft">
		<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
		<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
			  onClick="self.window.close();return false;"/>
	</div>
	</form>
</ssf:form>
</div>
</div>
</body>
</html>