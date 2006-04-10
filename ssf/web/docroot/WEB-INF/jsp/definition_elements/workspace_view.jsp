<% //View a workspace %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div class="ss_style ss_portlet">

<c:set var="ss_toolbar" value="${ssFolderToolbar}" scope="request" />
<%@ include file="/WEB-INF/jsp/definition_elements/toolbar_view.jsp" %>
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${item}" 
  configJspStyle="${ssConfigJspStyle}"
  binder="${ssBinder}" />
</div>
