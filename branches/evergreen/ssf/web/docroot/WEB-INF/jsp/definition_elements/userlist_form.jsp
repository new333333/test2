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
<%@ page import="org.kablink.teaming.domain.User"     %>
<%@ page import="org.kablink.teaming.util.ResolveIds" %>

<% // User list %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="ss_fieldModifyDisabled" value=""/>
<c:set var="ss_fieldModifyStyle" value=""/>
<c:if test="${ss_accessControlMap['ss_modifyEntryRightsSet']}">
  <c:if test="${(!ss_accessControlMap['ss_modifyEntryFieldsAllowed'] && !ss_accessControlMap['ss_modifyEntryAllowed']) || 
			(!ss_accessControlMap['ss_modifyEntryAllowed'] && !ss_fieldModificationsAllowed == 'true')}">
    <c:set var="ss_fieldModifyStyle" value="ss_modifyDisabled"/>
    <c:set var="ss_fieldModifyInputAttribute" value=" disabled='disabled' "/>
    <c:set var="ss_fieldModifyDisabled" value="true"/>
  </c:if>
</c:if>
<c:if test="${property_required}"><c:set var="ss_someFieldsRequired" value="true" scope="request"/></c:if>
<%
	String caption1 = (String) request.getAttribute("property_caption");
	String caption2 = NLT.get("general.required.caption", new Object[]{caption1});
%>
<%
	String propertyName = (String) request.getAttribute("property_name");
	java.util.List userList = new java.util.ArrayList();
	java.util.Set userListSet = new java.util.HashSet();
%>

<% // If we don't have any entries in the user list... %>
<c:if test="${empty ssDefinitionEntry}">
	<%
		// ...are we adding a new entry to a folder?
		String operation = (String) request.getAttribute("ssOperation");
		if ((null != operation) && operation.equalsIgnoreCase("add_folder_entry")) {
			// Yes!  If we're supposed to include the entry creator
			// during the add...
			String	includeCreator = (String) request.getAttribute("property_includeCreatorOnAdd");
			if ((null != includeCreator) && includeCreator.equalsIgnoreCase("true")) {
				// ...and we have their userId...
				User user = (User) request.getAttribute("ssUser");
				Long userId = ((null == user) ? null : user.getId());
				if (null != userId) {
					// ...treat it as though they were added manually.
					userList = ResolveIds.getPrincipals(String.valueOf(userId.longValue()));
					userListSet.addAll(userList);
				}
			}
		}
	%>
</c:if>

<c:if test="${! empty ssDefinitionEntry}">
  <c:set var="userlist_entry" value="${ssDefinitionEntry}"/>
  <jsp:useBean id="userlist_entry" type="org.kablink.teaming.domain.DefinableEntity" />
<%
	if (propertyName != null && !propertyName.equals("")) 
		userList = ResolveIds.getPrincipals(userlist_entry.getCustomAttribute(propertyName));
	if(userList != null) {
		userListSet.addAll(userList);
	}
%>
</c:if>
<div class="ss_entryContent ${ss_fieldModifyStyle}">
	<div class="ss_labelAbove">${property_caption}<c:if test="${property_required}"><span 
	  id="ss_required_${property_name}" title="<%= caption2 %>" class="ss_required">*</span></c:if></div>
	<c:if test="${empty ssReadOnlyFields[property_name] && empty ss_fieldModifyDisabled}">
		<ssf:find formName="${formName}" formElement="${property_name}" type="user" 
		  userList="<%= userListSet %>" width="100px"/>
		<ssf:clipboard type="user" formElement="${property_name}" />
	</c:if>
	<c:if test="${!empty ssReadOnlyFields[property_name] || !empty ss_fieldModifyDisabled}">
		<c:forEach var="userItem" items="<%= userListSet %>">
			${userItem.title}<br/>
		</c:forEach>
		<c:if test="${!empty ssReadOnlyFields[property_name]}">&nbsp;</c:if>
	</c:if>
</div>
