<% // The selectbox form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div class="ss_entryContent">
<div class="ss_labelLeft">${property_caption}:
<c:if test="${property_required == 'true'}">
<span class="ss_required">*</span>
</c:if>
</div>
<select name="${property_name}" >
<%
	java.util.TreeSet sort = new java.util.TreeSet();
	String [] zones = java.util.TimeZone.getAvailableIDs();
	for (int i=0; i<zones.length; ++i) {
		sort.add(zones[i]);
	}
%>
<c:forEach var="zone" items="<%= sort %>">
<option value="${zone}" 
<c:if test="${zone == ssDefinitionEntry.timeZoneName}"> selected </c:if>
>${zone}</option>
</c:forEach>
</select>
</div>
