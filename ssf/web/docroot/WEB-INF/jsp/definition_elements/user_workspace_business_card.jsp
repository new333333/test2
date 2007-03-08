<% //Business card view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/view_profile_data_name.jsp" %>

<div class="ss_content_window">
<c:if test="${!empty ssProfileConfigDefinition}">
<ssf:displayConfiguration configDefinition="${ssProfileConfigDefinition}" 
  configElement="${ssProfileConfigElement}" 
  configJspStyle="${ssProfileConfigJspStyle}"
  processThisItem="true" 
  entry="${ssProfileConfigEntry}" />
</c:if>
</div>