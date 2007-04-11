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
dayNamesShort : [<%--
--%><c:forEach var="d" items="${ssCalendarViewBean.dayHeaders}" varStatus="status"><%--
  --%>"${d}"<c:if test="${!status.last}">,</c:if><%--
--%></c:forEach><%--
--%>],

monthNamesShort : [<%--
--%><c:forEach var="d" items="${ssCalendarViewBean.monthNamesShort}" varStatus="status"><%--
  --%>"${d}"<c:if test="${!status.last}">,</c:if><%--
--%></c:forEach><%--
--%>],

monthNames : [<%--
--%><c:forEach var="d" items="${ssCalendarViewBean.monthNames}" varStatus="status"><%--
  --%>"${d}"<c:if test="${!status.last}">,</c:if><%--
--%></c:forEach><%--
--%>],

today : {year : <fmt:formatDate value="${ssCalendarViewBean.today.date}" pattern="yyyy" timeZone="${ssUser.timeZone.ID}"/>,
			month : <fmt:formatDate value="${ssCalendarViewBean.today.date}" pattern="M" timeZone="${ssUser.timeZone.ID}"/>, 
			dayOfMonth : <fmt:formatDate value="${ssCalendarViewBean.today.date}" pattern="d" timeZone="${ssUser.timeZone.ID}"/>},
						
currentDate : {year : <fmt:formatDate value="${ssCurrentDate}" pattern="yyyy" timeZone="${ssUser.timeZone.ID}"/>,
				month : <fmt:formatDate value="${ssCurrentDate}" pattern="M" timeZone="${ssUser.timeZone.ID}"/>,
				dayOfMonth : <fmt:formatDate value="${ssCurrentDate}" pattern="d" timeZone="${ssUser.timeZone.ID}"/>},

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
		  {eventId: "${evim.eventid}", 
		  	entryId : "<%= e.get("_docId").toString() %>",
		  	<c:set var="timeZone" value="${ssUser.timeZone.ID}"/>
		  	<c:if test="${evim.cal_duration == 0 && evim.eventType == 'event'}">
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
		  	text: "${evim.cal_endtimestring} // <fmt:formatDate value="${evim.cal_starttime}" pattern="HH:mm z"/>  // <fmt:formatDate value="${evim.cal_starttime}" timeZone="${timeZone}" pattern="HH:mm z"/>  ",
		  	dur: ${evim.cal_duration},
		  	title: "${evim.entry.title}", 
		  	calsrc: "cal1",
		  	eventType: "${evim.eventType}",
			viewOnClick: "ss_loadEntry(this,'<c:out value="${evim.entry._docId}"/>');"}<c:if test="${!status.last}">,</c:if><%--
	--%></c:forEach>]
}