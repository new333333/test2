<% // Calendar week view %>

<table border="1">
<tr>
<td colspan="2">
<b>Week beginning <fmt:formatDate value="${ssCalStartDate}" pattern="EEEE, MMMM dd, yyyy" /> </b>
</td>
</tr>

<c:forEach var="daymap" items="${ssCalendarViewBean}">

<tr>
<td align="center" width="1%">
${daymap.cal_dow}<br>${daymap.cal_dom}
</td>

<c:choose>
<c:when test="${empty daymap.cal_eventdatamap}">
<td>&nbsp; &nbsp; No items </td>
</c:when>
<c:otherwise>
<td>
<c:forEach var="ev" items="${daymap.cal_eventdatamap}">
<jsp:useBean id="ev" type="java.util.Map.Entry" />

<%
    Map m = (Map) ev.getValue();
    FolderEntry e = (FolderEntry) m.get("entry");
%>

&nbsp; &nbsp; ${ev.value.cal_starttimestring}-${ev.value.cal_endtimestring}: 

    <a href="<ssf:url 
    adapter="true" 
    portletName="ss_forum" 
    folderId="<%= folderId %>" 
    action="view_entry" 

    entryId="<%= ((FolderEntry) ((Map) ((Map.Entry)pageContext.getAttribute("ev")).getValue()).get("entry")).getId().toString() %>" actionUrl="false" />
    onClick="ss_loadEntry(this,'<c:out value="${ev.value.entry.id}"/>');return false;" >

${ev.value.entry.title}

    </a>

</c:forEach>
</td>


</c:otherwise>
</c:choose>

</tr>

</c:forEach>

</table>