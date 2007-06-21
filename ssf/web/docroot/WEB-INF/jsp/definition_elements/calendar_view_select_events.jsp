
	<form>
<%@ page import="com.sitescape.util.BrowserSniffer" %>
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
		<input type="checkbox" id="ss_calendarEventsTypeChoose" onclick="ss_cal_Events.changeEventType();"><label for="ss_calendarEventsTypeChoose"><ssf:nlt tag="folder.calendar.show.all.entries.by" />:</label>
		<select id="ss_calendarEventsTypeSelect" onclick="ss_cal_Events.changeEventType();">
			<option value="creation"><ssf:nlt tag="calendar.viewType.creation"/></option>
			<option value="activity"><ssf:nlt tag="calendar.viewType.activity"/></option>
		</select>
	</form>
