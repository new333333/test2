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
<% // radio selection %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="checked" value=""/>
<c:if test="${ssDefinitionEntry.customAttributes[radioGroupName].value == property_name}">
  <c:set var="checked" value="checked"/>
</c:if>
<input type="radio" name="<c:out value="${radioGroupName}"/>" 
  value="<c:out value="${property_name}"/>" <c:out value="${checked}"/>
/>&nbsp;<span class="ss_bold"><c:out value="${property_caption}"/></span><ssf:displayConfiguration 
  configDefinition="${ssConfigDefinition}" 
  configElement="${item}" 
  configJspStyle="${ssConfigJspStyle}" />
<br/>
