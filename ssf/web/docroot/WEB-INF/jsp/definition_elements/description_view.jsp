<% //Description view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${!empty ssDefinitionEntry.description.text}">
<div class="ss_entryContent ss_entryDescription">
 <span><c:out value="${ssDefinitionEntry.description.text}" escapeXml="false"/></span>
</div>
</c:if>
