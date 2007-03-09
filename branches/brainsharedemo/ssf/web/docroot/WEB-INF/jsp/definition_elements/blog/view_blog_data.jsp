<% // View entry data dispatcher %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="property_name" type="String" scope="request" />
<jsp:useBean id="property_caption" type="String" scope="request" />
<%
	//Get the item being displayed
	Element item = (Element) request.getAttribute("item");
	String itemType = (String) item.attributeValue("formItem", "");

	if (itemType.equals("title")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/blog/view_blog_title.jsp" %><%

	} else if (itemType.equals("description")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/blog/view_blog_description.jsp" %><%

	} else if (itemType.equals("text")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_entry_data_text.jsp" %><%
		
	} else if (itemType.equals("htmlEditorTextarea")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_entry_data_html_textarea.jsp" %><%
		
	} else if (itemType.equals("textarea")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_entry_data_textarea.jsp" %><%
		
	} else if (itemType.equals("checkbox")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_entry_data_checkbox.jsp" %><%
		
	} else if (itemType.equals("selectbox")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_entry_data_selectbox.jsp" %><%
		
	} else if (itemType.equals("radio")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_entry_data_radio.jsp" %><%
		
	} else if (itemType.equals("date")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_entry_data_date.jsp" %><%
		
	} else if (itemType.equals("file")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_entry_data_file.jsp" %><%
		
	} else if (itemType.equals("graphic")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_entry_data_graphic.jsp" %><%
		
	} else if (itemType.equals("user_list")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_entry_data_user_list.jsp" %><%
		
	} else if (itemType.equals("event")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_entry_data_event.jsp" %><%
	}
%>
