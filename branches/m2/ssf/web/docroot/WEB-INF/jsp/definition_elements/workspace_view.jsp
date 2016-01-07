<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
<% //View a workspace %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div class="ss_style ss_portlet">

<c:if test="${propertyValues_type[0] == 'team_root' && !empty ssAddTeamWorkspaceUrl}">
<div>
<a class="ss_linkButton" href="${ssAddTeamWorkspaceUrl}"
  onClick="ss_openUrlInPortlet(this.href, true, 800, 800);return false;"><ssf:nlt tag="team.addTeam"/></a>
<br/>
<br/>
</div>
</c:if>

<c:if test="${propertyValues_type[0] == 'discussion'}">
  <c:set var="ss_discussionWorkspaceView" value="true" scope="request"/>
</c:if>

<c:set var="ss_namespace" value="${renderResponse.namespace}" scope="request"/>
<div align="center">
	<div id="ss_diss_inset" class="discussionView">
	  <div id="ss_diss_top" align="left">

		<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
		  configElement="${item}" 
		  configJspStyle="${ssConfigJspStyle}"
		  entry="${ssDefinitionEntry}" />
	  	
	  </div><!-- end of top -->
	</div><!-- end of div inset -->
</div>
 
<c:if test="${!empty propertyValues_type && !empty propertyValues_type[0] && propertyValues_type[0] == 'project'}">
	<%@ include file="/WEB-INF/jsp/definition_elements/workspace_statistics.jsp" %>
</c:if>
  
</div>
