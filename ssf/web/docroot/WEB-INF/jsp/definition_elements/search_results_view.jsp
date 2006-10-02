<% // Folder listing %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	Map ssFolderColumns = (Map) ssUserProperties.get("userSearchResultsFolderColumns");
	if (ssFolderColumns == null) {
		ssFolderColumns = new java.util.HashMap();
		ssFolderColumns.put("folder", "folder");
		ssFolderColumns.put("title", "title");
		ssFolderColumns.put("state", "state");
		ssFolderColumns.put("author", "author");
		ssFolderColumns.put("date", "date");
	}
%>
<c:set var="ssFolderColumns" value="<%= ssFolderColumns %>" scope="request"/>
<%@ include file="/WEB-INF/jsp/definition_elements/search_results_view_common.jsp" %>
