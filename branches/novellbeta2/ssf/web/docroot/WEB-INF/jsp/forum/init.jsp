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
<%@ page import="org.dom4j.Document" %>
<%@ page import="org.dom4j.DocumentHelper" %>
<%@ page import="org.dom4j.Element" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="com.sitescape.team.ObjectKeys" %>
<%@ page import="com.sitescape.team.web.WebKeys" %>
<%@ page import="com.sitescape.team.domain.Folder" %>
<%@ page import="com.sitescape.team.domain.FolderEntry" %>
<%@ page import="com.sitescape.team.domain.Entry" %>
<%@ page import="com.sitescape.team.domain.SeenMap" %>
<%@ page import="com.sitescape.team.domain.UserProperties" %>
