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
<% //User_list view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="userlist_entry" value="${ssDefinitionEntry}"/>
<jsp:useBean id="userlist_entry" type="org.kablink.teaming.domain.Entry" />
<c:if test="${empty ss_element_display_style}">
<div class="ss_entryContent">
<span class="ss_labelLeft"><c:out value="${property_caption}" /></span>
<ul class="ss_nobullet">
<c:forEach var="selection" items="<%= org.kablink.teaming.util.ResolveIds.getPrincipals(userlist_entry.getCustomAttribute(property_name), false) %>" >
<li><ssf:showUser user="${selection}" /></li>
</c:forEach>
</ul>
</div>
</c:if>

<c:if test="${!empty ss_element_display_style && 
    ss_element_display_style == 'tableAlignLeft'}">
<tr>
  <td class="ss_table_spacer_right" valign="top" align="right">
    <span class="${ss_element_display_style_caption}"><c:out value="${property_caption}" /></span>
  </td>
  <td valign="top" align="left">
	<ul class="ss_nobullet">
	<c:forEach var="selection" items="<%= org.kablink.teaming.util.ResolveIds.getPrincipals(userlist_entry.getCustomAttribute(property_name), false) %>" >
 	 <li><ssf:showUser user="${selection}" /></li>
	</c:forEach>
	</ul>
  </td>
</tr>
</c:if>








