<% //Description view %>
<c:if test="${!empty ssDefinitionEntry.description}">
<div class="ss_entryContent">
<c:out value="${ssDefinitionEntry.description.text}" escapeXml="false"/>
</div>
</c:if>
