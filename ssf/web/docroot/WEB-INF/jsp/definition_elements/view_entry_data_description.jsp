<% //Description view %>
<c:if test="${!empty ssDefinitionEntry.description}">
<div class="ss_entryContent">
 <span><c:out value="${ssDefinitionEntry.description.text}" escapeXml="false"/></span>
</div>
<div class="ss_divider"></div>
</c:if>
