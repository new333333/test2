<% //Title view for folders %>
<div class="ss_entryContent">
<span class="ss_entryTitle">
<c:if test="${empty ssDefinitionEntry.title}">
    <span class="ss_gray">--no title--</span>
    </c:if><c:out value="${ssDefinitionEntry.title}"/></a></span>
</div>