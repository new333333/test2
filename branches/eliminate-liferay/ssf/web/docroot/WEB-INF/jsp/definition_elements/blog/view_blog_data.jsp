<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<% // View entry data dispatcher %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="property_name" type="String" scope="request" />
<jsp:useBean id="property_caption" type="String" scope="request" />
<jsp:useBean id="ssConfigDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ssDefinitionEntry" type="com.sitescape.team.domain.DefinableEntity" scope="request" />


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

	} else if (itemType.equals("remoteApp")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/remote_application_entry_view.jsp" %><%

	} else if (itemType.equals("entryAttributes")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/entry_attributes_view.jsp" %><%

	}
%>
