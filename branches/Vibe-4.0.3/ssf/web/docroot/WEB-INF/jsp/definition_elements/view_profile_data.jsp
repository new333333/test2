<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
<% // View profile data dispatcher %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="property_name" type="String" scope="request" />
<jsp:useBean id="property_caption" type="String" scope="request" />
<jsp:useBean id="ssConfigDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ssDefinitionEntry" type="org.kablink.teaming.domain.DefinableEntity" scope="request" />
<%
	//Get the item being displayed
	Element item = (Element) request.getAttribute("item");
	if (item != null) {
		String itemType = (String) item.attributeValue("formItem", "");
	
		if (itemType.equals("profileName")) {
			%><jsp:include page="/WEB-INF/jsp/definition_elements/view_profile_data_name.jsp" /><%
			
		} else if (itemType.equals("profileTimeZone")) {
			%><jsp:include page="/WEB-INF/jsp/definition_elements/view_profile_data_timezone.jsp" /><%
	
		} else if (itemType.equals("profileLocale")) {
			%><jsp:include page="/WEB-INF/jsp/definition_elements/view_profile_data_locale.jsp" /><%
		} else if (itemType.equals("text")) {
			%><jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_data_text.jsp" /><%
			
		} else if (itemType.equals("description")) {
			%><jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_data_description.jsp" /><%
		} else if (itemType.equals("htmlEditorTextarea")) {
			%><jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_data_html_textarea.jsp" /><%
			
		} else if (itemType.equals("url")) {
			%><jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_data_url.jsp" /><%
			
		} else if (itemType.equals("checkbox")) {
			%><jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_data_checkbox.jsp" /><%
			
		} else if (itemType.equals("selectbox")) {
			%><jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_data_selectbox.jsp" /><%
			
		} else if (itemType.equals("radio")) {
			%><jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_data_radio.jsp" /><%
			
		} else if (itemType.equals("date")) {
			%><jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_data_date.jsp" /><%
			
		} else if (itemType.equals("date_time")) {
			%><jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_data_date_time.jsp" /><%
	
		} else if (itemType.equals("file")) {
			%><jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_data_file.jsp" /><%
			
		} else if (itemType.equals("graphic") || itemType.equals("profileEntryPicture")) {
			%><jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_data_graphic.jsp" /><%			
		} else if (itemType.equals("user_list") || itemType.equals("userListSelectbox")) {
			%><jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_data_user_list.jsp" /><%			
		} else if (itemType.equals("group_list")) {
			%><jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_data_group_list.jsp" /><%
		} else if (itemType.equals("team_list")) {
			%><jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_data_team_list.jsp" /><%
		} else if (itemType.equals("event")) {
			%><jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_data_event.jsp" /><%
		} else  {
			%><%@ include file="/WEB-INF/jsp/definition_elements/view_profile_data_element.jsp" %><%
		}
	}
%>
