<% //View a profile entry %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div class="ss_style ss_portlet">
<c:set var="ss_toolbar" value="${ssFolderEntryToolbar}" scope="request" />
<c:set var="ss_toolbar_style" value="ss_toolbar" scope="request" />
<%@ include file="/WEB-INF/jsp/definition_elements/toolbar_view.jsp" %>
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${item}" 
  configJspStyle="${ssConfigJspStyle}" 
  entry="${ssDefinitionEntry}" />
</div>
