<% //View an entry %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div class="ss_style ss_portlet">
<%@ include file="/WEB-INF/jsp/definition_elements/navigation_links.jsp" %>
<c:set var="ss_toolbar" value="${ssFolderEntryToolbar}" scope="request" />
<%@ include file="/WEB-INF/jsp/definition_elements/toolbar_view.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/popular_view.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/tag_view.jsp" %>

<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${item}" 
  configJspStyle="${ssConfigJspStyle}" 
  entry="${ssDefinitionEntry}" />
</div>
