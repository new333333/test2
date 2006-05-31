<% // Calendar month view %>


<script type="text/javascript">
ss_entryList = new Array();
ss_entryCount = 0;
function setFilteredEntry(id) {
	ss_entryList[ss_entryCount++] = id;
}
</script>
<c:set var="delimiter" value=" | "/>
<table width="100%" border="0" cellpadding="2" cellspacing="0" class="ss_ruledTable">
<tr class="ss_toolbar_color">
<td colspan="8"><span>
   <fmt:formatDate value="${ssCalStartDate}" pattern="MMMM, yyyy" />
&nbsp;&nbsp;&nbsp;&nbsp;
<ssf:nlt tag="calendar.views" text="Views"/>:&nbsp;
<a href="${set_day_view}"><ssf:nlt tag="calendar.day" text="Day"/>
</a><c:out value="${delimiter}" />
<a href="${set_week_view}"><ssf:nlt tag="calendar.week" text="Week"/></a>
&nbsp;&nbsp;&nbsp;
</span>
<%@ include file="/WEB-INF/jsp/definition_elements/calendar_nav_bar.jsp" %>
</td>
</tr>
 
<% // the bean is a month's bean; we need to loop through the list of
   // weeks in the month, even though for this template there's only one
%>

<tr>
<td width="1%" class="ss_fineprint ss_gray" align="center">week</td>
<c:forEach var="dayabbrev" items="${ssCalendarViewBean.dayHeaders}">
<td width="13%" class="ss_bold" align="center">
${dayabbrev}</td>
</c:forEach>
</tr>

<c:forEach var="week" items="${ssCalendarViewBean.weekList}" >

<tr>

<td valign="top" align="center" width="5%">
  <a style="text-decoration: none;" href="${week.weekURL}">
    <span class="ss_fineprint ss_gray">${week.weekNum}</span>
  </a>
</td>

<c:forEach var="daymap" items="${week.dayList}">

<c:choose>
<c:when test="${daymap.isToday}">
<td class="ss_calendar_today ss_fineprint" 
  valign="top"><span class="ss_right ss_bold"><a 
  href="${daymap.dayURL}">${daymap.cal_dom}</a></span><br />&nbsp;
</c:when>
<c:when test="${!daymap.inView}">
<td class="ss_calendar_notInView ss_fineprint" 
  valign="top"><span class="ss_right ss_bold"><a 
  href="${daymap.dayURL}">${daymap.cal_dom}</a></span><br />&nbsp;
</c:when>
<c:otherwise>
<td valign="top" class="ss_fineprint"><span class="ss_right ss_bold"><a 
  href="${daymap.dayURL}">${daymap.cal_dom}</a></span><br />&nbsp;
</c:otherwise>
</c:choose>


<c:if test="${daymap.inView}"> <% // is this day part of the month, or context at front/end? %>

<c:if test="${!empty daymap.cal_eventdatamap}">

<c:forEach var="ev" items="${daymap.cal_eventdatamap}">

<c:forEach var="evim" items="${ev.value}"> 
<jsp:useBean id="evim" type="java.util.Map" />
<%
    java.util.HashMap e = (java.util.HashMap) evim.get("entry");
%>
<script type="text/javascript">
	setFilteredEntry('${evim.entry._docId}')
</script>
<div id="folderLine_${evim.entry._docId}">	
${evim.cal_starttimestring}: 
<%
if (ssSeenMap.checkIfSeen(e)) {
%><span><%
	} else {
%><span class="ss_bglightpink ss_bold"><%
	}
%>
    <a href="<ssf:url 
    adapter="true" 
    portletName="ss_forum" 
    folderId="${ssFolder.id}" 
    action="view_folder_entry" 
    entryId="<%= e.get("_docId").toString() %>" actionUrl="false" />"
    onClick="ss_loadEntry(this,'<c:out value="${evim.entry._docId}"/>');return false;" 
    ><c:if test="${empty evim.entry.title}"
    ><span class="ss_fineprint">--<ssf:nlt tag="entry.noTitle" text="no title"/>--</span
    ></c:if><c:out value="${evim.entry.title}"/></a></span></div>

</c:forEach> <% // end of events within a single time slot %>
</c:forEach> <% // end of time slot loop %>

</c:if> <% // end of case where there is at least one event in a cell %>
</c:if> <% // end of test to see if a day is in view %>

</td>

</c:forEach> <% // end of day loop %>
</tr>
</c:forEach> <% // end of week loop %>

</table>

