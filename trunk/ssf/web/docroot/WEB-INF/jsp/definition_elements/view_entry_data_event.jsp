<% //Event view %>
<div class="ss_entryContent">
<c:out value="${property_caption}" /> 
<c:choose>
<c:when test="${empty ssFolderEntry.customAttributes[property_name]}" >
--No event--
</c:when>
<c:otherwise>
<c:set var="ev" value="${ssFolderEntry.customAttributes[property_name].value}" />
<jsp:useBean id="ev" type="com.sitescape.ef.domain.Event"  />
<ssf:eventtext event="<%= ev %>" />
</c:otherwise>
</c:choose>

</div>

