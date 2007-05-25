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
<% //Title view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div class="ss_entryContent">
<h1 class="ss_entryTitle">

<c:if test="${empty ssDefinitionEntry.title}">
    <span class="ss_light">--no title--</span>
</c:if>
<c:if test="${!empty ssDefinitionEntry.title}">
<c:out value="${ssDefinitionEntry.title}"/>
</c:if>
</h1>
</div>
