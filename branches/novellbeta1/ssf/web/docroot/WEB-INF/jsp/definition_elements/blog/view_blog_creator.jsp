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
<% //blog creator view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="0">
<div class="ss_entryContent">
<c:out value="${property_caption}" />
<c:out value="${ssDefinitionEntry.creation.principal.title}"/>
</div>
</c:if>
