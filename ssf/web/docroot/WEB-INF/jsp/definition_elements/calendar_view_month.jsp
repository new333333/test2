<% // Calendar month view %>
<style type="text/css">
a.tinyControl {
  border: 1px solid #999999;
  background: #f5f599;
  padding: 1px;
  font-size: 9px;
  font-family: sans-serif;
}

span.tinyLabel {
  padding: 1px;
  font-size: 9px;
  font-family: sans-serif;
}
</style>
<div>
<span class="tinyLabel">Hours:</span>
<a class="tinyControl" id="dayGridToggle" href="javascript: ;" onclick="ss_cal_Grid.fullDayGrid(); return false;">Full Day</a>
<span class="tinyLabel">Grid:</span>
<a class="tinyControl" href="javascript: ;" onclick="ss_cal_Grid.gridSize = 1; ss_cal_Grid.activateGrid('day'); ss_cal_Events.redrawAll(); return false;">Single</a>
<a class="tinyControl" href="javascript: ;" onclick="ss_cal_Grid.gridSize = 3; ss_cal_Grid.activateGrid('day'); ss_cal_Events.redrawAll(); return false;">3-day</a>
<a class="tinyControl" href="javascript: ;" onclick="ss_cal_Events.switchDayView('workweek');  return false;">5-day</a>
<a class="tinyControl" href="javascript: ;" onclick="ss_cal_Events.switchDayView('week'); return false;">7-day</a>
<a class="tinyControl" href="javascript: ;" onclick="ss_cal_Events.switchDayView('fortnight'); return false;">14-day</a>
<fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${ssCalStartDate}" pattern="MMMM, yyyy" />
<ssf:nlt tag="calendar.views" text="Views"/>:&nbsp;
<a href="${set_week_view}"><ssf:nlt tag="calendar.Week" text="Week"/></a>
<%@ include file="/WEB-INF/jsp/definition_elements/calendar_nav_bar.jsp" %>
</div>
<div style="width: 100%"><%-- IE needs this for some stupid reason --%>
<div id="ss_cal_DayGridMaster" style="display:none;">
  <table class="ss_cal_gridTable">
    <tbody>
      <tr>
        <td class="ss_cal_dayGridHourTicksColumn" style="padding-right: 0px;"><div class="ss_cal_gridHeader"></td>
        <td><div id="ss_cal_dayGridHeader" class="ss_cal_gridHeader ss_cal_reserveWidth"></div></td>
      </tr>
      <tr>
        <td class="ss_cal_dayGridHourTicksColumn"></td>
        <td><div id="ss_cal_dayGridAllDay" class="ss_cal_dayGridHour ss_cal_dayGridAllDay ss_cal_reserveWidth"></div></td>
      </tr>
    </tbody>
  </table>
  <div class="ss_cal_dayGridDivider"></div>
  <div id="ss_cal_dayGridWindowOuter" class="ss_cal_dayGridWindowOuter">
    <div id="ss_cal_dayGridWindowInner" class="ss_cal_dayGridWindowInner">
      <table class="ss_cal_gridTable">
        <tbody>
          <tr>
            <td class="ss_cal_dayGridHourTicksColumn"><div id="hourHeader" class="ss_cal_dayGridHour"></div></td>
            <td><div id="ss_cal_dayGridHour" class="ss_cal_dayGridHour ss_cal_reserveWidth"></div></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</div>
<div id="ss_cal_MonthGridMaster" style="position: relative; display: none;">
  <table style="width: 100%" cellpadding=0 cellspacing=0 border=0>
    <tbody>
      <tr>
        <td><div id="ss_cal_monthGridHeader" class="ss_cal_gridHeader ss_cal_reserveWidth"></div></td>
      </tr>
      <tr>
        <td><div id="ss_cal_monthGrid" class="ss_cal_monthGrid ss_cal_reserveWidth"></div></td>
      </tr>
    </tbody>
  </table>
</div>
</div>

<div id="infoLightBox" style="display: none; visibility: hidden; width: 100%; height: 100%;"></div>

<div id="infoBox" style="display: none; visibility: hidden; position: absolute; padding: 25px; background-color: #FFFFFF; z-index: 2003;
        border: 1px solid blue; width: 250px; height: 150px; left: 200px; top: 200px;">
  <i>Imagine if you will...</i><br/>
  A particularly stylish form will be here pertaining to the details of this event.
  <p>
  <a href="javascript: ;" class="tinyControl" onclick="ss_ActiveGrid.saveCurrentEvent(); ss_cancelPopupDiv('infoBox');">Save</a>
  <a href="javascript: ;" class="tinyControl" onclick="ss_ActiveGrid.deleteCurrentEvent(); ss_cancelPopupDiv('infoBox');">Cancel</a>
</div>

<div id="infoBox2" style="display: none; visibility: hidden; position: absolute; padding: 25px; background-color: #FFFFFF; z-index: 2003;
          border: 1px solid blue; width: 250px; height: 150px; left: 200px; top: 200px;">
  Information about event: <span id="ib2eid">EVENT</span>
  <p>
  <span id="ib2view">VIEW</span>
  <p>
  <a href="javascript: ;" class="tinyControl" onclick="ss_cancelPopupDiv('infoBox2');">Save</a>
  <a href="javascript: ;" class="tinyControl" onclick="ss_cancelPopupDiv('infoBox2');">Cancel</a>
</div>


<div class="ss_cal_eventBody" id="hoverBox" style="display: none; visibility: hidden; position: absolute; padding: 10px; background-color: #FFFFFF; z-index: 2003; border: 1px solid black;"></div>

<script type="text/javascript">
//ss_createOnLoadObj('ss_cal_hoverBox', function() {ss_moveDivToBody("hoverBox");} );

ss_cal_CalData.setMap([
  {calsrc: "cal1", box: "#8888CC", border: "#6666AA"},
  {calsrc: "cal2", box: "#88CC88", border: "#66AA66"},
  {calsrc: "cal3", box: "#CC88CC", border: "#AA66AA"},
  {calsrc: "cal4", box: "#88CCCC", border: "#66AAAA"},
  {calsrc: "cal5", box: "#CCCC88", border: "#AAAA66"}]);

var inputEvents = [<%--
--%><c:set var="i" value="-1"/><%--
--%><c:forEach var="week" items="${ssCalendarViewBean.weekList}" ><%--
  --%><c:forEach var="daymap" items="${week.dayList}"><c:set var="i" value="${i + 1}"/><%--
    --%><c:if test="${daymap.inView}"><% // is this day part of the month, or context at front/end? %><%--
      --%><c:if test="${!empty daymap.cal_eventdatamap}"><%--
        --%><c:forEach var="ev" items="${daymap.cal_eventdatamap}"><%--
          --%><c:forEach var="evim" items="${ev.value}"><%--
            --%><jsp:useBean id="evim" type="java.util.Map" /><%--
            --%><% java.util.HashMap e = (java.util.HashMap) evim.get("entry"); %><%--
            --%>
  {eventId: "${evim.entry._docId}", day: ${i}, start: "<fmt:formatDate value="${evim.cal_starttime}" timeZone="${ssUser.timeZone.ID}" pattern="HH:mm"/>",  dur: ${evim.cal_duration}, title: "${evim.entry.title}", text: "${evim.cal_endtimestring}", calsrc: "cal1",
   viewHref: "<ssf:url adapter="<%= useAdaptor %>" portletName="ss_forum" folderId="${ssFolder.id}" action="view_folder_entry" entryId="<%= e.get("_docId").toString() %>" actionUrl="true" />",
   viewOnClick: "ss_loadEntry(this,'<c:out value="${evim.entry._docId}"/>');return false;"},<%--
          --%></c:forEach><% // end of events within a single time slot %><%--
        --%></c:forEach><% // end of time slot loop %><%--
      --%></c:if><% // end of case where there is at least one event in a cell %><%--
    --%></c:if><% // end of test to see if a day is in view %><%--
  --%></c:forEach><%--
--%></c:forEach>];

ss_cal_Events.clear();
ss_cal_Events.set(inputEvents);
ss_cal_CalData.dayHeaders = ["5-Jan","6-Jan","7-Jan","8-Jan","9-Jan","10-Jan","11-Jan","12-Jan"];
ss_cal_CalData.monthTickList = [<%--

--%><c:set var="today" value="-1"/><%--
--%><c:set var="i" value="-1"/><%--
--%><c:forEach var="week" items="${ssCalendarViewBean.weekList}" ><%--
  --%><c:forEach var="daymap" items="${week.dayList}"><%--
    --%><c:out value="${daymap.cal_dom},"/><%--
    --%><c:set var="i" value="${i + 1}"/><%--
    --%><c:if test="${daymap.isToday}"><%--
      --%><c:set var="today" value="${i}"/><%--
    --%></c:if><%--
  --%></c:forEach><%--
--%></c:forEach>];
ss_cal_CalData.monthTodayIndex = ${today};
ss_cal_CalData.dayTodayIndex = 4;
ss_cal_Grid.activateGrid("month");
ss_cal_Events.redrawAll();
ss_createOnLoadObj('ss_cal_hoverBox', function() {
	ss_moveDivToBody("hoverBox");
	ss_moveDivToBody("infoLightBox");
	ss_moveDivToBody("infoBox");
	ss_moveDivToBody("infoBox2");
});
</script>