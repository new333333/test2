<% // Calendar month view %>


<script language="javascript">
var ss_entryList = new Array();
var ss_entryCount = 0;
function getFilteredEntries() {
		ss_entryList[ss_entryCount++] = '<c:out value="${ev.value.entry.id}"/>';
//alert("cal view entryList "+ss_entryCount)
}
</script>
<c:set var="delimiter" value=" | "/>
<table width="100%" border="0" cellpadding="2" cellspacing="0" class="ss_style ss_ruledTable">
<tr class="ss_bglightgray">
<td colspan="8"><span class="ss_toolbar_item">
   <fmt:formatDate value="${ssCalStartDate}" pattern="MMMM, yyyy" />
&nbsp;&nbsp;&nbsp;&nbsp;
Views:&nbsp;<a href="${set_day_view}">Day</a><c:out value="${delimiter}" /><a href="${set_week_view}">Week</a>
<c:out value="${delimiter}" />
</td>
</tr>
 
<% // the bean is a month's bean; we need to loop through the list of
   // weeks in the month, even though for this template there's only one
%>

<tr>
<td width="1%" class="ss_fineprintgray" align="center">week</td>
<c:forEach var="dayabbrev" items="${ssCalendarViewBean.dayHeaders}">
<td width="13%" class="ss_bold" align="center">
${dayabbrev}</td>
</c:forEach>
</tr>

<c:forEach var="week" items="${ssCalendarViewBean.weekList}" >

<tr>

<td valign="top" align="center" width="5%"><a class="ss_link_nodec" href="${week.weekURL}"><span class="ss_fineprintgray">${week.weekNum}</span></a></td>

<c:forEach var="daymap" items="${week.dayList}">

<c:choose>
<c:when test="${daymap.isToday}">
<td class="ss_fineprint" bgcolor="#ffffe8" valign="top"><span class="ss_rightbold"><a href="${daymap.dayURL}">${daymap.cal_dom}</a></span><br />&nbsp;
</c:when>
<c:when test="${!daymap.inView}">
<td class="ss_fineprint" bgcolor="#f7f7f7" valign="top"><span class="ss_rightbold"><a href="${daymap.dayURL}">${daymap.cal_dom}</a></span><br />&nbsp;
</c:when>
<c:otherwise>
<td valign="top" class="ss_fineprint"><span class="ss_rightbold"><a href="${daymap.dayURL}">${daymap.cal_dom}</a></span><br />&nbsp;
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
<div id="folderLine_${evim.entry.id}">	
${evim.cal_starttimestring}: 
<%
if (ssSeenMap.checkIfSeen(e)) {
%><span><%
	} else {
%><span class="ss_bglightpink ss_bold"><%
	}
%>
    <a class="ss_link" href="<ssf:url 
    adapter="true" 
    portletName="ss_forum" 
    folderId="<%= folderId %>" 
    action="view_entry" 
    entryId="<%= e.get("_docId").toString() %>" actionUrl="false" />"
    onClick="ss_loadEntry(this,'<c:out value="${evim.entry._docId}"/>');return false;" 
    ><c:if test="${empty evim.entry._title}"
    ><span class="ss_fineprint">--<ssf:nlt tag="entry.noTitle" text="no title"/>--</span
    ></c:if><c:out value="${evim.entry._title}"/></a></span></div>

</c:forEach> <% // end of events within a single time slot %>
</c:forEach> <% // end of time slot loop %>

</c:if> <% // end of case where there is at least one event in a cell %>
</c:if> <% // end of test to see if a day is in view %>

</td>

</c:forEach> <% // end of day loop %>
</tr>
</c:forEach> <% // end of week loop %>

</table>

