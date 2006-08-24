<% // Folder listing %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	Map ssFolderColumns = new java.util.HashMap();
	ssFolderColumns.put("number", "number");
	ssFolderColumns.put("title", "title");
	ssFolderColumns.put("state", "state");
	ssFolderColumns.put("author", "author");
	ssFolderColumns.put("date", "date");
%>
<c:set var="ssFolderColumns" value="<%= ssFolderColumns %>" scope="request"/>
<%@ include file="/WEB-INF/jsp/definition_elements/folder_view_common.jsp" %>
