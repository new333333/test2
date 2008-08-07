<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.     
 */
%>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>


<ssf:sidebarPanel title="Team IT xxx" id="ss_team_sidebar" divClass="ss_place_tags" initOpen="true" sticky="true">

	<div class="ss_sub_sidebarMenu">
		<table width="100%"><tbody>
		  <c:if test="${!empty ss_toolbar_team_view_url}">
			<tr class="ss_rollover">
              <td><a href="${ss_toolbar_team_view_url}"><ssf:nlt tag="team.viewTeamMembership"/></a></td>
            </tr>
          </c:if>
		  <c:if test="${!empty ss_toolbar_team_add_url}">
            <tr>
              <td class="ss_rollover"><a href="${ss_toolbar_team_add_url}"
                onClick="ss_openUrlInWindow(this, '_blank', 500, 600);return false;"
              ><ssf:nlt tag="team.manageTeam"/></a></td>
            </tr>
          </c:if>
		  <c:if test="${!empty ss_toolbar_team_sendmail_url}">
			<tr>
              <td class="ss_rollover"><a href="${ss_toolbar_team_sendmail_url}"
                onClick="ss_openUrlInWindow(this, '_blank', 600, 800);return false;"
              ><ssf:nlt tag="team.sendMail"/></a></td>
            </tr>
          </c:if>
		  <c:if test="${!empty ss_toolbar_team_meet_url}">
            <tr class="ss_rollover">
              <td><a href="${ss_toolbar_team_meet_url}"
                onClick="ss_openUrlInWindow(this, '_blank', 500, 600);return false;"
              ><ssf:nlt tag="team.startTeamMeeting"/></a></td>
            </tr>
          </c:if>
		</tbody></table>
	</div>

</ssf:sidebarPanel>

