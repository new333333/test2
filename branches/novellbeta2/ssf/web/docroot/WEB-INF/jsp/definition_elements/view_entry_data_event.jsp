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
<% //Event view %>
<div class="ss_entryContent">
<div class="ss_labelAbove"><c:out value="${property_caption}" /></div>
<c:choose>
<c:when test="${empty ssDefinitionEntry.customAttributes[property_name]}" >
<span class="ss_gray">--no event--</span>
</c:when>
<c:otherwise>
<c:set var="ev" value="${ssDefinitionEntry.customAttributes[property_name].value}" />
<jsp:useBean id="ev" type="com.sitescape.team.domain.Event"  />
<ssf:eventtext event="<%= ev %>" />
</c:otherwise>
</c:choose>
</div>

