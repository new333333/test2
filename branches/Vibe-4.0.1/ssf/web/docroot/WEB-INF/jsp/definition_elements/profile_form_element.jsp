<%
/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
<% // Show a profile form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="property_name" type="String" scope="request" />
<jsp:useBean id="property_caption" type="String" scope="request" />
<%@ page import="java.lang.reflect.Method" %>

<c:set var="showElement" value="1"/>
<c:set var="emailElement" value="0"/>
<c:if test="${ss_profile_entry_form == 'true'}">
  <c:if test="${property_name == 'firstName' || 
                property_name == 'middleName' || 
                property_name == 'lastName' || 
                property_name == 'emailAddress' || 
                property_name == 'mobileEmailAddress' || 
                property_name == 'txtEmailAddress' || 
                property_name == 'zonName'}">
    <c:set var="showElement" value="0"/>
  </c:if>
</c:if>
<c:if test="${property_name == 'emailAddress' || 
                property_name == 'mobileEmailAddress' || 
                property_name == 'txtEmailAddress' ||
                property_name == 'bccEmailAddress'}">
    <c:set var="emailElement" value="1"/>
</c:if>

<c:if test="${showElement == '1'}">
<%
	//Get the item being displayed
	Element item = (Element) request.getAttribute("item");
	String itemType = (String) item.attributeValue("name", "");
	if (itemType.equals("profileName")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_profile_data_name.jsp" %><%
	
	} else {
		User entry = (User) request.getAttribute("ssDefinitionEntry");
		String value = "";
		if (entry != null) {
		    String prop = Character.toUpperCase(property_name.charAt(0)) + 
		    		property_name.substring(1);
		    String mName = "get" + prop;
		    Class[] types = new Class[] {};
		    Method method = entry.getClass().getMethod(mName, types);
		    value = (String)method.invoke(entry, new Object[0]);
		    if (value == null) value = "";
		}
		
		String elementName = (String) request.getAttribute("property_name");
		String caption = (String) request.getAttribute("property_caption");
		String caption2 = NLT.get("general.required.caption", new Object[]{caption});
		String required = (String) request.getAttribute("property_required");
		Boolean req = Boolean.parseBoolean(required);
		if (required == null) {required = "";}
		if (required.equals("true")) {
			required = "<span id=\"ss_required_"+elementName+"\" title=\""+caption2+"\" class=\"ss_required\">*</span>";
		} else {
			required = "";
		}
%>
<div class="ss_entryContent">
<c:if test="${!empty property_caption}">
<label for="<%= property_name %>">
  <div class="ss_labelAbove"><c:out value="${property_caption}"/><%= required %></div>
</label>
</c:if>
<c:if test="${empty ssReadOnlyFields[property_name]}">
<input type="text" class="ss_text" name="<%= property_name %>" 
	id="<%= property_name %>" value="<%= value %>" 
	<c:if test="${emailElement == '1'}"> 
	onBlur="validateEmailAddress(this, '<ssf:escapeJavaScript><ssf:nlt tag="email.apparentInvalidEmailFormat" /></ssf:escapeJavaScript>');"</c:if> >
</c:if>
<c:if test="${!empty ssReadOnlyFields[property_name]}"><%= value %>&nbsp;</c:if>

</div>
<%
	}
%>
</c:if>
