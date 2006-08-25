<% // File folder listing %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssUserFolderProperties" type="com.sitescape.ef.domain.UserProperties" scope="request" />
<%
	Map ssFolderColumns = (Map) ssUserFolderProperties.getProperty("userFolderColumns");
	if (ssFolderColumns == null) {
		ssFolderColumns = new java.util.HashMap();
		ssFolderColumns.put("title", "title");
		ssFolderColumns.put("author", "author");
		ssFolderColumns.put("date", "date");
	}
%>
<c:set var="ssFolderColumns" value="<%= ssFolderColumns %>" scope="request"/>
<%@ include file="/WEB-INF/jsp/definition_elements/folder_view_common.jsp" %>
