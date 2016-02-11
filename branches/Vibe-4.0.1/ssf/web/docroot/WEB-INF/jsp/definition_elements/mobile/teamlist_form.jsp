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
<% // Team list %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	String propertyName = (String) request.getAttribute("property_name");
	java.util.Set teamList = new java.util.HashSet();
	java.util.Set teamListSet = new java.util.HashSet();
%>
<c:if test="${! empty ssDefinitionEntry}">
  <c:set var="teamlist_entry" value="${ssDefinitionEntry}"/>
  <jsp:useBean id="teamlist_entry" type="org.kablink.teaming.domain.DefinableEntity" />
<%
	if (propertyName != null && !propertyName.equals("")) 
		teamList = org.kablink.teaming.util.ResolveIds.getBinders(teamlist_entry.getCustomAttribute(propertyName));
	if(teamList != null) {
		teamListSet.addAll(teamList);
	}
%>
</c:if>
<div class="ss_entryContent" style="border-bottom: 1px solid #b8b8b8; padding-bottom: 10px;">
	<div class="ss_labelAbove">
		<c:out value="${property_caption}"/>
	  <span>
		<input type="hidden" name="_entryOperationType" value="${ssOperationType}" />
		<input type="submit" name="addUGTBtn" value="<ssf:nlt tag="userlist.addTeam"/>"
		  onClick="ss_setUGT('${ss_form_form_formName}', '${property_name}', 'team');" />
	  </span>
	</div>
	  <c:forEach var="teamItem" items="<%= teamListSet %>">
	<div style="margin-left: 20px; padding-top: 8px; ">
		  ${teamItem.title}
		  <a href="javascript: ;" 
			onClick="ss_delete_hidden_field(this, '${ss_form_form_formName}', '${property_name}', '${teamItem.id}');return false;"
		  ><img style="padding-left: 5px" align="absmiddle" src="<html:rootPath/>images/icons/close_gray16.png"></a>
		  <input type="hidden" name="${property_name}" value="${teamItem.id}"/>
		</div>
	  </c:forEach>
</div>





