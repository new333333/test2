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

<td>&nbsp; &nbsp; &nbsp; No items </td>

</tr>

</c:forEach>

</table>