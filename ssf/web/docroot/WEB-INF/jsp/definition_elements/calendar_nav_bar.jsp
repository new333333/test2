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
<% // navigation bar to be placed on the various calendar
   // view templates, to be used to jump off to another date
   // (uses the datepicker tag)

  // Expand the nav bar to all calendar nav functions: views -- today, 
  // week, month, year (as appropriate). The datepicker widget is Day View.
%>
<script type="text/javascript">

	var ss_findEventsUrl = "<ssf:url 
		    	adapter="true" 
		    	portletName="ss_forum" 
		    	action="__ajax_request" 
		    	actionUrl="true" >
					<ssf:param name="binderId" value="${ssBinder.id}" />
					<ssf:param name="operation" value="find_calendar_events" />
		    	</ssf:url>";

	var ss_viewEventUrl = "<ssf:url 
				adapter="<%= useAdaptor %>" 
				portletName="ss_forum" 
				folderId="${ssFolder.id}" 
				action="view_folder_entry" 
				actionUrl="true" />";
	
	var ss_addCalendarEntryUrl = "${addDefaultEntryURL}";
	if (ss_addCalendarEntryUrl.indexOf("addEntryFromIFrame=1&") > -1) {
		ss_addCalendarEntryUrl = ss_addCalendarEntryUrl.replace("addEntryFromIFrame=1&", "");
	}
					
	var ss_calendarEntriesViewType_Events = "<ssf:nlt tag="calendar.viewType.events"/>";
	var ss_calendarEntriesViewType_Creation = "<ssf:nlt tag="calendar.viewType.creation"/>";
	var ss_calendarEntriesViewType_Activity = "<ssf:nlt tag="calendar.viewType.activity"/>";
	
	function ss_getMonthCalendarEvents() {
		var formObj = document.getElementById("ssCalNavBar");
		ss_cal_Events.switchView("monthdirect", formObj.ss_goto_year.value, formObj.ss_goto_month.value - 1, formObj.ss_goto_date.value);
	}
	
</script>
<form name="ssCalNavBar" id="ssCalNavBar" action="${goto_form_url}" 
  class="ss_toolbar_color"
  method="post" style="display:inline;"><div class="ss_toolbar_color" style="display:inline;">
	<ssf:datepicker formName="ssCalNavBar" showSelectors="true" 
	 popupDivId="ss_calDivPopup" id="ss_goto" initDate="${ssCurrentDate}"
	 callbackRoutine="ss_getMonthCalendarEvents" immediateMode="true" altText="<%= NLT.get("calendar.view.popupAltText") %>"
	 /></div></form>
<div id="ss_calDivPopup" class="ss_calPopupDiv"></div>


