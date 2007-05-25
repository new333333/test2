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
<% // The init jsp for all definition elements and building blocks %>
<%@ page import="org.dom4j.Document" %>
<%@ page import="org.dom4j.DocumentHelper" %>
<%@ page import="org.dom4j.Element" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Stack" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="com.sitescape.team.util.NLT" %>
<%@ page import="com.sitescape.team.web.WebKeys" %>
<%@ page import="com.sitescape.team.domain.Binder" %>
<%@ page import="com.sitescape.team.domain.DefinableEntity" %>
<%@ page import="com.sitescape.team.domain.Folder" %>
<%@ page import="com.sitescape.team.domain.Entry" %>
<%@ page import="com.sitescape.team.domain.FolderEntry" %>
<%@ page import="com.sitescape.team.domain.Event" %>
<%@ page import="com.sitescape.team.domain.CustomAttribute" %>
<%@ page import="com.sitescape.team.domain.User" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>
