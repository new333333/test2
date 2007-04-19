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
	<ul id="ss_calendarNaviBar">
		<li class="ss_calendarNaviBarOption">
			<span class="ss_calHoursSelectorMenu">
				<ssf:menu title="<%= NLT.get("calendar.hours.workday") %>" 
							titleId="ss_selectCalendarHours" 
							titleClass="ss_hoursSelectorTitle ss_tinyControl" 
							menuClass="ss_actions_bar3 ss_actions_bar_submenu" menuImage="pics/menudown.gif">
					<ul class="ss_actions_bar3 ss_hoursSelectorList" style="width: 100px; ">
						<li>
							<a href="javascript: ;" onClick="ss_cal_Grid.fullDayGrid(); return false;">
								<ssf:nlt tag="calendar.hours.fullday"/>
							</a>
						</li>
						<li>
							<a href="javascript: ;" onClick="ss_cal_Grid.workDayGrid(); return false;">
								<ssf:nlt tag="calendar.hours.workday"/>
							</a>
						</li>
					</ul>
				</ssf:menu>
			</span>
		</li>
		<li class="ss_calendarNaviBarSeparator"/>
		<li class="ss_calendarNaviBarOption ss_calendarNaviBarOptionMiddleImg">
			<a href="javascript: ;" onclick="ss_cal_Events.switchView('daydelta'); return false;" onmouseover="document.getElementById('ss_calDaySelector').src='<html:imagesPath/>icons/day_f.gif'" onmouseout="document.getElementById('ss_calDaySelector').src='<html:imagesPath/>icons/day.gif'">
				<img id="ss_calDaySelector" src="<html:imagesPath/>icons/day.gif" />
			</a>
		</li>
		<li class="ss_calendarNaviBarOption ss_calendarNaviBarOptionMiddleImg">
			<a href="javascript: ;" onclick="ss_cal_Events.switchView('3daydelta'); return false;" onmouseover="document.getElementById('ss_cal3DaySelector').src='<html:imagesPath/>icons/3_day_f.gif'" onmouseout="document.getElementById('ss_cal3DaySelector').src='<html:imagesPath/>icons/3_day.gif'">
				<img id="ss_cal3DaySelector" src="<html:imagesPath/>icons/3_day.gif" />
			</a>
		</li>
		<li class="ss_calendarNaviBarOption ss_calendarNaviBarOptionMiddleImg">
			<a href="javascript: ;" onclick="ss_cal_Events.switchView('workweek'); return false;" onmouseover="document.getElementById('ss_cal5DaySelector').src='<html:imagesPath/>icons/5_day_f.gif'" onmouseout="document.getElementById('ss_cal5DaySelector').src='<html:imagesPath/>icons/5_day.gif'">
				<img id="ss_cal5DaySelector" src="<html:imagesPath/>icons/5_day.gif" />
			</a>
		</li>
		<li class="ss_calendarNaviBarOption ss_calendarNaviBarOptionMiddleImg">
			<a href="javascript: ;" onclick="ss_cal_Events.switchView('week'); return false;" onmouseover="document.getElementById('ss_calWeekSelector').src='<html:imagesPath/>icons/7_day_f.gif'" onmouseout="document.getElementById('ss_calWeekSelector').src='<html:imagesPath/>icons/7_day.gif'">
				<img id="ss_calWeekSelector" src="<html:imagesPath/>icons/7_day.gif" />
			</a>
		</li>
		<li class="ss_calendarNaviBarOption ss_calendarNaviBarOptionMiddleImg">
			<a href="javascript: ;" onclick="ss_cal_Events.switchView('fortnight'); return false;" onmouseover="document.getElementById('ss_calFortnightSelector').src='<html:imagesPath/>icons/2_weeks_f.gif'" onmouseout="document.getElementById('ss_calFortnightSelector').src='<html:imagesPath/>icons/2_weeks.gif'">
				<img id="ss_calFortnightSelector" src="<html:imagesPath/>icons/2_weeks.gif" />
			</a>
		</li>
		<li class="ss_calendarNaviBarOption ss_calendarNaviBarOptionMiddleImg">
			<a href="javascript: ;" onclick="ss_cal_Events.switchView('month'); return false;" onmouseover="document.getElementById('ss_calMonthSelector').src='<html:imagesPath/>icons/month_f.gif'" onmouseout="document.getElementById('ss_calMonthSelector').src='<html:imagesPath/>icons/month.gif'">
				<img id="ss_calMonthSelector" src="<html:imagesPath/>icons/month.gif" />
			</a>
		</li>
		<li class="ss_calendarNaviBarOption ss_calendarNaviBarOptionBigImg">
			<a href="javascript: ;" onclick="ss_cal_Events.switchView('prev'); return false;" onmouseover="document.getElementById('ss_calPrevSelector').src='<html:imagesPath/>icons/date_down_f.gif'" onmouseout="document.getElementById('ss_calPrevSelector').src='<html:imagesPath/>icons/date_down.gif'">
				<img id="ss_calPrevSelector" class="ss_CalNaviBigImg" src="<html:imagesPath/>icons/date_down.gif" />
			</a>
		</li>
		<li class="ss_calViewDatesDescriptionLi ss_calendarNaviBarOption">
			<span id="ss_calViewDatesDescriptions"><fmt:formatDate value="${ssCalStartDate}" pattern="MMMM, yyyy" /></span>
		</li>
		<li class="ss_calendarNaviBarOption ss_calendarNaviBarOptionBigImg">
			<a href="javascript: ;" onclick="ss_cal_Events.switchView('next'); return false;" onmouseover="document.getElementById('ss_calNextSelector').src='<html:imagesPath/>icons/date_up_f.gif'" onmouseout="document.getElementById('ss_calNextSelector').src='<html:imagesPath/>icons/date_up.gif'">
				<img id="ss_calNextSelector" class="ss_fullHeight" src="<html:imagesPath/>icons/date_up.gif" />
			</a>
		</li>
		<li class="ss_calSelectDate ss_calendarNaviBarOption">
			<form name="ssCalNavBar" id="ssCalNavBar" action="${goto_form_url}" 
			  class="ss_toolbar_color"
			  method="post" style="display:inline;"><div class="ss_toolbar_color" style="display:inline;">
				<ssf:datepicker formName="ssCalNavBar" showSelectors="true" 
				 popupDivId="ss_calDivPopup" id="ss_goto" initDate="${ssCurrentDate}"
				 callbackRoutine="ss_getMonthCalendarEvents" immediateMode="true" altText="<%= NLT.get("calendar.view.popupAltText") %>"
				 /></div></form>
			<div id="ss_calDivPopup" class="ss_calPopupDiv"></div>
		</li>
		<li class="ss_calendarNaviBarOption" />	
	</ul>
</div>