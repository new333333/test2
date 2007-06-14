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
<div class="ss_clear"></div>
<div>

	<a class="ss_calDaySelectButton" href="javascript: ;" 
	  onclick="ss_cal_Events.switchView('daydelta'); return false;">
		<img <ssf:alt tag="alt.view1Day"/> title="<ssf:nlt tag="alt.view1Day"/>" 
		src="<html:imagesPath/>pics/1pix.gif" />
	</a>
	
	<a class="ss_cal3DaysSelectButton" href="javascript: ;" onclick="ss_cal_Events.switchView('3daydelta'); return false;">
		<img <ssf:alt tag="alt.view3Days"/> title="<ssf:nlt tag="alt.view3Days"/>" 
		src="<html:imagesPath/>pics/1pix.gif" />
	</a>
	
	<a class="ss_cal5DaysSelectButton" href="javascript: ;" onclick="ss_cal_Events.switchView('workweek'); return false;" >
		<img <ssf:alt tag="alt.view5Days"/> title="<ssf:nlt tag="alt.view5Days"/>"
		src="<html:imagesPath/>pics/1pix.gif" />
	</a>
	
	<a class="ss_cal7DaysSelectButton" href="javascript: ;" onclick="ss_cal_Events.switchView('week'); return false;">
		<img <ssf:alt tag="alt.view1Week"/> title="<ssf:nlt tag="alt.view1Week"/>"
		src="<html:imagesPath/>pics/1pix.gif" />
	</a>
	
	<a class="ss_cal14DaysSelectButton" href="javascript: ;" onclick="ss_cal_Events.switchView('fortnight'); return false;">
		<img <ssf:alt tag="alt.view2Weeks"/> title="<ssf:nlt tag="alt.view2Weeks"/>"
		src="<html:imagesPath/>pics/1pix.gif" />
	</a>
	
	<a class="ss_calMonthSelectButton" href="javascript: ;" onclick="ss_cal_Events.switchView('month'); return false;">
		<img <ssf:alt tag="alt.view1Month"/> title="<ssf:nlt tag="alt.view1Month"/>" 
		src="<html:imagesPath/>pics/1pix.gif" />
	</a>
	
	<a class="ss_calDateDownButton" href="javascript: ;" onclick="ss_cal_Events.switchView('prev'); return false;">
		<img <ssf:alt tag="alt.viewCalPrev"/> title="<ssf:nlt tag="alt.viewCalPrev"/>"
		src="<html:imagesPath/>pics/1pix.gif" />
	</a>
	
	<span id="ss_calViewDatesDescriptions"><fmt:formatDate value="${ssCalStartDate}" pattern="MMMM, yyyy" /></span>
	
	<a class="ss_calDateUpButton" href="javascript: ;" onclick="ss_cal_Events.switchView('next'); return false;">
		<img <ssf:alt tag="alt.viewCalNext"/> title="<ssf:nlt tag="alt.viewCalNext"/>"
		src="<html:imagesPath/>pics/1pix.gif" />
	</a>
	
	<form name="ssCalNavBar" id="ssCalNavBar" action="${goto_form_url}" 
	  class="ss_toolbar_color"
	  method="post" style="display:inline;"><div class="ss_toolbar_color" style="display:inline;">
		<ssf:datepicker formName="ssCalNavBar" showSelectors="true" 
		 popupDivId="ss_calDivPopup" id="ss_goto" initDate="${ssCurrentDate}"
		 callbackRoutine="ss_getMonthCalendarEvents" immediateMode="true" altText="<%= NLT.get("calendar.view.popupAltText") %>"
		 /></div></form>
	<div id="ss_calDivPopup" class="ss_calPopupDiv"></div>

</div>