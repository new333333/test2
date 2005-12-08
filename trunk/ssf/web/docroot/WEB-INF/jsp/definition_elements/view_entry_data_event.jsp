<% //Event view %>
<div class="entryContent">
<c:out value="${property_caption}" /> 
<c:choose>
<c:when test="${empty folderEntry.customAttributes[property_name]}" >
--No event--
</c:when>
<c:otherwise>
<c:set var="ev" value="${folderEntry.customAttributes[property_name].value}" />
<jsp:useBean id="ev" type="com.sitescape.ef.domain.Event"  />
<ssf:eventtext event="<%= ev %>" />
</c:otherwise>
</c:choose>

</div>

