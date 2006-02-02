<% // The folder collection view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	//Get the collection style displayed. 
	String style = (String) request.getAttribute("property_style");

	if (style.equals("folderList")) {
%>
<%@ include file="/WEB-INF/jsp/definition_elements/folder_list.jsp" %>
<%
	} else if (style.equals("fileLibrary")) {
%>
<%@ include file="/WEB-INF/jsp/definition_elements/file_library.jsp" %>
<%
	}
%>