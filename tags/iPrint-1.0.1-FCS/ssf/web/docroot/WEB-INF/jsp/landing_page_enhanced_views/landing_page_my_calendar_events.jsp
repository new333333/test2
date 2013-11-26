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
<%
/**
 * My Calendar Events Landing Page Display
 */
%>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%  
	Long ss_mashupTableNumber = (Long) request.getAttribute("ss_mashupTableNumber");
	Long ss_mashupTableDepth = (Long) request.getAttribute("ss_mashupTableDepth");
	Map ss_mashupTableItemCount = (Map) request.getAttribute("ss_mashupTableItemCount");
	ss_mashupTableItemCount.put(ss_mashupTableNumber, "url");  
	request.setAttribute("ss_mashupTableItemCount", ss_mashupTableItemCount);

	Long ss_mashupListDepth = (Long) request.getAttribute("ss_mashupListDepth");
%>
<%@ page import="java.util.TreeMap" %>
<%@ page import="org.kablink.teaming.comparator.StringComparator" %>
<jsp:useBean id="ssUser" type="org.kablink.teaming.domain.User" scope="request" />
<c:if test="${empty landingPageCalendarPrefix}">
  <c:set var="landingPageCalendarPrefix" value="0" scope="request"/>
</c:if>
<c:set var="landingPageCalendarPrefix" value="${landingPageCalendarPrefix + 1}" scope="request"/>
<c:set var="prefix" value="${landingPageCalendarPrefix}" />
<c:set var="componentId" value="landingPage${prefix}" />

<c:set var="calendarWidth" value="100%" />
<c:set var="calendarHeight" value="100%" />
<c:set var="calendarOverflow" value="auto" />
<c:set var="calendarPadding" value="" />

<c:if test="${!empty mashup_attributes['width']}">
	<c:set var="calendarWidth" value="${mashup_attributes['width']}" />
</c:if>

<c:if test="${!empty mashup_attributes['height']}">
	<c:set var="calendarHeight" value="${mashup_attributes['height']}" />
</c:if>

<c:if test="${!empty mashup_attributes['overflow']}">
	<c:set var="calendarOverflow" value="${mashup_attributes['overflow']}" />
	<c:if test="${mashup_attributes['overflow'] == 'auto'}">
	  <c:set var="calendarPadding" value="padding: 0 1px 0 0;" />
	</c:if>
</c:if>

<% if (ss_mashupListDepth > 0) { %>
<li>
<% } %>
<script type="text/javascript">
	<% //needs its own script section to endure loaded before accessed below %>
		ss_loadJsFile(ss_rootPath, "js/common/ss_calendar.js");
</script>
<script type="text/javascript">
function ss_getMonthCalendarEvents${prefix}() {
	var formObj = document.getElementById("ssCalNavBar${prefix}");
	if (formObj && formObj.ss_goto${prefix}_year && formObj.ss_goto${prefix}_month && formObj.ss_goto${prefix}_date) {
		ss_calendar_${prefix}.switchView("datedirect", formObj.ss_goto${prefix}_year.value, formObj.ss_goto${prefix}_month.value - 1, formObj.ss_goto${prefix}_date.value);
	}
}

<c:set var="binderIds" value="" />
<c:set var="defaultCalendarId" value="" />

ss_calendar_${prefix} = ss_calendar.createCalendar({
	containerId: "ss_calendar_container${prefix}", 
	readOnly: true,
	calendarDataProvider: new ss_calendar_data_provider("${ssBinder.id}",
								"none",
								"${ssBinder.id}_${componentId}",
								true,
								"myEvents"), 
    defaultCalendarId: "<c:choose><c:when test="${defaultCalendarId != ''}">${defaultCalendarId}</c:when><c:otherwise>${ssBinder.id}</c:otherwise></c:choose>",
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
			dojo.addClass(calendarHref, cssStyle);
			
			/*   WAITING TO BE PORTED (IF POSSIBLE) 
			dojo.lfx.rounded(
				{tl: {radius:2}, 
				tr: {radius:2}, 
				bl: {radius:2}, 
				br: {radius:2}}, [calendarHref]); 
			*/
		}
	},
	stickyId : "${ssBinder.id}_${componentId}",
	calendarModeType : "myEvents"
});

ss_calendar_${prefix}.locale.workDayGridTitle = "<ssf:nlt tag="calendar.hours.workday"/>";
ss_calendar_${prefix}.locale.fullDayGridTitle = "<ssf:nlt tag="calendar.hours.fullday"/>";
ss_calendar_${prefix}.locale.entriesLabel = "<ssf:nlt tag="statistic.unity.plural"/>";
ss_calendar_${prefix}.locale.minutesShortLabel = "<ssf:nlt tag="calendar.minutes.shortLabel"/>";
ss_calendar_${prefix}.locale.dayNamesShort = ["<ssf:nlt tag="calendar.day.abbrevs.su"/>", "<ssf:nlt tag="calendar.day.abbrevs.mo"/>", "<ssf:nlt tag="calendar.day.abbrevs.tu"/>", "<ssf:nlt tag="calendar.day.abbrevs.we"/>", "<ssf:nlt tag="calendar.day.abbrevs.th"/>", "<ssf:nlt tag="calendar.day.abbrevs.fr"/>", "<ssf:nlt tag="calendar.day.abbrevs.sa"/>"];
ss_calendar_${prefix}.locale.monthNamesShort = ["<ssf:nlt tag="calendar.abbreviation.january"/>", "<ssf:nlt tag="calendar.abbreviation.february"/>", "<ssf:nlt tag="calendar.abbreviation.march"/>", "<ssf:nlt tag="calendar.abbreviation.april"/>", "<ssf:nlt tag="calendar.abbreviation.may"/>", "<ssf:nlt tag="calendar.abbreviation.june"/>", "<ssf:nlt tag="calendar.abbreviation.july"/>", "<ssf:nlt tag="calendar.abbreviation.august"/>", "<ssf:nlt tag="calendar.abbreviation.september"/>", "<ssf:nlt tag="calendar.abbreviation.october"/>", "<ssf:nlt tag="calendar.abbreviation.november"/>", "<ssf:nlt tag="calendar.abbreviation.december"/>"];
ss_calendar_${prefix}.locale.monthNames = ["<ssf:nlt tag="calendar.january"/>", "<ssf:nlt tag="calendar.february"/>", "<ssf:nlt tag="calendar.march"/>", "<ssf:nlt tag="calendar.april"/>", "<ssf:nlt tag="calendar.may"/>", "<ssf:nlt tag="calendar.june"/>", "<ssf:nlt tag="calendar.july"/>", "<ssf:nlt tag="calendar.august"/>", "<ssf:nlt tag="calendar.september"/>", "<ssf:nlt tag="calendar.october"/>", "<ssf:nlt tag="calendar.november"/>", "<ssf:nlt tag="calendar.december"/>"];
ss_calendar_${prefix}.locale.allDay = "<ssf:nlt tag="calendar.allDay"/>";
ss_calendar_${prefix}.locale.noTitle = "--<ssf:nlt tag="entry.noTitle"/>--";
										
</script>

<div class="ss_mashup_element"
  <c:if test="${ssConfigJspStyle != 'form'}">
    style="width: ${calendarWidth}; overflow: hidden;"
  </c:if>
>
    <div class="ss_mashup_round_top"><div></div></div>
	<div class="ss_mashup_folder_header_view">
		<span><ssf:nlt tag="mashup.myCalendarEvents"/></span>
	</div>
	
  <div class="ss_mashup_folder_list_open_no_border" 
    style="height: ${calendarHeight}; overflow: ${calendarOverflow};">
    <div style="padding-right:1px;">
		<c:set var="isDashboard" value="true" />
		<%@ include file="/WEB-INF/jsp/definition_elements/calendar/calendar_view_content.jsp" %>
	</div>
  </div>
  <div class="ss_mashup_round_bottom"><div></div></div>
</div>
<script type="text/javascript">
ss_calendar_${prefix}.ss_initializeCalendar();
</script>

<% if (ss_mashupListDepth > 0) { %>
<c:if test="${!empty mashupBinder}">
</li>
</c:if>
<% } %>
