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
<% // Team list %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	String propertyName = (String) request.getAttribute("property_name");
	java.util.List teamList = new java.util.ArrayList();
	java.util.Set teamListSet = new java.util.HashSet();
	String caption1 = (String) request.getAttribute("property_caption");
	String caption2 = NLT.get("general.required.caption", new Object[]{caption1});
%>
<c:if test="${! empty ssDefinitionEntry}">
  <c:set var="teamlist_entry" value="${ssDefinitionEntry}"/>
  <jsp:useBean id="teamlist_entry" type="org.kablink.teaming.domain.DefinableEntity" />
<%
	if (propertyName != null && !propertyName.equals("")) {
		Map teams = org.kablink.teaming.util.ResolveIds.getBinderTitlesAndIcons(teamlist_entry.getCustomAttribute(propertyName)); 
		Iterator<Map.Entry> it = teams.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = it.next();
			teamList.add(entry.getValue());
		}
	}
	if(teamList != null) {
		teamListSet.addAll(teamList);
	}
%>
</c:if>
<div class="ss_entryContent">
<div class="ss_labelAbove">${property_caption}<c:if test="${property_required}"><span 
  id="ss_required_${property_name}" title="<%= caption2 %>" class="ss_required">*</span></c:if></div>
<c:if test="${empty ssReadOnlyFields[property_name]}">
<ssf:find formName="${formName}" formElement="${property_name}" type="teams" 
	userList="<%= teamListSet %>"/>
</c:if>
<c:if test="${!empty ssReadOnlyFields[property_name]}">
<c:forEach var="teamItem" items="<%= teamListSet %>">
	${teamItem.title}<br/>
</c:forEach>
</c:if>
</div>
