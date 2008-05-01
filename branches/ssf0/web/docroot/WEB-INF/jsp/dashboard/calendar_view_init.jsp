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
		<% //needs its own script section to endure loaded before accessed below %>
 		ss_loadJsFile(ss_rootPath, "js/common/ss_calendar.js");
	</script>

<script type="text/javascript">
	dojo.require("dojo.lfx.rounded");
	
	function ss_getMonthCalendarEvents${prefix}() {
		var formObj = document.getElementById("ssCalNavBar${prefix}");
		if (formObj && formObj.ss_goto${prefix}_year && formObj.ss_goto${prefix}_month && formObj.ss_goto${prefix}_date) {
			ss_calendar_${prefix}.switchView("datedirect", formObj.ss_goto${prefix}_year.value, formObj.ss_goto${prefix}_month.value - 1, formObj.ss_goto${prefix}_date.value);
		}
	}
	
	<c:set var="binderIds" value="" />
	<c:set var="defaultCalendarId" value="" />
	<c:forEach var="folder" items="${ssDashboard.beans[componentId].ssFolderList}" varStatus="status">
		<c:choose>
			<c:when test="${status.first}">
				<c:set var="binderIds" value="${folder.id}" />
				<c:set var="defaultCalendarId" value="${folder.id}" />
			</c:when>
			<c:otherwise>
				<c:set var="binderIds" value="${binderIds}, ${folder.id}" />
			</c:otherwise>
		</c:choose>
	</c:forEach>
	
	ss_calendar_${prefix} = ss_calendar.createCalendar({
		containerId: "ss_calendar_container${prefix}", 
		readOnly: true,
		calendarDataProvider: new ss_calendar_data_provider("${ssBinder.id}",
									<c:choose><c:when test="${binderIds == ''}">"none"</c:when><c:otherwise>[${binderIds}]</c:otherwise></c:choose>
									, "${ssBinder.id}_${componentId}"
									<c:if test="${!empty ssDashboard}">, true</c:if>), 
	    defaultCalendarId: "<c:choose><c:when test="${defaultCalendarId != ''}">${defaultCalendarId}</c:when><c:otherwise>${ssBinder.id}</c:otherwise></c:choose>",
	    <c:if test="${!empty ssUserProperties.calendarFirstDayOfWeek}">
	    	weekFirstDay: "${ssUserProperties.calendarFirstDayOfWeek}",
	    </c:if>
	    <c:if test="${!empty ssUserProperties.calendarWorkDayStart}">
	    	workDayStart: ${ssUserProperties.calendarWorkDayStart},
	    </c:if>
	    viewDatesDescriptionsFieldId : "ss_calViewDatesDescriptions${prefix}",
	    viewSelectorHrefIds: {
			days1: "ss_calDaySelectButton${prefix}", 
			days3: "ss_cal3DaysSelectButton${prefix}", 
			days5: "ss_cal5DaysSelectButton${prefix}", 
	    	days7: "ss_cal7DaysSelectButton${prefix}", 
			days14: "ss_cal14DaysSelectButton${prefix}", 
			month: "ss_calMonthSelectButton${prefix}"
		},
		calendarHoursSelectorId: "ss_selectCalendarHours${prefix}",
		eventsTypeChooseId: "ss_calendarEventsTypeChoose${prefix}",
		eventsTypeSelectId: "ss_calendarEventsTypeSelect${prefix}",
		onCalendarStyleChoose : function(binderId, cssStyle) {
			var calendarHref = document.getElementById("ssDashboardFolderLink${prefix}" + binderId);			
			if (calendarHref && calendarHref.style.borderWidth == "") {
				calendarHref.style.borderWidth = "1px";
				calendarHref.style.borderStyle = "solid";				
				calendarHref.style.marginBottom = "2px";
				dojo.html.addClass(calendarHref, cssStyle);
				
				dojo.lfx.rounded(
					{tl: {radius:2}, 
					tr: {radius:2}, 
					bl: {radius:2}, 
					br: {radius:2}}, [calendarHref]); 
			}
		},
		stickyId : "${ssBinder.id}_${componentId}"
	});
	
	ss_calendar_${prefix}.locale.workDayGridTitle = "<ssf:nlt tag="calendar.hours.workday"/>";
	ss_calendar_${prefix}.locale.fullDayGridTitle = "<ssf:nlt tag="calendar.hours.fullday"/>";
	ss_calendar_${prefix}.locale.entriesLabel = "<ssf:nlt tag="statistic.unity.plural"/>";
	ss_calendar_${prefix}.locale.dayNamesShort = ["<ssf:nlt tag="calendar.day.abbrevs.su"/>", "<ssf:nlt tag="calendar.day.abbrevs.mo"/>", "<ssf:nlt tag="calendar.day.abbrevs.tu"/>", "<ssf:nlt tag="calendar.day.abbrevs.we"/>", "<ssf:nlt tag="calendar.day.abbrevs.th"/>", "<ssf:nlt tag="calendar.day.abbrevs.fr"/>", "<ssf:nlt tag="calendar.day.abbrevs.sa"/>"];
	ss_calendar_${prefix}.locale.monthNamesShort = ["<ssf:nlt tag="calendar.abbreviation.january"/>", "<ssf:nlt tag="calendar.abbreviation.february"/>", "<ssf:nlt tag="calendar.abbreviation.march"/>", "<ssf:nlt tag="calendar.abbreviation.april"/>", "<ssf:nlt tag="calendar.abbreviation.may"/>", "<ssf:nlt tag="calendar.abbreviation.june"/>", "<ssf:nlt tag="calendar.abbreviation.july"/>", "<ssf:nlt tag="calendar.abbreviation.august"/>", "<ssf:nlt tag="calendar.abbreviation.september"/>", "<ssf:nlt tag="calendar.abbreviation.october"/>", "<ssf:nlt tag="calendar.abbreviation.november"/>", "<ssf:nlt tag="calendar.abbreviation.december"/>"];
	ss_calendar_${prefix}.locale.monthNames = ["<ssf:nlt tag="calendar.january"/>", "<ssf:nlt tag="calendar.february"/>", "<ssf:nlt tag="calendar.march"/>", "<ssf:nlt tag="calendar.april"/>", "<ssf:nlt tag="calendar.may"/>", "<ssf:nlt tag="calendar.june"/>", "<ssf:nlt tag="calendar.july"/>", "<ssf:nlt tag="calendar.august"/>", "<ssf:nlt tag="calendar.september"/>", "<ssf:nlt tag="calendar.october"/>", "<ssf:nlt tag="calendar.november"/>", "<ssf:nlt tag="calendar.december"/>"];
	ss_calendar_${prefix}.locale.allDay = "<ssf:nlt tag="calendar.allDay"/>";
	ss_calendar_${prefix}.locale.noTitle = "--<ssf:nlt tag="entry.noTitle"/>--";
											
	ss_addDashboardEvent("${componentId}", 
						"onAfterShow",
						function() {
							ss_calendar_${prefix}.ss_initializeCalendar();
						});
	ss_addDashboardEvent("${componentId}", 
						"onAfterHide",
						function() {
							ss_calendar_${prefix}.ss_uninitializeCalendar();
						});						
	if (!window["ssScope"]) { ssScope = {}; };
	ssScope.refreshView = function (entryId) {
		ss_calendar_${prefix}.refreshEntryEvents(entryId);
	}

</script>
