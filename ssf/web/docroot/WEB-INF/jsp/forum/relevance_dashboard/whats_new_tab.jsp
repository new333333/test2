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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<div style="padding-bottom:10px;">
<c:if test="${empty ss_type3}"><c:set var="ss_type3" value="teams"/></c:if>
<c:if test="${ssBinderId != ssUser.workspaceId && ss_type3 == 'teams'}"><c:set var="ss_type3" value="tracked"/></c:if>
  <c:if test="${ssBinderId == ssUser.workspaceId}">
	  <input type="radio" name="whatsNewType" value="teams"
	    <c:if test="${ss_type3 == 'teams'}">checked="checked"</c:if>
	  	onClick="ss_selectRelevanceTab(null, 'whats_new', 'teams', '${ssBinderId}', '${renderResponse.namespace}');return false;"
	  ><a href="javascript: ;" 
	  	onClick="ss_selectRelevanceTab(null, 'whats_new', 'teams', '${ssBinderId}', '${renderResponse.namespace}');return false;"
	  ><span><ssf:nlt tag="relevance.whatsNewTypeTeams"/></span></a>
  </c:if>
	  
  <input type="radio" name="whatsNewType" value="tracked" style="padding-left:20px;"
    <c:if test="${ss_type3 == 'tracked'}">checked="checked"</c:if>
  	onClick="ss_selectRelevanceTab(null, 'whats_new', 'tracked', '${ssBinderId}', '${renderResponse.namespace}');return false;"
  ><a href="javascript: ;" 
  	onClick="ss_selectRelevanceTab(null, 'whats_new', 'tracked', '${ssBinderId}', '${renderResponse.namespace}');return false;"
  ><span><ssf:nlt tag="relevance.whatsNewTypeTracked"/></span></a>
  
  <input type="radio" name="whatsNewType" value="site" style="padding-left:20px;"
    <c:if test="${ss_type3 == 'site'}">checked="checked"</c:if>
  	onClick="ss_selectRelevanceTab(null, 'whats_new', 'site', '${ssBinderId}', '${renderResponse.namespace}');return false;"
  ><a href="javascript: ;" 
  	onClick="ss_selectRelevanceTab(null, 'whats_new', 'site', '${ssBinderId}', '${renderResponse.namespace}');return false;"
  ><span><ssf:nlt tag="relevance.whatsNewTypeSite"/></span></a>
  
</div>

<div id="ss_dashboard_content" class="ss_doublecolumn">
  <div id="ss_column_L" class="ss_dblcolleft">
      <div class="ss_dblcol1">
      <!-- Start Left Column -->

		<c:if test="${ss_type3 == 'teams' && ssBinderId == ssUser.workspaceId}">
			<ssf:canvas id="relevanceTracked" type="inline" styleId="ss_shared">
			<ssf:param name="title" useBody="true" >
				<div id="ss_title" class="ss_pt_title ss_green ss_tracked_img">
				  <ssf:nlt tag="relevance.whatsNewTeams"/>
				</div>
			</ssf:param>
				<div id="ss_dashboardWhatsNewTracked${renderResponse.namespace}">
				<jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/whats_new_teams.jsp" />
				</div>
			</ssf:canvas>
		</c:if>
		
		<c:if test="${ss_type3 == 'tracked'}">
			<ssf:canvas id="relevanceTracked" type="inline" styleId="ss_shared">
			<ssf:param name="title" useBody="true" >
				<div id="ss_title" class="ss_pt_title ss_green ss_tracked_img">
				  <ssf:nlt tag="relevance.whatsNewTracked"/>
				</div>
			</ssf:param>
				<div id="ss_dashboardWhatsNewTracked${renderResponse.namespace}">
				<jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/whats_new_tracked.jsp" />
				</div>
			</ssf:canvas>
		</c:if>
	

		<c:if test="${ss_type3 == 'site'}">
			<ssf:canvas id="relevanceWhatsNewSite" type="inline" styleId="ss_trackedItems">
			<ssf:param name="title" useBody="true" >
				<div id="ss_title" class="ss_pt_title ss_green">
		          <ssf:nlt tag="relevance.whatsNewSite"/>
		        </div>
			</ssf:param>
				<div id="ss_dashboardWhatsNewSite${renderResponse.namespace}">
				  <jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/whats_new_site.jsp" /></div>
			</ssf:canvas>
		</c:if>

        </div><!-- end of ss_col 1 -->
        <!-- Start Right Column -->
      	<div id="ss_column_R">
      	<div class="ss_dblcol2">

		<c:if test="${ss_type3 == 'tracked'}">
			<ssf:canvas id="relevancePeople" type="inline" styleId="ss_trackedPeople">
			<ssf:param name="title" useBody="true" >
				<div id="ss_title" class="ss_pt_title ss_green ss_ping_img ss_para">
				  <ssf:nlt tag="relevance.trackedPeople"/></div>
			</ssf:param>
			  <c:set var="ss_show_tracked_item_delete_button" value="true" scope="request"/>
			  <jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/tracked_people.jsp" />
			</ssf:canvas>
			<br/>
			<br/>
			<ssf:canvas id="relevanceFolders" type="inline" styleId="ss_trackedItems">
			<ssf:param name="title" useBody="true" >
				<div id="ss_title" class="ss_pt_title ss_green ss_tracked_img">
				  <ssf:nlt tag="relevance.trackedFolders"/>
			    </div>
			</ssf:param>
			<jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/tracked_items.jsp" />
			</ssf:canvas>
		</c:if>
		
		<c:if test="${ss_type3 == 'site'}">
			<ssf:canvas id="relevanceHot" type="inline" styleId="ss_whatshot">
			<ssf:param name="title" useBody="true" >
				<div id="ss_title" class="ss_pt_title ss_green"><ssf:nlt tag="relevance.whatsHot"/></div>
			</ssf:param>
				<div id="ss_dashboardWhatsHot${renderResponse.namespace}">
				  <jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/whats_hot.jsp" />
				</div>
			</ssf:canvas>
		</c:if>

      </div><!-- end of col2 (right column) -->
      </div><!-- end of ss_col_R (right column placeholder) -->
    </div><!-- end of col left -->

</div><!-- end of content -->
<div class="ss_clear_float"></div>
