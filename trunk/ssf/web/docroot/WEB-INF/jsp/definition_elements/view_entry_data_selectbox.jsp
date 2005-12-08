<% //Selectbox view %>
<div class="ss_entryContent">
<span class="ss_labelLeft"><c:out value="${property_caption}" /></span>
<c:forEach var="selection" items="${ssFolderEntry.customAttributes[property_name].valueSet}" >
<c:out value="${selection}" escapeXml="false"/><br />
</c:forEach>
</div>
