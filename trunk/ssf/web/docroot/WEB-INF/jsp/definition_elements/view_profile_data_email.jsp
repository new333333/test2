<% //email address view %>
<c:if test="${!empty ssDefinitionEntry.emailAddress}">
<div class="ss_entryContent">
<a class="ss_link_nodec" href="mailto:<c:out value="${ssDefinitionEntry.emailAddress}"/>">
  <c:out value="${ssDefinitionEntry.emailAddress}"/>
</a>
</div>
</c:if>
