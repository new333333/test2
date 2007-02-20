<% //Description view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${!empty ssDefinitionEntry.description.text}">
<div class="ss_entryDescriptionLead"></div>
<div class="ss_entryContent ss_entryDescription">
 <span><ssf:markup type="view" entity="${ssDefinitionEntry}"><c:out 
   value="${ssDefinitionEntry.description.text}" escapeXml="false"/></ssf:markup></span>
      <div style="display: inline; float: right; margin-left: 5px;">[EDIT]</div>   
</div>
</c:if>
