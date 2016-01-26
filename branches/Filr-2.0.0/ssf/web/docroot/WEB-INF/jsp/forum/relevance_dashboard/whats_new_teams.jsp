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
<div id="ss_para">

	<div id="ss_title" class="ss_pt_title ss_green">
		<ssf:nlt tag="relevance.whatsNewTeams"/>

		<span class="col-nextback-but">
		<c:if test="${ss_trackedTeamsPage > '0'}">
			<a href="javascript: ;" 
			  onclick="ss_showDashboardPage('${ssBinder.id}', '${ssRDCurrentTab}', 'newTeams', '${ss_trackedTeamsPage}', 'previous', 'ss_dashboardWhatsNewTracked', '${ss_relevanceDashboardNamespace}');return false;">
			<img align="absmiddle" src="<html:imagesPath/>pics/sym_arrow_left_.png" 
			  title="<ssf:nlt tag="general.previousPage"/>" <ssf:alt/>/>
			</a>
		</c:if>
		<c:if test="${empty ss_trackedTeamsPage || ss_trackedTeamsPage <= '0'}">
			<img align="absmiddle" src="<html:imagesPath/>pics/sym_arrow_left_g.png" <ssf:alt/>/>
		</c:if>
		<c:if test="${!empty ss_whatsNewTeamPlaces}">
			<a href="javascript: ;" 
			  onclick="ss_showDashboardPage('${ssBinder.id}', '${ssRDCurrentTab}', 'newTeams', '${ss_trackedTeamsPage}', 'next', 'ss_dashboardWhatsNewTracked', '${ss_relevanceDashboardNamespace}');return false;">
			<img align="absmiddle" src="<html:imagesPath/>pics/sym_arrow_right_.png"
			  title="<ssf:nlt tag="general.nextPage"/>" <ssf:alt/>/>
			</a>
		</c:if>
		<c:if test="${empty ss_whatsNewTeamPlaces}">
			<img align="absmiddle" src="<html:imagesPath/>pics/sym_arrow_right_g.png" <ssf:alt/>/>
		</c:if>
		</span>
	</div><!--end of arrow division-->

	<div id="ss_today">
  		<div id="ss_mydocs_para" >
			<c:forEach var="entry" items="${ss_whatsNewTeamPlaces}">
			<jsp:useBean id="entry" type="java.util.Map" />
	    	<div class="item">
				<c:set var="isDashboard" value="yes"/>
				<ssf:titleLink hrefClass="ss_link_3"
					entryId="${entry._docId}" binderId="${entry._binderId}" 
					entityType="${entry._entityType}" 
					namespace="${ss_namespace}" 
					isDashboard="${isDashboard}" dashboardType="${ssDashboard.scope}">
					<ssf:param name="url" useBody="true">
						<ssf:url adapter="true" portletName="ss_forum" folderId="${entry._binderId}" 
						  action="view_folder_entry" entryId="${entry._docId}" actionUrl="true" />
					</ssf:param>
					<c:out value="${entry.title}" escapeXml="false"/>
				</ssf:titleLink>
			  
				<div class="item-sub margintop1">
					<ssf:showUser user='<%=(org.kablink.teaming.domain.User)entry.get("_principal")%>' titleStyle="ss_link_1" /> 
					<span class="ss_entryDate">
						<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
					  value="${entry._modificationDate}" type="both" 
					  timeStyle="short" dateStyle="medium" />
					</span>
			  
					<c:set var="path" value=""/>
					<c:if test="${!empty ss_whatsNewTeamPlacesFolders[entry._binderId]}">
						<c:set var="path" value="${ss_whatsNewTeamPlacesFolders[entry._binderId]}"/>
						<c:set var="title" value="${ss_whatsNewTeamPlacesFolders[entry._binderId].parentBinder.title} // ${ss_whatsNewTeamPlacesFolders[entry._binderId].title}"/>
					</c:if>

					<c:if test="${!empty path}">
						<div class="list-indent">
						  <span class="ss_link_2">
							<c:set var="isDashboard" value="yes"/>
							<c:if test="${!empty path}">
								<a href="javascript: ;"
									onclick="return ss_gotoPermalink('${entry._binderId}', '${entry._binderId}', 'folder', '${ss_namespace}', 'yes');"
									title="${path}"
									><span><ssf:makeWrapable>${title}</ssf:makeWrapable></span></a>
							</c:if>
						  </span>
						</div>
					</c:if>
					<c:if test="${!empty entry._desc}">
						<div class="list-indent">
							<span class="ss_summary"><ssf:textFormat 
							  formatAction="limitedDescription" 
							  textMaxWords="15"><ssf:markup search="${entry}">${entry._desc}</ssf:markup></ssf:textFormat>
							</span>
						</div>
					</c:if>
				</div>
			</div>
			</c:forEach>

			<c:if test="${empty ss_whatsNewTeamPlaces && ss_pageNumber > '0'}">
				<span class="ss_italic" style="padding: 5px;"><ssf:nlt tag="whatsnew.noMoreEntriesFound"/></span>
			</c:if>

		</div><!-- end of para -->
	</div><!-- end of today -->
</div><!-- end of ss_para -->

