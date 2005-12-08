<% //Description view %>
<c:if test="${!empty definitionEntry.description}">
<div class="entryContent">
<c:out value="${definitionEntry.description.text}" escapeXml="false"/>
</div>
</c:if>
