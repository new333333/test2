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

ss_cal_CalData.dayNamesShort = [<%--
--%><c:forEach var="d" items="${ssCalendarViewBean.dayHeaders}" ><%--
  --%>"${d}",<%--
--%></c:forEach><%--
--%>];

var inputEvents = [<%--
--%><c:set var="i" value="-1"/><%--
--%><c:forEach var="week" items="${ssCalendarViewBean.weekList}" ><%--
  --%><c:forEach var="daymap" items="${week.dayList}"><c:set var="i" value="${i + 1}"/><%--
    --%><c:if test="${daymap.inView}"><% // is this day part of the month, or context at front/end? %><%--
      --%><c:if test="${!empty daymap.cal_eventdatamap}"><%--
        --%><c:forEach var="ev" items="${daymap.cal_eventdatamap}"><%--
          --%><c:forEach var="evim" items="${ev.value}"><%--
            --%><jsp:useBean id="evim" type="java.util.Map" /><%--
            --%><% java.util.HashMap e = (java.util.HashMap) evim.get("entry"); %><%--
            --%>
  {eventId: "${evim.eventid}", day: ${i}, start: "<fmt:formatDate value="${evim.cal_starttime}" timeZone="${ssUser.timeZone.ID}" pattern="HH:mm"/>",  dur: ${evim.cal_duration}, title: "${evim.entry.title}", text: "${evim.cal_endtimestring} // <fmt:formatDate value="${evim.cal_starttime}" pattern="HH:mm z"/>  // <fmt:formatDate value="${evim.cal_starttime}" timeZone="${ssUser.timeZone.ID}" pattern="HH:mm z"/>  ", calsrc: "cal1",
   viewHref: "<ssf:url adapter="<%= useAdaptor %>" portletName="ss_forum" folderId="${ssFolder.id}" action="view_folder_entry" entryId="<%= e.get("_docId").toString() %>" actionUrl="true" />",
   viewOnClick: "ss_loadEntry(this,'<c:out value="${evim.entry._docId}"/>');return false;"},<%--
          --%></c:forEach><% // end of events within a single time slot %><%--
        --%></c:forEach><% // end of time slot loop %><%--
      --%></c:if><% // end of case where there is at least one event in a cell %><%--
    --%></c:if><% // end of test to see if a day is in view %><%--
  --%></c:forEach><%--
--%></c:forEach>];

ss_cal_Events.clear();
ss_cal_Events.set(inputEvents);
ss_cal_CalData.dayHeaders = [<%--
--%><c:forEach var="week" items="${ssCalendarViewBean.weekList}" ><%--
  --%><c:forEach var="daymap" items="${week.dayList}"><%--
    --%>"<fmt:formatDate value="${daymap.cal_dmgCalDate}" pattern="d-MMM" />",<%--
  --%></c:forEach><%--
--%></c:forEach>];
ss_cal_CalData.monthTickList = [<%--

--%><c:set var="today" value="-1"/><%--
--%><c:set var="i" value="-1"/><%--
--%><c:forEach var="week" items="${ssCalendarViewBean.weekList}" ><%--
  --%><c:forEach var="daymap" items="${week.dayList}"><%--
    --%><c:out value="${daymap.cal_dom},"/><%--
    --%><c:set var="i" value="${i + 1}"/><%--
    --%><c:if test="${daymap.isToday}"><%--
      --%><c:set var="today" value="${i}"/><%--
    --%></c:if><%--
  --%></c:forEach><%--
--%></c:forEach>];
ss_cal_CalData.todayIndex = ${today};
