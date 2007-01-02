<% //Entry creator view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<div class="ss_entryContent ss_entrySignature">
  <c:out value="${property_caption}" />
  <ssf:presenceInfo user="${ssDefinitionEntry.creation.principal}" showTitle="true"/>
  <c:if test="${!empty ssDefinitionEntry.postedBy}">
    (<ssf:nlt tag="entry.postedBy"/>&nbsp;<c:out value="${ssDefinitionEntry.postedBy}"/>)
  </c:if>
</div>
