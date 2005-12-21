<% //Title view %>
<div class="ss_entryContent">
<h1 class="ss_entryTitle">
<c:if test="${empty ssDefinitionEntry.title}">
    <span class="ss_contentgray">--no title--</span>
</c:if>
<c:out value="${ssDefinitionEntry.title}"/>
</h1>
</div>
