<% // Calendar month view %>
<style type="text/css">


span.tinyLabel {
  padding: 1px;
  font-size: 9px;
  font-family: sans-serif;
}
</style>
<div style="margin-bottom: 5px;">
<span class="tinyLabel">Hours:</span>
<a class="ss_linkButton ss_tinyControl" id="dayGridToggle" href="javascript: ;" onclick="ss_cal_Grid.fullDayGrid(); return false;">Full Day</a>
<span class="tinyLabel">Grid:</span>
<a class="ss_linkButton ss_tinyControl" href="javascript: ;" onclick="ss_cal_Events.switchDayView('daydelta', 0); return false;">Single</a>
<a class="ss_linkButton ss_tinyControl" href="javascript: ;" onclick="ss_cal_Events.switchDayView('3daydelta', 0); return false;">3-day</a>
<a class="ss_linkButton ss_tinyControl" href="javascript: ;" onclick="ss_cal_Events.switchDayView('workweek'); return false;">5-day</a>
<a class="ss_linkButton ss_tinyControl" href="javascript: ;" onclick="ss_cal_Events.switchDayView('week'); return false;">7-day</a>
<a class="ss_linkButton ss_tinyControl" href="javascript: ;" onclick="ss_cal_Events.switchDayView('fortnight'); return false;">14-day</a>
<a class="ss_linkButton ss_tinyControl" href="javascript: ;" onclick="ss_cal_Grid.gridOffset -= ss_cal_Grid.gridIncr; ss_cal_Grid.activateGrid('day'); ss_cal_Events.redrawAll(); return false;">&lt;&lt;</a>
<a class="ss_linkButton ss_tinyControl" href="javascript: ;" onclick="ss_cal_Grid.gridOffset += ss_cal_Grid.gridIncr; ss_cal_Grid.activateGrid('day'); ss_cal_Events.redrawAll(); return false;">&gt;&gt;</a>
<a class="ss_linkButton ss_tinyControl" href="javascript: ;" onclick="ss_cal_Grid.activateGrid('month'); ss_cal_Events.redrawAll(); return false;">MonthGrid</a>
<fmt:formatDate value="${ssCalStartDate}" pattern="MMMM, yyyy" />
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
  <a href="javascript: ;" class="ss_linkButton" onclick="ss_ActiveGrid.saveCurrentEvent(); ss_cancelPopupDiv('infoBox');">Save</a>
  <a href="javascript: ;" class="ss_linkButton" onclick="ss_ActiveGrid.deleteCurrentEvent(); ss_cancelPopupDiv('infoBox');">Cancel</a>
</div>

<div id="infoBox2" style="display: none; visibility: hidden; position: absolute; padding: 25px; background-color: #FFFFFF; z-index: 2003;
          border: 1px solid blue; width: 250px; height: 150px; left: 200px; top: 200px;">
  Information about event: <span id="ib2eid">EVENT</span>
  <p>
  <span id="ib2view">VIEW</span>
  <p>
  <a href="javascript: ;" class="ss_linkButton" onclick="ss_cancelPopupDiv('infoBox2');">Save</a>
  <a href="javascript: ;" class="ss_linkButton" onclick="ss_cancelPopupDiv('infoBox2');">Cancel</a>
</div>

<div class="ss_cal_eventBody" id="hoverBox" style="display: none; visibility: hidden; position: absolute; padding: 10px; background-color: #FFFFFF; z-index: 2003; border: 1px solid black;"></div>
<script type="text/javascript">
<%@ include file="/WEB-INF/jsp/definition_elements/calendar_view_data.jsp" %>
ss_cal_Grid.activateGrid("month");
ss_cal_Events.redrawAll();
ss_createOnLoadObj('ss_cal_hoverBox', function() {
	ss_moveDivToBody("hoverBox");
	ss_moveDivToBody("infoLightBox");
	ss_moveDivToBody("infoBox");
	ss_moveDivToBody("infoBox2");
});
</script>