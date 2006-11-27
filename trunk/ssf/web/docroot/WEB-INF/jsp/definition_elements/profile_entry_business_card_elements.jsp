<% //Business card elements %>
<%@ page import="java.lang.reflect.Method" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
		//Get the form item being displayed
		Element item = (Element) request.getAttribute("item");
%>

<table width="100%">
<tr>
<td valign="top">
<div>
<c:if test="${!empty ssDefinitionEntry.customAttributes['picture']}">
<c:set var="selections" value="${ssDefinitionEntry.customAttributes['picture'].value}" />
<c:set var="count" value="0"/>
<c:forEach var="selection" items="${selections}">
  <c:if test="${count == 0}">
	<img border="0" src="<ssf:url 
	    webPath="viewFile"
	    folderId="${ssDefinitionEntry.parentBinder.id}"
	    entryId="${ssDefinitionEntry.id}" >
	    <ssf:param name="fileId" value="${selection.id}"/>
	    <ssf:param name="viewType" value="scaled"/>
	    </ssf:url>" alt="${property_caption}" />
  </c:if>
  <c:set var="count" value="${count + 1}"/>
</c:forEach>
</c:if>
</div>

</td>
<td valign="top" style="padding-left:30px;">

<c:set var="ss_profile_element_display_style" value="tableAlignLeft" scope="request"/>
<table>

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

</table>
<c:set var="ss_profile_element_display_style" value="" scope="request"/>

</td>
<td valign="top" class="ss_profile_elements_spacer">
  <ssf:presenceInfo user="${ssDefinitionEntry}"/>
</td>
</tr>
</table>
