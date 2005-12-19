<% // View profile data dispatcher %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="property_name" type="String" scope="request" />
<jsp:useBean id="property_caption" type="String" scope="request" />
<%
	//Get the item being displayed
	Element item = (Element) request.getAttribute("item");
	String itemType = (String) item.attributeValue("formItem", "");

	if (itemType.equals("name")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_profile_data_name.jsp" %><%
	
	} else if (itemType.equals("profileElements")) {
		Element profileElementNameProperty = (Element) item.selectSingleNode("./properties/property[@name='name']");
		String profileElementType = profileElementNameProperty.attributeValue("value", "");

		if (profileElementType.equals("title")) {
			%><%@ include file="/WEB-INF/jsp/definition_elements/view_profile_data_title.jsp" %><%
	
		} else if (profileElementType.equals("emailAddress")) {
			%><%@ include file="/WEB-INF/jsp/definition_elements/view_profile_data_email.jsp" %><%
	
		} else if (profileElementType.equals("text")) {
			%><%@ include file="/WEB-INF/jsp/definition_elements/view_entry_data_text.jsp" %><%
			
		} else if (profileElementType.equals("htmlEditorTextarea")) {
			%><%@ include file="/WEB-INF/jsp/definition_elements/view_entry_data_html_textarea.jsp" %><%
			
		} else if (profileElementType.equals("textarea")) {
			%><%@ include file="/WEB-INF/jsp/definition_elements/view_entry_data_textarea.jsp" %><%
			
		} else if (profileElementType.equals("checkbox")) {
			%><%@ include file="/WEB-INF/jsp/definition_elements/view_entry_data_checkbox.jsp" %><%
			
		} else if (profileElementType.equals("selectbox")) {
			%><%@ include file="/WEB-INF/jsp/definition_elements/view_entry_data_selectbox.jsp" %><%
			
		} else if (profileElementType.equals("radio")) {
			%><%@ include file="/WEB-INF/jsp/definition_elements/view_entry_data_radio.jsp" %><%
			
		} else if (profileElementType.equals("date")) {
			%><%@ include file="/WEB-INF/jsp/definition_elements/view_entry_data_date.jsp" %><%
			
		} else if (profileElementType.equals("file")) {
			%><%@ include file="/WEB-INF/jsp/definition_elements/view_entry_data_file.jsp" %><%
			
		} else if (profileElementType.equals("user_list")) {
			%><%@ include file="/WEB-INF/jsp/definition_elements/view_entry_data_user_list.jsp" %><%
			
		} else if (profileElementType.equals("event")) {
			%><%@ include file="/WEB-INF/jsp/definition_elements/view_entry_data_event.jsp" %><%
		}
	}
%>
