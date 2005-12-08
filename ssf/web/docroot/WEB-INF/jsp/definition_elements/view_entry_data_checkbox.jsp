<% //checkbox view %>
<div class="ss_entryContent">
<c:if test="${ssDefinitionEntry.customAttributes[property_name].value}" >
<input type="checkbox" class="ss_content" checked DISABLED>
</c:if>
<c:if test="${!ssDefinitionEntry.customAttributes[property_name].value}" >
<input type="checkbox" DISABLED>
</c:if>
<span class="ss_labelRight"><c:out value="${property_caption}" /></span>
</div>