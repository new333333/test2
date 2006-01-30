<% // Calendar day view %>

<script type="text/javascript">
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
<td colspan="2"><span class="ss_toolbar_item">
<fmt:formatDate value="${ssCalStartDate}" pattern="EEEE, MMMM dd, yyyy" />
&nbsp;&nbsp;&nbsp;&nbsp;
Views:&nbsp;<a href="${set_week_view}">Week</a><c:out value="${delimiter}" /><a href="${set_month_view}">Month</a>
<c:out value="${delimiter}" />
</span></td>
</tr>

<% // the bean is a month's bean; we need to loop through the list of
   // weeks in the month, even though for this template there's only one
%>

<c:forEach var="week" items="${ssCalendarViewBean.weekList}" >

<c:forEach var="daymap" items="${week.dayList}">
<tr>
<c:choose>
<c:when test="${daymap.isToday}">
<td align="center" bgcolor="#ffffe8" width="1%" valign="top">
</c:when>
<c:otherwise>
<td align="center" width="1%" valign="top">
</c:otherwise>
</c:choose>
<span>${daymap.cal_dow}</span>
 <span class="ss_bold">${daymap.cal_dom}</td>

<c:choose>
<c:when test="${empty daymap.cal_eventdatamap}">
<td>&nbsp;</td>
</c:when>
<c:otherwise>

<td valign="top">
<c:forEach var="ev" items="${daymap.cal_eventdatamap}">

<c:forEach var="evid" items="${ev.value}"> 
<jsp:useBean id="evid" type="java.util.Map" />
<%
    java.util.HashMap e = (java.util.HashMap) evid.get("entry");
%>
<script type="text/javascript">
//getFilteredEntries()
</script>
<div id="folderLine_<c:out value="${evid.entry._docId}"/>">	
<%
if (ssSeenMap.checkIfSeen(e)) {
%><span><%
	} else {
%><span class="ss_bold"><%
	}
%>
    <c:if test="${evid.cal_starttimestring == evid.cal_endtimestring}">
    <c:out value="${evid.cal_starttimestring}"/>: 
    </c:if>
    <c:if test="${evid.cal_starttimestring != evid.cal_endtimestring}">
    <c:out value="${evid.cal_starttimestring}"/>-<c:out value="${evid.cal_endtimestring}"/>: 
    </c:if>
    <a class="ss_link" href="<ssf:url 
    adapter="true" 
    portletName="ss_forum" 
    folderId="<%= folderId %>" 
    action="view_entry" 
    entryId="<%= e.get("_docId").toString() %>" actionUrl="false" />"
    onClick="ss_loadEntry(this,'<c:out value="${evid.entry._docId}"/>');return false;" 
    ><c:if test="${empty evid.entry.title}"
    ><span class="ss_fineprint">--<ssf:nlt tag="entry.noTitle" text="no title"/>--</span
    ></c:if><c:out value="${evid.entry.title}"/></a></span></div>

</c:forEach>
</c:forEach></td>

</c:otherwise>
</c:choose>
</tr>

</c:forEach>
</c:forEach>

</table>
