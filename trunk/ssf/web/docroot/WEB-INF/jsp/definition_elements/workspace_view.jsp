<% //View a workspace %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div class="ss_style ss_portlet">

<div align="right" width="100%">
<%@ include file="/WEB-INF/jsp/definition_elements/tag_view.jsp" %>
</div>

<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${item}" 
  configJspStyle="${ssConfigJspStyle}"
  entry="${ssDefinitionEntry}" />
</div>
