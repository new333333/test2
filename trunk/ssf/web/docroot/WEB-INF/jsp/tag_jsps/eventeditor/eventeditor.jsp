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
<%@ page import="com.sitescape.team.util.CalendarHelper" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>


<c:set var="allDayEventId" value="allDayEvent_${evid}" />
<c:set var="dateId" value="dp_${evid}" />
<c:set var="dateId2" value="dp2_${evid}" />
<c:set var="endrangeId" value="endRange_${evid}" />

<script type="text/javascript">
	dojo.require('sitescape.widget.DropdownDatePickerActivateByInput');
	dojo.require('sitescape.widget.DropdownTimePickerActivateByInput');
	dojo.require('sitescape.widget.DropdownEventDatePicker');
	dojo.require('sitescape.widget.DropdownEventTimePicker');
</script>

<script type="text/javascript" src="<html:rootPath />js/common/ss_event.js"></script>
<c:set var="prefix" value="${evid}" />

<%
	// Tests:
	// - create event
	// - event created in time zone 1 and updated into time zone 2
	// - event imported in time zone 1 and updated into time zone 2
	// - all day event
%>
<c:choose>
	<c:when test="${initEvent.allDayEvent || !initEvent.timeZoneSensitive}">
		<c:set var="timeZoneID" value="GMT" />
	</c:when>
	<c:otherwise>
		<c:set var="timeZoneID" value="${ssUser.timeZone.ID}" />
	</c:otherwise>
</c:choose>

<div class="ss_event_editor">
	<table class="ss_style">
		<tr>
			<td class="contentbold"><ssf:nlt tag="event.start" />:</td>
			<td>
				<div dojoType="DropdownEventDatePicker" 
					widgetId="event_start_${prefix}" 
					name="${dateId}_fullDate" 
					id="${dateId}_${prefix}"
					lang="<ssf:convertLocaleToDojoStyle />"
					<c:choose>
					    <c:when test="${!empty ssUserProperties.calendarFirstDayOfWeek}">
						    weekStartsOn="${ssUserProperties.calendarFirstDayOfWeek - 1}"
					    </c:when>
					    <c:otherwise>
					    	weekStartsOn="<%= CalendarHelper.getFirstDayOfWeek() - 1 %>"
					    </c:otherwise>
					</c:choose>
					<c:if test="${!empty startDate}">
						value="<fmt:formatDate value="${startDate}" pattern="yyyy-MM-dd" timeZone="${timeZoneID}"/>"
					</c:if>
					startDateWidgetId="event_start_${prefix}"
					startTimeWidgetId="event_start_time_${prefix}"
					endDateWidgetId="event_end_${prefix}"
					endTimeWidgetId="event_end_time_${prefix}"></div>
			</td>
			<td>
				<span id="${prefix}eventStartTime"
					<c:if test="${initEvent.allDayEvent}">
						style="display: none; "
					</c:if>
					>
					<div dojoType="DropdownEventTimePicker"
						widgetId="event_start_time_${prefix}" 
						name="${dateId}_0_fullTime" 
						id="${dateId}_time_${prefix}"
						lang="<ssf:convertLocaleToDojoStyle />" 	
						<c:choose>
							<c:when test="${initEvent.allDayEvent}">
								value="08:00:00"
							</c:when>
							<c:otherwise>
								<c:if test="${!empty startDate}">
									value="<fmt:formatDate value="${startDate}" pattern="HH:mm:ss" timeZone="${timeZoneID}"/>"
								</c:if>
							</c:otherwise>
						</c:choose>
						startDateWidgetId="event_start_${prefix}"
						startTimeWidgetId="event_start_time_${prefix}"
						endDateWidgetId="event_end_${prefix}"
						endTimeWidgetId="event_end_time_${prefix}"></div>
						
					<input type="hidden" name="${dateId}_timezoneid" value="${ssUser.timeZone.ID}" />
					<input type="hidden" name="${dateId}_skipTime" id="${dateId}_skipTime_${prefix}"
						<c:choose>
							<c:when test="${initEvent.allDayEvent}">
								value="true"
							</c:when>
							<c:otherwise>
								value="false"
							</c:otherwise>
						</c:choose>
						/>
					<input type="hidden" name="${dateId}_timeZoneSensitive" id="${dateId}_timeZoneSensitive" value="" />
					
				</span>	
			</td>
			<c:if test="${attMap.hasDur}">
				<td>
					<input type="checkbox" name="${allDayEventId}"
					<c:if test="${initEvent.allDayEvent}">
						checked="checked"
					</c:if> id="${prefix}_${dateId}_allDayEvent" 
					onclick="${prefix}ssEventEditor.toggleAllDay(this, ['${dateId}_skipTime_${prefix}', '${dateId2}_skipTime_${prefix}']); " /><label for="${prefix}_${dateId}_allDayEvent"><ssf:nlt tag="event.allDay" /></label>
				</td>
			</c:if>
		</tr>
	
	<c:if test="${attMap.hasDur}">
			<tr>
				<td class="contentbold"><ssf:nlt tag="event.end" />:</td>
				<td>
					<div dojoType="DropdownEventDatePicker" 
						widgetId="event_end_${prefix}" 
						name="${dateId2}_fullDate" 
						id="${dateId2}_${prefix}"
						lang="<ssf:convertLocaleToDojoStyle />" 
						<c:choose>
						    <c:when test="${!empty ssUserProperties.calendarFirstDayOfWeek}">
							    weekStartsOn="${ssUserProperties.calendarFirstDayOfWeek - 1}"
						    </c:when>
						    <c:otherwise>
						    	weekStartsOn="<%= CalendarHelper.getFirstDayOfWeek() - 1 %>"
						    </c:otherwise>
						</c:choose>
						<c:if test="${!empty endDate}">			
							value="<fmt:formatDate value="${endDate}" pattern="yyyy-MM-dd" timeZone="${timeZoneID}"/>"
						</c:if>
						startDateWidgetId="event_start_${prefix}"
						startTimeWidgetId="event_start_time_${prefix}"
						endDateWidgetId="event_end_${prefix}"
						endTimeWidgetId="event_end_time_${prefix}"></div>
				</td>
				<td>
					<span id="${prefix}eventEndTime"
						<c:if test="${initEvent.allDayEvent}">
							style="display: none; "
						</c:if>			
						>
						<div dojoType="DropdownEventTimePicker"
							widgetId="event_end_time_${prefix}" 
							name="${dateId2}_0_fullTime" 
							id="${dateId2}_time_${prefix}"
							lang="<ssf:convertLocaleToDojoStyle />" 
							<c:choose>
								<c:when test="${initEvent.allDayEvent}">
									value="08:30:00"
								</c:when>
								<c:otherwise>
									<c:if test="${!empty endDate}">	
										value="<fmt:formatDate value="${endDate}" pattern="HH:mm:ss" timeZone="${timeZoneID}"/>"
									</c:if>
								</c:otherwise>
							</c:choose>						
							startDateWidgetId="event_start_${prefix}"
							startTimeWidgetId="event_start_time_${prefix}"
							endDateWidgetId="event_end_${prefix}"
							endTimeWidgetId="event_end_time_${prefix}"></div>
							
						<input type="hidden" name="${dateId2}_timezoneid" value="${ssUser.timeZone.ID}" />
						<input type="hidden" name="${dateId2}_skipTime" id="${dateId2}_skipTime_${prefix}"
							<c:choose>
								<c:when test="${initEvent.allDayEvent}">
									value="true"
								</c:when>
								<c:otherwise>
									value="false"
								</c:otherwise>
							</c:choose>
							/>
						<input type="hidden" name="${dateId2}_timeZoneSensitive" id="${dateId2}_timeZoneSensitive" value="" />							
					</span>
				</td>
			</tr>
	</c:if>
	<c:if test="${attMap.isTimeZoneSensitiveActive}">
		<tr>
			<td colspan="4">
				<input type="checkbox" name="timeZoneSensitive_${evid}"
					<c:if test="${initEvent.timeZoneSensitive}">
						checked="checked"
					</c:if> id="timeZoneSensitive_${evid}" value="true" /><label for="timeZoneSensitive_${evid}"><ssf:nlt tag="event.timeZoneSensitive" /></label>
			</td>
		</tr>
	</c:if>	
	</table>
	

<c:set var="interval" value="1" />
<c:set var="frequency" value="none" />
	
<c:if test="${attMap.hasRecur}">
	<c:if test="${!empty initEvent.interval}">
		<c:set var="interval" value="${initEvent.interval}" />
	</c:if>

	<c:choose>
		<c:when test="${initEvent.frequencyString == 'DAILY'}">
			<c:set var="frequency" value="day" />
		</c:when>
		<c:when test="${initEvent.frequencyString == 'WEEKLY'}">
			<c:set var="frequency" value="week" />
		</c:when>
		<c:when test="${initEvent.frequencyString == 'MONTHLY'}">
			<c:set var="frequency" value="month" />
		</c:when>
		<c:when test="${initEvent.frequencyString == 'YEARLY'}">
			<c:set var="frequency" value="year" />
		</c:when>
	</c:choose>
	
	<c:set var="sundaySeleted" value="false" />
	<c:set var="mondaySeleted" value="false" />
	<c:set var="tuesdaySeleted" value="false" />
	<c:set var="wednesdaySeleted" value="false" />
	<c:set var="thursdaySeleted" value="false" />
	<c:set var="fridaySeleted" value="false" />
	<c:set var="saturdaySeleted" value="false" />
	<c:set var="daynum" value="" />
	<c:set var="dowstring" value="" />

	<c:forEach var="daypos" items="${initEvent.byDay}">
		<c:choose>
			<c:when test="${daypos.dayOfWeek == 1}">
				<c:set var="sundaySeleted" value="true" />
			</c:when>
			<c:when test="${daypos.dayOfWeek == 2}">
				<c:set var="mondaySeleted" value="true" />
			</c:when>
			<c:when test="${daypos.dayOfWeek == 3}">
				<c:set var="tuesdaySeleted" value="true" />
			</c:when>
			<c:when test="${daypos.dayOfWeek == 4}">
				<c:set var="wednesdaySeleted" value="true" />
			</c:when>
			<c:when test="${daypos.dayOfWeek == 5}">
				<c:set var="thursdaySeleted" value="true" />
			</c:when>
			<c:when test="${daypos.dayOfWeek == 6}">
				<c:set var="fridaySeleted" value="true" />
			</c:when>
			<c:when test="${daypos.dayOfWeek == 7}">
				<c:set var="saturdaySeleted" value="true" />
			</c:when>
		</c:choose>
		<% // we only implement daynum (onDayCard) for months... in that case,
		   // there will only be one DayPositiion entry in the array
		%>
		<c:choose>
			<c:when test="${daypos.dayPosition == 0}" >
				<c:set var="daystring" value="none" />
			</c:when>
			<c:when test="${daypos.dayPosition == 1}" >
				<c:set var="daystring" value="first" />
			</c:when>
			<c:when test="${daypos.dayPosition == 2}" >
				<c:set var="daystring" value="second" />
			</c:when>
			<c:when test="${daypos.dayPosition == 3}" >
				<c:set var="daystring" value="third" />
			</c:when>
			<c:when test="${daypos.dayPosition == 4}" >
				<c:set var="daystring" value="fourth" />
			</c:when>
			<c:when test="${daypos.dayPosition == 5}" >
				<c:set var="daystring" value="last" />
			</c:when>
		</c:choose>

		<c:set var="dowstring" value="${daypos.dayOfWeekString}" />

	</c:forEach>	

	<div class="ss_event_recurences">
		
		<label for="${prefix}RepeatFrequency"><ssf:nlt tag="event.repeat" /></label>
		
		<select name="${prefix}_repeatUnit" 
				id="${prefix}RepeatFrequency" 
				onchange="${prefix}ssEventEditor.setFrequency(this)">
			<option value="none"<c:if test="${frequency == 'none'}"> selected="true"</c:if>><ssf:nlt tag="event.editor.frequency.none" /></option>
			<option value="day"<c:if test="${frequency == 'day'}"> selected="true"</c:if>><ssf:nlt tag="event.editor.frequency.daily" /></option>
			<option value="week"<c:if test="${frequency == 'week'}"> selected="true"</c:if>><ssf:nlt tag="event.editor.frequency.weekly" /></option>
			<option value="month"<c:if test="${frequency == 'month'}"> selected="true"</c:if>><ssf:nlt tag="event.editor.frequency.monthly" /></option>
			<option value="year"<c:if test="${frequency == 'year'}"> selected="true"</c:if>><ssf:nlt tag="event.editor.frequency.yearly" /></option>								
		</select>
		
		<div id="${prefix}RequrencyDefinitions" style="visibility:hidden; display:none;" class="ss_event_repeat"></div>
		<div id="${prefix}Range" style="visibility:hidden; display:none;" class="ss_event_range">
			<table class="ss_style" border="0" cellpadding="2" cellspacing="0">
				<% /* 
					* Until stuff works like this:
					*   count == 0 means repeats forever
					*   count == -1 means until was specified and we don't know the count
					*   count > 0 means we do know the count and the until member is also there and computed from count
					*/
				%>
				<c:set var="count" value="0" />
				<c:if test="${!empty initEvent.count}"> 
					<c:set var="count" value="${initEvent.count}" />
				</c:if>
				<tr>
					<td>
						<ssf:nlt tag="event.repeatrange" />
					</td>
				</tr>
				<tr>
					<td 
						<c:choose>
							<c:when test="${count > 0}">class="ss_requrency_row_active"</c:when>
							<c:otherwise>class="ss_requrency_row_unactive"</c:otherwise>
						</c:choose>
						id="${prefix}_rangeSel_count_row">
						<input type="radio" 
								name="${prefix}_rangeSel" 
								id="${prefix}_rangeSel_count" value="count"
								<c:if test="${count > 0}" > checked="checked" </c:if>
								onchange="${prefix}_rangeRowMarker.mark(this, '${prefix}_rangeSel_count_row')" />
						<label for="${prefix}_rangeSel_count"><ssf:nlt tag="event.repeat" /></label>
						<input type="text" class="ss_text" size="2" 
								name="${prefix}_repeatCount" 
								id="${prefix}_repeatCount"
								<c:choose>
									<c:when test="${count > 0}" > value="${count}" </c:when>
									<c:otherwise> value="10" </c:otherwise>
								</c:choose>
								onfocus="${prefix}_checkRadio('${prefix}_rangeSel_count'); ${prefix}_rangeRowMarker.mark(document.getElementById('${prefix}_rangeSel_count'), '${prefix}_rangeSel_count_row'); " />
						<label for="${prefix}_repeatCount"><ssf:nlt tag="event.times" /></label>
					</td>
				</tr>

				<tr>
					<td 
						<c:choose>
							<c:when test="${count == -1}">class="ss_requrency_row_active"</c:when>
							<c:otherwise>class="ss_requrency_row_unactive"</c:otherwise>
						</c:choose>   	
						id="${prefix}_rangeSel_until_row" >
						<input type="radio" name="${prefix}_rangeSel" id="${prefix}_rangeSel_until" value="until"
							<c:if test="${count == -1}"> checked="checked" </c:if>
							onchange="${prefix}_rangeRowMarker.mark(this, '${prefix}_rangeSel_until_row')" /> 
						<label for="${prefix}_rangeSel_until"><ssf:nlt tag="event.repeat_until" /> </label>

						<div dojoType="DropdownDatePickerActivateByInput" 
						widgetId="repeat_until_${prefix}" 
						name="${endrangeId}_fullDate" 
						id="${endrangeId}_${prefix}"
						lang="<ssf:convertLocaleToDojoStyle />" 
						<c:choose>
						    <c:when test="${!empty ssUserProperties.calendarFirstDayOfWeek}">
							    weekStartsOn="${ssUserProperties.calendarFirstDayOfWeek - 1}"
						    </c:when>
						    <c:otherwise>
						    	weekStartsOn="<%= CalendarHelper.getFirstDayOfWeek() - 1 %>"
						    </c:otherwise>
						</c:choose>
						<c:if test="${!empty initEvent.until}">
							value="<fmt:formatDate value="${initEvent.until.time}" pattern="yyyy-MM-dd" timeZone="${timeZoneID}"/>"
						</c:if>
						></div>
    
					</td>
				</tr>
				<tr>
					<td 
						<c:choose>
							<c:when test="${count == 0}">class="ss_requrency_row_active"</c:when>
							<c:otherwise>class="ss_requrency_row_unactive"</c:otherwise>
						</c:choose>   
						id="${prefix}_rangeSel_forever_row">
						<input type="radio" name="${prefix}_rangeSel" 
								id="${prefix}_rangeSel_forever" value="forever" 
								<c:if test="${count == 0}"> checked="checked" </c:if>
								onchange="${prefix}_rangeRowMarker.mark(this, '${prefix}_rangeSel_forever_row')" /> 
						<label for="${prefix}_rangeSel_forever"><ssf:nlt tag="event.repeat_forever" /> </label>
					</td>
				</tr>
			</table>			
		</div>		
	</div>
</c:if>
	
	<script type="text/javascript">
		
		var ${prefix}ssEventEditor = new ssEventEditor("${prefix}", "${frequency}", ${interval}, 
											[${sundaySeleted}, ${mondaySeleted}, ${tuesdaySeleted}, ${wednesdaySeleted}, ${thursdaySeleted}, ${fridaySeleted}, ${saturdaySeleted}], 
											{dayPosition: "${daystring}", dayOfWeek: "${dowstring}"}
										);
		
		${prefix}ssEventEditor.locale.every = "<ssf:nlt tag="event.every" />";
		${prefix}ssEventEditor.locale.days = "<ssf:nlt tag="event.days" />";
		${prefix}ssEventEditor.locale.weeks = "<ssf:nlt tag="event.weeks" />";
		${prefix}ssEventEditor.locale.weeksOccurson = "<ssf:nlt tag="event.editor.weeks.occurson" />";
		${prefix}ssEventEditor.locale.months = "<ssf:nlt tag="event.months" />";
		${prefix}ssEventEditor.locale.years = "<ssf:nlt tag="event.years" />";
		${prefix}ssEventEditor.locale.dayNamesShort = ["<ssf:nlt tag="calendar.day.abbrevs.su"/>", "<ssf:nlt tag="calendar.day.abbrevs.mo"/>", "<ssf:nlt tag="calendar.day.abbrevs.tu"/>", "<ssf:nlt tag="calendar.day.abbrevs.we"/>", "<ssf:nlt tag="calendar.day.abbrevs.th"/>", "<ssf:nlt tag="calendar.day.abbrevs.fr"/>", "<ssf:nlt tag="calendar.day.abbrevs.sa"/>"];
		${prefix}ssEventEditor.locale.monthOnWeeksTitle = "<ssf:nlt tag="event.month.onWeeks.title" />";
		${prefix}ssEventEditor.locale.monthOnDaysTitle = "<ssf:nlt tag="event.month.onDays.title" />";
		${prefix}ssEventEditor.locale.pleaseSelect = "<ssf:nlt tag="general.please_select" />";
		${prefix}ssEventEditor.locale.weekFirst = "<ssf:nlt tag="event.whichweek.first" />";
		${prefix}ssEventEditor.locale.weekSecond = "<ssf:nlt tag="event.whichweek.second" />";
		${prefix}ssEventEditor.locale.weekThird = "<ssf:nlt tag="event.whichweek.third" />";
		${prefix}ssEventEditor.locale.weekFourth = "<ssf:nlt tag="event.whichweek.fourth" />";
		${prefix}ssEventEditor.locale.weekLast = "<ssf:nlt tag="event.whichweek.last" />";
		${prefix}ssEventEditor.locale.weekday = "<ssf:nlt tag="calendar.day.names.weekday" />";
		${prefix}ssEventEditor.locale.weekendday = "<ssf:nlt tag="calendar.day.names.weekendday" />";
			
		${prefix}ssEventEditor.setFrequency();
	
	</script>
	
	
	<script type="text/javascript">
	
	function ${prefix}_checkRadio(id) {
		if (document.getElementById) {
			var el = document.getElementById(id);
			if (el && el.checked !== undefined) {
				el.checked = true;
			}
		}
	}
	
	function rowMarker () {
		this.currentRow = undefined;
		
		this.mark = function(radioObj, rowId) {
			if (this.currentRow) {
				this.currentRow.className = "ss_requrency_row_unactive";
			}
			if (document.getElementById && radioObj) {
				var row = document.getElementById(rowId);
				if (row) {
					if (radioObj.checked) {
						row.className= "ss_requrency_row_active";
						this.currentRow = row;
					}
				}
			}
		}
	}
	
	var ${prefix}_rangeRowMarker = new rowMarker();
	
	</script>
	
	<c:if test="${attMap.hasRecur}">
	
	<script type="text/javascript">
	
		<c:choose>
			<c:when test="${count == -1}">
				${prefix}_rangeRowMarker.currentRow = document.getElementById("${prefix}_rangeSel_until_row");
			</c:when>
			<c:when test="${count > 0}">
				${prefix}_rangeRowMarker.currentRow = document.getElementById("${prefix}_rangeSel_count_row");
			</c:when>
			<c:when test="${count == 0}">
				${prefix}_rangeRowMarker.currentRow = document.getElementById("${prefix}_rangeSel_forever_row");
			</c:when>
		</c:choose>
	
		dojo.addOnLoad( function() { 
			dojo.event.connect(dojo.widget.byId("repeat_until_${prefix}"), "onValueChanged", function() { 
				${prefix}_checkRadio('${prefix}_rangeSel_until');
				${prefix}_rangeRowMarker.mark(document.getElementById('${prefix}_rangeSel_until'), '${prefix}_rangeSel_until_row');
			});
		 });
	
	</script>   
	</c:if>
	
	<input type="hidden" name="${prefix}_event_uid" value="${initEvent.uid}" />
	
	<script type="text/javascript">
	
		djConfig.searchIds.push("${dateId}_${prefix}");
		djConfig.searchIds.push("${dateId2}_${prefix}");
		djConfig.searchIds.push("${endrangeId}_${prefix}");
		djConfig.searchIds.push("${dateId}_time_${prefix}");
		djConfig.searchIds.push("${dateId2}_time_${prefix}");
	
			function ${prefix}_onEventFormSubmit() {
		
			var eventTimeZoneSensitiveObj = document.getElementById("timeZoneSensitive_${evid}");
			var startDateTimeZoneSensitiveObj = document.getElementById("${dateId}_timeZoneSensitive");
			var endDateTimeZoneSensitiveObj = document.getElementById("${dateId2}_timeZoneSensitive");
			if (eventTimeZoneSensitiveObj && startDateTimeZoneSensitiveObj && endDateTimeZoneSensitiveObj) {
				startDateTimeZoneSensitiveObj.value = "" + eventTimeZoneSensitiveObj.checked;
				endDateTimeZoneSensitiveObj.value = "" + eventTimeZoneSensitiveObj.checked;				
			}
		
			return true;
		}

		 ss_createOnSubmitObj('${prefix}onsub', '${formName}', ${prefix}_onEventFormSubmit);
		 
	</script>

</div>