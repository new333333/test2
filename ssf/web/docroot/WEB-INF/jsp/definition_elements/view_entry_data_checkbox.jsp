<% //checkbox view %>
<div class="entryContent">
<c:if test="${ss_definition_folder_entry.customAttributes[property_name].value}" >
<input type="checkbox" checked DISABLED>
</c:if>
<c:if test="${!ss_definition_folder_entry.customAttributes[property_name].value}" >
<input type="checkbox" DISABLED>
</c:if>
<c:out value="${property_caption}" />
</div>