<% //checkbox view %>
<div class="entryContent">
<c:if test="${definitionEntry.customAttributes[property_name].value}" >
<input type="checkbox" checked DISABLED>
</c:if>
<c:if test="${!definitionEntry.customAttributes[property_name].value}" >
<input type="checkbox" DISABLED>
</c:if>
<c:out value="${property_caption}" />
</div>