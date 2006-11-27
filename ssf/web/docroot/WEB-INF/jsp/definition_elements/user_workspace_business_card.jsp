<% //Business card view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/view_profile_data_name.jsp" %>

<div class="ss_decor-round-corners-top2 ss_innerContentBegins" ><div><div></div></div></div>
<div class="ss_decor-border5">
  <div class="ss_decor-border6">
    <div class="ss_content_window">

<ssf:displayConfiguration configDefinition="${ssProfileConfigDefinition}" 
  configElement="${ssProfileConfigElement}" 
  configJspStyle="${ssProfileConfigJspStyle}"
  processThisItem="true" 
  entry="${ssProfileConfigEntry}" />

    </div>
  </div>
</div>
<div class="ss_decor-round-corners-bottom2"><div><div></div></div></div>
  