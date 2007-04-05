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
<% //fieldset %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	//Get the item being displayed
	Element item = (Element) request.getAttribute("item");
%>
<div class="formBreak">
<fieldset class="ss_fieldset">
<c:if test="${!empty property_legend}">
<legend class="ss_legend"><c:out value="${property_legend}"/></legend>
</c:if>
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="<%= item %>" 
  configJspStyle="${ssConfigJspStyle}" />
</fieldset>
</div>
