<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<% // selectbox option %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	//Get the form item being displayed
	Element item = (Element) request.getAttribute("item");
%>
<c:if test="${empty ssReadOnlyFields[selectboxName]}">
<c:set var="checked" value=""/>
<c:forEach var="selection" items="${ssDefinitionEntry.customAttributes[selectboxName].valueSet}" >
  <c:if test="${selection == property_name}">
    <c:set var="checked" value="selected=\"selected\""/>
  </c:if>
</c:forEach>

<option value="<c:out value="${property_name}"/>" ${checked}
<% // property_name == 'p3' is only for backward compatibility with v1.0 %>
  	<c:if test="${(property_default || property_name == 'p3') && empty ssDefinitionEntry.customAttributes[selectboxName].valueSet}">
  		selected="selected"
  	</c:if>
  >
  <c:out value="${property_caption}"/><ssf:displayConfiguration 
  configDefinition="${ssConfigDefinition}" 
  configElement="<%= item %>" 
  configJspStyle="${ssConfigJspStyle}" /></option>
</c:if>
<c:if test="${!empty ssReadOnlyFields[selectboxName]}">
<c:forEach var="selection" items="${ssDefinitionEntry.customAttributes[selectboxName].valueSet}" >
  <c:if test="${selection == property_name}">
 		 <c:out value="${property_caption}"/>
 </c:if>
</c:forEach>
 <ssf:displayConfiguration 
  configDefinition="${ssConfigDefinition}" 
  configElement="<%= item %>" 
  configJspStyle="${ssConfigJspStyle}" />

</c:if>