<% //checkbox view %>
<c:if test="${ssDefinitionEntry.customAttributes[property_name].value}" >
<div class="ss_entryContent">
<input type="checkbox" checked DISABLED>
</c:if>
<c:if test="${!ssDefinitionEntry.customAttributes[property_name].value}" >
<input type="checkbox" DISABLED>
<span class="ss_labelRight"><c:out value="${property_caption}" /></span>
</div>
<div class="ss_divider"></div>
</c:if>