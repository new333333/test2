<% // Calendar day view %>

<script language="javascript">
var ss_entryList = new Array();
var ss_entryCount = 0;
function getFilteredEntries() {
		ss_entryList[ss_entryCount++] = '<c:out value="${ev.value.entry.id}"/>';
//alert("cal view entryList "+ss_entryCount)
}
</script>
<c:set var="delimiter" value=" | "/>
<table width="100%" border="0" cellpadding="2" cellspacing="0" class="ss_ruledTable">
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
<span class="ss_content">${daymap.cal_dow}</span>
 <span class="ss_contentbold">${daymap.cal_dom}</td>

<c:choose>
<c:when test="${empty daymap.cal_eventdatamap}">
<td class="ss_content">&nbsp;</td>
</c:when>
<c:otherwise>

<td class="ss_content" valign="top">
<c:forEach var="ev" items="${daymap.cal_eventdatamap}">

<c:forEach var="evid" items="${ev.value}"> 
<jsp:useBean id="evid" type="java.util.Map" />
<%
    Entry e = (Entry) evid.get("entry");
%>
<script language="javascript">
//getFilteredEntries()
</script>
<div id="folderLine_<c:out value="${evid.entry.id}"/>">	
<%
if (ssSeenMap.checkIfSeen(e)) {
%><img src="<html:imagesPath/>pics/1pix.gif" width="7px" alt="" \><%
	} else {
%><img border="0" src="<html:imagesPath/>pics/sym_s_unseen.gif" alt="unread entry" \><%
	}
%>
    ${evid.cal_starttimestring}-${evid.cal_endtimestring}: 
    <a class="ss_link" href="<ssf:url 
    adapter="true" 
    portletName="ss_forum" 
    folderId="<%= folderId %>" 
    action="view_entry" 
    entryId="<%= e.getId().toString() %>" actionUrl="false" />"
    onClick="ss_loadEntry(this,'<c:out value="${evid.entry.id}"/>');return false;" >${evid.entry.title}</a></div>

</c:forEach>
</c:forEach></td>

</c:otherwise>
</c:choose>
</tr>

</c:forEach>
</c:forEach>

</table>
