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
<% // User list %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	String propertyName = (String) request.getAttribute("property_name");
	java.util.List userList = new java.util.ArrayList();
	java.util.Set userListSet = new java.util.HashSet();
%>
<c:if test="${! empty ssDefinitionEntry}">
  <c:set var="userlist_entry" value="${ssDefinitionEntry}"/>
  <jsp:useBean id="userlist_entry" type="com.sitescape.team.domain.Entry" />
<%
	if (propertyName != null && !propertyName.equals("")) 
		userList = com.sitescape.team.util.ResolveIds.getPrincipals(userlist_entry.getCustomAttribute(propertyName));
	if(userList != null) {
		userListSet.addAll(userList);
	}
%>
</c:if>
<div class="ss_entryContent">
<div class="ss_labelAbove"><c:out value="${property_caption}"/></div>
<ssf:find formName="${formName}" formElement="${property_name}" type="user" 
  userList="<%= userListSet %>"/>
</div>
