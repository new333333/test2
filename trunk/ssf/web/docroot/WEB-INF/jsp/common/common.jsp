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
%><%--
--%><%@ page session="false" %><%--
--%><%@ page isELIgnored="false" %><%--
--%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%--
--%><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%--
--%><%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %><%--
--%><%@ taglib prefix="ssf" uri="http://www.sitescape.com/tags-ssf" %><%--
--%><%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %><%--
--%><%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %><%--
--%><%@ taglib prefix="portletadapter" uri="http://www.sitescape.com/tags-portletadapter" %><%--

--%><portletadapter:defineObjects1/><%--
--%><%

//Set up the user object
if (org.kablink.teaming.context.request.RequestContextHolder.getRequestContext() != null) {
	org.kablink.teaming.domain.User user = org.kablink.teaming.context.request.RequestContextHolder.getRequestContext().getUser();
	request.setAttribute("ssUser", user);
}

%><%--
--%><ssf:ifadapter><portletadapter:defineObjects2/></ssf:ifadapter><%--
--%><ssf:ifnotadapter><portlet:defineObjects/></ssf:ifnotadapter><%--

--%>

<% // Read the key "Teaming.Lang" from the properties file.  This will tell us %>
<% // what language we are running in. %>
<% request.setAttribute( "teamingLang", org.kablink.teaming.util.NLT.get( "Teaming.Lang" ) ); %>

<% // Set up the path to the directory that holds the help html files. %>
<c:set var="helpDocPath" value="${pageContext.request.contextPath}/help_doc/${teamingLang}" scope="application"/>
