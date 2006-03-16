<% // Calendar week view %>


<script type="text/javascript">
var ss_entryList = new Array();
var ss_entryCount = 0;
function setFilteredEntry(id) {
		ss_entryList[ss_entryCount++] = id;
}
</script>
<c:set var="delimiter" value=" | "/>

<table width="100%" border="0" cellpadding="2" cellspacing="0" class="ss_ruledTable">
<tr class="ss_toolbar_color">
<td colspan="2">
<span>
<ssf:nlt tag="calendar.begining" text="Week beginning"/>
   <fmt:formatDate value="${ssCalStartDate}" pattern="EEEE, MMMM dd, yyyy" />
&nbsp;&nbsp;&nbsp;&nbsp;
<ssf:nlt tag="calendar.views" text="Views"/>:&nbsp;
<a href="${set_day_view}"><ssf:nlt tag="calendar.day" text="Day"/></a>
<c:out value="${delimiter}" />
<a href="${set_month_view}"><ssf:nlt tag="calendar.month" text="Month"/></a>
&nbsp;&nbsp;&nbsp;
</span>
<%@ include file="/WEB-INF/jsp/definition_elements/calendar_nav_bar.jsp" %>
</td>
</tr>

<% // the bean is a month's bean; we need to loop through the list of
   // weeks in the month, even though for this template there's only one
%>

<c:forEach var="week" items="${ssCalendarViewBean.weekList}" >

<c:forEach var="daymap" items="${week.dayList}">

<c:choose>
<c:when test="${daymap.isToday}">
<tr class="ss_highlightManila">
</c:when>
<c:otherwise>
<tr>
</c:otherwise>
</c:choose>
<td align="center" width="1%" valign="top"><a style="text-decoration: none;" href="${daymap.dayURL}"><span>${daymap.cal_dow}</span>
<br /><span class="ss_bold">${daymap.cal_dom}</a></td>

<c:choose>
<c:when test="${empty daymap.cal_eventdatamap}">
<td>&nbsp;</td>
</c:when>
<c:otherwise>

<td valign="top">
<c:forEach var="ev" items="${daymap.cal_eventdatamap}">

<c:forEach var="eviw" items="${ev.value}"> 
<jsp:useBean id="eviw" type="java.util.Map" />
<%
    java.util.HashMap e = (java.util.HashMap) eviw.get("entry");
%>
<script type="text/javascript">
	setFilteredEntry('${eviw.entry._docId}')
</script>
<div id="folderLine_${eviw.entry._docId}">	
<%
if (ssSeenMap.checkIfSeen(e)) {
%><span><%
	} else {
%><span class="ss_bold"><%
	}
%>
    <c:if test="${eviw.cal_starttimestring == eviw.cal_endtimestring}">
    <c:out value="${eviw.cal_starttimestring}"/>: 
    </c:if>
    <c:if test="${eviw.cal_starttimestring != eviw.cal_endtimestring}">
    <c:out value="${eviw.cal_starttimestring}"/>-<c:out value="${eviw.cal_endtimestring}"/>: 
    </c:if>
    <a href="<ssf:url 
    adapter="true" 
    portletName="ss_forum" 
    folderId="${ssFolder.id}" 
    action="view_entry" 
    entryId="<%= e.get("_docId").toString() %>" actionUrl="false" />"
    onClick="ss_loadEntry(this,'<c:out value="${eviw.entry._docId}"/>');return false;" 
    ><c:if test="${empty eviw.entry.title}"
    ><span class="ss_fineprint">--<ssf:nlt tag="entry.noTitle" text="no title"/>--</span
    ></c:if><c:out value="${eviw.entry.title}"/></a></span></div>

</c:forEach>
</c:forEach></td>

</c:otherwise>
</c:choose>
</tr>

</c:forEach>
</c:forEach>

</table>

