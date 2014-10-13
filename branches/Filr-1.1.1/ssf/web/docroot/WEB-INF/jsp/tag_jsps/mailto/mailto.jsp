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
<% //mailto link %><%--
--%><%@ page import="org.kablink.util.BrowserSniffer" %><%--
--%><%
	String userAgents = org.kablink.teaming.util.SPropsUtil.getString("mobile.userAgents", "");
	String tabletUserAgents = org.kablink.teaming.util.SPropsUtil.getString("tablet.userAgentRegexp", "");
	Boolean testForAndroid = org.kablink.teaming.util.SPropsUtil.getBoolean("tablet.useDefaultTestForAndroidTablets", false);
	boolean isMobile = (org.kablink.util.BrowserSniffer.is_mobile(request, userAgents) && 
		!org.kablink.util.BrowserSniffer.is_tablet(request, tabletUserAgents, testForAndroid));  
%><%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %><%--
--%><c:set var="guestInternalId" value="<%= ObjectKeys.GUEST_USER_INTERNALID %>"/><%--
--%><c:set var="isMobileDevice" value="<%= isMobile %>"/>
<c:if test="${ssUser.internalId == guestInternalId || !isMobileDevice }">
<c:if test="${!empty emailName && !empty emailHost}">
  <c:if test="${!noLink}">
	<a href=""><ssmailto 
	  	name="<c:out value="${emailName}" escapeXml="true"/>" 
	  	host="<c:out value="${emailHost}" escapeXml="true"/>"></ssmailto></a>
  </c:if>
  <c:if test="${noLink}">
    <span><ssmailto 
	  	name="<c:out value="${emailName}" escapeXml="true"/>" 
	  	host="<c:out value="${emailHost}" escapeXml="true"/>"
	  	noLink="true"></ssmailto></span>
  </c:if>
<script type="text/javascript">
ss_createOnLoadObj("ss_showEmailLinks", ss_showEmailLinks);
</script>
</c:if>
<c:if test="${empty emailName || empty emailHost}">
	<a href="mailto:${email}"><span><c:out value="${email}" escapeXml="true"/></span></a>
</c:if>
</c:if>
<c:if test="${ssUser.internalId != guestInternalId && isMobileDevice }">
    <a href="mailto:${email}"><span><c:out value="${email}" escapeXml="true"/></span></a>
</c:if>