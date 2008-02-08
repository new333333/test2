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
--%><%-- // The init jsp for all definition elements and building blocks --%><%--
--%><%@ page import="org.dom4j.Document" %><%--
--%><%@ page import="org.dom4j.DocumentHelper" %><%--
--%><%@ page import="org.dom4j.Element" %><%--
--%><%@ page import="java.util.Map" %><%--
--%><%@ page import="java.util.Stack" %><%--
--%><%@ page import="java.util.Iterator" %><%--
--%><%@ page import="com.sitescape.team.util.NLT" %><%--
--%><%@ page import="com.sitescape.team.web.WebKeys" %><%--
--%><%@ page import="com.sitescape.team.domain.Binder" %><%--
--%><%@ page import="com.sitescape.team.domain.DefinableEntity" %><%--
--%><%@ page import="com.sitescape.team.domain.Folder" %><%--
--%><%@ page import="com.sitescape.team.domain.Entry" %><%--
--%><%@ page import="com.sitescape.team.domain.FolderEntry" %><%--
--%><%@ page import="com.sitescape.team.domain.Event" %><%--
--%><%@ page import="com.sitescape.team.domain.CustomAttribute" %><%--
--%><%@ page import="com.sitescape.team.domain.User" %><%--
--%><%@ page import="com.sitescape.team.util.NLT" %><%--
--%><%@ include file="/WEB-INF/jsp/common/include.jsp" %><%--
--%><%@ include file="/WEB-INF/jsp/forum/init.jsp" %>
