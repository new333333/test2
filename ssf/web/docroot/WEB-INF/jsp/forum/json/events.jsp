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
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<% // This is JSON type AJAX response  %>
{
<c:if test="${!empty ssCalendarViewBean.eventType}">
	eventType : "<ssf:escapeJavaScript value="${ssCalendarViewBean.eventType}"/>",
</c:if>
<c:if test="${!empty ssCalendarViewBean.dayViewType}">
	dayViewType : "<ssf:escapeJavaScript value="${ssCalendarViewBean.dayViewType}"/>",
</c:if>
<c:if test="${!empty ssCalendarViewBean.dayHeaders}">
	dayNamesShort : [<%--
	--%><c:forEach var="d" items="${ssCalendarViewBean.dayHeaders}" varStatus="status"><%--
	  --%>"<ssf:escapeJavaScript value="${d}"/>"<c:if test="${!status.last}">,</c:if><%--
	--%></c:forEach><%--
	--%>],
</c:if>
<c:if test="${!empty ssCalendarViewBean.monthNamesShort}">
	monthNamesShort : [<%--
	--%><c:forEach var="d1" items="${ssCalendarViewBean.monthNamesShort}" varStatus="status"><%--
	--%><jsp:useBean id="d1" type="java.lang.String" />"<ssf:escapeJavaScript 
	  value="<%= com.sitescape.team.util.NLT.get(d1) %>"/>"<c:if test="${!status.last}">,</c:if><%--
	--%></c:forEach><%--
	--%>],
</c:if>
<c:if test="${!empty ssCalendarViewBean.monthNames}">
monthNames : [<%--
--%><c:forEach var="d2" items="${ssCalendarViewBean.monthNames}" varStatus="status"><%--
  --%><jsp:useBean id="d2" type="java.lang.String" />"<ssf:escapeJavaScript 
  value="<%= com.sitescape.team.util.NLT.get(d2) %>"/>"<c:if test="${!status.last}">,</c:if><%--
--%></c:forEach><%--
--%>],
</c:if>
<c:if test="${!empty ssCalendarViewBean.today}">
	today : {year : <fmt:formatDate value="${ssCalendarViewBean.today}" pattern="yyyy" timeZone="${ssUser.timeZone.ID}"/>,
				month : <fmt:formatDate value="${ssCalendarViewBean.today}" pattern="M" timeZone="${ssUser.timeZone.ID}"/>, 
				dayOfMonth : <fmt:formatDate value="${ssCalendarViewBean.today}" pattern="d" timeZone="${ssUser.timeZone.ID}"/>},
</c:if>
<c:if test="${!empty ssCurrentDate}">
	currentDate : {year : <fmt:formatDate value="${ssCurrentDate}" pattern="yyyy" timeZone="${ssUser.timeZone.ID}"/>,
					month : <fmt:formatDate value="${ssCurrentDate}" pattern="M" timeZone="${ssUser.timeZone.ID}"/>,
					dayOfMonth : <fmt:formatDate value="${ssCurrentDate}" pattern="d" timeZone="${ssUser.timeZone.ID}"/>},
</c:if>
<c:if test="${!empty ssCurrentGridType}">
	gridType : "${ssCurrentGridType}",
</c:if>
<c:if test="${!empty ssCurrentGridSize}">
	gridSize : "${ssCurrentGridSize}",
</c:if>
<c:if test="${!empty ssCalendarViewBean.monthInfo}">
	monthViewInfo : {year : ${ssCalendarViewBean.monthInfo.year}, 
					month : ${ssCalendarViewBean.monthInfo.month},
					numberOfDaysInView: ${ssCalendarViewBean.monthInfo.numberOfDaysInView},
					startViewDate : {year : ${ssCalendarViewBean.monthInfo.beginView.year}, 
							month : ${ssCalendarViewBean.monthInfo.beginView.month}, 
							dayOfMonth : ${ssCalendarViewBean.monthInfo.beginView.dayOfMonth}},
					endViewDate : {year : ${ssCalendarViewBean.monthInfo.endView.year}, 
							month : ${ssCalendarViewBean.monthInfo.endView.month}, 
							dayOfMonth : ${ssCalendarViewBean.monthInfo.endView.dayOfMonth}}},
</c:if>
events : [<%--
  --%><c:forEach var="evim" items="${ssCalendarViewBean.events}" varStatus="status"><%--
    --%><jsp:useBean id="evim" type="java.util.Map" /><%--
    --%>
		  {
		  eventId: "<ssf:escapeJavaScript value="${evim.eventid}"/>", 
		  	entryId : "${evim.entry['_docId']}",
		  	binderId : "${evim.entry['_binderId']}",
		  	<c:set var="timeZone" value="${ssUser.timeZone.ID}"/>
		  	<c:if test="${evim.cal_allDay && evim.eventType == 'event'}">
		  		<c:set var="timeZone" value="GMT"/>
		  	</c:if>
		  	startDate : {
			  	year : <fmt:formatDate value="${evim.cal_starttime}" timeZone="${timeZone}" pattern="yyyy"/>, 
			  	month : <fmt:formatDate value="${evim.cal_starttime}" timeZone="${timeZone}" pattern="M"/>, 
			  	dayOfMonth : <fmt:formatDate value="${evim.cal_starttime}" timeZone="${timeZone}" pattern="d"/>,
			  	hour: "<fmt:formatDate value="${evim.cal_starttime}" timeZone="${timeZone}" pattern="HH"/>",
			  	minutes: "<fmt:formatDate value="${evim.cal_starttime}" timeZone="${timeZone}" pattern="mm"/>"
		  	},
		  	endDate : {
			  	year : <fmt:formatDate value="${evim.cal_endtime}" timeZone="${timeZone}" pattern="yyyy"/>, 
			  	month : <fmt:formatDate value="${evim.cal_endtime}" timeZone="${timeZone}" pattern="M"/>, 
			  	dayOfMonth : <fmt:formatDate value="${evim.cal_endtime}" timeZone="${timeZone}" pattern="d"/>,
			  	hour: "<fmt:formatDate value="${evim.cal_endtime}" timeZone="${timeZone}" pattern="HH"/>",
			  	minutes: "<fmt:formatDate value="${evim.cal_endtime}" timeZone="${timeZone}" pattern="mm"/>"
		  	},
		  	<c:choose>
			  	<c:when test="${!evim.cal_allDay}">
				  	<c:choose>
				  		<c:when test="${!evim.cal_oneDayEvent}">
			  				text: "<fmt:formatDate value="${evim.cal_starttime}" timeZone="${timeZone}" type="both" timeStyle="short" dateStyle="short" /> - <fmt:formatDate value="${evim.cal_endtime}" timeZone="${timeZone}" type="both" timeStyle="short" dateStyle="short" />",
					  	</c:when>
					  	<c:otherwise>
					  	  	<c:choose>
						  		<c:when test="${evim.cal_starttime == evim.cal_endtime}">
					  				text: "<fmt:formatDate value="${evim.cal_starttime}" timeZone="${timeZone}" type="time" timeStyle="short" />",
							  	</c:when>
							  	<c:otherwise>
							  		text: "<fmt:formatDate value="${evim.cal_starttime}" timeZone="${timeZone}" type="time" timeStyle="short" /> - <fmt:formatDate value="${evim.cal_endtime}" timeZone="${timeZone}" type="time" timeStyle="short" />",
							  	</c:otherwise>
							</c:choose>
					  	</c:otherwise>
					</c:choose>			  		
			  	</c:when>
			  	<c:otherwise>
			  		text: "",
			  	</c:otherwise>
			</c:choose>
		  	dur: ${evim.cal_duration},
		  	allDay: ${evim.cal_allDay},
		  	title: "<ssf:escapeJavaScript value="${evim.entry.title}"/>", 
		  	calsrc: "cal1",
		  	eventType: "<ssf:escapeJavaScript value="${evim.eventType}"/>",
			viewOnClick: "ss_loadEntry(this, '${evim.entry._docId}', '${evim.entry._binderId}', '${evim.entry._entityType}', '${ss_namespace}'<c:if test="${ssDashboardRequest}">, 'yes'</c:if>);"}<c:if test="${!status.last}">,</c:if><%--
	--%></c:forEach>]
}