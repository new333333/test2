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
<% //Icon form for folders %>
<%@ page import="com.sitescape.team.util.SPropsUtil" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="iconListPath" value="icons.folder" scope="request"/>
<c:set var="iconValue" value="${ssDefinitionEntry.iconName}" scope="request"/>
<%@ include file="/WEB-INF/jsp/definition_elements/iconForm.jsp" %>
