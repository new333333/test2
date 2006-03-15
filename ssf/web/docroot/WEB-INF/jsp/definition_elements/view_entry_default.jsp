<% // The default entry view if no definition exists for an entry %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div class="ss_style ss_portlet" width="100%">
<c:if test="${ssDefinitionEntry.anyOwnerType == 'principal'}">
<%@ include file="/WEB-INF/jsp/definition_elements/view_profile_data_title.jsp" %>
</c:if>
<c:if test="${ssDefinitionEntry.anyOwnerType != 'principal'}">
<%@ include file="/WEB-INF/jsp/definition_elements/view_entry_data_title.jsp" %>
</c:if>

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
