<% //User_list view %>
<div class="ss_entryContent">

<span class="ss_labelLeft"><c:out value="${property_caption}" /></span>
<ul class="ss_nobullet">
<c:forEach var="selection" items="<%= com.sitescape.ef.util.ResolveIds.getPrincipals(ssDefinitionEntry.getCustomAttribute(property_name)) %>" >
<li><c:out value="${selection.title}" escapeXml="false"/></span></li>
</c:forEach>
</ul>
</div>
<div class="ss_divider"></div>