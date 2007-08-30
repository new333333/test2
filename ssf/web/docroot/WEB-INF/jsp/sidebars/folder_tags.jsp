<c:if test="${ssDefinitionEntry.entityType == 'folder'}">
<ssf:sidebarPanel title="sidebar.tags.folder" id="ss_placetags_sidebar" divClass="ss_place_tags"
    initOpen="false" sticky="true">
  <c:set var="ss_tagObject" value="${ssDefinitionEntry}" scope="request"/>
  <%@ include file="/WEB-INF/jsp/definition_elements/tag_view.jsp" %>
</ssf:sidebarPanel>
</c:if>
<c:if test="${ssDefinitionEntry.entityType == 'workspace'}">
<ssf:sidebarPanel title="sidebar.tags.workspace" id="ss_placetags_sidebar" divClass="ss_place_tags"
    initOpen="false" sticky="true">
  <c:set var="ss_tagObject" value="${ssDefinitionEntry}" scope="request"/>
  <%@ include file="/WEB-INF/jsp/definition_elements/tag_view.jsp" %>
</ssf:sidebarPanel>
</c:if>