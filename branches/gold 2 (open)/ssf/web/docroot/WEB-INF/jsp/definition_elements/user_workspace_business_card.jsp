<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<% //Business card view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div class="ss_content_window">
<c:if test="${!empty ssProfileConfigDefinition}">
<ssf:displayConfiguration configDefinition="${ssProfileConfigDefinition}" 
  configElement="${ssProfileConfigElement}" 
  configJspStyle="${ssProfileConfigJspStyle}"
  processThisItem="true" 
  entry="${ssProfileConfigEntry}" />
</c:if>
</div>