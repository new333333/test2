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
<% //Workspace title view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<p><span class="ss_largestprint ss_bold">
 <a style="text-decoration: none;" href="<ssf:url 
    folderId="${ssDefinitionEntry.id}" 
    action="view_workspace"/>">
<c:if test="${empty ssDefinitionEntry.title}">
    <span class="ss_light">--no title--</span>
    </c:if><c:out value="${ssDefinitionEntry.title}"/></a>
</span></p>
