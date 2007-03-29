<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<% // This is JSON type AJAX response  %>

{
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
  --%><c:forEach var="evim" items="${ssCalendarViewBean.events}"><%--
    --%><jsp:useBean id="evim" type="java.util.Map" /><%--
    --%><% java.util.HashMap e = (java.util.HashMap) evim.get("entry"); %><%--
    --%>
		  {eventId: "${evim.eventid}", year : <fmt:formatDate value="${evim.cal_starttime}" timeZone="${ssUser.timeZone.ID}" pattern="yyyy"/>, month : <fmt:formatDate value="${evim.cal_starttime}" timeZone="${ssUser.timeZone.ID}" pattern="M"/>, dayOfMonth : <fmt:formatDate value="${evim.cal_starttime}" timeZone="${ssUser.timeZone.ID}" pattern="d"/>, 
		  	start: "<fmt:formatDate value="${evim.cal_starttime}" timeZone="${ssUser.timeZone.ID}" pattern="HH:mm"/>",  dur: ${evim.cal_duration}, title: "${evim.entry.title}", text: "${evim.cal_endtimestring} // <fmt:formatDate value="${evim.cal_starttime}" pattern="HH:mm z"/>  // <fmt:formatDate value="${evim.cal_starttime}" timeZone="${ssUser.timeZone.ID}" pattern="HH:mm z"/>  ", calsrc: "cal1",
			entryId: "<%= e.get("_docId").toString() %>", eventType: "${evim.eventType}",
			viewOnClick: "ss_loadEntry(this,'<c:out value="${evim.entry._docId}"/>');"},<%--
	--%></c:forEach>]
}
