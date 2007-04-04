<%--
//
// Generates Javascript objects needed by the calendar view
//--%>

ss_cal_CalData.setMap([
  {calsrc: "cal1", box: "#8888CC", border: "#6666AA"},
  {calsrc: "cal2", box: "#88CC88", border: "#66AA66"},
  {calsrc: "cal3", box: "#CC88CC", border: "#AA66AA"},
  {calsrc: "cal4", box: "#88CCCC", border: "#66AAAA"},
  {calsrc: "cal5", box: "#CCCC88", border: "#AAAA66"}]);

Date.dayNamesShort = [<%--
--%><c:forEach var="d" items="${ssCalendarViewBean.dayHeaders}" ><%--
  --%>"${d}",<%--
--%></c:forEach><%--
--%>];

Date.monthNamesShort = [<%--
--%><c:forEach var="d" items="${ssCalendarViewBean.monthNamesShort}" ><%--
  --%>"${d}",<%--
--%></c:forEach><%--
--%>];

Date.monthNames = [<%--
--%><c:forEach var="d" items="${ssCalendarViewBean.monthNames}" ><%--
  --%>"${d}",<%--
--%></c:forEach><%--
--%>];

ss_cal_CalData.setMonthViewInfo(${ssCalendarViewBean.monthInfo.year}, ${ssCalendarViewBean.monthInfo.month}, ${ssCalendarViewBean.monthInfo.numberOfDaysInView},
								{year : ${ssCalendarViewBean.monthInfo.beginView.year}, 
									month : ${ssCalendarViewBean.monthInfo.beginView.month}, 
									dayOfMonth : ${ssCalendarViewBean.monthInfo.beginView.dayOfMonth}},
								{year : ${ssCalendarViewBean.monthInfo.endView.year}, 
									month : ${ssCalendarViewBean.monthInfo.endView.month}, 
									dayOfMonth : ${ssCalendarViewBean.monthInfo.endView.dayOfMonth}});

ss_cal_CalData.setToday({year : <fmt:formatDate value="${ssCalendarViewBean.today.date}" pattern="yyyy" timeZone="${ssUser.timeZone.ID}"/>,
						month : (<fmt:formatDate value="${ssCalendarViewBean.today.date}" pattern="M" timeZone="${ssUser.timeZone.ID}"/> - 1), 
						dayOfMonth : <fmt:formatDate value="${ssCalendarViewBean.today.date}" pattern="d" timeZone="${ssUser.timeZone.ID}"/>});
						
ss_cal_Grid.setCurrentDate(new Date(<fmt:formatDate value="${ssCurrentDate}" pattern="yyyy" timeZone="${ssUser.timeZone.ID}"/>, 
							(<fmt:formatDate value="${ssCurrentDate}" pattern="M" timeZone="${ssUser.timeZone.ID}"/> - 1), 
							<fmt:formatDate value="${ssCurrentDate}" pattern="d" timeZone="${ssUser.timeZone.ID}"/>));

ss_cal_Events.set([<%--
  --%><c:forEach var="evim" items="${ssCalendarViewBean.events}"><%--
    --%><jsp:useBean id="evim" type="java.util.Map" /><%--
    --%><% java.util.HashMap e = (java.util.HashMap) evim.get("entry"); %><%--
    --%>
		  {eventId: "${evim.eventid}", 
		  	entryId : "<%= e.get("_docId").toString() %>",
		  	<c:set var="timeZone" value="${ssUser.timeZone.ID}"/>
		  	<c:if test="${evim.cal_duration == 0}">
		  		<c:set var="timeZone" value="GMT"/>
		  	</c:if>
		  	year : <fmt:formatDate value="${evim.cal_starttime}" timeZone="${timeZone}" pattern="yyyy"/>, 
		  	month : <fmt:formatDate value="${evim.cal_starttime}" timeZone="${timeZone}" pattern="M"/>, 
		  	dayOfMonth : <fmt:formatDate value="${evim.cal_starttime}" timeZone="${timeZone}" pattern="d"/>, 		  	
		  	start: "<fmt:formatDate value="${evim.cal_starttime}" timeZone="${timeZone}" pattern="HH:mm"/>",
		  	text: "${evim.cal_endtimestring} // <fmt:formatDate value="${evim.cal_starttime}" pattern="HH:mm z"/>  // <fmt:formatDate value="${evim.cal_starttime}" timeZone="${timeZone}" pattern="HH:mm z"/>  ",
		  	dur: ${evim.cal_duration},
		  	title: "${evim.entry.title}", 
		  	calsrc: "cal1",
		  	eventType: "${evim.eventType}",
			viewOnClick: "ss_loadEntry(this,'<c:out value="${evim.entry._docId}"/>');"},<%--
	--%></c:forEach>]);
