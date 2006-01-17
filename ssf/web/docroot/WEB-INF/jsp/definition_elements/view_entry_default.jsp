<% // The default entry view if no definition exists for an entry %>

<div class="ss_style ss_portlet" width="100%">

<%@ include file="/WEB-INF/jsp/definition_elements/title_view.jsp" %>

<div class="formBreak">
<div class="ss_entryContent">
<c:out value="${ssDefinitionEntry.description.text}"/>
</div>
</div>
<c:forEach var="descendant" items="${ssFolderEntryDescendants}">
<div class="formBreak">
<c:out value="${descendant}"/>
</div>
</c:forEach>

</div>
