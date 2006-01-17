<% //Event view %>
<div class="ss_entryContent">
<div class="ss_labelAbove"><c:out value="${property_caption}" /></div>
<c:choose>
<c:when test="${empty ssDefinitionEntry.customAttributes[property_name]}" >
<span class="ss_gray">--no event--</span>
</c:when>
<c:otherwise>
<c:set var="ev" value="${ssDefinitionEntry.customAttributes[property_name].value}" />
<jsp:useBean id="ev" type="com.sitescape.ef.domain.Event"  />
<ssf:eventtext event="<%= ev %>" />
</c:otherwise>
</c:choose>
</div>
<div class="ss_divider"></div>

