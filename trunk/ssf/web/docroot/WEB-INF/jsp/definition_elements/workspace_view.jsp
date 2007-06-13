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
<% //View a workspace %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div class="ss_style ss_portlet">

<div align="right" width="100%">
<%@ include file="/WEB-INF/jsp/definition_elements/tag_view.jsp" %>
</div>

<c:if test="${propertyValues_type[0] == 'team_root' && !empty ssAddTeamWorkspaceUrl}">
<div>
<a class="ss_linkButton" href="${ssAddTeamWorkspaceUrl}"><ssf:nlt tag="team.addTeam"/></a>
<br/>
<br/>
</div>
</c:if>

<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${item}" 
  configJspStyle="${ssConfigJspStyle}"
  entry="${ssDefinitionEntry}" />
</div>
