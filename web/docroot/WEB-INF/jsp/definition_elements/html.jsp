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
<% // html %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${empty ss_html_element}">
  <c:set var="ss_html_element" value="0" scope="request"/>
  <c:set var="ss_html_element_map" value="<%= new java.util.HashMap() %>" scope="request"/>
</c:if>
<c:set var="ss_html_element" value="${ss_html_element + 1}" scope="request"/>
<jsp:useBean id="ss_html_element" type="java.lang.Long" scope="request" />
<jsp:useBean id="property_htmlBottom" type="String" scope="request" />
<jsp:useBean id="ss_html_element_map" type="java.util.HashMap" scope="request" />
<%
ss_html_element_map.put(ss_html_element.toString(), property_htmlBottom);
%>
<c:if test="${!empty ss_element_display_style && 
    ss_element_display_style == 'tableAlignLeft'}">
<tr>
  <td class="ss_table_spacer_right" valign="top" align="right">
  </td>
  <td valign="top">
</c:if>
<c:out value="${property_htmlTop}" escapeXml="false"/>
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${item}" 
  configJspStyle="${ssConfigJspStyle}"
  entry="${ssDefinitionEntry}" />
<c:out value="<%= ss_html_element_map.get(ss_html_element.toString()) %>" escapeXml="false"/>
<c:set var="ss_html_element" value="${ss_html_element - 1}" scope="request"/>
<c:if test="${!empty ss_element_display_style && 
    ss_element_display_style == 'tableAlignLeft'}">
  </td>
</tr>
</c:if>
