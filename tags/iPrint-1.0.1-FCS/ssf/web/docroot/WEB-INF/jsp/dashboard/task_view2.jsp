<%
// The dashboard "search" component
/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
  //this is used by penlets and portlets
 //Don't include "include.jsp" directly 
%>
<%@ include file="/WEB-INF/jsp/dashboard/common_setup.jsp" %>

<div class="ss_searchResult_dashboardHeader">
	<div class="ss_dashboardPaginator"> 
		<c:if test="${ssDashboard.scope != 'portlet'}">
			<c:set var="binderId" value="${ssBinder.id}"/>
		</c:if>
		<c:if test="${ssDashboard.scope == 'portlet'}">
			<c:set var="binderId" value="${ssDashboardPortlet.id}"/>
		</c:if>
		  <c:if test="${ss_pageNumber > 0}">
		    <span>
		      <a onclick="ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber - 1}', '${ss_pageSize}',  '${ss_namespace}', '${ss_divId}', '${componentId}', 'task'); return false;"
		        href="javascript:;" ><img <ssf:alt tag="general.previousPage"/> src="<html:imagesPath/>pics/sym_arrow_left_.gif" /></a>&nbsp;&nbsp;&nbsp;
		    </span>
		  </c:if>
          <c:if test="${ss_pageNumber > 0}">
		     <span class="ss_pageNumber">${ss_pageNumber+1}</span>
		  </c:if>
		  <c:if test="${(ss_pageNumber * ss_pageSize + hitCount) < ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchCount}">
		    <span>&nbsp;&nbsp;
		      <a onclick="ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber + 1}', '${ss_pageSize}',  '${ss_namespace}', '${ss_divId}', '${componentId}', 'task'); return false;"
		        href="javascript:;" ><img <ssf:alt tag="general.nextPage"/> src="<html:imagesPath/>pics/sym_arrow_right_.gif"/></a>
		    </span>
		  </c:if>
	</div>
	<div class="ss_searchResult_dashboardNumbers">
	<c:if test="${hitCount > 0}">
		    [<ssf:nlt tag="folder.Results">
		    <ssf:param name="value" value="${ss_pageNumber * ss_pageSize + 1}"/>
		    <ssf:param name="value" value="${ss_pageNumber * ss_pageSize + hitCount}"/>
		    <ssf:param name="value" value="${ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchCount}"/>
		    </ssf:nlt>]
	</c:if>
	<c:if test="${hitCount == 0}">
	    <span class="ss_light ss_fineprint">
		  [<ssf:nlt tag="dashboard.noEntriesFound"/>]
		</span>
	</c:if>
	</div>
	<div class="ss_clear"></div>
</div>
<div class="ss_task_list_container">
<table class="ss_tasks_list" style="text-align: left;">
	<tr class="columnhead">
		<td><ssf:nlt tag="task.title"/></td>
		<td><ssf:nlt tag="task.priority"/></td>
		<td><ssf:nlt tag="task.dueDate"/></td>
		<td><ssf:nlt tag="task.status"/></td>
		<td><ssf:nlt tag="task.assigned"/></td>
		<td><ssf:nlt tag="task.done"/></td>		
		<td><ssf:nlt tag="task.location"/></td>
	</tr>

<c:forEach var="entry" items="${ssDashboard.beans[componentId].ssSearchFormData.searchResults}" >
	<jsp:useBean id="entry" type="java.util.HashMap" />
	<tr>
		<td class="ss_entryTitle">
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
		<c:if test="${! empty entry.priority}"><c:forEach var="prio" items="${entry.ssEntryDefinitionElementData.priority.values}"><c:if test="${entry.priority == prio.key}"><img src="<html:imagesPath/>icons/prio_${prio.key}.png"	alt="${prio.value}" title="${prio.value}" class="ss_taskPriority" /></c:if></c:forEach></c:if></td>
		<td>
			<c:choose>
				<c:when test="${!empty entry['start_end#LogicalEndDate']}">
					<c:choose>
						<c:when test="${!empty entry['start_end#TimeZoneID']}">
							<fmt:formatDate 
									timeZone="${ssUser.timeZone.ID}"
									value="${entry['start_end#LogicalEndDate']}" type="both" 
									dateStyle="short" timeStyle="short" />						
						</c:when>	
						<c:otherwise>
							<fmt:formatDate 
									timeZone="GMT"
									value="${entry['start_end#LogicalEndDate']}" type="date" 
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
		<td class="ss_iconsContainer"  style="text-align: center;"><c:if test="${! empty entry.status}"><c:forEach var="status" items="${entry.ssEntryDefinitionElementData.status.values}"><c:if test="${entry.status == status.key}"><img src="<html:imagesPath/>icons/status_${status.key}.png" class="ss_taskStatus" alt="${status.value}" title="${status.value}" /></c:if></c:forEach></c:if></td>
		<td>
			<c:set var="assignment" value='<%= org.kablink.teaming.util.ResolveIds.getPrincipals(entry.get("assignment"), false) %>' />
			<c:set var="assignment_groups" value='<%= org.kablink.teaming.util.ResolveIds.getPrincipals(entry.get("assignment_groups")) %>' />
			<c:set var="assignment_teams" value='<%= org.kablink.teaming.util.ResolveIds.getBinders(entry.get("assignment_teams")) %>' />
			<c:if test="${!empty assignment}">
					<c:forEach var="assigned" items="${assignment}">
						<div><ssf:showUser user="${assigned}"/></div>
					</c:forEach>
			</c:if>
			<c:if test="${!empty assignment_groups}">
					<c:forEach var="assigned" items="${assignment_groups}">
						<div><ssf:showGroup group="${assigned}"/></div>
					</c:forEach>
			</c:if>	
			<c:if test="${!empty assignment_teams}">
					<c:forEach var="assigned" items="${assignment_teams}">
						<div><ssf:showTeam team="${assigned}"/></div>
					</c:forEach>
			</c:if>							
			
			<c:if test="${empty assignment && empty assignment_groups && empty assignment_teams}">
				&nbsp;
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

			<c:if test="${!empty ssDashboard.beans[componentId].ssFolderList}">
				<c:forEach var="folder" items="${ssDashboard.beans[componentId].ssFolderList}">
					<c:if test="${folder.id == entry._binderId}">
						<c:set var="path" value="${folder}"/>
						<c:set var="title" value="${folder.title} (${folder.parentWorkArea.title})"/>
					</c:if>
				</c:forEach>
			</c:if>
			<c:set var="isDashboard" value="yes"/>

			<c:if test="${!empty path}">
	    		<a href="javascript: ;"
					onclick="return ss_gotoPermalink('${entry._binderId}', '${entry._binderId}', 'folder', '${ss_namespace}', 'yes');"
					title="${path}"
					><span>${title}</span></a>
			</c:if>
				
								
		</td>	
	</tr>
	</c:forEach>
</table>
</div>
			