<% // View entry data dispatcher %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="property_name" type="String" scope="request" />
<jsp:useBean id="property_caption" type="String" scope="request" />
<%
	//Get the item being displayed
	Element item = (Element) request.getAttribute("item");
	String itemType = (String) item.attributeValue("formItem", "");

	if (itemType.equals("title")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_workspace_title.jsp" %><%

	} else if (itemType.equals("description")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_workspace_description.jsp" %><%

	} else if (itemType.equals("htmlEditorTextarea")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_workspace_data_html_textarea.jsp" %><%
		
	} else if (itemType.equals("textarea")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_workspace_data_textarea.jsp" %><%
		
	} else if (itemType.equals("file")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_workspace_data_file.jsp" %><%
		
	} else if (itemType.equals("graphic")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_workspace_data_graphic.jsp" %><%
		
	}
%>
