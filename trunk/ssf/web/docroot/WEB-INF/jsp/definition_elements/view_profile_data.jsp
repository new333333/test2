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
<% // View profile data dispatcher %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="property_name" type="String" scope="request" />
<jsp:useBean id="property_caption" type="String" scope="request" />
<jsp:useBean id="ssConfigDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ssDefinitionEntry" type="com.sitescape.team.domain.DefinableEntity" scope="request" />
<%
	//Get the item being displayed
	Element item = (Element) request.getAttribute("item");
	if (item != null) {
		String itemType = (String) item.attributeValue("formItem", "");
	
		if (itemType.equals("name")) {
			%><%@ include file="/WEB-INF/jsp/definition_elements/view_profile_data_name.jsp" %><%
			
		} else if (itemType.equals("timeZone")) {
			%><%@ include file="/WEB-INF/jsp/definition_elements/view_profile_data_timezone.jsp" %><%
	
		} else if (itemType.equals("text")) {
			%><%@ include file="/WEB-INF/jsp/definition_elements/view_entry_data_text.jsp" %><%
			
		} else if (itemType.equals("htmlEditorTextarea")) {
			%><%@ include file="/WEB-INF/jsp/definition_elements/view_entry_data_html_textarea.jsp" %><%
			
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
			
		} else if (itemType.equals("graphic") || itemType.equals("profileEntryPicture")) {
			%><%@ include file="/WEB-INF/jsp/definition_elements/view_entry_data_graphic.jsp" %><%
			
		} else if (itemType.equals("user_list")) {
			%><%@ include file="/WEB-INF/jsp/definition_elements/view_entry_data_user_list.jsp" %><%
			
		} else if (itemType.equals("event")) {
			%><%@ include file="/WEB-INF/jsp/definition_elements/view_entry_data_event.jsp" %><%
		} else  {
			%><%@ include file="/WEB-INF/jsp/definition_elements/view_profile_data_element.jsp" %><%
		}
	}
%>
