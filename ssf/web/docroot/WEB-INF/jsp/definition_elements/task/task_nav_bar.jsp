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
		<li class="ss_calendarNaviBarOption ss_calendarNaviBarOptionMiddleImg">
			<a class="ss_calDaySelectButton" href="javascript: ;" 
			  onclick="myTasks_<portlet:namespace/>.filterTasks('CLOSED'); return false;">
				<img <ssf:alt tag="alt.viewClosed"/> title="<ssf:nlt tag="alt.viewClosed"/>" 
				src="<html:imagesPath/>pics/1pix.gif" />
			</a>
		</li>
		<li class="ss_calendarNaviBarOption ss_calendarNaviBarOptionMiddleImg">
			<a class="ss_cal3DaysSelectButton" href="javascript: ;" 
				onclick="myTasks_<portlet:namespace/>.filterTasks('DAY'); return false;">
				<img <ssf:alt tag="alt.viewToday"/> title="<ssf:nlt tag="alt.viewToday"/>"
				src="<html:imagesPath/>pics/1pix.gif" />
			</a>
		</li>
		<li class="ss_calendarNaviBarOption ss_calendarNaviBarOptionMiddleImg">
			<a class="ss_cal5DaysSelectButton" href="javascript: ;" 
				onclick="myTasks_<portlet:namespace/>.filterTasks('WEEK'); return false;">
				<img <ssf:alt tag="alt.viewWeek"/> title="<ssf:nlt tag="alt.viewWeek"/>" 
				src="<html:imagesPath/>pics/1pix.gif" />
			</a>
		</li>
		<li class="ss_calendarNaviBarOption ss_calendarNaviBarOptionMiddleImg">
			<a class="ss_cal7DaysSelectButton" href="javascript: ;" 
				onclick="myTasks_<portlet:namespace/>.filterTasks('MONTH'); return false;">
				<img <ssf:alt tag="alt.viewMonth"/> title="<ssf:nlt tag="alt.viewMonth"/>" 
				src="<html:imagesPath/>pics/1pix.gif" />
			</a>
		</li>
		<li class="ss_calendarNaviBarOption ss_calendarNaviBarOptionMiddleImg">
			<a class="ss_calMonthSelectButton" href="javascript: ;" 
				onclick="myTasks_<portlet:namespace/>.filterTasks('ACTIVE'); return false;">
				<img <ssf:alt tag="alt.viewAllActive"/> title="<ssf:nlt tag="alt.viewAllActive"/>" 
				src="<html:imagesPath/>pics/1pix.gif" />
			</a>
		</li>
		<li class="ss_calendarNaviBarOption" />
	</ul>
</div>