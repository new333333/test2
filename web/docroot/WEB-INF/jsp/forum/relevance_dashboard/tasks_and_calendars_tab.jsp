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
<%@ page import="org.kablink.teaming.util.SPropsUtil" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<div id="ss_dashboard_content" class="ss_doublecolumn">
  <div id="ss_column_L" class="ss_dblcolleft">
      <div class="ss_dblcol1">
      <!-- Start Left Column -->
      
	<c:if test="${empty ss_type3}"><c:set var="ss_type3" value="2weeks"/></c:if>
	<ssf:canvas id="relevanceTasks" type="inline" styleId="ss_tasks">
	<ssf:param name="title" useBody="true" >
		<div id="ss_title" class="ss_pt_title ss_green ss_tasks_img">
		  <ssf:nlt tag="relevance.tasksDue"/>
		</div>
	</ssf:param>
		<div style="padding-bottom:10px;">
		  <input type="radio" name="tasksType" value="2weeks"
		    <c:if test="${ss_type3 == '2weeks'}">checked="checked"</c:if>
		  	onclick="ss_selectRelevanceTab(null, 'tasks_and_calendars', '2weeks', '${ssBinderId}', '${renderResponse.namespace}');return false;"
		  ><a href="javascript: ;" 
		  	onclick="ss_selectRelevanceTab(null, 'tasks_and_calendars', '2weeks', '${ssBinderId}', '${renderResponse.namespace}');return false;"
		  ><span><ssf:nlt tag="relevance.tasksFewWeeks">
		    <ssf:param name="value" value='<%= SPropsUtil.getString("relevance.tasks2WeeksAhead") %>'/>
		  </ssf:nlt></span></a>
			  
		  <input type="radio" name="tasksType" value="all" style="padding-left:20px;"
		    <c:if test="${ss_type3 == 'all'}">checked="checked"</c:if>
		  	onclick="ss_selectRelevanceTab(null, 'tasks_and_calendars', 'all', '${ssBinderId}', '${renderResponse.namespace}');return false;"
		  ><a href="javascript: ;" 
		  	onclick="ss_selectRelevanceTab(null, 'tasks_and_calendars', 'all', '${ssBinderId}', '${renderResponse.namespace}');return false;"
		  ><span><ssf:nlt tag="relevance.tasksAll"/></span></a>
		  
		</div>
		<div id="ss_dashboardTasks${renderResponse.namespace}">
		  <jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/my_tasks.jsp" />
		</div>
	</ssf:canvas>
		
        </div><!-- end of ss_col 1 -->
        <!-- Start Right Column -->
      	<div id="ss_column_R">
      	<div class="ss_dblcol2">
      
	<ssf:canvas id="relevanceCalendars" type="inline" styleId="ss_calendar">
	<ssf:param name="title" useBody="true" >
		<div id="ss_title" class="ss_pt_title ss_green ss_cal_img"> 
		  <ssf:nlt tag="relevance.calendar"/> 
		</div>
	</ssf:param>
		<jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/my_calendars.jsp" />
	</ssf:canvas>

	<ssf:canvas id="relevanceFolders" type="inline" styleId="ss_trackedItems">
	<ssf:param name="title" useBody="true" >
		<div id="ss_title" class="ss_pt_title ss_green ss_tracked_img">
		  <ssf:nlt tag="relevance.trackedCalendars"/>
	    </div>
	</ssf:param>
	<jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/tracked_calendars.jsp" />
	</ssf:canvas>

      </div><!-- end of col2 (right column) -->
      </div><!-- end of ss_col_R (right column placeholder) -->
    </div><!-- end of col left -->

</div><!-- end of content -->
<div class="ss_clear_float"></div>
