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
<% //Description view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${!empty ssDefinitionEntry.description}">
<div class="ss_entryContent">
 <span><ssf:markup type="view" entity="${ssDefinitionEntry}"><c:out 
   value="${ssDefinitionEntry.description.text}" escapeXml="false"/></ssf:markup></span>
</div>
</c:if>
