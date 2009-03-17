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
<% // View workspace data dispatcher %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="property_name" type="String" scope="request" />
<jsp:useBean id="property_caption" type="String" scope="request" />
<%
	//Get the item being displayed
	Element item = (Element) request.getAttribute("item");
	String itemType = (String) item.attributeValue("formItem", "");

	if (itemType.equals("title")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_folder_title.jsp" %><%

	} else if (itemType.equals("description")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_workspace_description.jsp" %><%

	} else if (itemType.equals("htmlEditorTextarea")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_workspace_data_html_textarea.jsp" %><%
		
	} else if (itemType.equals("file")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_workspace_data_file.jsp" %><%
		
	} else if (itemType.equals("graphic")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_workspace_data_graphic.jsp" %><%
		
	} else if (itemType.equals("attachFiles")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_entry_attachments.jsp" %><%		
	
	} else if (itemType.equals("folderRemoteApp")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/remote_application_view.jsp" %><%

	} else if (itemType.equals("folderAttributeList")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/binder_attributes_view.jsp" %><%

	} else if (itemType.equals("mashupCanvas")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/mashup_canvas_form.jsp" %><%

	} else if (itemType.equals("user_list") || itemType.equals("userListSelectbox")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_folder_data_user_list.jsp" %><%
		
	} else {
        %>
        <ssf:nlt tag="definition.error.unknownDefinitionElement">
         <ssf:param name="value" value="<%= itemType %>"/>
        </ssf:nlt><br/>
        <%
	}
%>
