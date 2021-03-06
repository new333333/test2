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
<%@ page import="org.kablink.teaming.lucene.util.SearchFieldResult" %>
<%@ page import="org.kablink.teaming.domain.CustomAttribute" %>
<%@ page import="org.kablink.teaming.domain.FolderEntry" %>
<%@ page import="java.util.Map" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	String s_property_name = (String) request.getAttribute("property_name");
	java.lang.Object thisEntry = (java.lang.Object) request.getAttribute("ssDefinitionEntry");
	java.util.Set placesIds = new java.util.HashSet();
	if (thisEntry instanceof FolderEntry) {
		CustomAttribute attr = ((FolderEntry)thisEntry).getCustomAttribute(s_property_name);
		if (attr != null) placesIds = attr.getValueSet();
	} else if (thisEntry instanceof Map) {
		Object valueObj = (Object) ((Map)thisEntry).get(s_property_name);
		if (valueObj != null && valueObj instanceof String) {
			placesIds.add(valueObj);
		} else if (valueObj != null && valueObj instanceof SearchFieldResult) {
			placesIds = ((SearchFieldResult)valueObj).getValueSet();
		}
	}
%>
<div class="ss_entryContent">
	<span class="ss_labelLeft"><c:out value="${property_caption}" /></span>
	<ul class="ss_nobullet">
	<c:forEach var="selection" items="<%= org.kablink.teaming.util.ResolveIds.getBinderTitlesAndIcons(placesIds) %>" >
		<li><img border="0" <ssf:alt/>
		          src="<html:imagesPath/>${selection.value.iconName}" />
	          <c:if test="${!selection.value.deleted}">
				<a       
		          <ssf:ifadapter>
		            <c:if test="${selection.value.definitionType == '5'}">
			          href="<ssf:url adapter="true" portletName="ss_forum" action="view_permalink" binderId="${selection.key}">
							<ssf:param name="entityType" value="folder"/></ssf:url>"
					  onclick="ss_openUrlInParentWorkarea(this.href, '${selection.key}', 'view_folder_listing'); return false;"
					</c:if>
		            <c:if test="${selection.value.definitionType == '6'}">
			          href="<ssf:url adapter="true" portletName="ss_forum" action="view_permalink" binderId="${selection.key}">
							<ssf:param name="entityType" value="profiles"/></ssf:url>"
					  onclick="ss_openUrlInParentWorkarea(this.href, '${selection.key}', 'view_profile_listing'); return false;"
					</c:if>
		            <c:if test="${selection.value.definitionType == '8' || selection.value.definitionType == 12}">
			          href="<ssf:url adapter="true" portletName="ss_forum" action="view_permalink" binderId="${selection.key}">
							<ssf:param name="entityType" value="workspace"/></ssf:url>"
					  onclick="ss_openUrlInParentWorkarea(this.href, '${selection.key}', 'view_ws_listing'); return false;"
					</c:if>
		          </ssf:ifadapter>
		          <ssf:ifnotadapter>
				     <c:if test="${selection.value.definitionType == '5'}">
				       href="<ssf:url adapter="false" portletName="ss_forum" folderId="${selection.key}" 
				         action="view_folder_listing" actionUrl="false" >
		    			 <ssf:param name="binderId" value="${selection.key}"/>
						 </ssf:url>"
					 </c:if>
				     <c:if test="${selection.value.definitionType == '6'}">
				       href="<ssf:url adapter="false" portletName="ss_forum" folderId="${selection.key}" 
				         action="view_profile_listing" actionUrl="false" >
		    			 <ssf:param name="binderId" value="${selection.key}"/>
						 </ssf:url>"
					 </c:if>
				     <c:if test="${selection.value.definitionType == '8' || selection.value.definitionType == '12'}">
				       href="<ssf:url adapter="false" portletName="ss_forum" folderId="${selection.key}" 
				         action="view_ws_listing" actionUrl="false" >
		    			 <ssf:param name="binderId" value="${selection.key}"/>
						 </ssf:url>"
					 </c:if>
		          </ssf:ifnotadapter>
		          class="ss_parentPointer"></c:if><c:out value="${selection.value.title} (${selection.value.parentTitle})" escapeXml="false"/><c:if test="${!selection.value.deleted}"></a></c:if>
				<c:if test="${selection.value.deleted}">
					<span class="ss_fineprint ss_light"><ssf:nlt tag="milestone.folder.deleted"/></span>
				</c:if>
		</li>
	</c:forEach>
	</ul>
</div>
