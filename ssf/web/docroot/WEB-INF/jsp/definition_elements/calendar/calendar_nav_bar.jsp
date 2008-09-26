<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<%@ page import="java.util.Date" %>
<div class="ss_clear"></div>
<div>
  <ssHelpSpot helpId="workspaces_folders/misc_tools/calendar_tools" offsetX="0" 
    title="<ssf:nlt tag="helpSpot.calendarTools"/>"></ssHelpSpot>
    
	<ul class="ss_calendarNaviBar">
		<li class="ss_calendarNaviBarOption ss_calHoursSelectorMenu">
			<span class="ss_calHoursSelectorMenu">
				<ssf:menu title="<%= NLT.get("calendar.hours.workday") %>" 
							titleId="ss_selectCalendarHours${prefix}" 
							titleClass="ss_hoursSelectorTitle ss_tinyControl" 
							menuClass="ss_actions_bar5 ss_actions_bar_submenu" menuImage="pics/menudown.gif">
					<ul class="ss_actions_bar3 ss_hoursSelectorList" style="width: 100px; ">
						<li>
							<a href="javascript: ;" onClick="ss_calendar_${prefix}.fullDayGrid(); return false;">
								<ssf:nlt tag="calendar.hours.fullday"/>
							</a>
						</li>
						<li>
							<a href="javascript: ;" onClick="ss_calendar_${prefix}.workDayGrid(); return false;">
								<ssf:nlt tag="calendar.hours.workday"/>
							</a>
						</li>
					</ul>
				</ssf:menu>
			</span>
		</li>
		<li class="ss_calendarNaviBarSeparator"/>
		<li class="ss_calendarNaviBarOption ss_calendarNaviBarOptionMiddleImg"><a class="ss_calDaySelectButton" id="ss_calDaySelectButton${prefix}" href="javascript: ;" 
			  onclick="ss_calendar_${prefix}.switchView('daydelta'); return false;"><img <ssf:alt tag="alt.view1Day"/> title="<ssf:nlt tag="alt.view1Day"/>" 
				src="<html:imagesPath/>pics/1pix.gif" /></a>
				
				<a class="ss_cal3DaysSelectButton" id="ss_cal3DaysSelectButton${prefix}" href="javascript: ;" onclick="ss_calendar_${prefix}.switchView('3daydelta'); return false;"><img <ssf:alt tag="alt.view3Days"/> title="<ssf:nlt tag="alt.view3Days"/>" 
					src="<html:imagesPath/>pics/1pix.gif" /></a>
				
				<a class="ss_cal5DaysSelectButton" id="ss_cal5DaysSelectButton${prefix}" href="javascript: ;" onclick="ss_calendar_${prefix}.switchView('workweek'); return false;" ><img <ssf:alt tag="alt.view5Days"/> title="<ssf:nlt tag="alt.view5Days"/>"
					src="<html:imagesPath/>pics/1pix.gif" /></a>
				
				<a class="ss_cal7DaysSelectButton" id="ss_cal7DaysSelectButton${prefix}" href="javascript: ;" onclick="ss_calendar_${prefix}.switchView('week'); return false;"><img <ssf:alt tag="alt.view1Week"/> title="<ssf:nlt tag="alt.view1Week"/>"
					src="<html:imagesPath/>pics/1pix.gif" /></a>
				
				<a class="ss_cal14DaysSelectButton" id="ss_cal14DaysSelectButton${prefix}" href="javascript: ;" onclick="ss_calendar_${prefix}.switchView('fortnight'); return false;"><img <ssf:alt tag="alt.view2Weeks"/> title="<ssf:nlt tag="alt.view2Weeks"/>"
					src="<html:imagesPath/>pics/1pix.gif" /></a>
				
				<a class="ss_calMonthSelectButton" id="ss_calMonthSelectButton${prefix}" href="javascript: ;" onclick="ss_calendar_${prefix}.switchView('month'); return false;"><img <ssf:alt tag="alt.view1Month"/> title="<ssf:nlt tag="alt.view1Month"/>" 
					src="<html:imagesPath/>pics/1pix.gif" /></a></li>
		<li class="ss_calendarNaviBarOption ss_calViewDatesDescriptions">
			<table>
				<tr>
					<td>
						<a class="ss_calDateDownButton" href="javascript: ;" onclick="ss_calendar_${prefix}.switchView('prev'); return false;">
							<img <ssf:alt tag="alt.viewCalPrev"/> title="<ssf:nlt tag="alt.viewCalPrev"/>"
							src="<html:imagesPath/>pics/1pix.gif" />
						</a>
					</td>
					<td style="width: 208px; text-align: center; ">
						<span id="ss_calViewDatesDescriptions${prefix}"><fmt:formatDate value="${ssCalStartDate}" pattern="MMMM, yyyy" /></span>
					</td>
					<td>
						<a class="ss_calDateUpButton" href="javascript: ;" onclick="ss_calendar_${prefix}.switchView('next'); return false;">
							<img <ssf:alt tag="alt.viewCalNext"/> title="<ssf:nlt tag="alt.viewCalNext"/>"
							src="<html:imagesPath/>pics/1pix.gif" />
						</a>
					</td>
				</tr>
			</table></li>
		<li class="ss_calendarNaviBarOption ss_calendarNaviBarOptionMiddleImg"><a href="javascript: // " onclick="ss_calendar_settings.configure('${ssUserProperties.calendarFirstDayOfWeek}'<c:if test="${!empty ssUserProperties.calendarWorkDayStart}">, ${ssUserProperties.calendarWorkDayStart}</c:if>);"><img border="0" class="ss_accessory_modify" src="<html:imagesPath/>pics/1pix.gif" 
				    title="<ssf:nlt tag="calendar.settings.link"/>>" /></a></li>			
		<li class="ss_calSelectDate ss_calendarNaviBarOption">
			<form name="ssCalNavBar${prefix}" id="ssCalNavBar${prefix}" action="${goto_form_url}" 
			  class="ss_toolbar_color"
			  method="post" style="display:inline;float:left;"><div class="ss_toolbar_color" style="display:inline;float:left;">
				<ssf:datepicker formName="ssCalNavBar${prefix}" showSelectors="true" 
				 popupDivId="ss_calDivPopup${prefix}" id="ss_goto${prefix}" initDate="${ssCurrentDate}"
				 callbackRoutine="ss_getMonthCalendarEvents${prefix}" immediateMode="true" altText="<%= NLT.get("calendar.view.popupAltText") %>"
				 /></div></form>
				 <div id="ss_calDivPopup${prefix}" class="ss_calPopupDiv"  style="display:inline;float:left;"></div>
		</li>
	</ul>
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			if (ss_calendar_settings) {
				if (!ss_calendar_settings["locale"]) {
					ss_calendar_settings.locale = {};
				}
				ss_calendar_settings.locale.title = "<ssf:nlt tag="calendar.settings.title"/>";
				ss_calendar_settings.locale.weekStartsOnLabel = "<ssf:nlt tag="calendar.settings.weekStartsOn"/>";
				ss_calendar_settings.locale.workDayStartsAtLabel = "<ssf:nlt tag="calendar.settings.workDayStartsAt"/>";
				ss_calendar_settings.locale.dayNames = ["<ssf:nlt tag="calendar.day.names.su"/>", "<ssf:nlt tag="calendar.day.names.mo"/>", "<ssf:nlt tag="calendar.day.names.tu"/>", "<ssf:nlt tag="calendar.day.names.we"/>", "<ssf:nlt tag="calendar.day.names.th"/>", "<ssf:nlt tag="calendar.day.names.fr"/>", "<ssf:nlt tag="calendar.day.names.sa"/>"];
				ss_calendar_settings.locale.submitLabel = "<ssf:nlt tag="calendar.settings.submit"/>";				
			}
		});
	</script>
</div>