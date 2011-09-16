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
<%@ page import="org.kablink.teaming.web.util.DefinitionHelper" %>
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="property_name" type="String" scope="request" />
<jsp:useBean id="property_caption" type="String" scope="request" />
<jsp:useBean id="ssConfigDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ssDefinitionEntry" type="org.kablink.teaming.domain.DefinableEntity" scope="request" />
<%
//Get the item being displayed
Element item = (Element) request.getAttribute("item");
%>

<c:set var="captionValue" value="${ssDefinitionEntry.customAttributes[property_name].value}"/>
<c:set var="caption" value=""/>
<% String caption = ""; %>
<c:if test="${!empty captionValue}">
<%
	caption = DefinitionHelper.findCaptionForValue(ssConfigDefinition, item,
			(String) pageContext.getAttribute("captionValue"));
	caption = NLT.getDef(caption);
%>
</c:if>
<c:set var="caption" value="<%= caption %>"/>

<% //Radio view %>
<c:if test="${empty ss_element_display_style}">
<div class="ss_entryContent">
<span class="ss_labelRight"><c:out value="${property_caption}" /></span>
<c:out value="${caption}" escapeXml="false"/>
</div>
  <c:if test="${!empty ss_userVersionPrincipals}">
    <div class="ss_entryContent">
    <div class="ss_perUserViewElement">
    <ssf:expandableArea title='<%= NLT.get("element.perUser.viewPersonalVersions") %>' titleClass="ss_fineprint">
    <table cellspacing="0" cellpadding="0">
    <c:forEach var="perUserUser" items="${ss_userVersionPrincipals}">
      <c:set var="perUserPropertyName1" value="${property_name}.${perUserUser.name}"/>
      <jsp:useBean id="perUserPropertyName1" type="String" />
<c:set var="captionValue" value="${ssDefinitionEntry.customAttributes[perUserPropertyName1].value}"/>
<c:set var="caption" value=""/>
<% caption = ""; %>
<c:if test="${!empty captionValue}">
<%
	caption = DefinitionHelper.findCaptionForValue(ssConfigDefinition, item,
			(String) pageContext.getAttribute("captionValue"));
	caption = NLT.getDef(caption);
%>
</c:if>
<c:set var="caption" value="<%= caption %>"/>
      <tr>
      <td style="padding-left:10px;">
		<div class="ss_entryContent"><ssf:showUser user="${perUserUser}"/></div>
	  </td>
	  <td style="padding-left:10px;">
		<span class="${ss_element_display_style_item}">
	  		<c:out value="${caption}" escapeXml="false"/>
		</span>
      </td>
      </tr>
    </c:forEach>
    </table>
    </ssf:expandableArea>
    </div>
    </div>
  </c:if>
</c:if>

<c:if test="${!empty ss_element_display_style && 
    ss_element_display_style == 'tableAlignLeft'}">
<tr>
  <td class="ss_table_spacer_right" valign="top" align="right">
    <span class="${ss_element_display_style_caption}"><c:out value="${property_caption}" /></span>
  </td>
  <td valign="top">
	<span class="${ss_element_display_style_item}">
	  <c:out value="${caption}" escapeXml="false"/>
	</span>
  </td>
</tr>
  <c:if test="${!empty ss_userVersionPrincipals}">
    <tr>
    <td colspan="2">
    <div class="ss_perUserViewElement">
    <ssf:expandableArea title='<%= NLT.get("element.perUser.viewPersonalVersions") %>' titleClass="ss_fineprint">
    <table cellspacing="0" cellpadding="0">
    <c:forEach var="perUserUser" items="${ss_userVersionPrincipals}">
      <c:set var="perUserPropertyName2" value="${property_name}.${perUserUser.name}"/>
      <jsp:useBean id="perUserPropertyName2" type="String" />
<c:set var="captionValue" value="${ssDefinitionEntry.customAttributes[perUserPropertyName2].value}"/>
<c:set var="caption" value=""/>
<% caption = ""; %>
<c:if test="${!empty captionValue}">
<%
	caption = DefinitionHelper.findCaptionForValue(ssConfigDefinition, item,
			(String) pageContext.getAttribute("captionValue"));
	caption = NLT.getDef(caption);
%>
</c:if>
<c:set var="caption" value="<%= caption %>"/>
      <tr>
      <td style="padding-left:10px;">
		<div class="ss_entryContent"><ssf:showUser user="${perUserUser}"/></div>
	  </td>
	  <td style="padding-left:10px;">
		<span class="${ss_element_display_style_item}">
	  		<c:out value="${caption}" escapeXml="false"/>
		</span>
      </td>
      </tr>
    </c:forEach>
    </table>
    </ssf:expandableArea>
    </div>
    </td>
    </tr>
  </c:if>
</c:if>
