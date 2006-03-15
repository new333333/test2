<% //Textarea view %>
<c:if test="${!empty ssDefinitionEntry.customAttributes[property_name].value}">
<div class="ss_entryContent">
<span>
<c:out value="${ssDefinitionEntry.customAttributes[property_name].value}" escapeXml="false"/>
</span>
</div>
</c:if>