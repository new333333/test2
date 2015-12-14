<%
/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.GregorianCalendar" %>
<%@ page import="java.util.TimeZone" %>
<%@ page import="org.kablink.teaming.domain.User" %>
<%@ page import="org.kablink.teaming.util.NLT" %>
 
<%
Calendar calendarPrevDate = (java.util.Calendar) request.getAttribute("ssPrevDate");
Calendar calendarNextDate = (java.util.Calendar) request.getAttribute("ssNextDate");
Calendar calendarCurrDate = (java.util.Calendar) request.getAttribute("ssCurrDate");

User user = (User) request.getAttribute("ssUser");
TimeZone timeZone = user.getTimeZone();
Calendar calendarTodayDate = new GregorianCalendar(timeZone);

Integer gridSize = (java.lang.Integer) request.getAttribute("ssGridSize");
String strGridSize = "";
if (gridSize != null) {
	strGridSize = gridSize.toString();;
}

String strPrevDay = "" + calendarPrevDate.get(Calendar.DAY_OF_MONTH);
int intPrevMonth = calendarCurrDate.get(Calendar.MONTH) ;
String strPrevMonth = "" + intPrevMonth;
String strPrevYear = "" + calendarPrevDate.get(Calendar.YEAR);

String strNextDay = "" + calendarNextDate.get(Calendar.DAY_OF_MONTH);
int intNextMonth = calendarCurrDate.get(Calendar.MONTH) + 2;
String strNextMonth = "" + intNextMonth;
String strNextYear = "" + calendarNextDate.get(Calendar.YEAR);

int intCurrDay = calendarCurrDate.get(Calendar.DAY_OF_MONTH);
String strCurrDay = "" + calendarCurrDate.get(Calendar.DAY_OF_MONTH);
int intCurrMonth = calendarCurrDate.get(Calendar.MONTH) + 1;
String strCurrMonth = "" + intCurrMonth;
String strCurrYear = "" + calendarCurrDate.get(Calendar.YEAR);
int intCurrYear = calendarCurrDate.get(Calendar.YEAR);

String[] monthNames = new String[] {"calendar.january", "calendar.february", "calendar.march", "calendar.april", "calendar.may", "calendar.june", "calendar.july", "calendar.august", "calendar.september", "calendar.october", "calendar.november", "calendar.december"};
String strMonthName = NLT.get(monthNames[intCurrMonth - 1]);	

String strTodayDay = "" + calendarTodayDate.get(Calendar.DAY_OF_MONTH);
int intTodayDay = calendarTodayDate.get(Calendar.DAY_OF_MONTH);
int intTodayMonth = calendarTodayDate.get(Calendar.MONTH) + 1;
String strTodayMonth = "" + intTodayMonth;
String strTodayYear = "" + calendarTodayDate.get(Calendar.YEAR);

Date currDate = calendarCurrDate.getTime();
Date todayDate = calendarTodayDate.getTime();
Date nextDate = calendarNextDate.getTime();
%>

<c:set var="ssCurrDateFormat" value="<%= currDate %>" />
<c:set var="currMonthDate"><fmt:formatDate value="<%= currDate %>" pattern="MMM" timeZone="${ssUser.timeZone.ID}" /></c:set>
<c:set var="todayMonthDate"><fmt:formatDate value="<%= todayDate %>" pattern="MMM" timeZone="${ssUser.timeZone.ID}" /></c:set>

<%
 GregorianCalendar cal = new GregorianCalendar (intCurrYear, calendarCurrDate.get(Calendar.MONTH), 1); 
 int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
 int weekStartDay=cal.get(Calendar.DAY_OF_WEEK);
 
 cal = new GregorianCalendar (intCurrYear, calendarCurrDate.get(Calendar.MONTH), days); 
 int iTotalweeks=cal.get(Calendar.WEEK_OF_MONTH);
%>

<div class="folders">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr class="ss_mobile_calendar_monthyear">
  <td align="center" valign="middle">
	<a class="ss_calDateDownButton"
	  href="<ssf:url adapter="true" portletName="ss_forum" 
          folderId="${ssBinder.id}" action="__ajax_mobile" 
          operation="mobile_show_folder" actionUrl="false">
			<ssf:param name="day" value="1" />
			<ssf:param name="dayOfMonth" value="1" />
			<ssf:param name="month" value="<%= strPrevMonth %>" />
			<ssf:param name="year" value="<%= strCurrYear %>" />
			<ssf:param name="ssGridType" value="month" />
			<ssf:param name="ssGridSize" value="1" />
		  </ssf:url>" >
		<img <ssf:alt tag="alt.viewCalPrev"/> title="<ssf:nlt tag="alt.viewCalPrev"/>"
		border="0" src="<html:imagesPath/>pics/nl_left_noborder_20.png" align="absbottom" />
	</a>
	<a href="<ssf:url adapter="true" portletName="ss_forum" 
          folderId="${ssBinder.id}" action="__ajax_mobile" 
          operation="mobile_show_folder" actionUrl="false">
			<ssf:param name="day" value="1" />
			<ssf:param name="dayOfMonth" value="1" />
			<ssf:param name="month" value="<%= strCurrMonth %>" />
			<ssf:param name="year" value="<%= strCurrYear %>" />
			<ssf:param name="ssGridType" value="month" />
			<ssf:param name="ssGridSize" value="1" />
		  </ssf:url>" >
      <span class="ss_mobile_calendar_header">
		<%= strMonthName %>&nbsp;<fmt:formatDate value="${ssCurrDateFormat}" pattern="yyyy" timeZone="${ssUser.timeZone.ID}" />
	  </span>
	</a>
	<a class="ss_calDateUpButton"
	  href="<ssf:url adapter="true" portletName="ss_forum" 
          folderId="${ssBinder.id}" action="__ajax_mobile" 
          operation="mobile_show_folder" actionUrl="false">
			<ssf:param name="day" value="1" />
			<ssf:param name="dayOfMonth" value="1" />
			<ssf:param name="month" value="<%= strNextMonth %>" />
			<ssf:param name="year" value="<%= strCurrYear %>" />
			<ssf:param name="ssGridType" value="month" />
			<ssf:param name="ssGridSize" value="1" />
		  </ssf:url>">
		<img <ssf:alt tag="alt.viewCalNext"/> title="<ssf:nlt tag="alt.viewCalNext"/>"
		border="0" src="<html:imagesPath/>pics/nl_right_noborder_20.png" align="absbottom" />
	</a>
  </td>
  </tr>
</table>

<table class="ss_mobile_calendar_month" border="0" cellspacing="0" cellpadding="0" width="100%">
      <tbody>
        <tr class="ss_mobile_calendar_weekdayLabel">
          <th><ssf:nlt tag="calendar.day.abbrevs.su"/></th>
          <th><ssf:nlt tag="calendar.day.abbrevs.mo"/></th>
          <th><ssf:nlt tag="calendar.day.abbrevs.tu"/></th>
          <th><ssf:nlt tag="calendar.day.abbrevs.we"/></th>
          <th><ssf:nlt tag="calendar.day.abbrevs.th"/></th>
          <th><ssf:nlt tag="calendar.day.abbrevs.fr"/></th>
          <th><ssf:nlt tag="calendar.day.abbrevs.sa"/></th>
        </tr>
        <%
        int cnt = 1;
        for (int i = 1; i <= iTotalweeks; i++) {
		%>
        <tr>
          <% 
	        for(int j=1; j <= 7; j++)
	        {

				if (j==1 || j==7)
				{
					if (cnt < weekStartDay || (cnt - weekStartDay + 1) > days)
					{
						%>
						<td align="center" class="ss_mobile_calendar_weekend">&nbsp;</td>
						<% 
					}
					else
					{
						%>
						<td align="center" id="day_<%= (cnt - weekStartDay + 1) %>"
							class="ss_mobile_calendar_weekend 
								<c:if test="${ssGridType == 'day'}">
									<% if (intCurrDay == (cnt - weekStartDay + 1)) { %> ss_mobile_calendar_current_day <% } %>
								</c:if>
								<c:if test="${currMonthDate == todayMonthDate}">
									<% if (intTodayDay == cnt - weekStartDay + 1) { %> ss_mobile_calendar_today <% } %>
								</c:if>
							"
						>
						  <a href="<ssf:url adapter="true" portletName="ss_forum" 
							folderId="${ssBinder.id}" action="__ajax_mobile" 
							operation="mobile_show_folder" actionUrl="false">
								<ssf:param name="day" value="<%= strCurrDay %>" />
								<ssf:param name="dayOfMonth" value="<%= String.valueOf((cnt - weekStartDay + 1)) %>" />
								<ssf:param name="month" value="<%= strCurrMonth %>" />
								<ssf:param name="year" value="<%= strCurrYear %>" />
								<ssf:param name="ssGridType" value="day" />
								<ssf:param name="ssGridSize" value="1" />
							</ssf:url>"
						  ><span><%= (cnt - weekStartDay + 1) %></span></a>
						</td>
						<% 
					}
				}
				else
				{
					if (cnt < weekStartDay || (cnt - weekStartDay + 1) > days)
					{
						%>
						<td align="center" class="ss_mobile_calendar_day">&nbsp;</td>
						<% 
					}
					else
					{
						%>
						<td align="center" id="day_<%= (cnt - weekStartDay + 1) %>"
							class="ss_mobile_calendar_day 
								<c:if test="${ssGridType == 'day'}">
									<% if (intCurrDay == (cnt - weekStartDay + 1)) { %> ss_mobile_calendar_current_day <% } %>
								</c:if>
								<c:if test="${currMonthDate == todayMonthDate}">
									<% if (intTodayDay == cnt - weekStartDay + 1) { %> ss_mobile_calendar_today <% } %>
								</c:if>
							"
						>
						  <a href="<ssf:url adapter="true" portletName="ss_forum" 
							folderId="${ssBinder.id}" action="__ajax_mobile" 
							operation="mobile_show_folder" actionUrl="false">
								<ssf:param name="day" value="<%= strCurrDay %>" />
								<ssf:param name="dayOfMonth" value="<%= String.valueOf((cnt - weekStartDay + 1)) %>" />
								<ssf:param name="month" value="<%= strCurrMonth %>" />
								<ssf:param name="year" value="<%= strCurrYear %>" />
								<ssf:param name="ssGridType" value="day" />
								<ssf:param name="ssGridSize" value="1" />
							</ssf:url>"
						  ><span><%= (cnt - weekStartDay + 1) %></span></a>
						</td>
						<% 
					}
				}

			cnt++;
			}
	        %>
        </tr>
        <% 
	    }
	    %>
      </tbody>
     </table>

    <div>
		<form id="calendarShowFilterForm" name="calendarShowFilterForm" 
		  method="post" 
		  action="<ssf:url adapter="true" portletName="ss_forum" 
		    action="__ajax_mobile" operation="mobile_show_folder" 
		    actionUrl="true" 
		    folderId="${ssBinder.id}" />"
		>
        <div class="folder-select">
          
          <a href="javascript: ;" 
      		  onClick="ss_toggleDivVisibility('calendar-show-filter-menu');return false;">
      	    <span style="color: #949494;"><ssf:nlt tag="calendar.navi.chooseMode"/></span>
      	    <span  style="color: #000; padding-left: 7px; padding-right: 5px;">
              <c:if test="${ss_calendarEventType == 'event'}"><ssf:nlt tag="calendar.navi.mode.alt.physical"/></c:if>
              <c:if test="${ss_calendarEventType == 'creation'}"><ssf:nlt tag="calendar.navi.mode.alt.physical.byCreation"/></c:if>
              <c:if test="${ss_calendarEventType == 'activity'}"><ssf:nlt tag="calendar.navi.mode.alt.physical.byActivity"/></c:if>
              <c:if test="${ss_calendarEventType == 'virtual'}"><ssf:nlt tag="calendar.navi.mode.alt.virtual"/></c:if>
            </span>
			<img border="0" src="<html:rootPath/>images/pics/menu_sm.png"/>
      	  </a>
          
			<div id="calendar-show-filter-menu" class="action-dialog" 
			  style="display:none; z-index:2; font-size: 1.2em;">
			    <div class="dialog-content">
		      		<div class="menu-item">
				      <a href="<ssf:url adapter="true" portletName="ss_forum" 
				        action="__ajax_mobile" operation="mobile_show_folder" 
				        folderId="${ssBinder.id}" 
				        actionUrl="true" ><ssf:param name="eventType" value="event"/></ssf:url>"
				      ><ssf:nlt tag="calendar.navi.mode.alt.physical"/></a>
				    </div>
				</div>
			    <div class="dialog-content">
		      		<div class="menu-item">
				      <a href="<ssf:url adapter="true" portletName="ss_forum" 
				        action="__ajax_mobile" operation="mobile_show_folder" 
				        folderId="${ssBinder.id}" 
				        actionUrl="true" ><ssf:param name="eventType" value="creation"/></ssf:url>"
				      ><ssf:nlt tag="calendar.navi.mode.alt.physical.byCreation"/></a>
				    </div>
				</div>
			    <div class="dialog-content">
		      		<div class="menu-item">
				      <a href="<ssf:url adapter="true" portletName="ss_forum" 
				        action="__ajax_mobile" operation="mobile_show_folder" 
				        folderId="${ssBinder.id}" 
				        actionUrl="true" ><ssf:param name="eventType" value="activity"/></ssf:url>"
				      ><ssf:nlt tag="calendar.navi.mode.alt.physical.byActivity"/></a>
				    </div>
				</div>
			    <div class="dialog-content">
		      		<div class="menu-item">
				      <a href="<ssf:url adapter="true" portletName="ss_forum" 
				        action="__ajax_mobile" operation="mobile_show_folder" 
				        folderId="${ssBinder.id}" 
				        actionUrl="true" ><ssf:param name="eventType" value="virtual"/></ssf:url>"
				      ><ssf:nlt tag="calendar.navi.mode.alt.virtual"/></a>
				    </div>
				</div>
			</div>
		</div>
		</form>
    </div>

	<c:set var="lastCalDayDate" value=""/>
	<c:set var="lastFamilyType" value=""/>
	<table class="ss_mobile_calendar" cellspacing="0" cellpadding="0">
	<c:set var="entriesSeen" value="0"/>
	<c:forEach var="familyType" items='<%= java.util.Arrays.asList(new String[]{"calendar", "task"}) %>' >
	<jsp:useBean id="familyType" type="java.lang.String" />
	<c:forEach var="entry2" items="${ssFolderEntries}" >
	<jsp:useBean id="entry2" type="java.util.HashMap" />
	<%
		if (entry2.containsKey("_event0")) {
			String eventName = (String)entry2.get("_event0");
			String family = (String)entry2.get("_family");
			if (familyType.equals(family)) {
				boolean allDayEvent = false;
				java.util.Date eventDate = null;
				java.util.Date startDate = null;
				if (entry2.containsKey(eventName + "#LogicalStartDate")) {
					startDate = (java.util.Date)entry2.get(eventName + "#LogicalStartDate");
					eventDate = startDate;
				}
				java.util.Date endDate = null;
				if (entry2.containsKey(eventName + "#LogicalEndDate")) {
					endDate = (java.util.Date)entry2.get(eventName + "#LogicalEndDate");
				}
				if (eventDate == null || 
						(endDate != null && "task".equals(family))) eventDate = endDate;
				String eventTimeZoneId = null;
				if (entry2.containsKey(eventName + "#TimeZoneID")) eventTimeZoneId = (String)entry2.get(eventName + "#TimeZoneID");
				if (eventDate != null && endDate != null) {
					if (eventDate.getHours() == 0 && eventDate.getMinutes() == 0 && 
							endDate.getHours() == 23 && endDate.getMinutes() == 59) {
						allDayEvent = true;
					}
				}
				if (eventDate != null) {
		%>
	    <%  if (allDayEvent) {  %>
			<c:set var="calMonthDate"><fmt:formatDate timeZone="GMT"
	      		value="<%= eventDate %>" pattern="MMM" /></c:set>
		<%  } else { %>
			<c:set var="calMonthDate"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
	      		value="<%= eventDate %>" pattern="MMM" /></c:set>
		<%  } %>
	<c:set var="startDate" value="<%= startDate %>"/>
	<c:set var="endDate" value="<%= endDate %>"/>
	<c:set var="currDate" value="<%= currDate %>"/>
	<c:if test="${currMonthDate == calMonthDate || (startDate != null && endDate != null && startDate.time <= currDate.time && endDate.time >= currDate.time)}">
	    <c:set var="entriesSeen" value="${entriesSeen + 1}"/>
	    <%  if (allDayEvent) {  %>
		    <c:set var="calDayDate"><fmt:formatDate timeZone="GMT"
	      		value="<%= eventDate %>" type="date" dateStyle="short" /></c:set>
		<%  } else { %>
		    <c:set var="calDayDate"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
	      		value="<%= eventDate %>" type="date" dateStyle="short" /></c:set>
		<%  } %>
	    <c:if test="${familyType != lastFamilyType}">
	    <c:set var="lastFamilyType" value="${familyType }" />
		</c:if>
	    <c:if test="${calDayDate != lastCalDayDate}">
	    <tr>
			<td colspan="2">
			<div class="ss_mobile_calendar_entries_header">
			    <%  if (allDayEvent) {  %>
					<span><fmt:formatDate value="<%= eventDate %>" pattern="EEE" timeZone="GMT" /></span>
					<span><fmt:formatDate value="<%= eventDate %>" pattern="d MMM" timeZone="GMT" /></span>
				<%  } else { %>
					<span><fmt:formatDate value="<%= eventDate %>" pattern="EEE" timeZone="${ssUser.timeZone.ID}" /></span>
					<span><fmt:formatDate value="<%= eventDate %>" pattern="d MMM" timeZone="${ssUser.timeZone.ID}" /></span>
				<%  } %>
			</div>
			</td>
	    </tr>
		</c:if>
		
	    <c:set var="lastCalDayDate" value="${calDayDate}"/>
		 <tr class="ss_mobile_calendar_entry_row">
		   <td class="ss_mobile_calendar_time">
			  <%
			  	if (eventTimeZoneId != null) {
			  %>
			  <fmt:formatDate 
			    timeZone="${ssUser.timeZone.ID}"
	      		value="<%= eventDate %>" type="time" timeStyle="short" 
	      	  />
	      	  <%
			  	} else {
			  %>
			  <span><ssf:nlt tag="event.allDay"/></span>
			  <%
			  	}
			  %>
		  	</td>
		  	<td>
			  <a href="<ssf:url adapter="true" portletName="ss_forum" 
				folderId="${entry2._binderId}"  entryId="${entry2._docId}"
				action="__ajax_mobile" operation="mobile_show_entry" actionUrl="false" />">
		    	<c:if test="${empty entry2.title}">
		    		(<ssf:nlt tag="entry.noTitle"/>)
		    	</c:if>
				<c:out value="${entry2.title}" escapeXml="true"/>
			  </a>
		   </td>
		 </tr>
	</c:if>
		 <%
		 		}
			}
	 	}
	 %>
	</c:forEach>
	</c:forEach>
	</table>

	<c:if test="${entriesSeen == 0}">
      <div class="ss_mobile_calendar_no_entries_header">
        <c:if test="${ssGridType != 'month'}">
          <span><fmt:formatDate value="<%= currDate %>" pattern="EEE" timeZone="${ssUser.timeZone.ID}" /></span>
          <span><fmt:formatDate value="<%= currDate %>" pattern="d MMM" timeZone="${ssUser.timeZone.ID}" /></span>
        </c:if>
        <c:if test="${ssGridType == 'month'}">
          <span><%= strMonthName %>&nbsp;<fmt:formatDate value="${ssCurrDateFormat}" pattern="yyyy" timeZone="${ssUser.timeZone.ID}" /></span>
        </c:if>
      </div>
	  <div class="ss_mobile_calendar_no_entries_content">
	  	<ssf:nlt tag="mobile.calendar.NoEvents"/>
	  </div>
	</c:if>
</div>
