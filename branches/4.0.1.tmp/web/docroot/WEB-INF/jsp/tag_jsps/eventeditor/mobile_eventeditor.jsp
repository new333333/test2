<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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

<div class="ss_mobile_form_element">
	<table cellpadding="0" cellspacing="0">
		<tr class="ss_eventdateinput">
			<td><ssf:nlt tag="event.start" />:</td>
			<td colspan="2">
                <c:set var="ss_dateWidgetId" value="${dateId}" scope="request"/>
                <c:set var="ss_dateWidgetDate" value="${startDate}" scope="request"/>
                <%@ include file="/WEB-INF/jsp/mobile/date_widget.jsp" %>
			</td>
		</tr>
		<tr>	
			<td>&nbsp;</td>
			<td style="padding-bottom: 5px;">
				<div id="${prefix}eventStartTime"
					<c:if test="${initEvent.allDayEvent}">
						style="display: none; "
					</c:if>
					>
				    <div><span class="ss_mobile_small"><ssf:nlt tag="event.time"/><span style="padding-left: 10px;"><ssf:nlt tag="mobile.timeFormat"/></span></span></div>
					<input type="text" 
						id="event_start_time_${prefix}" 
						name="${dateId}_0_fullTime" 
						<c:choose>
							<c:when test="${initEvent.allDayEvent}">
								value="08:00"
							</c:when>
							<c:otherwise>
								<c:if test="${!empty startDate}">
									value="<fmt:formatDate value="${startDate}" 
										pattern="HH:mm" timeZone="${timeZoneID}"/>"
								</c:if>
							</c:otherwise>
						</c:choose>
						startDateWidgetId="event_start_${prefix}"
						startTimeWidgetId="event_start_time_${prefix}"
						endDateWidgetId="event_end_${prefix}"
						endTimeWidgetId="event_end_time_${prefix}" />
						
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
					
				</div>	
			</td>
			<c:if test="${attMap.hasDur}">
				<td valign="top">
					<input type="checkbox" 
					    name="${allDayEventId}"
					    <c:if test="${initEvent.allDayEvent}"> checked="checked" </c:if> 
					    id="${prefix}_allDayEvent" 
					/><label for="${prefix}_allDayEvent"><ssf:nlt tag="event.allDay" /></label>
				</td>
			</c:if>
		</tr>
	
	<c:if test="${attMap.hasDur}">
			<tr class="ss_eventdateinput">
				<td><ssf:nlt tag="event.end" />:</td>
				<td colspan="2">
                  <c:set var="ss_dateWidgetId" value="${dateId2}" scope="request"/>
                  <c:set var="ss_dateWidgetDate" value="${endDate}" scope="request"/>
                  <%@ include file="/WEB-INF/jsp/mobile/date_widget.jsp" %>
				</td>
			</tr>
			<tr class="ss_eventdateinput2">
				<td>&nbsp;</td>	
				<td colspan="2">
					<div id="${prefix}eventEndTime"
						<c:if test="${initEvent.allDayEvent}">
							style="display: none; "
						</c:if>			
						>
				    	<div><span class="ss_mobile_small"><ssf:nlt tag="event.time"/><span style="padding-left: 10px;"><ssf:nlt tag="mobile.timeFormat"/></span></span></div>
						<input type="text" 
							id="event_end_time_${prefix}" 
							name="${dateId2}_0_fullTime" 
							<c:choose>
								<c:when test="${initEvent.allDayEvent}">
									value="08:30"
								</c:when>
								<c:otherwise>
									<c:if test="${!empty endDate}">	
										value="<fmt:formatDate value="${endDate}" 
											pattern="HH:mm" timeZone="${timeZoneID}"/>"
									</c:if>
								</c:otherwise>
							</c:choose>						
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
					</div>
				</td>
			</tr>
	</c:if>
	<input type="hidden" name="timeZoneSensitive_${evid}" id="timeZoneSensitive_${evid}" checked="checked" value="true" />
	<c:if test="${attMap.isFreeBusyActive}">
		<tr>
			<td colspan="4">
				<label for="${prefix}freeBusy"><ssf:nlt tag="event.freeBusy.legend" /></label> 
				<select name="${prefix}_freeBusy" id="${prefix}freeBusy">
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
	

<c:set var="interval" value="1" />
<c:set var="frequency" value="none" />
	
	
	<input type="hidden" name="${prefix}_event_uid" value="${initEvent.uid}" />
	<c:if test="${!empty initEvent.timeZone}">
		<input type="hidden" name="${timeZoneFieldName}" value="${initEvent.timeZone.ID}" />
	</c:if>
	
</div>