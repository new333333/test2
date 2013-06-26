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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<!-- Start Radio Buttons -->
<div style="padding-bottom:10px;">
	<c:if test="${empty ss_type3}">
		<c:set var="ss_type3" value="teams"/>
	</c:if>
	<ssf:ifNotLoggedIn><c:set var="ss_type3" value="site"/></ssf:ifNotLoggedIn>
	<c:if test="${ssBinderId != ssUser.workspaceId && ss_type3 == 'teams'}">
		<c:set var="ss_type3" value="tracked"/>
	</c:if>
 	<ssf:ifLoggedIn>
		<c:if test="${ssBinderId == ssUser.workspaceId}">
			<input type="radio" name="whatsNewType" value="teams"
			<c:if test="${ss_type3 == 'teams'}">checked="checked"</c:if>
			onclick="ss_selectRelevanceTab(null, 'whatsNew', 'teams', '${ssBinderId}', '${renderResponse.namespace}');return false;"
		  ><a href="javascript: ;" 
			onclick="ss_selectRelevanceTab(null, 'whatsNew', 'teams', '${ssBinderId}', '${renderResponse.namespace}');return false;"
		  ><span style="padding-right:10px;"><ssf:nlt tag="relevance.whatsNewTypeTeams"/></span></a>
		</c:if>
	</ssf:ifLoggedIn>

	<ssf:ifLoggedIn>
		<input type="radio" name="whatsNewType" value="tracked" style="padding-left:20px;"
		<c:if test="${ss_type3 == 'tracked'}">checked="checked"</c:if>
		onclick="ss_selectRelevanceTab(null, 'whatsNew', 'tracked', '${ssBinderId}', '${renderResponse.namespace}');return false;"
		><a href="javascript: ;" 
		onclick="ss_selectRelevanceTab(null, 'whatsNew', 'tracked', '${ssBinderId}', '${renderResponse.namespace}');return false;"
		><span style="padding-right:10px;"><ssf:nlt tag="relevance.whatsNewTypeTracked"/></span></a>
	</ssf:ifLoggedIn>
   
	<input type="radio" name="whatsNewType" value="site" style="padding-left:20px;"
		<c:if test="${ss_type3 == 'site'}">checked="checked"</c:if>
		onclick="ss_selectRelevanceTab(null, 'whatsNew', 'site', '${ssBinderId}', '${renderResponse.namespace}');return false;"
		><a href="javascript: ;" 
		onclick="ss_selectRelevanceTab(null, 'whatsNew', 'site', '${ssBinderId}', '${renderResponse.namespace}');return false;"
		><span style="padding-right:10px;"><ssf:nlt tag="relevance.whatsNewTypeSite"/></span></a>
</div>

<div id="ss_dashboard_content" class="ss_doublecolumn">
	<table cellpadding="0" cellspacing="0" class="marginbottom3">
		<tr>
			<td width="50%" style="padding-right: 15px; vertical-align: top;">
			<!-- Start Left Column -->

				<c:if test="${ss_type3 == 'teams' && ssBinderId == ssUser.workspaceId}">
					<ssf:canvas id="relevanceTracked" type="inline" styleId="ss_shared">
						<div id="ss_dashboardWhatsNewTracked${renderResponse.namespace}">
						<jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/whats_new_teams.jsp" />
						</div>
					</ssf:canvas>
				</c:if>
		
				<c:if test="${ss_type3 == 'tracked'}">
					<ssf:canvas id="relevanceTracked" type="inline" styleId="ss_shared">
						<div id="ss_dashboardWhatsNewTracked${renderResponse.namespace}">
						<jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/whats_new_tracked.jsp" />
						</div>
					</ssf:canvas>
				</c:if>
	
				<c:if test="${ss_type3 == 'site'}">
					<ssf:canvas id="relevanceWhatsNewSite" type="inline" styleId="ss_trackedItems">
						<div id="ss_dashboardWhatsNewSite${renderResponse.namespace}">
						  <jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/whats_new_site.jsp" /></div>
					</ssf:canvas>
				</c:if>
        	<!-- end of left column -->
			</td>
			<td width="50%" style="padding-right:10px; vertical-align:top;">
	        <!-- Start Right Column -->

				<c:if test="${ss_type3 == 'tracked'}">
					<ssf:canvas id="relevancePeople" type="inline" styleId="ss_trackedPeople">
					<ssf:param name="title" useBody="true" >
						<div id="ss_title" class="ss_pt_title ss_green ss_para" style="margin-top: 7px;">
						  <ssf:nlt tag="relevance.trackedPeople"/></div>
					</ssf:param>
					  <c:if test="${ssBinderId == ssUser.workspaceId}">
						<c:set var="ss_show_tracked_item_delete_button" value="true" scope="request"/>
					  </c:if>
					  <jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/tracked_people.jsp" />
					</ssf:canvas>
				<div class="margintop3">	
					<ssf:canvas id="relevanceFolders" type="inline" styleId="ss_trackedItems">
					<ssf:param name="title" useBody="true" >
						<div id="ss_title" class="ss_pt_title ss_green">
						  <ssf:nlt tag="relevance.trackedFolders"/>
						</div>
					</ssf:param>
					<jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/tracked_items.jsp" />
					</ssf:canvas>
				</div>	
				</c:if>
				
				<c:if test="${0 == 1 && ss_type3 == 'site'}">  
					<!-- What's Hot has been turned off due to bad performance  -->
					<ssf:canvas id="relevanceHot" type="inline" styleId="ss_whatshot">
						<div id="ss_dashboardWhatsHot${renderResponse.namespace}">
						  <jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/whats_hot.jsp" />
						</div>
					</ssf:canvas>
				</c:if>
			<!-- end of right column -->
			</td>
		</td>
	</table>	
</div><!-- end of content -->
