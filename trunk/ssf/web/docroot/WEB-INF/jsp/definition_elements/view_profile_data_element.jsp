<% //Profile element view %>
<%@ page import="java.lang.reflect.Method" %>
<%
	User entry = (User) request.getAttribute("ssDefinitionEntry");
	//Get the value of the item being displayed
    String prop = Character.toUpperCase(property_name.charAt(0)) + 
    		property_name.substring(1);
    String mName = "get" + prop;
    Class[] types = new Class[] {};
    Method method = entry.getClass().getMethod(mName, types);
    String ss_profileElementValue = (String) method.invoke(entry, new Object[0]);
%>
<div class="ss_entryContent">
<span class="ss_bold"><c:out value="${property_caption}"/>:</span>
<%= ss_profileElementValue %>
</div>
