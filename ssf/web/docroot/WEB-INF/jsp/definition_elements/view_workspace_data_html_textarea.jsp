<% //Textarea view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<div class="ss_entryContent">
 <span>
<c:out value="${ssDefinitionEntry.customAttributes[property_name].value.text}" escapeXml="false"/>
 </span>
</div>
