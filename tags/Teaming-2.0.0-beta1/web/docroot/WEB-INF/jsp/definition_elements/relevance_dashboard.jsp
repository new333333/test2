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
<% //Relevance dashboard %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<% /* Set a variable that tells us if we are dealing with a TemplateBinder. */ %>
<% /* If we are dealing with a TemplateBinder we will only add the Overview tab. */ %>
<c:set var="usingTemplateBinder" value="false" scope="request" />
<c:if test="${!empty ssBinderConfig}">
	<c:set var="usingTemplateBinder" value="true" scope="request" />
</c:if>


<ssf:ifLoggedIn>
<script type="text/javascript">
var ss_relevanceAjaxUrl${renderResponse.namespace};
var ss_relevanceProfileUrl${renderResponse.namespace};
var ss_relevanceOverviewUrl${renderResponse.namespace};
if (ss_userDisplayStyle != "accessible") {
   ss_relevanceAjaxUrl${renderResponse.namespace} = "<ssf:url 
  		adapter="true" portletName="ss_forum" 
		action="__ajax_relevance" actionUrl="false"><ssf:param 
		name="operation" value="get_relevance_dashboard" /><ssf:param 
		name="type" value="ss_typePlaceHolder" /><ssf:param 
		name="type2" value="ss_type2PlaceHolder" /><ssf:param 
		name="type3" value="ss_type3PlaceHolder" /><ssf:param 
		name="page" value="0" /><ssf:param 
		name="binderId" value="ss_binderIdPlaceHolder" /><ssf:param 
		name="namespace" value="${renderResponse.namespace}" /><ssf:param 
		name="rn" value="ss_rnPlaceHolder" /></ssf:url>";
   ss_relevanceProfileUrl${renderResponse.namespace} = "<ssf:url 
        action="view_ws_listing" ><ssf:param 
      	name="binderId" value="ss_binderIdPlaceHolder"/><ssf:param 
		name="type" value="profile" /><ssf:param 
		name="page" value="0" /></ssf:url>";
   ss_relevanceOverviewUrl${renderResponse.namespace} = "<ssf:url 
        action="view_ws_listing" ><ssf:param 
      	name="binderId" value="ss_binderIdPlaceHolder"/><ssf:param 
		name="type" value="overview" /><ssf:param 
		name="page" value="0" /></ssf:url>";
} else {
  <c:if test="${ssBinder.entityType == 'workspace'}">
     ss_relevanceAjaxUrl${renderResponse.namespace} = "<ssf:url 
        action="view_ws_listing" ><ssf:param 
      	name="binderId" value="ss_binderIdPlaceHolder"/><ssf:param 
		name="type" value="ss_typePlaceHolder" /><ssf:param 
		name="type2" value="ss_type2PlaceHolder" /><ssf:param 
		name="type3" value="ss_type3PlaceHolder" /><ssf:param 
		name="page" value="ss_pagePlaceHolder" /></ssf:url>";
  </c:if>
  <c:if test="${ssBinder.entityType == 'folder'}">
     ss_relevanceAjaxUrl${renderResponse.namespace} = "<ssf:url 
    	action="view_folder_listing" ><ssf:param 
      	name="binderId" value="ss_binderIdPlaceHolder"/><ssf:param 
		name="type" value="ss_typePlaceHolder" /><ssf:param 
		name="type2" value="ss_type2PlaceHolder" /><ssf:param 
		name="type3" value="ss_type3PlaceHolder" /><ssf:param 
		name="page" value="ss_pagePlaceHolder" /></ssf:url>";
  </c:if>
}
</script>

<% //Tabs %>

<div id="ss_wrap" align="center">
<div id="ss_tabsC" align="left">
  <ul>
	<!-- CSS Tabs -->
	<% /* If we are dealing with a Template Binder select the Overview tab as the default tab. */ %>
	<c:if test="${usingTemplateBinder == 'true'}">
		<c:set var="ssRDCurrentTab" value="overview" scope="request"/>
	</c:if>

	<% /* Do we have a current tab? */ %>
	<c:if test="${empty ssRDCurrentTab}">
		<% /* No, set the current tab to the "Profile" tab. */ %>
		<c:set var="ssRDCurrentTab" value="profile" scope="request"/>

		<% /* Is the user looking at their own workspace? */ %>
		<c:if test="${ssBinder.id == ssUser.workspaceId}">
			<% /* Yes, set the current tab to the "Overview" tab. */ %>
			<c:set var="ssRDCurrentTab" value="overview" scope="request"/>
		</c:if>
	</c:if>

	<% /* Add the "Overview" tab as the first tab. */ %>
	<li <c:if test="${ssRDCurrentTab == 'overview'}">class="ss_tabsCCurrent"</c:if>>
		<a <c:if test="${ssRDCurrentTab == 'overview'}">id="ss_relevanceInitialTab${renderResponse.namespace}"</c:if>
			href="javascript: ;"
			<% /* We only need to do something when the user clicks on the overview tab if we are not dealing with a TemplateBinder. */ %>
			<c:choose>
		 		<c:when test="${usingTemplateBinder == 'false'}">
					onclick="ss_selectRelevanceTab(this, 'overview', '', '${ssBinder.id}', '${renderResponse.namespace}');return false;">
				</c:when>
			  	<c:otherwise>
					onclick="return false;">
				</c:otherwise>
			</c:choose>
			<span><ssf:nlt tag="relevance.tab.overview"/></span>
		</a>
	</li>

<% /* Only add the What's New tab if we are not dealing with a Template Binder. */ %>
<c:if test="${!empty ssRelevanceDashboardConfigElement && usingTemplateBinder == 'false'}">
	<li <c:if test="${ssRDCurrentTab == 'whatsNew'}">class="ss_tabsCCurrent"</c:if>
	><a 
	  <c:if test="${ssRDCurrentTab == 'whatsNew'}">id="ss_relevanceInitialTab${renderResponse.namespace}"</c:if>
	  href="javascript: ;"
		onclick="ss_selectRelevanceTab(this, 'whatsNew', '', '${ssBinder.id}', '${renderResponse.namespace}');return false;"
		><span><ssf:nlt tag="relevance.tab.whatsNew"/></span></a></li>
</c:if>
<c:if test="${empty ssRelevanceDashboardConfigElement && usingTemplateBinder == 'false'}">
  <c:if test="${empty ssRDCurrentTab}"><c:set var="ssRDCurrentTab" value="whatsNew" scope="request"/></c:if>
	<li <c:if test="${ssRDCurrentTab == 'whatsNew'}">class="ss_tabsCCurrent"</c:if>
	  class="ss_tabsCCurrent"><a 
	  <c:if test="${ssRDCurrentTab == 'whatsNew'}">id="ss_relevanceInitialTab${renderResponse.namespace}"</c:if>
	  href="javascript: ;"
		onclick="ss_selectRelevanceTab(this, 'whatsNew', '', '${ssBinder.id}', '${renderResponse.namespace}');return false;"
		><span><ssf:nlt tag="relevance.tab.whatsNew"/></span></a></li>
</c:if>

	<% /* Only add the other tabs if we are not dealing with a Template Binder. */ %>
	<c:if test="${usingTemplateBinder == 'false'}">
		<li <c:if test="${ssRDCurrentTab == 'tasks_and_calendars'}">class="ss_tabsCCurrent"</c:if>>
		<a 
		  <c:if test="${ssRDCurrentTab == 'tasks_and_calendars'}">id="ss_relevanceInitialTab${renderResponse.namespace}"</c:if>
		  href="javascript: ;"
			onclick="ss_selectRelevanceTab(this, 'tasks_and_calendars', '', '${ssBinder.id}', '${renderResponse.namespace}');return false;">
			<span><ssf:nlt tag="relevance.tab.tasksAndCalendars"/></span></a></li>
	
	
		<li <c:if test="${ssRDCurrentTab == 'activities'}">class="ss_tabsCCurrent"</c:if>>
		<a 
		  <c:if test="${ssRDCurrentTab == 'activities'}">id="ss_relevanceInitialTab${renderResponse.namespace}"</c:if>
		  href="javascript: ;"
			onclick="ss_selectRelevanceTab(this, 'activities', '', '${ssBinder.id}', '${renderResponse.namespace}');return false;">
			<span><ssf:nlt tag="relevance.tab.activities"/></span></a></li>
	
		<li <c:if test="${ssRDCurrentTab == 'miniblogs'}">class="ss_tabsCCurrent"</c:if>>
		<a 
		  <c:if test="${ssRDCurrentTab == 'miniblogs'}">id="ss_relevanceInitialTab${renderResponse.namespace}"</c:if>
		  href="javascript: ;"
			onclick="ss_selectRelevanceTab(this, 'miniblogs', '', '${ssBinder.id}', '${renderResponse.namespace}');return false;">
			<span><ssf:nlt tag="relevance.tab.miniblogs"/></span></a></li>
	
		<% /* Add the "Profile" tab */ %>
		<c:if test="${!empty ssRelevanceDashboardConfigElement}">
			<c:if test="${empty ssRDCurrentTab}"><c:set var="ssRDCurrentTab" value="profile" scope="request"/></c:if>
			<li <c:if test="${ssRDCurrentTab == 'profile'}">class="ss_tabsCCurrent"</c:if>>
				<a <c:if test="${ssRDCurrentTab == 'profile'}">id="ss_relevanceInitialTab${renderResponse.namespace}"</c:if>
					href="javascript: ;"
					onclick="ss_selectRelevanceTab(this, 'profile', '', '${ssBinder.id}', '${renderResponse.namespace}');return false;">
					<span><ssf:nlt tag="relevance.tab.profile"/></span>
				</a>
			</li>
		</c:if>
	</c:if>	
  </ul>
</div>
<div class="ss_clear_float"></div>
<script type="text/javascript">
var ss_relevanceTabCurrent_${renderResponse.namespace} = self.document.getElementById('ss_relevanceInitialTab${renderResponse.namespace}');
ss_loadJsFile(ss_rootPath, "js/common/ss_calendar.js");
</script>

<% //Changeable tab canvas; this gets replaced when a tab is clicked %>

<div id="relevanceCanvas_${renderResponse.namespace}" style="margin:4px 10px 10px 10px;">
<c:set var="ss_relevanceDashboardNamespace" value="${renderResponse.namespace}" scope="request"/>
<c:if test="${empty ssRelevanceDashboardConfigElement}">
  <c:if test="${ssRDCurrentTab == 'overview'}"><jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/overview.jsp" /></c:if>
  <c:if test="${ssRDCurrentTab == 'whatsNew'}"><jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/whats_new_tab.jsp" /></c:if>
  <c:if test="${ssRDCurrentTab == 'tasks_and_calendars'}"><jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/tasks_and_calendars_tab.jsp" /></c:if>
  <c:if test="${ssRDCurrentTab == 'activities'}"><jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/activities_tab.jsp" /></c:if>
  <c:if test="${ssRDCurrentTab == 'miniblogs'}"><jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/miniblogs_tab.jsp" /></c:if>
</c:if>
<c:if test="${!empty ssRelevanceDashboardConfigElement}">
  <c:if test="${ssRDCurrentTab == 'overview'}"><jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/overview.jsp" /></c:if>
  <c:if test="${ssRDCurrentTab == 'whatsNew'}"><jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/whats_new_tab.jsp" /></c:if>
  <c:if test="${ssRDCurrentTab == 'tasks_and_calendars'}"><jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/tasks_and_calendars_tab.jsp" /></c:if>
  <c:if test="${ssRDCurrentTab == 'activities'}"><jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/activities_tab.jsp" /></c:if>
  <c:if test="${ssRDCurrentTab == 'miniblogs'}"><jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/miniblogs_tab.jsp" /></c:if>
  <c:if test="${ssRDCurrentTab == 'profile'}"><jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/profile.jsp" /></c:if>
</c:if>
</div>
</div>
</ssf:ifLoggedIn>
