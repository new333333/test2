<% //Business card elements %>
<%@ page import="java.lang.reflect.Method" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
		//Get the form item being displayed
		Element item = (Element) request.getAttribute("item");
%>

<c:forEach var="element" items="${propertyValues_elements}">
<jsp:useBean id="element" type="String" scope="page" />
<%
	Object entry = (Object) request.getAttribute("ssDefinitionEntry");
	//Get the value of the item being displayed
    String prop = Character.toUpperCase(element.charAt(0)) + 
    		element.substring(1);
    String mName = "get" + prop;
    Class[] types = new Class[] {};
    String ss_profileElementValue = null;
    try {
    	Method method = entry.getClass().getMethod(mName, types);
        ss_profileElementValue = (String) method.invoke(entry, new Object[0]);
    } catch (Exception ex) {}
    if (ss_profileElementValue == null) ss_profileElementValue = "";
%>
<tr>
<td align="right" class="ss_profile_elements_spacer">
  <span class="ss_bold"><ssf:nlt tag="profile.element.${element}"/></span>
</td>
<td>
  <c:if test="${element == 'name' || element == 'title' || 
      element == 'emailAddress' || element == 'phone' || element == 'zonName' || 
      element == 'country' || element == 'organization' || 
      element == 'timeZoneName'}">
    <c:out value="<%= ss_profileElementValue %>"/>
  </c:if>
  <c:if test="${!empty ssDefinitionEntry.customAttributes[element]}">
    <c:out value="${ssDefinitionEntry[element]}"/>
  </c:if>
</td>
</tr>
</c:forEach>
