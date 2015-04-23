<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
<% //Business card view %>
<%@ page import="java.lang.reflect.Method" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
		//Get the form item being displayed
		Element item = (Element) request.getAttribute("item");
%>

<c:set var="ss_element_display_style" value="tableAlignLeft" scope="request"/>
<c:set var="ss_element_display_style_caption" value="ss_light" scope="request"/>
<c:set var="ss_element_display_style_item" value="ss_bold" scope="request"/>

<fieldset><legend><ssf:nlt tag="${property_caption}" checkIfTag="true"/></legend>
<table class="ss_transparent" style="border-spacing: 10px 2px;">

<c:forEach var="element" items="${propertyValues__elements}">
 <c:if test="${element != 'name' && element != 'title'}">
 <tr>
  <td valign="top" align="left">
   <span class="${ss_element_display_style_caption}"><ssf:nlt tag="profile.element.${element}"/></span>
  </td>
  <td valign="top" align="left">
   <c:if test="${!empty ssDefinitionEntry[element]}">
    <span class="${ss_element_display_style_item}">
	    <c:if test="${element == 'emailAddress' || element == 'mobileEmailAddress' || element == 'txtEmailAddress'}">
	        <ssf:mailto email="${ssDefinitionEntry[element]}"/>
	    </c:if>    
	    <c:if test="${element != 'emailAddress' && element != 'mobileEmailAddress' && element != 'txtEmailAddress'}">
	    	<c:out value="${ssDefinitionEntry[element]}" escapeXml="true"/>
		</c:if>
    </span>
   </c:if>
   <c:if test="${!empty ssDefinitionEntry.customAttributes[element]}">
    <span class="${ss_element_display_style_item}"><c:out value="${ssDefinitionEntry.customAttributes[element]}" escapeXml="true"/></span>
   </c:if>
  </td>
 </tr>
 </c:if>
</c:forEach>
</table>

  <ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
       configElement="<%= item %>" 
       configJspStyle="${ssConfigJspStyle}" 
       entry="${ssDefinitionEntry}" />
 
</fieldset>
 