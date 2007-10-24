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
<%@ page import="com.sitescape.team.util.NLT" %>

<c:if test="${empty isDashboard}">
	<script type="text/javascript">
		<% //needs its own script section to endure loaded before accessed below %>
 		ss_loadJsFile(ss_rootPath, "js/common/ss_calendar.js");
	</script>
	<script type="text/javascript">
					
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
		
		ss_calendar.entriesLabel = "<ssf:nlt tag="statistic.unity.plural"/>";
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