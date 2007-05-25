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
  <ssHelpSpot helpId="tools/calendar_tools" offsetX="0" 
    title="<ssf:nlt tag="helpSpot.calendarTools"/>"></ssHelpSpot>
    
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
			<a class="ss_calDaySelectButton" href="javascript: ;" 
			  onclick="ss_cal_Events.switchView('daydelta'); return false;">
				<img <ssf:alt tag="alt.view1Day"/> title="<ssf:nlt tag="alt.view1Day"/>" 
				src="<html:imagesPath/>pics/1pix.gif" />
			</a>
		</li>
		<li class="ss_calendarNaviBarOption ss_calendarNaviBarOptionMiddleImg">
			<a class="ss_cal3DaysSelectButton" href="javascript: ;" onclick="ss_cal_Events.switchView('3daydelta'); return false;">
				<img <ssf:alt tag="alt.view3Days"/> title="<ssf:nlt tag="alt.view3Days"/>" 
				src="<html:imagesPath/>pics/1pix.gif" />
			</a>
		</li>
		<li class="ss_calendarNaviBarOption ss_calendarNaviBarOptionMiddleImg">
			<a class="ss_cal5DaysSelectButton" href="javascript: ;" onclick="ss_cal_Events.switchView('workweek'); return false;" >
				<img <ssf:alt tag="alt.view5Days"/> title="<ssf:nlt tag="alt.view5Days"/>"
				src="<html:imagesPath/>pics/1pix.gif" />
			</a>
		</li>
		<li class="ss_calendarNaviBarOption ss_calendarNaviBarOptionMiddleImg">
			<a class="ss_cal7DaysSelectButton" href="javascript: ;" onclick="ss_cal_Events.switchView('week'); return false;">
				<img <ssf:alt tag="alt.view1Week"/> title="<ssf:nlt tag="alt.view1Week"/>"
				src="<html:imagesPath/>pics/1pix.gif" />
			</a>
		</li>
		<li class="ss_calendarNaviBarOption ss_calendarNaviBarOptionMiddleImg">
			<a class="ss_cal14DaysSelectButton" href="javascript: ;" onclick="ss_cal_Events.switchView('fortnight'); return false;">
				<img <ssf:alt tag="alt.view2Weeks"/> title="<ssf:nlt tag="alt.view2Weeks"/>"
				src="<html:imagesPath/>pics/1pix.gif" />
			</a>
		</li>
		<li class="ss_calendarNaviBarOption ss_calendarNaviBarOptionMiddleImg">
			<a class="ss_calMonthSelectButton" href="javascript: ;" onclick="ss_cal_Events.switchView('month'); return false;">
				<img <ssf:alt tag="alt.view1Month"/> title="<ssf:nlt tag="alt.view1Month"/>" 
				src="<html:imagesPath/>pics/1pix.gif" />
			</a>
		</li>
		<li class="ss_calendarNaviBarOption ss_calendarNaviBarOptionBigImg">
			<a class="ss_calDateDownButton" href="javascript: ;" onclick="ss_cal_Events.switchView('prev'); return false;">
				<img <ssf:alt tag="alt.viewCalPrev"/> title="<ssf:nlt tag="alt.viewCalPrev"/>"
				src="<html:imagesPath/>pics/1pix.gif" />
			</a>
		</li>
		<li class="ss_calViewDatesDescriptionLi ss_calendarNaviBarOption">
			<span id="ss_calViewDatesDescriptions"><fmt:formatDate value="${ssCalStartDate}" pattern="MMMM, yyyy" /></span>
		</li>
		<li class="ss_calendarNaviBarOption ss_calendarNaviBarOptionBigImg">
			<a class="ss_calDateUpButton" href="javascript: ;" onclick="ss_cal_Events.switchView('next'); return false;">
				<img <ssf:alt tag="alt.viewCalNext"/> title="<ssf:nlt tag="alt.viewCalNext"/>"
				src="<html:imagesPath/>pics/1pix.gif" />
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