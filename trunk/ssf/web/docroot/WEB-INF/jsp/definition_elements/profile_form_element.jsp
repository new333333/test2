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
<% // Show a profile form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="property_name" type="String" scope="request" />
<jsp:useBean id="property_caption" type="String" scope="request" />
<%@ page import="java.lang.reflect.Method" %>

<c:set var="showElement" value="1"/>
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
<c:if test="${showElement == '1'}">
<%
	//Get the item being displayed
	Element item = (Element) request.getAttribute("item");
	String itemType = (String) item.attributeValue("name", "");
	if (itemType.equals("name")) {
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
		    value = (String) method.invoke(entry, new Object[0]);
		    if (value == null) value = "";
		}
%>
<div class="ss_entryContent">
<c:if test="${!empty property_caption}">
<span class="ss_labelAbove"><c:out value="${property_caption}"/></span>
</c:if>
<input type="text" class="ss_text" name="<%= property_name %>" 
 value="<%= value %>">
</div>
<%
	}
%>
</c:if>
