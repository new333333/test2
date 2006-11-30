<% //Entry creator view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<div class="ss_entryContent">
<c:out value="${property_caption}" />
<c:out value="${ssDefinitionEntry.creation.principal.title}"/>
<c:if test="${!empty ssDefinitionEntry.postedBy}">
(<ssf:nlt tag="entry.postedBy"/>&nbsp;<c:out value="${ssDefinitionEntry.postedBy}"/>)
</c:if>
</div>
