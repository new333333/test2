<% //Business card view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/view_profile_data_name.jsp" %>

<ssf:displayConfiguration configDefinition="${ssProfileConfigDefinition}" 
  configElement="${ssProfileConfigElement}" 
  configJspStyle="${ssProfileConfigJspStyle}"
  processThisItem="true" 
  entry="${ssProfileConfigEntry}" />
