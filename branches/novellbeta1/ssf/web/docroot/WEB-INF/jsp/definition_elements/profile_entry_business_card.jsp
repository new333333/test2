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
<% //Business card view %>
<%@ page import="java.lang.reflect.Method" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
		//Get the form item being displayed
		Element item = (Element) request.getAttribute("item");
%>
<div class="ss_entryContent">
<c:if test="${empty ssDefinitionEntry.title}">
<span class="ss_largestprint ss_bold"><c:out value="${ssDefinitionEntry.name}"/></span>
</c:if>
<c:if test="${!empty ssDefinitionEntry.title}">
<span class="ss_largestprint ss_bold"><c:out value="${ssDefinitionEntry.title}"/></span> 
<span class="ss_normalprint ss_light">(<c:out value="${ssDefinitionEntry.name}"/>)</span>
</c:if>
</div>

<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="<%= item %>" 
  configJspStyle="${ssConfigJspStyle}" />
