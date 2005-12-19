<% //Profile name view %>
<c:if test="${!empty ssDefinitionEntry.name}">
<div class="ss_entryContent">
<h1 class="ss_entryTitle">
<c:out value="${ssDefinitionEntry.name}"/>
</h1>
</div>
</c:if>
