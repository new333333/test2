<% //Description view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${!empty ssDefinitionEntry.description}">
<div class="ss_entryContent">
 <span><ssf:markup type="view" entity="${ssDefinitionEntry}"><c:out 
   value="${ssDefinitionEntry.description.text}" escapeXml="false"/></ssf:markup></span>
</div>
</c:if>
