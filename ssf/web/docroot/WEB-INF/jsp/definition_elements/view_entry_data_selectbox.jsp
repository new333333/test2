<% //Selectbox view %>
<div class="ss_entryContent">
<span class="ss_labelLeft"><c:out value="${property_caption}" /></span>
<ul class="ss_nobullet">
<c:forEach var="selection" items="${ssFolderEntry.customAttributes[property_name].valueSet}" >
<li class="ss_content"><c:out value="${selection}" escapeXml="false"/></span></li>
</c:forEach>
</ul>
</div>
<div class="ss_divider"></div>
