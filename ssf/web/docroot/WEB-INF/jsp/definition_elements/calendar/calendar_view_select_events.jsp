
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<div class="ss_style">
	<form style="display: inline;">
<%
boolean isIE = BrowserSniffer.is_ie(request);
%>
	  <ssHelpSpot helpId="workspaces_folders/misc_tools/calendar_entry_control" offsetX="0" 
        <c:if test="<%= isIE %>">
          offsetY="7" 
        </c:if>
        <c:if test="<%= !isIE %>">
          offsetY="-15" 
        </c:if>
	    title="<ssf:nlt tag="helpSpot.calendarEntryControl"/>"></ssHelpSpot>
		<input style="margin: 0px" type="checkbox" id="ss_calendarEventsTypeChoose${prefix}" onclick="ss_calendar_${prefix}.ss_cal_Events.changeEventType();"><label for="ss_calendarEventsTypeChoose${prefix}">&nbsp;<ssf:nlt tag="folder.calendar.show.all.entries.by" />:</label>
		<select id="ss_calendarEventsTypeSelect${prefix}" onclick="ss_calendar_${prefix}.ss_cal_Events.changeEventType();">
			<option value="creation"><ssf:nlt tag="calendar.viewType.creation"/></option>
			<option value="activity"><ssf:nlt tag="calendar.viewType.activity"/></option>
		</select>
	</form>
</div>