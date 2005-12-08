<% //Description view %>
<c:if test="${!empty ss_definition_folder_entry.description}">
<div class="entryContent">
<c:out value="${ss_definition_folder_entry.description.text}" escapeXml="false"/>
</div>
</c:if>
