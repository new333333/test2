
<form>
	<input type="checkbox" id="ss_calendarEventsTypeChoose" onclick="ss_cal_Events.changeEventType();"><label for="ss_calendarEventsTypeChoose">Show all entries by:</label>
	<select id="ss_calendarEventsTypeSelect" onclick="ss_cal_Events.changeEventType();">
		<option value="creation"><ssf:nlt tag="calendar.viewType.creation"/></option>
		<option value="activity"><ssf:nlt tag="calendar.viewType.activity"/></option>
	</select>
</form>
