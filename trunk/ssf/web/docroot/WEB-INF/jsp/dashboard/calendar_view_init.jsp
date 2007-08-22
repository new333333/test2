<%
// The dashboard "search" component
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
