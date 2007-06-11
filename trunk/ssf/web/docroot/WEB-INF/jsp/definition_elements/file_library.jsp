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
<% // File folder listing %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssUserFolderProperties" type="com.sitescape.team.domain.UserProperties" scope="request" />
<%
	Map ssFolderColumns = (Map) ssUserFolderProperties.getProperty("userFolderColumns");
	if (ssFolderColumns == null) {
		ssFolderColumns.put("title", "title");
		ssFolderColumns.put("size", "size");
		ssFolderColumns.put("download", "download");
		ssFolderColumns.put("html", "html");
		ssFolderColumns.put("state", "state");
		ssFolderColumns.put("author", "author");
		ssFolderColumns.put("date", "date");
	}
%>
<c:set var="ssFolderColumns" value="<%= ssFolderColumns %>" scope="request"/>
<%@ include file="/WEB-INF/jsp/definition_elements/folder_view_common.jsp" %>
