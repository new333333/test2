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
<% //View the listing part of a tasks folder %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<script type="text/javascript">
	var myTasks_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter> = new ss_tasks ('ss_tasks_list_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>', '${ssFolder.id}', '<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>');
	ss_tasks.overdueLabel = "<ssf:nlt tag="milestone.overdue"/>";
</script>
<table class="ss_tasks_list" id="ss_tasks_list_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>">
	<thead>
		<tr>
			<th>
<c:if test="${ssConfigJspStyle != 'template'}">
			    <a href="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
			    	name="action" value="${action}"/><portlet:param 
			    	name="operation" value="save_folder_sort_info"/><portlet:param 
			    	name="binderId" value="${ssFolder.id}"/><portlet:param 
			    	name="ssFolderSortBy" value="_sortTitle"/><c:choose><c:when 
			    	test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'false'}"><portlet:param 
			    	name="ssFolderSortDescend" value="true"/></c:when><c:otherwise><portlet:param 
			    	name="ssFolderSortDescend" value="false"/></c:otherwise></c:choose><portlet:param 
			    	name="tabId" value="${tabId}"/><portlet:param 
			    	name="ssTaskFilterType" value="${ssTaskFilterType}"/></portlet:actionURL>"
			
				<c:choose>
				  <c:when test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'false'}">
				  	<ssf:title tag="title.sort.by.column.desc">
				  		<ssf:param name="value" value="<%= NLT.get("task.title") %>" />
				  	</ssf:title>
				  </c:when>
				  <c:otherwise>
				  	<ssf:title tag="title.sort.by.column.asc">
				  		<ssf:param name="value" value="<%= NLT.get("task.title") %>" />
				  	</ssf:title>
				  </c:otherwise>
				</c:choose>	
				 >
</c:if>	
			      <div class="ss_title_menu"><ssf:nlt tag="task.title"/> </div>
			    	<c:if test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'true'}">
						<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" value="<%= NLT.get("folder.column.Title") %>" /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
					</c:if>
					<c:if test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'false'}">
						<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" value="<%= NLT.get("folder.column.Title") %>" /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
					</c:if>
<c:if test="${ssConfigJspStyle != 'template'}">
			    </a>
</c:if>
			</th>
			<th>
<c:if test="${ssConfigJspStyle != 'template'}">
			    <a href="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
			    	name="action" value="${action}"/><portlet:param 
			    	name="operation" value="save_folder_sort_info"/><portlet:param 
			    	name="binderId" value="${ssFolder.id}"/><portlet:param 
			    	name="ssFolderSortBy" value="priority"/><c:choose><c:when 
			    	test="${ ssFolderSortBy == 'priority' && ssFolderSortDescend == 'false'}"><portlet:param 
			    	name="ssFolderSortDescend" value="true"/></c:when><c:otherwise><portlet:param 
			    	name="ssFolderSortDescend" value="false"/></c:otherwise></c:choose><portlet:param 
			    	name="tabId" value="${tabId}"/><portlet:param 
			    	name="ssTaskFilterType" value="${ssTaskFilterType}"/></portlet:actionURL>"
				
				<c:choose>
				  <c:when test="${ ssFolderSortBy == 'priority' && ssFolderSortDescend == 'false'}">
				  	<ssf:title tag="title.sort.by.column.desc">
				  		<ssf:param name="value" value="<%= NLT.get("task.priority") %>" />
				  	</ssf:title>
				  </c:when>
				  <c:otherwise>
				  	<ssf:title tag="title.sort.by.column.asc">
				  		<ssf:param name="value" value="<%= NLT.get("task.priority") %>" />
				  	</ssf:title>
				  </c:otherwise>
				</c:choose>	
				 >
</c:if>
			      <div class="ss_title_menu"><ssf:nlt tag="task.priority"/> </div>
			    	<c:if test="${ ssFolderSortBy == 'priority' && ssFolderSortDescend == 'true'}">
						<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" value="<%= NLT.get("folder.column.Title") %>" /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
					</c:if>
					<c:if test="${ ssFolderSortBy == 'priority' && ssFolderSortDescend == 'false'}">
						<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" value="<%= NLT.get("folder.column.Title") %>" /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
					</c:if>
<c:if test="${ssConfigJspStyle != 'template'}">
			    </a>
</c:if>
			</th>
			<th>
<c:if test="${ssConfigJspStyle != 'template'}">
			    <a href="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
			    	name="action" value="${action}"/><portlet:param 
			    	name="operation" value="save_folder_sort_info"/><portlet:param 
			    	name="binderId" value="${ssFolder.id}"/><portlet:param 
			    	name="ssFolderSortBy" value="start_end#EndDate"/><c:choose><c:when 
			    	test="${ ssFolderSortBy == 'start_end#EndDate' && ssFolderSortDescend == 'false'}"><portlet:param 
			    	name="ssFolderSortDescend" value="true"/></c:when><c:otherwise><portlet:param 
			    	name="ssFolderSortDescend" value="false"/></c:otherwise></c:choose><portlet:param 
			    	name="tabId" value="${tabId}"/><portlet:param 
			    	name="ssTaskFilterType" value="${ssTaskFilterType}"/></portlet:actionURL>"
				
				<c:choose>
				  <c:when test="${ ssFolderSortBy == 'start_end#EndDate' && ssFolderSortDescend == 'false'}">
				  	<ssf:title tag="title.sort.by.column.desc">
				  		<ssf:param name="value" value="<%= NLT.get("task.dueDate") %>" />
				  	</ssf:title>
				  </c:when>
				  <c:otherwise>
				  	<ssf:title tag="title.sort.by.column.asc">
				  		<ssf:param name="value" value="<%= NLT.get("task.dueDate") %>" />
				  	</ssf:title>
				  </c:otherwise>
				</c:choose>	
				 >
</c:if>
			      <div class="ss_title_menu"><ssf:nlt tag="task.dueDate"/> </div>
			    	<c:if test="${ ssFolderSortBy == 'start_end#EndDate' && ssFolderSortDescend == 'true'}">
						<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" value="<%= NLT.get("folder.column.Title") %>" /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
					</c:if>
					<c:if test="${ ssFolderSortBy == 'start_end#EndDate' && ssFolderSortDescend == 'false'}">
						<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" value="<%= NLT.get("folder.column.Title") %>" /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
					</c:if>
<c:if test="${ssConfigJspStyle != 'template'}">
			    </a>
</c:if>
			</th>
			<th>
<c:if test="${ssConfigJspStyle != 'template'}">
				<a href="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
					name="action" value="${action}"/><portlet:param 
					name="operation" value="save_folder_sort_info"/><portlet:param 
					name="binderId" value="${ssFolder.id}"/><portlet:param 
					name="ssFolderSortBy" value="status"/><c:choose><c:when 
					test="${ ssFolderSortBy == 'status' && ssFolderSortDescend == 'false'}"><portlet:param 
					name="ssFolderSortDescend" value="true"/></c:when><c:otherwise><portlet:param 
					name="ssFolderSortDescend" value="false"/></c:otherwise></c:choose><portlet:param 
					name="tabId" value="${tabId}"/><portlet:param 
					name="ssTaskFilterType" value="${ssTaskFilterType}"/></portlet:actionURL>"
				
				<c:choose>
				  <c:when test="${ ssFolderSortBy == 'status' && ssFolderSortDescend == 'false'}">
				  	<ssf:title tag="title.sort.by.column.desc">
				  		<ssf:param name="value" value="<%= NLT.get("task.status") %>" />
				  	</ssf:title>
				  </c:when>
				  <c:otherwise>
				  	<ssf:title tag="title.sort.by.column.asc">
				  		<ssf:param name="value" value="<%= NLT.get("task.status") %>" />
				  	</ssf:title>
				  </c:otherwise>
				</c:choose>	
				 >
</c:if>
			      <div class="ss_title_menu"><ssf:nlt tag="task.status"/> </div>
			    	<c:if test="${ ssFolderSortBy == 'status' && ssFolderSortDescend == 'true'}">
						<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" value="<%= NLT.get("folder.column.Title") %>" /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
					</c:if>
					<c:if test="${ ssFolderSortBy == 'status' && ssFolderSortDescend == 'false'}">
						<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" value="<%= NLT.get("folder.column.Title") %>" /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
					</c:if>
<c:if test="${ssConfigJspStyle != 'template'}">
			    </a>
</c:if>
			</th>
			<th>
<c:if test="${ssConfigJspStyle != 'template'}">
			    <a href="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
			    	name="action" value="${action}"/><portlet:param 
			    	name="operation" value="save_folder_sort_info"/><portlet:param 
			    	name="binderId" value="${ssFolder.id}"/><portlet:param 
			    	name="ssFolderSortBy" value="assignment"/><c:choose><c:when 
			    	test="${ ssFolderSortBy == 'assignment' && ssFolderSortDescend == 'false'}"><portlet:param 
			    	name="ssFolderSortDescend" value="true"/></c:when><c:otherwise><portlet:param 
			    	name="ssFolderSortDescend" value="false"/></c:otherwise></c:choose><portlet:param 
			    	name="tabId" value="${tabId}"/><portlet:param 
			    	name="ssTaskFilterType" value="${ssTaskFilterType}"/></portlet:actionURL>"
				
				<c:choose>
				  <c:when test="${ ssFolderSortBy == 'assignment' && ssFolderSortDescend == 'false'}">
				  	<ssf:title tag="title.sort.by.column.desc">
				  		<ssf:param name="value" value="<%= NLT.get("task.assigned") %>" />
				  	</ssf:title>
				  </c:when>
				  <c:otherwise>
				  	<ssf:title tag="title.sort.by.column.asc">
				  		<ssf:param name="value" value="<%= NLT.get("task.assigned") %>" />
				  	</ssf:title>
				  </c:otherwise>
				</c:choose>	
				 >
</c:if>
			      <div class="ss_title_menu"><ssf:nlt tag="task.assigned"/> </div>
			    	<c:if test="${ ssFolderSortBy == 'assignment' && ssFolderSortDescend == 'true'}">
						<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" value="<%= NLT.get("folder.column.Title") %>" /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
					</c:if>
					<c:if test="${ ssFolderSortBy == 'assignment' && ssFolderSortDescend == 'false'}">
						<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" value="<%= NLT.get("folder.column.Title") %>" /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
					</c:if>
<c:if test="${ssConfigJspStyle != 'template'}">
			    </a>
</c:if>			
			</th>
			<th>
<c:if test="${ssConfigJspStyle != 'template'}">
				<a href="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
					name="action" value="${action}"/><portlet:param 
					name="operation" value="save_folder_sort_info"/><portlet:param 
					name="binderId" value="${ssFolder.id}"/><portlet:param 
					name="ssFolderSortBy" value="completed"/><c:choose><c:when 
					test="${ ssFolderSortBy == 'completed' && ssFolderSortDescend == 'false'}"><portlet:param 
					name="ssFolderSortDescend" value="true"/></c:when><c:otherwise><portlet:param 
					name="ssFolderSortDescend" value="false"/></c:otherwise></c:choose><portlet:param 
					name="tabId" value="${tabId}"/><portlet:param 
					name="ssTaskFilterType" value="${ssTaskFilterType}"/></portlet:actionURL>"
				
				<c:choose>
				  <c:when test="${ ssFolderSortBy == 'completed' && ssFolderSortDescend == 'false'}">
				  	<ssf:title tag="title.sort.by.column.desc">
				  		<ssf:param name="value" value="<%= NLT.get("task.done") %>" />
				  	</ssf:title>
				  </c:when>
				  <c:otherwise>
				  	<ssf:title tag="title.sort.by.column.asc">
				  		<ssf:param name="value" value="<%= NLT.get("task.done") %>" />
				  	</ssf:title>
				  </c:otherwise>
				</c:choose>	
				 >
</c:if>
			      <div class="ss_title_menu"><ssf:nlt tag="task.done"/> </div>
			    	<c:if test="${ ssFolderSortBy == 'completed' && ssFolderSortDescend == 'true'}">
						<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" value="<%= NLT.get("folder.column.Title") %>" /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
					</c:if>
					<c:if test="${ ssFolderSortBy == 'completed' && ssFolderSortDescend == 'false'}">
						<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" value="<%= NLT.get("folder.column.Title") %>" /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
					</c:if>
<c:if test="${ssConfigJspStyle != 'template'}">
			    </a>
</c:if>
			</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="entry" items="${ssFolderEntries}" >
			<jsp:useBean id="entry" type="java.util.HashMap" />
			
			<%
				boolean overdue = com.sitescape.team.util.DateComparer.isOverdue((Date)entry.get("start_end#EndDate"));
			%>
			<c:set var="overdue" value="<%= overdue %>"/>
			<c:if test="${entry.status == 's3' || entry.status == 's4'}">
				<c:set var="overdue" value="false" />
			</c:if>
			
			<script type="text/javascript">
				myTasks_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>.addTask({"id" : ${entry._docId},
													"completed" : "${entry.completed}",
													completedValues : {
														<c:forEach var="completed" items="${entry.ssEntryDefinitionElementData['completed'].values}" varStatus="loopStatus">
															"<c:out value="${completed.key}" escapeXml="false"/>" : "<c:out value="${completed.value}" escapeXml="false"/>"
															<c:if test="${!loopStatus.last}">,</c:if>
														</c:forEach>
													}});
			</script>
			
			<tr>
				<td class="ss_entryTitle ss_normalprint<c:if test="${entry.status == 's3' || entry.status == 's4'}"> ss_task_completed</c:if>" 
					id="ss_tasks_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_${entry._docId}_title">
					<c:set var="isDashboard" value="yes"/>
						<ssf:menuLink 
							displayDiv="false" action="view_folder_entry" 
							adapter="true" entryId="${entry._docId}" binderId="${entry._binderId}" 
							entityType="${entry._entityType}" imageId='menuimg_${entry._docId}_${renderResponse.namespace}' 
							menuDivId="ss_emd_${renderResponse.namespace}" linkMenuObjIdx="${renderResponse.namespace}" 
							namespace="${renderResponse.namespace}" entryCallbackRoutine="${showEntryCallbackRoutine}">		
						
							<ssf:param name="url" useBody="true">
								<ssf:url adapter="true" portletName="ss_forum" folderId="${entry._binderId}" 
								action="view_folder_entry" entryId="${entry._docId}" actionUrl="true" />
							</ssf:param>
						
							<c:out value="${entry.title}" escapeXml="false"/>
						</ssf:menuLink>
				</td>
				<td class="ss_iconsContainer" id="ss_tasks_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_${entry._docId}_priority">
				<c:if test="${! empty entry.priority}">
					<c:forEach var="prio" items="${entry.ssEntryDefinitionElementData.priority.values}"><a <c:if test="${entry.priority == prio.key}">href="javascript:// ;" class="ss_taskPriority"</c:if><c:if test="${entry.priority != prio.key}">href="javascript: myTasks_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>.changePriority(${entry._docId}, '${prio.key}');" class="ss_taskPriority ss_taskPriority_${prio.key}_u"</c:if> ><img <c:if test="${entry.priority == prio.key}"> src="<html:imagesPath/>icons/prio_${prio.key}.gif" </c:if><c:if test="${entry.priority != prio.key}">src="<html:imagesPath/>pics/1pix.gif"</c:if>	alt="${prio.value}" title="${prio.value}"></a></c:forEach></c:if></td>
				<td class="ss_due<c:if test="${overdue}"> ss_overdue</c:if>" id="ss_tasks_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_${entry._docId}_due">
					<c:choose>
						<c:when test="${!empty entry['start_end#TimeZoneID']}">
							<fmt:formatDate 
									timeZone="${ssUser.timeZone.ID}"
									value="${entry['start_end#EndDate']}" type="both" 
									dateStyle="short" timeStyle="short" />						
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
				</td>
				<td class="ss_iconsContainer" id="ss_tasks_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_${entry._docId}_status"><c:if test="${! empty entry.status}"><c:forEach var="status" items="${entry.ssEntryDefinitionElementData.status.values}"><a <c:if test="${entry.status == status.key}">href="javascript: //" class="ss_taskStatus" </c:if><c:if test="${entry.status != status.key}">href="javascript:  myTasks_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>.changeStatus(${entry._docId}, '${status.key}');" class="ss_taskStatus ss_taskStatus_${status.key}_u" </c:if>><img <c:if test="${entry.status == status.key}"> src="<html:imagesPath/>icons/status_${status.key}.gif" </c:if><c:if test="${entry.status != status.key}"> src="<html:imagesPath/>pics/1pix.gif" </c:if> alt="${status.value}" title="${status.value}"></a></c:forEach></c:if></td>
				<td class="ss_assigned">
					<ul>
						<c:forEach var="assigned" items="<%= com.sitescape.team.util.ResolveIds.getPrincipals(entry.get("assignment")) %>">
							<li><ssf:showUser user="${assigned}"/></li>
						</c:forEach>
					</ul>
				</td>
				<td id="ss_tasks_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_${entry._docId}_completed">
					<c:if test="${! empty entry.completed}">
						<ssf:progressBar currentValue="${entry.completed}" 
							valuesMap="${entry.ssEntryDefinitionElementData.completed.values}" 
							namespace="${renderResponse.namespace}" 
							entryId="${entry._docId}}" />
					</c:if>
				</td>
			</tr>
		
		</c:forEach>
	</tbody>
</table>


<ssf:menuLink displayDiv="true" menuDivId="ss_emd_${renderResponse.namespace}" linkMenuObjIdx="${renderResponse.namespace}" 
	namespace="${renderResponse.namespace}">
</ssf:menuLink>