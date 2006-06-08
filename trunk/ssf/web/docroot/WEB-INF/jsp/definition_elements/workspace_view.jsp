<% //View a workspace %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div class="ss_style ss_portlet">

<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${item}" 
  configJspStyle="${ssConfigJspStyle}"
  entry="${ssDefinitionEntry}" />
</div>
