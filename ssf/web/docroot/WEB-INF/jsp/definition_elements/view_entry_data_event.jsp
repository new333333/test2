<% //Event view %>
<div class="ss_entryContent">
<div class="ss_labelAbove"><c:out value="${property_caption}" /></div>
<c:choose>
<c:when test="${empty ssFolderEntry.customAttributes[property_name]}" >
<span class="contentgray">--no event--</span>
</c:when>
<c:otherwise>
<c:set var="ev" value="${ssFolderEntry.customAttributes[property_name].value}" />
<jsp:useBean id="ev" type="com.sitescape.ef.domain.Event"  />
<span class="ss_content"><ssf:eventtext event="<%= ev %>" /></span>
</c:otherwise>
</c:choose>
</div>

