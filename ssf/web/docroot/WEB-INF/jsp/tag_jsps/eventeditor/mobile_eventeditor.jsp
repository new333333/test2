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
<c:choose>
	<c:when test="${initEvent.allDayEvent || !initEvent.timeZoneSensitive}">
		<c:set var="timeZoneID" value="GMT" />
	</c:when>
	<c:otherwise>
		<c:set var="timeZoneID" value="${ssUser.timeZone.ID}" />
	</c:otherwise>
</c:choose>

<div class="ss_mobile_form_element">
	<table>
		<tr>
			<td><ssf:nlt tag="event.start" />:</td>
			<td>
                
				<input type="text"
					name="${dateId}_fullDate" 
					id="event_start_${prefix}"
					<c:if test="${!empty startDate}">
						value="<fmt:formatDate value="${startDate}" 
							pattern="yyyy-MM-dd" timeZone="${timeZoneID}"/>"
					</c:if>
					startDateWidgetId="event_start_${prefix}"
					startTimeWidgetId="event_start_time_${prefix}"
					endDateWidgetId="event_end_${prefix}"
					endTimeWidgetId="event_end_time_${prefix}" />
				<div><span class="ss_mobile_small"><ssf:nlt tag="mobile.dateFormat"/></span></div>
			</td>
			<td>
				<span id="${prefix}eventStartTime"
					<c:if test="${initEvent.allDayEvent}">
						style="display: none; "
					</c:if>
					>
					<input type="text" 
						id="event_start_time_${prefix}" 
						name="${dateId}_0_fullTime" 
						<c:choose>
							<c:when test="${initEvent.allDayEvent}">
								value="T08:00:00"
							</c:when>
							<c:otherwise>
								<c:if test="${!empty startDate}">
									value="T<fmt:formatDate value="${startDate}" 
										pattern="HH:mm:ss" timeZone="${timeZoneID}"/>"
								</c:if>
							</c:otherwise>
						</c:choose>
						startDateWidgetId="event_start_${prefix}"
						startTimeWidgetId="event_start_time_${prefix}"
						endDateWidgetId="event_end_${prefix}"
						endTimeWidgetId="event_end_time_${prefix}" />
					<div><span class="ss_mobile_small"><ssf:nlt tag="mobile.timeFormat"/></span></div>
						
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
					<input type="checkbox" 
					    name="${allDayEventId}"
					    <c:if test="${initEvent.allDayEvent}"> checked="checked" </c:if> 
					    id="${prefix}_allDayEvent" 
					/><label for="${prefix}_allDayEvent"><ssf:nlt tag="event.allDay" /></label>
				</td>
			</c:if>
		</tr>
	
	<c:if test="${attMap.hasDur}">
			<tr>
				<td><ssf:nlt tag="event.end" />:</td>
				<td>
					<input type="text" 
						id="event_end_${prefix}" 
						name="${dateId2}_fullDate" 
						<c:if test="${!empty endDate}">			
							value="<fmt:formatDate value="${endDate}" 
								pattern="yyyy-MM-dd" timeZone="${timeZoneID}"/>"
						</c:if>
						startDateWidgetId="event_start_${prefix}"
						startTimeWidgetId="event_start_time_${prefix}"
						endDateWidgetId="event_end_${prefix}"
						endTimeWidgetId="event_end_time_${prefix}" />
					<div><span class="ss_mobile_small"><ssf:nlt tag="mobile.dateFormat"/></span></div>
				</td>
				<td>
					<span id="${prefix}eventEndTime"
						<c:if test="${initEvent.allDayEvent}">
							style="display: none; "
						</c:if>			
						>
						<input type="text" 
							id="event_end_time_${prefix}" 
							name="${dateId2}_0_fullTime" 
							<c:choose>
								<c:when test="${initEvent.allDayEvent}">
									value="T08:30:00"
								</c:when>
								<c:otherwise>
									<c:if test="${!empty endDate}">	
										value="T<fmt:formatDate value="${endDate}" 
											pattern="HH:mm:ss" timeZone="${timeZoneID}"/>"
									</c:if>
								</c:otherwise>
							</c:choose>						
							startDateWidgetId="event_start_${prefix}"
							startTimeWidgetId="event_start_time_${prefix}"
							endDateWidgetId="event_end_${prefix}"
							endTimeWidgetId="event_end_time_${prefix}" />
							
						<div><span class="ss_mobile_small"><ssf:nlt tag="mobile.timeFormat"/></span></div>
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
					</c:if> id="timeZoneSensitive_${evid}" value="true" 
				/><label for="timeZoneSensitive_${evid}"><ssf:nlt tag="event.timeZoneSensitive" /></label>
			</td>
		</tr>
	</c:if>	
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