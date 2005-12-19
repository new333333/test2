<% //Title view %>
<jsp:useBean id="ssDefinitionEntry" type="com.sitescape.ef.domain.Entry" scope="request" />
<div class="ss_entryContent">
<h1 class="ss_entryTitle">
<c:if test="${empty ssDefinitionEntry.title}">
    <span class="ss_contentgray">--no title--</span>
</c:if>
<c:out value="${ssDefinitionEntry.title}"/>
</h1>
</div>
