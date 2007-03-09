<% //Title view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div class="ss_entryContent">
<h1 class="ss_entryTitle">

<c:if test="${empty ssDefinitionEntry.title}">
    <span class="ss_light">--no title--</span>
</c:if>
<c:if test="${!empty ssDefinitionEntry.title}">
<c:out value="${ssDefinitionEntry.title}"/>
</c:if>
</h1>
</div>
