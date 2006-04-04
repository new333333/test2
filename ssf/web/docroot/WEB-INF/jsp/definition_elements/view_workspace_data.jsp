<% // View entry data dispatcher %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="property_name" type="String" scope="request" />
<jsp:useBean id="property_caption" type="String" scope="request" />
<%
	if (property_name.equals("title")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_workspace_data_title.jsp" %><%

	} else if (property_name.equals("description")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_workspace_data_description.jsp" %><%

	} else if (property_name.equals("htmlEditorTextarea")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_workspace_data_html_textarea.jsp" %><%
		
	} else if (property_name.equals("textarea")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_workspace_data_textarea.jsp" %><%
		
	} else if (property_name.equals("file")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_workspace_data_file.jsp" %><%
		
	} else if (property_name.equals("graphic")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_workspace_data_graphic.jsp" %><%
		
	}
%>
<br><%= property_name %>, <%= property_caption %> <br>
