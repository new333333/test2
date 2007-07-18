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
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<% // This is JSON type AJAX response  %>
{

eventType : "${ssCalendarViewBean.eventType}",

dayNamesShort : [<%--
--%><c:forEach var="d" items="${ssCalendarViewBean.dayHeaders}" varStatus="status"><%--
  --%>"<ssf:escapeJavaScript value="${d}"/>"<c:if test="${!status.last}">,</c:if><%--
--%></c:forEach><%--
--%>],

monthNamesShort : [<%--
--%><c:forEach var="d1" items="${ssCalendarViewBean.monthNamesShort}" varStatus="status"><%--
--%><jsp:useBean id="d1" type="java.lang.String" />"<ssf:escapeJavaScript 
  value="<%= com.sitescape.team.util.NLT.get(d1) %>"/>"<c:if test="${!status.last}">,</c:if><%--
--%></c:forEach><%--
--%>],

monthNames : [<%--
--%><c:forEach var="d2" items="${ssCalendarViewBean.monthNames}" varStatus="status"><%--
  --%><jsp:useBean id="d2" type="java.lang.String" />"<ssf:escapeJavaScript 
  value="<%= com.sitescape.team.util.NLT.get(d2) %>"/>"<c:if test="${!status.last}">,</c:if><%--
--%></c:forEach><%--
--%>],

today : {year : <fmt:formatDate value="${ssCalendarViewBean.today}" pattern="yyyy" timeZone="${ssUser.timeZone.ID}"/>,
			month : <fmt:formatDate value="${ssCalendarViewBean.today}" pattern="M" timeZone="${ssUser.timeZone.ID}"/>, 
			dayOfMonth : <fmt:formatDate value="${ssCalendarViewBean.today}" pattern="d" timeZone="${ssUser.timeZone.ID}"/>},
						
currentDate : {year : <fmt:formatDate value="${ssCurrentDate}" pattern="yyyy" timeZone="${ssUser.timeZone.ID}"/>,
				month : <fmt:formatDate value="${ssCurrentDate}" pattern="M" timeZone="${ssUser.timeZone.ID}"/>,
				dayOfMonth : <fmt:formatDate value="${ssCurrentDate}" pattern="d" timeZone="${ssUser.timeZone.ID}"/>},

gridType : "${ssCurrentGridType}",

gridSize : "${ssCurrentGridSize}",

monthViewInfo : {year : ${ssCalendarViewBean.monthInfo.year}, 
				month : ${ssCalendarViewBean.monthInfo.month},
				numberOfDaysInView: ${ssCalendarViewBean.monthInfo.numberOfDaysInView},
				startViewDate : {year : ${ssCalendarViewBean.monthInfo.beginView.year}, 
						month : ${ssCalendarViewBean.monthInfo.beginView.month}, 
						dayOfMonth : ${ssCalendarViewBean.monthInfo.beginView.dayOfMonth}},
				endViewDate : {year : ${ssCalendarViewBean.monthInfo.endView.year}, 
						month : ${ssCalendarViewBean.monthInfo.endView.month}, 
						dayOfMonth : ${ssCalendarViewBean.monthInfo.endView.dayOfMonth}}},

events : [<%--
  --%><c:forEach var="evim" items="${ssCalendarViewBean.events}" varStatus="status"><%--
    --%><jsp:useBean id="evim" type="java.util.Map" /><%--
    --%><% java.util.HashMap e = (java.util.HashMap) evim.get("entry"); %><%--
    --%>
		  {eventId: "<ssf:escapeJavaScript value="${evim.eventid}"/>", 
		  	entryId : "<%= e.get("_docId").toString() %>",
		  	binderId : "<%= e.get("_binderId").toString() %>",
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
			viewOnClick: "ss_loadEntry(this,'${evim.entry._docId}', '${evim.entry._binderId}', '${evim.entry._entityType}'<c:if test="${!empty ssDashboardRequest}">, true</c:if>);"}<c:if test="${!status.last}">,</c:if><%--
	--%></c:forEach>]
}