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
<%@ page import="com.sitescape.team.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:if test="${empty ss_myTasks}">
<span><ssf:nlt tag="relevance.none"/></span>
</c:if>
<c:if test="${!empty ss_myTasks}">
<table class="ss_tasks_list" >
<tbody>
	<tr>
		<th><ssf:nlt tag="task.title"/></th>
		<th><ssf:nlt tag="task.priority"/></th>
		<th><ssf:nlt tag="task.dueDate"/></th>
		<th><ssf:nlt tag="task.status"/></th>
		<th><ssf:nlt tag="task.done"/></th>		
		<th><ssf:nlt tag="task.location"/></th>
	</tr>

<c:forEach var="entry" items="${ss_myTasks}">
	<jsp:useBean id="entry" type="java.util.HashMap" />
	<tr>
		<td class="ss_entryTitle ss_normalprint">
			<c:set var="isDashboard" value="yes"/>
				
			<ssf:titleLink 
				entryId="${entry._docId}" binderId="${entry._binderId}" 
				entityType="${entry._entityType}" 
				namespace="${ss_namespace}" 
				isDashboard="${isDashboard}" dashboardType="${ssDashboard.scope}">

				
				<ssf:param name="url" useBody="true">
					<ssf:url adapter="true" portletName="ss_forum" folderId="${entry._binderId}" 
					  action="view_folder_entry" entryId="${entry._docId}" actionUrl="true" />
				</ssf:param>
				
				<c:out value="${entry.title}" escapeXml="false"/>
			</ssf:titleLink>
		</td>
		<td class="ss_iconsContainer" style="text-align: center;">
		<c:if test="${! empty entry.priority}">
		  <c:forEach var="prio" items="${entry.ssEntryDefinitionElementData.priority.values}">
		    <c:if test="${entry.priority == prio.key}">
		      <img src="<html:imagesPath/>icons/prio_${prio.key}.gif" 
		        alt="${prio.value}" 
		        title="${prio.value}" 
		        class="ss_taskPriority" />
		    </c:if>
		  </c:forEach>
		</c:if></td>
		<td>
			<c:choose>
				<c:when test="${!empty entry['start_end#EndDate']}">
					<c:choose>
						<c:when test="${!empty entry['start_end#TimeZoneID']}">
							<fmt:formatDate 
									timeZone="${ssUser.timeZone.ID}"
									value="${entry['start_end#EndDate']}" type="date" 
									dateStyle="short" />						
						</c:when>	
						<c:otherwise>
							<fmt:formatDate 
									timeZone="GMT"
									value="${entry['start_end#EndDate']}" type="date" 
									dateStyle="short"/>
						</c:otherwise>
					</c:choose>
					<c:if test="${overdue}">
						<ssf:nlt tag="milestone.overdue"/>
					</c:if>
				</c:when>
				<c:otherwise>
					&nbsp;
				</c:otherwise>
			</c:choose>				  
		</td>
		<td class="ss_iconsContainer"  style="text-align: center;">
		  <c:if test="${! empty entry.status}">
		    <c:forEach var="status" items="${entry.ssEntryDefinitionElementData.status.values}">
		      <c:if test="${entry.status == status.key}">
		        <img src="<html:imagesPath/>icons/status_${status.key}.gif" 
		          class="ss_taskStatus" 
		          alt="${status.value}" 
		          title="${status.value}" />
		      </c:if>
		    </c:forEach>
		  </c:if>
		</td>
		<td>
			<c:if test="${! empty entry.completed && !empty entry.ssEntryDefinitionElementData.completed.values}">
				<ssf:progressBar currentValue="${entry.completed}" 
					valuesMap="${entry.ssEntryDefinitionElementData.completed.values}" 
					namespace="${ss_namespace}" 
					entryId="${entry._docId}}" 
					readOnly="true"/>
			</c:if>
		</td>
		<td class="ss_normalprint" width="20%">

			<c:set var="path" value=""/>

			<c:if test="${!empty ss_myTasksFolders[entry._binderId]}">
				<c:set var="path" value="${ss_myTasksFolders[entry._binderId]}"/>
				<c:set var="title" value="${ss_myTasksFolders[entry._binderId].parentBinder.title} // ${ss_myTasksFolders[entry._binderId].title}"/>
			</c:if>
			<c:set var="isDashboard" value="yes"/>

			<c:if test="${!empty path}">
	    		<a href="javascript: ;"
					onClick="return ss_gotoPermalink('${entry._binderId}', '${entry._binderId}', 'folder', '${ss_namespace}', 'yes');"
					title="${path}"
					><span class="ss_bold">${title}</span></a>
			</c:if>
				
								
		</td>	
	</tr>
</c:forEach>
</tbody>
</table>
</c:if>
