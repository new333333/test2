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
<% // Show a profile form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="property_name" type="String" scope="request" />
<jsp:useBean id="property_caption" type="String" scope="request" />
<%@ page import="java.lang.reflect.Method" %>

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
<div >
<c:if test="${!empty property_caption}">
<span class="ss_labelAbove"><c:out value="${property_caption}"/></span>
</c:if>
<input type="text" class="ss_text" name="<%= property_name %>" 
 value="<%= value %>">
</div>
<%
	}
%>
