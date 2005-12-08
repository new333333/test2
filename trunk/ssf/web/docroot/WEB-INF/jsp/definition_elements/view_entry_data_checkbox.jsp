<% //checkbox view %>
<div class="ss_entryContent">
<c:if test="${ssDefinitionEntry.customAttributes[property_name].value}" >
<input type="checkbox" checked DISABLED>
</c:if>
<c:if test="${!ssDefinitionEntry.customAttributes[property_name].value}" >
<input type="checkbox" DISABLED>
</c:if>
<c:out value="${property_caption}" />
</div>