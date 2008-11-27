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
<%@ page import="com.sitescape.team.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<div id="ss_para">
	<div align="right">
		<c:if test="${ss_activitiesPage > '0'}">
			<a href="javascript: ;" 
		  		onClick="ss_showDashboardPage('${ssBinder.id}', '${ssRDCurrentTab}', 'miniblogs', '${ss_activitiesPage}', 'previous', 'ss_dashboardActivities', '${ss_relevanceDashboardNamespace}');return false;">
				<img src="<html:imagesPath/>pics/sym_arrow_left_.gif" 
		  		title="<ssf:nlt tag="general.previousPage"/>"/>
			</a>
		</c:if>
		<c:if test="${empty ss_activitiesPage || ss_activitiesPage <= '0'}">
			<img src="<html:imagesPath/>pics/sym_arrow_left_g.gif"/>
		</c:if>
		<c:if test="${!empty ss_activities}">
			<a href="javascript: ;" 
		  		onClick="ss_showDashboardPage('${ssBinder.id}', '${ssRDCurrentTab}', 'miniblogs', '${ss_activitiesPage}', 'next', 'ss_dashboardActivities', '${ss_relevanceDashboardNamespace}');return false;">
				<img src="<html:imagesPath/>pics/sym_arrow_right_.gif"
		  		title="<ssf:nlt tag="general.nextPage"/>"/>
			</a>
		</c:if>
		<c:if test="${empty ss_activities}">
			<img src="<html:imagesPath/>pics/sym_arrow_right_g.gif"/>
		</c:if>
	</div>

	<div id="ss_hints"><em><ssf:nlt tag="relevance.hint.miniblogs"/></em></div>
	
	<div id="ss_today">
	<div id="ss_mydocs_para">
	  <c:forEach var="activity" items="${ss_activities}">
	  
	    <li>
		  
		    	<ssf:showUser user="${activity.user}" titleStyle="ss_link_1"/>
		      
		        &nbsp;&nbsp;&nbsp;<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
					      value="${activity.date}" type="both" 
						  timeStyle="short" dateStyle="short" />
				<br/>
		    	<span class="ss_italic">${activity.description}</span>

	    </li><br/>
	    

	  </c:forEach>
   </div><!-- end of ss_mydocs_para -->
   </div><!-- end of ss_today -->

  <c:if test="${empty ss_activities && ss_pageNumber > '0'}">
    <span class="ss_italic"><ssf:nlt tag="whatsnew.noMoreEntriesFound"/></span>
  </c:if>
</div><!-- end of ss_para -->

