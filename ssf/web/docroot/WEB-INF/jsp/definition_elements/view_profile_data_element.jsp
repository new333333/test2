<% //Profile element view %>
<%@ page import="java.lang.reflect.Method" %>
<%
	Object entry = (Object) request.getAttribute("ssDefinitionEntry");
	//Get the value of the item being displayed
    String prop = Character.toUpperCase(property_name.charAt(0)) + 
    		property_name.substring(1);
    String mName = "get" + prop;
    Class[] types = new Class[] {};
    String ss_profileElementValue = null;
    try {
    	Method method = entry.getClass().getMethod(mName, types);
        ss_profileElementValue = (String) method.invoke(entry, new Object[0]);
    } catch (Exception ex) {}
    if (ss_profileElementValue == null) ss_profileElementValue = "";
%>
<c:if test="${empty ss_element_display_style}">
<div class="ss_entryContent">
<c:if test="${!empty property_caption}">
<span class="ss_bold"><c:out value="${property_caption}"/>:</span>
</c:if>
<%= ss_profileElementValue %>
</div>
</c:if>
<c:if test="${!empty ss_element_display_style && 
    ss_element_display_style == 'tableAlignLeft'}">
<tr>
  <td class="ss_table_spacer_right" valign="top" align="right">
    <c:out value="${property_caption}" />
  </td>
  <td valign="top">
	<span class="ss_bold">
	  <%= ss_profileElementValue %>
	</span>
  </td>
</tr>
</c:if>
