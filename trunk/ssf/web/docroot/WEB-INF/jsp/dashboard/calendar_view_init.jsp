<%
// The dashboard "search" component
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
  //this is used by penlets and portlets
 //Don't include "include.jsp" directly 
%>
<%@ include file="/WEB-INF/jsp/dashboard/common_setup.jsp" %>
<c:set var="prefix" value="${ssComponentId}${renderResponse.namespace}" />

<script type="text/javascript">

	var ss_saveSubscriptionUrl = "<portlet:actionURL windowState="maximized"><portlet:param 
			name="action" value="${action}"/><portlet:param 
			name="binderId" value="${ssBinder.id}"/><portlet:param 
			name="operation" value="subscribe"/></portlet:actionURL>";

	var ss_findEventsUrl${prefix} = "<ssf:url 
		    	adapter="true" 
		    	portletName="ss_forum" 
		    	action="__ajax_request" 
		    	actionUrl="true" >
					<ssf:param name="binderId" value="${ssBinder.id}" />
					<ssf:param name="operation" value="find_calendar_events" />
		    	</ssf:url><c:if test="${!empty ssDashboard}">&ssDashboardRequest=true</c:if>";
		    	
	var ss_viewEventUrl = ss_viewEntryURL;
				
	var ss_stickyCalendarDisplaySettings =  "<ssf:url 
		    	adapter="true" 
		    	portletName="ss_forum" 
		    	action="__ajax_request" 
		    	actionUrl="true" >
					<ssf:param name="binderId" value="${ssBinder.id}" />
					<ssf:param name="operation" value="sticky_calendar_display_settings" />
		    	</ssf:url>";				
	
	var ss_addCalendarEntryUrl = "${addDefaultEntryURL}";
	if (ss_addCalendarEntryUrl.indexOf("addEntryFromIFrame=1&") > -1) {
		ss_addCalendarEntryUrl = ss_addCalendarEntryUrl.replace("addEntryFromIFrame=1&", "");
	}
					
	var ss_calendarWorkDayGridTitle = "<ssf:nlt tag="calendar.hours.workday"/>";
	var ss_calendarFullDayGridTitle = "<ssf:nlt tag="calendar.hours.fullday"/>";
	
	function ss_getMonthCalendarEvents${prefix}() {
		var formObj = document.getElementById("ssCalNavBar${prefix}");
		if (formObj && formObj.ss_goto${prefix}_year && formObj.ss_goto${prefix}_month && formObj.ss_goto${prefix}_date) {
			ss_calendar_${prefix}.ss_cal_Events.switchView("datedirect", formObj.ss_goto${prefix}_year.value, formObj.ss_goto${prefix}_month.value - 1, formObj.ss_goto${prefix}_date.value);
		}
	}

	ss_calendar_${prefix} = new ss_calendar("${prefix}");
	ss_calendar_${prefix}.ss_cal_Grid.readOnly = true;

	ss_addDashboardEvent("${componentId}", 
						"onAfterShow",
						function() {
							ss_calendar_${prefix}.ss_initializeCalendar();
							
							ss_createOnLoadObj('ss_cal_hoverBox${prefix}', function() {
								ss_moveDivToBody("hoverBox${prefix}");
								ss_moveDivToBody("infoLightBox${prefix}");
								ss_moveDivToBody("infoBox${prefix}");
								ss_moveDivToBody("infoBox2${prefix}");
							});
						});
	if (!window.ssScope) { ssScope = {}; };
		ssScope.refreshView = function (entryId) {
		ss_calendar_${prefix}.refreshEntryEvents(entryId);
	}

</script>
