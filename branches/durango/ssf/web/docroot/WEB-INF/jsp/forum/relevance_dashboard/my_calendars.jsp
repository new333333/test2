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

<c:if test="${empty ss_whatsNewTrackedCalendars}">
<span style="padding: 5px 15px;"><ssf:nlt tag="relevance.none"/></span>
</c:if>

<c:if test="${!empty ss_whatsNewTrackedCalendars}">
<div id="ss_para" class="ss_paraC">
	<div id="ss_today">
		<div id="ss_cal_para" > 
			<script type="text/javascript">
				ss_createOnLoadObj("my_calendars", onLoadHandler_my_calendars);
				function onLoadHandler_my_calendars() {
					ss_calendar_${ss_namespace} = ss_calendar.createCalendar({
						containerId: "ss_cal_para", 
						calendarDataProvider: new function() {
								this.loadEventsByDate = function(reqParams, date, calendarObj) {
									calendarObj.addEvents(<jsp:include page="/WEB-INF/jsp/forum/json/events_uncommented.jsp" />, date);
								}
								this.stickyCalendarDisplaySettings = function(){}
								this.loadEntryEvents = function(options) {}
						}, 
						readOnly: true,
					    defaultCalendarId: "${ssBinder.id}",
					    weekFirstDayDefault:  "${ssUser.weekFirstDayDefault}",
					    <c:if test="${empty ssUserProperties.calendarFirstDayOfWeek}">
				    		weekFirstDay: "${ssUser.weekFirstDayDefault}",
					    </c:if>
					    <c:if test="${!empty ssUserProperties.calendarFirstDayOfWeek}">
					    	weekFirstDay: "${ssUserProperties.calendarFirstDayOfWeek}",
					    </c:if>
					    workDayStartDefault:  ${ssUser.workDayStartDefault},
					    <c:if test="${empty ssUserProperties.calendarWorkDayStart}">
					    	workDayStart: ${ssUser.workDayStartDefault},
					    </c:if>
					    <c:if test="${!empty ssUserProperties.calendarWorkDayStart}">
					    	workDayStart: ${ssUserProperties.calendarWorkDayStart},
					    </c:if>
					    viewDatesDescriptionsFieldId : "ss_calViewDatesDescriptions${ss_namespace}",
					    viewSelectorHrefIds: {
							days1: "ss_calDaySelectButton${ss_namespace}", 
							days3: "ss_cal3DaysSelectButton${ss_namespace}", 
							days5: "ss_cal5DaysSelectButton${ss_namespace}", 
					    	days7: "ss_cal7DaysSelectButton${ss_namespace}", 
							days14: "ss_cal14DaysSelectButton${ss_namespace}", 
							month: "ss_calMonthSelectButton${ss_namespace}"
						},
						calendarHoursSelectorId: "ss_selectCalendarHours${ss_namespace}",
						eventsTypeChooseId: "ss_calendarEventsTypeChoose${ss_namespace}",
						eventsTypeSelectId: "ss_calendarEventsTypeSelect${ss_namespace}",
						addEntryURL: "${addDefaultEntryURL}".replace("addEntryFromIFrame=1&", ""),
						stickyId: "${ssBinder.id}"
					});
			
					ss_calendar_${ss_namespace}.locale.workDayGridTitle = "<ssf:nlt tag="calendar.hours.workday"/>";
					ss_calendar_${ss_namespace}.locale.fullDayGridTitle = "<ssf:nlt tag="calendar.hours.fullday"/>";
					ss_calendar_${ss_namespace}.locale.entriesLabel = "<ssf:nlt tag="statistic.unity.plural"/>";
					ss_calendar_${ss_namespace}.locale.minutesShortLabel = "<ssf:nlt tag="calendar.minutes.shortLabel"/>";
					ss_calendar_${ss_namespace}.locale.dayNamesShort = ["<ssf:nlt tag="calendar.day.abbrevs.su"/>", "<ssf:nlt tag="calendar.day.abbrevs.mo"/>", "<ssf:nlt tag="calendar.day.abbrevs.tu"/>", "<ssf:nlt tag="calendar.day.abbrevs.we"/>", "<ssf:nlt tag="calendar.day.abbrevs.th"/>", "<ssf:nlt tag="calendar.day.abbrevs.fr"/>", "<ssf:nlt tag="calendar.day.abbrevs.sa"/>"];
					ss_calendar_${ss_namespace}.locale.monthNamesShort = ["<ssf:nlt tag="calendar.abbreviation.january"/>", "<ssf:nlt tag="calendar.abbreviation.february"/>", "<ssf:nlt tag="calendar.abbreviation.march"/>", "<ssf:nlt tag="calendar.abbreviation.april"/>", "<ssf:nlt tag="calendar.abbreviation.may"/>", "<ssf:nlt tag="calendar.abbreviation.june"/>", "<ssf:nlt tag="calendar.abbreviation.july"/>", "<ssf:nlt tag="calendar.abbreviation.august"/>", "<ssf:nlt tag="calendar.abbreviation.september"/>", "<ssf:nlt tag="calendar.abbreviation.october"/>", "<ssf:nlt tag="calendar.abbreviation.november"/>", "<ssf:nlt tag="calendar.abbreviation.december"/>"];
					ss_calendar_${ss_namespace}.locale.monthNames = ["<ssf:nlt tag="calendar.january"/>", "<ssf:nlt tag="calendar.february"/>", "<ssf:nlt tag="calendar.march"/>", "<ssf:nlt tag="calendar.april"/>", "<ssf:nlt tag="calendar.may"/>", "<ssf:nlt tag="calendar.june"/>", "<ssf:nlt tag="calendar.july"/>", "<ssf:nlt tag="calendar.august"/>", "<ssf:nlt tag="calendar.september"/>", "<ssf:nlt tag="calendar.october"/>", "<ssf:nlt tag="calendar.november"/>", "<ssf:nlt tag="calendar.december"/>"];
					ss_calendar_${ss_namespace}.locale.allDay = "<ssf:nlt tag="calendar.allDay"/>";
					ss_calendar_${ss_namespace}.locale.noTitle = "--<ssf:nlt tag="entry.noTitle"/>--";
					ss_calendar_${ss_namespace}.ss_initializeCalendar();
				}
			</script>
		</div><!-- end of para -->
	</div><!-- end of today -->
</div><!-- end of ss_para -->
</c:if>

