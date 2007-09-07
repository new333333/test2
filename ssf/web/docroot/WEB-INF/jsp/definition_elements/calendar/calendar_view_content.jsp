<%
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
%>
<%@ page import="com.sitescape.team.util.NLT" %>

<c:if test="${empty isDashboard}">
	<script type="text/javascript">
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
		if (!window.ssScope) { ssScope = {}; };
		ssScope.refreshView = function (entryId) {
			ss_calendar_${prefix}.refreshEntryEvents(entryId);
		}
	</script>
</c:if>
<%@ include file="/WEB-INF/jsp/definition_elements/calendar/calendar_nav_bar.jsp" %>


<div style="width: 100%"><%-- IE needs this for some stupid reason --%>
<div id="ss_cal_DayGridMaster${prefix}" style="display:none;">
  <table class="ss_cal_gridTable">
    <tbody>
      <tr>
        <td class="ss_cal_dayGridHourTicksColumn" style="padding-right: 0px;"><div class="ss_cal_gridHeader"></td>
        <td><div width="100%" id="ss_cal_dayGridHeader${prefix}" class="ss_cal_gridHeader ss_cal_reserveWidth"></div></td>
      </tr>
      <tr>
        <td class="ss_cal_dayGridHourTicksColumn">All day</td>
        <td><div id="ss_cal_dayGridAllDay${prefix}" class="ss_cal_dayGridHour ss_cal_dayGridAllDay ss_cal_reserveWidth"></div></td>
      </tr>
    </tbody>
  </table>
  <div class="ss_cal_dayGridDivider"></div>
  <div id="ss_cal_dayGridWindowOuter${prefix}" class="ss_cal_dayGridWindowOuter">
    <div id="ss_cal_dayGridWindowInner${prefix}" class="ss_cal_dayGridWindowInner">
      <table class="ss_cal_gridTable">
        <tbody>
          <tr>
            <td class="ss_cal_dayGridHourTicksColumn"><div id="hourHeader${prefix}" class="ss_cal_dayGridHour"></div></td>
            <td><div id="ss_cal_dayGridHour${prefix}" class="ss_cal_dayGridHour ss_cal_reserveWidth"></div></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</div>
<div id="ss_cal_MonthGridMaster${prefix}" style="position: relative; display: none;">
  <table style="width: 100%" cellpadding=0 cellspacing=0 border=0>
    <tbody>
      <tr>
        <td id="ss_cal_monthGridHeader${prefix}" class="ss_cal_gridHeader ss_cal_reserveWidth"></td>
      </tr>
      <tr>
        <td><div id="ss_cal_monthGrid${prefix}" class="ss_cal_monthGrid ss_cal_reserveWidth"></div></td>
      </tr>
    </tbody>
  </table>
</div>
</div>

<div id="infoLightBox${prefix}" style="display: none; visibility: hidden; width: 100%; height: 100%;"></div>

<div id="infoBox${prefix}" style="display: none; visibility: hidden; position: absolute; padding: 25px; background-color: #FFFFFF; z-index: 2003;
        border: 1px solid blue; width: 250px; height: 150px; left: 200px; top: 200px;">
  <i>Imagine if you will...</i><br/>
  A particularly stylish form will be here pertaining to the details of this event.
  <p>
  <a href="javascript: ;" class="ss_linkButton" onclick="ss_ActiveGrid.saveCurrentEvent(); ss_cancelPopupDiv('infoBox${prefix}');">Save</a>
  <a href="javascript: ;" class="ss_linkButton" onclick="ss_ActiveGrid.deleteCurrentEvent(); ss_cancelPopupDiv('infoBox${prefix}');">Cancel</a>
</div>

<div id="infoBox2${prefix}" style="display: none; visibility: hidden; position: absolute; padding: 25px; background-color: #FFFFFF; z-index: 2003;
          border: 1px solid blue; width: 250px; height: 150px; left: 200px; top: 200px;">
  Information about event: <span id="ib2eid${prefix}">EVENT</span>
  <p>
  <span id="ib2view">VIEW</span>
  <p>
  <a href="javascript: ;" class="ss_linkButton" onclick="ss_cancelPopupDiv('infoBox2${prefix}');">Save</a>
  <a href="javascript: ;" class="ss_linkButton" onclick="ss_cancelPopupDiv('infoBox2${prefix}');">Cancel</a>
</div>

<div class="ss_cal_eventBody" id="hoverBox${prefix}" style="display: none; visibility: hidden; position: absolute; padding: 10px; background-color: #FFFFFF; z-index: 2003; border: 1px solid black;"></div>
<c:if test="${!isDashboard}">
	<script type="text/javascript">
		ss_calendar_${prefix}.ss_initializeCalendar();
		
		ss_createOnLoadObj('ss_cal_hoverBox${prefix}', function() {
			ss_moveDivToBody("hoverBox${prefix}");
			ss_moveDivToBody("infoLightBox${prefix}");
			ss_moveDivToBody("infoBox${prefix}");
			ss_moveDivToBody("infoBox2${prefix}");
		});
		
	</script>
</c:if>
<div id="ss_loading${prefix}" class="ss_loading"><img <ssf:alt tag="Loading"/> src="<html:imagesPath/>pics/ajax-loader.gif" /></div>