<% // Calendar week view %>

<table width="100%" border="0" cellpadding="2" cellspacing="0" class="ss_ruledTable">
<tr class="ss_bglightgray">
<td colspan="2" class="ss_contentbold">Week beginning 
   <fmt:formatDate value="${ssCalStartDate}" pattern="EEEE, MMMM dd, yyyy" /></td>
</tr>

<c:forEach var="daymap" items="${ssCalendarViewBean}">

<tr>
<td align="center" width="1%" valign="top"><span class="ss_content">${daymap.cal_dow}</span>
 <span class="ss_contentbold">${daymap.cal_dom}</td>

<c:choose>
<c:when test="${empty daymap.cal_eventdatamap}">
<td class="ss_content">&nbsp;</td>
</c:when>
<c:otherwise>
<td class="ss_content" valign="top">
<c:forEach var="ev" items="${daymap.cal_eventdatamap}">
<jsp:useBean id="ev" type="java.util.Map.Entry" />
<%
    Map m = (Map) ev.getValue();
    FolderEntry e = (FolderEntry) m.get("entry");
%>
<div id="folderLine_<c:out value="${ev.value.entry.id}"/>">	
<%
if (ssSeenMap.checkIfSeen(e)) {
%><img src="<html:imagesPath/>pics/1pix.gif" width="7px" alt="" \><%
	} else {
%><img border="0" src="<html:imagesPath/>pics/sym_s_unseen.gif" alt="unread entry" \><%
	}
%>
    ${ev.value.cal_starttimestring}-${ev.value.cal_endtimestring}: 
    <a class="ss_link" href="<ssf:url 
    adapter="true" 
    portletName="ss_forum" 
    folderId="<%= folderId %>" 
    action="view_entry" 
    entryId="<%= e.getId().toString() %>" actionUrl="false" />"
    onClick="ss_loadEntry(this,'<c:out value="${ev.value.entry.id}"/>');return false;" >${ev.value.entry.title}</a></div>

</c:forEach></td>

</c:otherwise>
</c:choose>
</tr>

</c:forEach>

</table>