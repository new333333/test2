<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
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
<c:if test="${zone == ssDefinitionEntry.timeZone.ID}"> selected </c:if>
>${zone}</option>
</c:forEach>
</select>
</div>
