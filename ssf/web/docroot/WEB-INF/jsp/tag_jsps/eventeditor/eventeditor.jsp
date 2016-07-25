<%
/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
<%@ page import="org.kablink.teaming.util.CalendarHelper" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>


<c:set var="allDayEventId" value="allDayEvent_${evid}" />
<c:set var="timeZoneFieldName" value="timeZone_${evid}" />
<c:set var="dateId" value="dp_${evid}" />
<c:set var="dateId2" value="dp2_${evid}" />
<c:set var="endrangeId" value="endRange_${evid}" />

<style type="text/css">
        @import "<html:rootPath />js/dojo/dijit/themes/tundra/tundra.css";
        @import "<html:rootPath />js/dojo/dojo/resources/dojo.css"
</style>

<script type="text/javascript">
	dojo.require("dojo.parser");
	dojo.require("dijit.form.DateTextBox");
	dojo.require("dijit.form.TimeTextBox");
	dojo.require("dijit.form.DateTextBoxEventEditor");
	dojo.require("dijit.form.TimeTextBoxEventEditor");
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
<c:if test="${initEvent.allDayEvent}">
	<c:set var="timeZoneID" value="GMT" />
</c:if>
<c:if test="${!initEvent.allDayEvent}">
	<c:set var="timeZoneID" value="${ssUser.timeZone.ID}" />
</c:if>

<%
	/*
	 * Bugzilla 488921:  I added the widgetId settings to the <input>'s
	 * below and modified ssf/web/docroot/js/dojo/dijit/form/DateTextBoxEventEditor.js
	 * to handle it.  If/when we move to a newer version of dojo, the
	 * handling of widgetId will most like have to be readdressed.
	 */
%>

<c:set var="interval" value="0" />
<c:set var="frequency" value="none" />
<c:set var="sundaySeleted" value="false" />
<c:set var="mondaySeleted" value="false" />
<c:set var="tuesdaySeleted" value="false" />
<c:set var="wednesdaySeleted" value="false" />
<c:set var="thursdaySeleted" value="false" />
<c:set var="fridaySeleted" value="false" />
<c:set var="saturdaySeleted" value="false" />

<div class="ss_event_editor tundra">
	<table class="ss_style">
		<tr>
			<td class="contentbold"><ssf:nlt tag="event.start" />:</td>
			<td>
                
				<input type="text" dojoType="dijit.form.DateTextBoxEventEditor" 
					name="${dateId}_fullDate" 
					id="event_start_${prefix}"
					lang="<ssf:convertLocaleToDojoStyle />"
					<c:if test="${!empty startDate}">
						value="<fmt:formatDate value="${startDate}" pattern="yyyy-MM-dd" timeZone="${timeZoneID}"/>"
					</c:if>
					widgetId="event_start_${prefix}"
					startDateWidgetId="event_start_${prefix}"
					startTimeWidgetId="event_start_time_${prefix}"
					<c:if test="${!attMap.hasDurDays}">						
						endDateWidgetId="event_end_${prefix}"
						endTimeWidgetId="event_end_time_${prefix}"
					</c:if>
					/>
			</td>
			<td>
				<span id="${prefix}eventStartTime"
					<c:if test="${initEvent.allDayEvent}">
						style="display: none; "
					</c:if>
					>
					<input type="text" dojoType="dijit.form.TimeTextBoxEventEditor"
						id="event_start_time_${prefix}" 
						name="${dateId}_0_fullTime" 
						lang="<ssf:convertLocaleToDojoStyle />" 	
						<c:choose>
							<c:when test="${initEvent.allDayEvent}">
								value="T08:00:00"
							</c:when>
							<c:otherwise>
								<c:if test="${!empty startDate}">
									value="T<fmt:formatDate value="${startDate}" pattern="HH:mm:ss" timeZone="${timeZoneID}"/>"
								</c:if>
							</c:otherwise>
						</c:choose>
						widgetId="event_start_time_${prefix}"
						startDateWidgetId="event_start_${prefix}"
						startTimeWidgetId="event_start_time_${prefix}"
						<c:if test="${!attMap.hasDurDays}">						
							endDateWidgetId="event_end_${prefix}"
							endTimeWidgetId="event_end_time_${prefix}"
						</c:if>
						/>
						
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
			<td>
				<c:if test="${attMap.hasDur}">
					<div class="marginleft1">
						<input type="checkbox" name="${allDayEventId}"
						<c:if test="${initEvent.allDayEvent}">
							checked="checked"
						</c:if> id="${prefix}_allDayEvent" 
						onclick="${prefix}ssEventEditor.toggleAllDay(this, ['${dateId}_skipTime_${prefix}', '${dateId2}_skipTime_${prefix}']); " />&nbsp;<label for="${prefix}_allDayEvent"><ssf:nlt tag="event.allDay" /></label>
					</div>
				</c:if>	
			</td>
		</tr>

	
	<c:if test="${attMap.hasDur}">
			<tr>
				<td valign="top" class="contentbold" style="padding-top: 4px;"><ssf:nlt tag="event.end" />:</td>
				<td valign="top">
					<input type="text" dojoType="dijit.form.DateTextBoxEventEditor" 
						id="event_end_${prefix}" 
						name="${dateId2}_fullDate" 
						lang="<ssf:convertLocaleToDojoStyle />" 
						<c:if test="${!empty endDate}">			
							value="<fmt:formatDate value="${endDate}" pattern="yyyy-MM-dd" timeZone="${timeZoneID}"/>"
						</c:if>
						widgetId="event_end_${prefix}"
						startDateWidgetId="event_start_${prefix}"
						startTimeWidgetId="event_start_time_${prefix}"
						endDateWidgetId="event_end_${prefix}"
						endTimeWidgetId="event_end_time_${prefix}" />
				</td>
				<td valign="top">
					<span id="${prefix}eventEndTime"
						<c:if test="${initEvent.allDayEvent}">
							style="display: none; "
						</c:if>			
						>
						<input type="text" dojoType="dijit.form.TimeTextBoxEventEditor"
							id="event_end_time_${prefix}" 
							name="${dateId2}_0_fullTime" 
							lang="<ssf:convertLocaleToDojoStyle />" 
							<c:choose>
								<c:when test="${initEvent.allDayEvent}">
									value="T08:30:00"
								</c:when>
								<c:otherwise>
									<c:if test="${!empty endDate}">	
										value="T<fmt:formatDate value="${endDate}" pattern="HH:mm:ss" timeZone="${timeZoneID}"/>"
									</c:if>
								</c:otherwise>
							</c:choose>						
							widgetId="event_end_time_${prefix}"
							startDateWidgetId="event_start_${prefix}"
							startTimeWidgetId="event_start_time_${prefix}"
							endDateWidgetId="event_end_${prefix}"
							endTimeWidgetId="event_end_time_${prefix}" />
							
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
				<td valign="top">
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
									<c:set var="daystring" value="fifth" />
								</c:when>
								<c:when test="${daypos.dayPosition == -1}" >
									<c:set var="daystring" value="last" />
								</c:when>
							</c:choose>
					
							<c:set var="dowstring" value="${daypos.dayOfWeekString}" />
					
						</c:forEach>	
					
						<div class="ss_event_recurences marginleft1">
							
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
													onfocus="${prefix}_checkRadio('${prefix}_rangeSel_count'); ${prefix}_rangeRowMarker.mark(document.getElementById('${prefix}_rangeSel_count'), '${prefix}_rangeSel_count_row'); "
													onBlur="intRequiredBlur(this, INT_MODE_GT_ZERO, '<ssf:escapeJavaScript><ssf:nlt tag="event.error.integerRequired" /></ssf:escapeJavaScript>');" />
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
					
											<input type="text" dojoType="dijit.form.DateTextBox" 
											id="repeat_until_${prefix}" 
											name="${endrangeId}_fullDate" 
											lang="<ssf:convertLocaleToDojoStyle />" 
											<c:if test="${!empty initEvent.until}">
												value="<fmt:formatDate value="${initEvent.until.time}" pattern="yyyy-MM-dd" timeZone="${timeZoneID}"/>"
											</c:if>
											/>
						
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
											
					<c:if test="${!attMap.hasRecur}">
						<div class="marginleft1">
							<input
								class="ss_submit"
								type="button"
								onclick="${prefix}_ss_clearStartEnd(); return false;"
								value="<ssf:nlt tag="event.clear.startEnd" />"
								name="clearBtn" />
						</div>
					</c:if>
				</td>
			</tr>
	</c:if>
	
	<c:if test="${attMap.hasDurDays}">
		<tr>
			<td class="contentbold"><ssf:nlt tag="event.duration_days" />:</td>
			<td nowrap>
				<input
						type="text"
						class="ss_text"
						size="5" 
						name="${prefix}_durationDays" 
						id="${prefix}_durationDays"
						<c:choose>
							<c:when test="${durationDays > 0}" > value="${durationDays}" </c:when>
							<c:otherwise> value="" </c:otherwise>
						</c:choose>
						onBlur="intRequiredBlur(this, INT_MODE_GT_ZERO, '<ssf:escapeJavaScript><ssf:nlt tag="event.error.integerRequired" /></ssf:escapeJavaScript>');" />
				&nbsp;<ssf:nlt tag="event.duration_days.hint" />
			</td>
		</tr>
	</c:if>
	
	<c:if test="${attMap.isFreeBusyActive}">
		<tr>
			<td colspan="4">
				<label for="${prefix}freeBusy"><ssf:nlt tag="event.freeBusy.legend" /></label> <select name="${prefix}_freeBusy" id="${prefix}freeBusy">
				<c:forEach var="freeBusyType" items="<%= org.kablink.teaming.domain.Event.FreeBusyType.values() %>">
					<option value="${freeBusyType}" <c:if test="${initEvent.freeBusy == freeBusyType}">
						selected="true"
					</c:if>><ssf:nlt tag="event.freeBusy.${freeBusyType}" /></option>
				</c:forEach>
				</select>
			</td>
		</tr>
	</c:if>		
	</table>
	<c:if test="${!empty ssDefinitionEntry.customAttributes[property_name].value}">
		<input type="hidden" name="${property_name}_dateExistedBefore" value="true" />
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
		${prefix}ssEventEditor.locale.weekFifth = "<ssf:nlt tag="event.whichweek.fifth" />";
		${prefix}ssEventEditor.locale.weekLast = "<ssf:nlt tag="event.whichweek.last" />";
		${prefix}ssEventEditor.locale.weekday = "<ssf:nlt tag="calendar.day.names.weekday" />";
		${prefix}ssEventEditor.locale.weekendday = "<ssf:nlt tag="calendar.day.names.weekendday" />";
		${prefix}ssEventEditor.locale.weekFirstDayDefault = (Number( ${ssUser.weekFirstDayDefault} ) - 1);
		${prefix}ssEventEditor.locale.integerRequired = "<ssf:escapeJavaScript><ssf:nlt tag="event.error.integerRequired" /></ssf:escapeJavaScript>";
			
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
			dojo.connect(dijit.byId("repeat_until_${prefix}"), "onValueChanged", function() { 
				${prefix}_checkRadio('${prefix}_rangeSel_until');
				${prefix}_rangeRowMarker.mark(document.getElementById('${prefix}_rangeSel_until'), '${prefix}_rangeSel_until_row');
			});
		 });
	
	</script>   
	</c:if>
	
	<input type="hidden" name="${prefix}_event_uid" value="${initEvent.uid}" />
	<c:if test="${!empty initEvent.timeZone}">
		<input type="hidden" name="${timeZoneFieldName}" value="${initEvent.timeZone.ID}" />
	</c:if>
	
	<script type="text/javascript">
		dojo.addOnLoad(function() {
				dojo.addClass(document.body, "tundra");
			}
		);

		function checkWidgetHasValue(id,err) {
			var	eWidget = document.getElementById(id);
			var	sValue = eWidget.value;
			if ((null == sValue) || (0 == sValue.length)) {
				alert(err);
				window.setTimeout(function(){eWidget.focus();}, 100);
				return( false );
			}
			return( true );
		}
		
		// Called to validate the combination of start/end/duration
		// values specified by the user.
		//
		// If the values are invalid, or contain an invalid
		// combination of values, the user is informed of the
		// error and false is returned.  Otherwise, true is
		// returned.
		function ${prefix}_validateDuration() {
			// Can we access all the widgets we need to validate
			// things?
			var eAllDayEvent  = document.getElementById("${prefix}_allDayEvent"     );
			var eDurationDays = document.getElementById("${prefix}_durationDays"    );
			var eEnd          = document.getElementById("event_end_${prefix}"       );
			var eEndTime      = document.getElementById("event_end_time_${prefix}"  );
			var eStart        = document.getElementById("event_start_${prefix}"     );
			var eStartTime    = document.getElementById("event_start_time_${prefix}");

			if ((null != eAllDayEvent)      &&
					(null != eDurationDays) &&
					(null != eEnd)          && (null != eEndTime) &&
					(null != eStart)        && (null != eStartTime)) {
				// Yes!  What data was supplied?
				var hasDurationDays = (0 < eDurationDays.value.length);
				var hasEnd          = (0 < eEnd.value.length);
				var hasStart        = (0 < eStart.value.length);
				
				// Is the 'All day' checkbox checked?
				if (eAllDayEvent.checked) {
					// Yes!  If we have a 'Duration'...
					if (hasDurationDays) {
						// ...that's invalid.
						alert("<ssf:escapeJavaScript><ssf:nlt tag="event.error.duration.daysWithAllDay" /></ssf:escapeJavaScript>");
						return false;
					}
					
					// If we don't have a starting date...
					if (!hasStart) {
						// ...that's invalid.
						alert("<ssf:escapeJavaScript><ssf:nlt tag="event.error.no.start" /></ssf:escapeJavaScript>");
						return false;
					}
					
					// If we don't have an ending date...
					if (!hasEnd) {
						// ...that's invalid.
						alert("<ssf:escapeJavaScript><ssf:nlt tag="event.error.no.end" /></ssf:escapeJavaScript>");
						return false;
					}
					
					// Otherwise, things are valid.
					return true;
				}
				
				else {
					// No, the 'All day' checkbox is not checked!
					//
					// Bugzilla 682430:
					//    If the start, end and duration are all blank,
					//    supply a default duration days of 1.
					if ((!hasStart) && (0 == eStartTime.value.length) &&	
						(!hasEnd)   && (0 == eEndTime.value.length)   &&
							(!hasDurationDays)) {
						eDurationDays.value = "1";
						hasDurationDays     = true;
						return true;
					}
					
					// As per the Task Improvements for Evergreen
					// design document, the following items must be
					// supplied:
					// 1) A 'Start' date; or
					// 2) Both a 'Start' and an End' date; or
					// 3) A 'Start' date and a 'Duration' (in days); or
					// 4) A 'Duration' (in days.)
					hasEnd   = (hasEnd   && true);	// (0 < eEndTime.value.length));		// Commented out and leave it to the defaults...
					hasStart = (hasStart && true);	// (0 < eStartTime.value.length));		// ...as per Bugzilla 712328 and 714419.
					if (hasStart && (!hasEnd) && (!hasDurationDays)) {
						// Condition 1 has been met.
						return true;
					}
					if (hasStart && hasEnd && (!hasDurationDays)) {
						// Condition 2 has been met.
						return true;
					}
					if (hasStart && (!hasEnd) && hasDurationDays) {
						// Condition 3 has been met.
						return true;
					}
					if ((!hasStart) && (!hasEnd) && hasDurationDays) {
						// Condition 4 has been met.
						return true;
					}

					// One of the conditions has not been met.
					alert("<ssf:escapeJavaScript><ssf:nlt tag="event.error.duration.invalidCombination" /></ssf:escapeJavaScript>");
					return false;
				}
			}

			// If we get here, things are assumed to be valid.  Return
			// true.
			return true;
		}
		
		function ${prefix}_onEventFormSubmit() {
			<c:if test="${required}">
				if (!(checkWidgetHasValue("event_start_${prefix}", "<ssf:escapeJavaScript><ssf:nlt tag="event.error.no.start" /></ssf:escapeJavaScript>"))) return( false );
				if (!(checkWidgetHasValue("event_end_${prefix}",   "<ssf:escapeJavaScript><ssf:nlt tag="event.error.no.end"   /></ssf:escapeJavaScript>"))) return( false );
				if (!(document.getElementById("${prefix}_allDayEvent").checked)) {
					if (!(checkWidgetHasValue("event_start_time_${prefix}", "<ssf:escapeJavaScript><ssf:nlt tag="event.error.no.start.time" /></ssf:escapeJavaScript>"))) return( false );
					if (!(checkWidgetHasValue("event_end_time_${prefix}",   "<ssf:escapeJavaScript><ssf:nlt tag="event.error.no.end.time"   /></ssf:escapeJavaScript>"))) return( false );
				}
			</c:if>

			var startDateTimeZoneSensitiveObj = document.getElementById("${dateId}_timeZoneSensitive");
			if (startDateTimeZoneSensitiveObj) {
				startDateTimeZoneSensitiveObj.value = "true";
			}
			var endDateTimeZoneSensitiveObj = document.getElementById("${dateId2}_timeZoneSensitive");
			if (endDateTimeZoneSensitiveObj) {
				endDateTimeZoneSensitiveObj.value = "true";				
			}
			
			// Does the form contain a duration days <INPUT>?
			<c:if test="${attMap.hasDurDays}">
				// Yes!  Are the start/end/duration values specified
				// valid?
				if (!(${prefix}_validateDuration())) {
					// No!  validateDuration() will have told the user
					// about the problems.  Simply bail.
					return false;
				}
			</c:if>
		
			return true;
		}

		/*
		 * Clears the start/end date/time entry widgets.
		 */
		function ${prefix}_ss_clearStartEnd() {
			var e;
			if (null != document.getElementById("event_end_${prefix}"       )) ${prefix}_ss_clearOneByDijitId("event_end_${prefix}"       ); 
			if (null != document.getElementById("event_end_time_${prefix}"  )) ${prefix}_ss_clearOneByDijitId("event_end_time_${prefix}"  ); 
			if (null != document.getElementById("event_start_${prefix}"     )) ${prefix}_ss_clearOneByDijitId("event_start_${prefix}"     ); 
			if (null != document.getElementById("event_start_time_${prefix}")) ${prefix}_ss_clearOneByDijitId("event_start_time_${prefix}"); 
		}
		
		function ${prefix}_ss_clearOneByDijitId(dijitId) {
			var e = dijit.byId(dijitId);
			e.reset();
			e.attr('value',null);
		}

		ss_createOnSubmitObj('${prefix}onsub', '${formName}', ${prefix}_onEventFormSubmit);		 
	</script>
</div>
