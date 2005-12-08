<% // Calendar week view %>


<script language="javascript">
var ss_entryList = new Array();
var ss_entryCount = 0;
function getFilteredEntries() {
		ss_entryList[ss_entryCount++] = '<c:out value="${ev.value.entry.id}"/>';
//alert("cal view entryList "+ss_entryCount)
}
</script>
<table width="100%" border="0" cellpadding="2" cellspacing="0" class="ss_ruledTable">
<tr class="ss_bglightgray">
<td colspan="2" class="ss_contentbold">Week beginning 
   <fmt:formatDate value="${ssCalStartDate}" pattern="EEEE, MMMM dd, yyyy" />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 
<a href="${set_day_view}">Day view</a>
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 
<a href="${set_month_view}">Month view</a>
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
<td align="center" width="1%" valign="top"><a class="ss_link_nodec" href="${daymap.dayURL}"><span class="ss_content">${daymap.cal_dow}</span>
 <span class="ss_contentbold">${daymap.cal_dom}</a></td>

<c:choose>
<c:when test="${empty daymap.cal_eventdatamap}">
<td class="ss_content">&nbsp;</td>
</c:when>
<c:otherwise>

<td class="ss_content" valign="top">
<c:forEach var="ev" items="${daymap.cal_eventdatamap}">

<c:forEach var="eviw" items="${ev.value}"> 
<jsp:useBean id="eviw" type="java.util.Map" />
<%
    FolderEntry e = (FolderEntry) eviw.get("entry");
%>
<script language="javascript">
//getFilteredEntries()
</script>
<div id="folderLine_<c:out value="${eviw.entry.id}"/>">	
<%
if (ssSeenMap.checkIfSeen(e)) {
%><img src="<html:imagesPath/>pics/1pix.gif" width="7px" alt="" \><%
	} else {
%><img border="0" src="<html:imagesPath/>pics/sym_s_unseen.gif" alt="unread entry" \><%
	}
%>
    ${eviw.cal_starttimestring}-${eviw.cal_endtimestring}: 
    <a class="ss_link" href="<ssf:url 
    adapter="true" 
    portletName="ss_forum" 
    folderId="<%= folderId %>" 
    action="view_entry" 
    entryId="<%= e.getId().toString() %>" actionUrl="false" />"
    onClick="ss_loadEntry(this,'<c:out value="${eviw.entry.id}"/>');return false;" >${eviw.entry.title}</a></div>

</c:forEach>
</c:forEach></td>

</c:otherwise>
</c:choose>
</tr>

</c:forEach>
</c:forEach>

</table>