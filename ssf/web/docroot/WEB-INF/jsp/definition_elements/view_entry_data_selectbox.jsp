<% //Selectbox view %>
<div class="entryContent">
<table cellpadding="0" cellspacing="0">
<tr>
<td valign="top"><c:out value="${property_caption}" /></td>
<td>&nbsp;</td>
<td valign="top">
<c:forEach var="selection" items="${ss_forum_entry.customAttributes[property_name].valueSet}" >
<c:out value="${selection}" escapeXml="false"/><br>
</c:forEach>
</td>
</tr>
</table>
</div>
