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
<%@ page import="org.kablink.teaming.ObjectKeys" %>

<div id="footer">
  <ul>
	<li>
		<a href="<ssf:url adapter="true" portletName="ss_forum" 
			action="__ajax_mobile" operation="mobile_show_front_page" actionUrl="false" />"
			><img src="<html:rootPath/>css/images/mobile/home_icon.png" 
			 <ssf:alt tag=""/> width="14" height="15" 
			 hspace="3" border="0" align="bottom" /><ssf:nlt tag="mobile.returnToTop"/></a>
	</li>
	<li>
		<c:set var="guestInternalId" value="<%= ObjectKeys.GUEST_USER_INTERNALID %>"/>
		<c:if test="${ssUser.internalId == guestInternalId}">
		  <c:if test='<%= !org.kablink.teaming.util.SPropsUtil.getBoolean("form.login.auth.disallowed",false) %>' >
		    <a href="<ssf:url action="__ajax_mobile" actionUrl="false" 
							operation="mobile_login" />"
		    >
		    <span><ssf:nlt tag="login"/></span>
		    </a>
		  </c:if>
		</c:if>
		
		<c:if test="${ssUser.internalId != guestInternalId}">
			<a href="${ss_logoutUrl}"><span><ssf:nlt tag="logout"/></span></a>
		</c:if>
	</li>
  </ul>
</div>
