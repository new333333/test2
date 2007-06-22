<%
/**
 * This is an example of a custom jsp
 * 
 * There are 3 beans passed in to your custom jsp:
 *   property_name - the element name used to save the data in the database
 *   property_caption - the caption specified in the entry definition
 *   ssDefinitionEntry.customAttributes[property_name].value - the current value of the data element (if this is a "modify entry" operation)
 */
%>

<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div style="padding:10px 0px 10px 0px;">
<span class="ss_bold">This is a custom jsp form element</span>
<br/>
<br/>
<c:if test="${!empty property_caption}">
  <span class="ss_bold">${property_caption}</span>
  <br/>
</c:if>

<input type="text" id="${property_name}" name="${property_name}" 
  value="${ssDefinitionEntry.customAttributes[property_name].value}"/>
</div>
