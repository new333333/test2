<%
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
%>
<% //View the listing part of a tasks folder %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="java.util.Date" %>
<jsp:useBean id="ssSeenMap" type="org.kablink.teaming.domain.SeenMap" scope="request" />

<script type="text/javascript">
	var myTasks_${renderResponse.namespace} = new ss_tasks ('ss_tasks_list_${renderResponse.namespace}', '${ssBinder.id}', '${renderResponse.namespace}');
	ss_tasks.overdueLabel = "<ssf:nlt tag="milestone.overdue"/>";
	var ss_showingFolder = true;
</script>
<table class="ss_tasks_list" id="ss_tasks_list_${renderResponse.namespace}">
	<thead>
		<tr>
			<th>
			    <a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
			    	name="operation" value="save_folder_sort_info"/><ssf:param 
			    	name="binderId" value="${ssBinder.id}"/><ssf:param 
			    	name="ssFolderSortBy" value="_sortTitle"/><c:choose><c:when 
			    	test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'false'}"><ssf:param 
			    	name="ssFolderSortDescend" value="true"/></c:when><c:otherwise><ssf:param 
			    	name="ssFolderSortDescend" value="false"/></c:otherwise></c:choose><ssf:param 
			    	name="ssFolderModeType" value="${ssCurrentFolderModeType}"/><ssf:param
			    	name="ssTaskFilterType" value="${ssTaskFilterType}"/></ssf:url>"
			
				<c:choose>
				  <c:when test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'false'}">
				  	<ssf:title tag="title.sort.by.column.desc">
				  		<ssf:param name="value" value='<%= NLT.get("task.title") %>' />
				  	</ssf:title>
				  </c:when>
				  <c:otherwise>
				  	<ssf:title tag="title.sort.by.column.asc">
				  		<ssf:param name="value" value='<%= NLT.get("task.title") %>' />
				  	</ssf:title>
				  </c:otherwise>
				</c:choose>	
				 >

			      <div ><span><ssf:nlt tag="task.title"/></span>
			    	<c:if test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'true'}">
						<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" 
						value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
					</c:if>
					<c:if test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'false'}">
						<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" 
						value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
					</c:if>
			      </div>
			    </a>
			</th>
			<th>
			    <a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
			    	name="operation" value="save_folder_sort_info"/><ssf:param 
			    	name="binderId" value="${ssBinder.id}"/><ssf:param 
			    	name="ssFolderSortBy" value="priority"/><c:choose><c:when 
			    	test="${ ssFolderSortBy == 'priority' && ssFolderSortDescend == 'false'}"><ssf:param 
			    	name="ssFolderSortDescend" value="true"/></c:when><c:otherwise><ssf:param 
			    	name="ssFolderSortDescend" value="false"/></c:otherwise></c:choose><ssf:param 
			    	name="ssFolderModeType" value="${ssCurrentFolderModeType}"/><ssf:param
			    	name="ssTaskFilterType" value="${ssTaskFilterType}"/></ssf:url>"
				
				<c:choose>
				  <c:when test="${ ssFolderSortBy == 'priority' && ssFolderSortDescend == 'false'}">
				  	<ssf:title tag="title.sort.by.column.desc">
				  		<ssf:param name="value" value='<%= NLT.get("task.priority") %>' />
				  	</ssf:title>
				  </c:when>
				  <c:otherwise>
				  	<ssf:title tag="title.sort.by.column.asc">
				  		<ssf:param name="value" value='<%= NLT.get("task.priority") %>' />
				  	</ssf:title>
				  </c:otherwise>
				</c:choose>	
				 >

			      <div >
			        <span><ssf:nlt tag="task.priority"/></span>
			    	<c:if test="${ ssFolderSortBy == 'priority' && ssFolderSortDescend == 'true'}">
						<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" 
						value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
					</c:if>
					<c:if test="${ ssFolderSortBy == 'priority' && ssFolderSortDescend == 'false'}">
						<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" 
						value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
					</c:if>
			      </div>
			    </a>
			</th>
			<th>
			    <a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
			    	name="operation" value="save_folder_sort_info"/><ssf:param 
			    	name="binderId" value="${ssBinder.id}"/><ssf:param 
			    	name="ssFolderSortBy" value="start_end#LogicalEndDate"/><c:choose><c:when 
			    	test="${ ssFolderSortBy == 'start_end#LogicalEndDate' && ssFolderSortDescend == 'false'}"><ssf:param 
			    	name="ssFolderSortDescend" value="true"/></c:when><c:otherwise><ssf:param 
			    	name="ssFolderSortDescend" value="false"/></c:otherwise></c:choose><ssf:param 
			    	name="ssFolderModeType" value="${ssCurrentFolderModeType}"/><ssf:param
			    	name="ssTaskFilterType" value="${ssTaskFilterType}"/></ssf:url>"
				
				<c:choose>
				  <c:when test="${ ssFolderSortBy == 'start_end#LogicalEndDate' && ssFolderSortDescend == 'false'}">
				  	<ssf:title tag="title.sort.by.column.desc">
				  		<ssf:param name="value" value='<%= NLT.get("task.dueDate") %>' />
				  	</ssf:title>
				  </c:when>
				  <c:otherwise>
				  	<ssf:title tag="title.sort.by.column.asc">
				  		<ssf:param name="value" value='<%= NLT.get("task.dueDate") %>' />
				  	</ssf:title>
				  </c:otherwise>
				</c:choose>	
				 >
			      <div >
			        <span><ssf:nlt tag="task.dueDate"/></span>
			    	<c:if test="${ ssFolderSortBy == 'start_end#LogicalEndDate' && ssFolderSortDescend == 'true'}">
						<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" 
						value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
					</c:if>
					<c:if test="${ ssFolderSortBy == 'start_end#LogicalEndDate' && ssFolderSortDescend == 'false'}">
						<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" 
						value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
					</c:if>
			      </div>
			    </a>
			</th>
			<th>
				<a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
					name="operation" value="save_folder_sort_info"/><ssf:param 
					name="binderId" value="${ssBinder.id}"/><ssf:param 
					name="ssFolderSortBy" value="status"/><c:choose><c:when 
					test="${ ssFolderSortBy == 'status' && ssFolderSortDescend == 'false'}"><ssf:param 
					name="ssFolderSortDescend" value="true"/></c:when><c:otherwise><ssf:param 
					name="ssFolderSortDescend" value="false"/></c:otherwise></c:choose><ssf:param 
			    	name="ssFolderModeType" value="${ssCurrentFolderModeType}"/><ssf:param
					name="ssTaskFilterType" value="${ssTaskFilterType}"/></ssf:url>"
				
				<c:choose>
				  <c:when test="${ ssFolderSortBy == 'status' && ssFolderSortDescend == 'false'}">
				  	<ssf:title tag="title.sort.by.column.desc">
				  		<ssf:param name="value" value='<%= NLT.get("task.status") %>' />
				  	</ssf:title>
				  </c:when>
				  <c:otherwise>
				  	<ssf:title tag="title.sort.by.column.asc">
				  		<ssf:param name="value" value='<%= NLT.get("task.status") %>' />
				  	</ssf:title>
				  </c:otherwise>
				</c:choose>	
				 >

			      <div >
			        <span><ssf:nlt tag="task.status"/></span>
			    	<c:if test="${ ssFolderSortBy == 'status' && ssFolderSortDescend == 'true'}">
						<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" 
						value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
					</c:if>
					<c:if test="${ ssFolderSortBy == 'status' && ssFolderSortDescend == 'false'}">
						<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" 
						value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
					</c:if>
			      </div>
			    </a>

			</th>
			<th>
			    <a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
			    	name="operation" value="save_folder_sort_info"/><ssf:param 
			    	name="binderId" value="${ssBinder.id}"/><ssf:param 
			    	name="ssFolderSortBy" value="assignment"/><c:choose><c:when 
			    	test="${ ssFolderSortBy == 'assignment' && ssFolderSortDescend == 'false'}"><ssf:param 
			    	name="ssFolderSortDescend" value="true"/></c:when><c:otherwise><ssf:param 
			    	name="ssFolderSortDescend" value="false"/></c:otherwise></c:choose><ssf:param 
			    	name="ssFolderModeType" value="${ssCurrentFolderModeType}"/><ssf:param
			    	name="ssTaskFilterType" value="${ssTaskFilterType}"/></ssf:url>"
				
				<c:choose>
				  <c:when test="${ ssFolderSortBy == 'assignment' && ssFolderSortDescend == 'false'}">
				  	<ssf:title tag="title.sort.by.column.desc">
				  		<ssf:param name="value" value='<%= NLT.get("task.assigned") %>' />
				  	</ssf:title>
				  </c:when>
				  <c:otherwise>
				  	<ssf:title tag="title.sort.by.column.asc">
				  		<ssf:param name="value" value='<%= NLT.get("task.assigned") %>' />
				  	</ssf:title>
				  </c:otherwise>
				</c:choose>	
				 >
			      <div >
			        <span><ssf:nlt tag="task.assigned"/></span>
			    	<c:if test="${ ssFolderSortBy == 'assignment' && ssFolderSortDescend == 'true'}">
						<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" 
						value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
					</c:if>
					<c:if test="${ ssFolderSortBy == 'assignment' && ssFolderSortDescend == 'false'}">
						<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" 
						value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
					</c:if>
			      </div>
			    </a>
			
			</th>
			<th>
				<a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
					name="operation" value="save_folder_sort_info"/><ssf:param 
					name="binderId" value="${ssBinder.id}"/><ssf:param 
					name="ssFolderSortBy" value="completed"/><c:choose><c:when 
					test="${ ssFolderSortBy == 'completed' && ssFolderSortDescend == 'false'}"><ssf:param 
					name="ssFolderSortDescend" value="true"/></c:when><c:otherwise><ssf:param 
					name="ssFolderSortDescend" value="false"/></c:otherwise></c:choose><ssf:param 
			    	name="ssFolderModeType" value="${ssCurrentFolderModeType}"/><ssf:param
					name="ssTaskFilterType" value="${ssTaskFilterType}"/></ssf:url>"
				
				<c:choose>
				  <c:when test="${ ssFolderSortBy == 'completed' && ssFolderSortDescend == 'false'}">
				  	<ssf:title tag="title.sort.by.column.desc">
				  		<ssf:param name="value" value='<%= NLT.get("task.done") %>' />
				  	</ssf:title>
				  </c:when>
				  <c:otherwise>
				  	<ssf:title tag="title.sort.by.column.asc">
				  		<ssf:param name="value" value='<%= NLT.get("task.done") %>' />
				  	</ssf:title>
				  </c:otherwise>
				</c:choose>	
				 >

			      <div >
			        <span><ssf:nlt tag="task.done"/></span>
			    	<c:if test="${ ssFolderSortBy == 'completed' && ssFolderSortDescend == 'true'}">
						<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" 
						value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
					</c:if>
					<c:if test="${ ssFolderSortBy == 'completed' && ssFolderSortDescend == 'false'}">
						<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" 
						value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
					</c:if>
			      </div>
			    </a>
			</th>
			<c:if test="${ !empty ssCurrentFolderModeType && ssCurrentFolderModeType == 'VIRTUAL' }">
				<th>
					<a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
						name="operation" value="save_folder_sort_info"/><ssf:param 
						name="binderId" value="${ssBinder.id}"/><ssf:param 
						name="ssFolderSortBy" value="location"/><c:choose><c:when 
						test="${ ssFolderSortBy == 'location' && ssFolderSortDescend == 'false'}"><ssf:param 
						name="ssFolderSortDescend" value="true"/></c:when><c:otherwise><ssf:param 
						name="ssFolderSortDescend" value="false"/></c:otherwise></c:choose><ssf:param 
				    	name="ssFolderModeType" value="${ssCurrentFolderModeType}"/><ssf:param
						name="ssTaskFilterType" value="${ssTaskFilterType}"/></ssf:url>"
					
					<c:choose>
					  <c:when test="${ ssFolderSortBy == 'location' && ssFolderSortDescend == 'false'}">
					  	<ssf:title tag="title.sort.by.column.desc">
					  		<ssf:param name="value" value='<%= NLT.get("task.location") %>' />
					  	</ssf:title>
					  </c:when>
					  <c:otherwise>
					  	<ssf:title tag="title.sort.by.column.asc">
					  		<ssf:param name="value" value='<%= NLT.get("task.location") %>' />
					  	</ssf:title>
					  </c:otherwise>
					</c:choose>	
					 >
	
				      <div >
				        <span><ssf:nlt tag="task.location"/></span>
				    	<c:if test="${ ssFolderSortBy == 'location' && ssFolderSortDescend == 'true'}">
							<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" 
							value='<%= NLT.get("folder.column.Location") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
						</c:if>
						<c:if test="${ ssFolderSortBy == 'location' && ssFolderSortDescend == 'false'}">
							<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" 
							value='<%= NLT.get("folder.column.Location") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
						</c:if>
				      </div>
				    </a>
				</th>
			</c:if>
		</tr>
	</thead>
	<tbody>
		<c:if test="${empty ssFolderEntries}">
			<tr><td colspan="6"><jsp:include page="/WEB-INF/jsp/forum/view_no_entries.jsp" /></td></tr>
		</c:if>
		<c:if test="${!empty ssFolderEntries}">
			<c:forEach var="entry" items="${ssFolderEntries}" >
				<jsp:useBean id="entry" type="java.util.HashMap" />

				<%
					boolean overdue = org.kablink.teaming.util.DateComparer.isOverdue((Date)entry.get("start_end#LogicalEndDate"));
				%>
				<c:set var="overdue" value="<%= overdue %>"/>
				<c:if test="${entry.status == 's3' || entry.status == 's4'}">
					<c:set var="overdue" value="false" />
				</c:if>
				
				<script type="text/javascript">
					myTasks_${renderResponse.namespace}.addTask({"id" : ${entry._docId},
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
						id="ss_tasks_${renderResponse.namespace}_${entry._docId}_title">
						<c:set var="isDashboard" value="yes"/>
						
						   	<% if (!ssSeenMap.checkIfSeen(entry)) { %>
						    
							<a id="ss_sunburstDiv${entry._binderId}_${entry._docId}" href="javascript: ;" 
							 title="<ssf:nlt tag="sunburst.click"/>"
							 onClick="ss_hideSunburst('${entry._docId}', '${entry._binderId}');return false;"
							><span 
							 style="display:${ss_sunburstVisibilityHide};"
							 id="ss_sunburstShow${renderResponse.namespace}" 
							 class="ss_fineprint">
							 	<img src="<html:rootPath/>images/pics/discussion/sunburst.png" align="absmiddle" border="0" <ssf:alt tag="alt.new"/> />
							 </span>
							</a>
										    
							<% } %>
						
							<ssf:titleLink 
								action="view_folder_entry" 
								entryId="${entry._docId}" binderId="${entry._binderId}" 
								entityType="${entry._entityType}"  
								namespace="${renderResponse.namespace}" >		
							
								<ssf:param name="url" useBody="true">
									<ssf:url crawlable="true" adapter="true" 
									portletName="ss_forum" folderId="${entry._binderId}" 
									action="view_folder_entry" entryId="${entry._docId}" actionUrl="true" />
								</ssf:param>
							
								<c:out value="${entry.title}" escapeXml="false"/>
							</ssf:titleLink>
					</td>
					<td class="ss_iconsContainer" id="ss_tasks_${renderResponse.namespace}_${entry._docId}_priority">
						<c:if test="${! empty entry.priority}">
							<ssf:ifEntryModifiable entryMap = "${entry}">
								<c:forEach var="prio" items="${entry.ssEntryDefinitionElementData.priority.values}"><a <c:if test="${entry.priority == prio.key}">href="javascript:// ;" class="ss_taskPriority"</c:if><c:if test="${entry.priority != prio.key}">href="javascript: myTasks_${renderResponse.namespace}.changePriority(${entry._docId}, '${prio.key}');" class="ss_taskPriority ss_taskPriority_${prio.key}_u"</c:if> ><img <c:if test="${entry.priority == prio.key}"> src="<html:imagesPath/>icons/prio_${prio.key}.png" </c:if><c:if test="${entry.priority != prio.key}">src="<html:imagesPath/>pics/1pix.gif"</c:if>	alt="${prio.value}" title="${prio.value}"></a></c:forEach>
							</ssf:ifEntryModifiable>
							<ssf:ifEntryModifiable entryMap = "${entry}" invert="true">
								<c:forEach var="prio" items="${entry.ssEntryDefinitionElementData.priority.values}"><a href="javascript:// ;" <c:if test="${entry.priority == prio.key}"> class="ss_taskPriority"</c:if><c:if test="${entry.priority != prio.key}"> class="ss_taskPriority ss_taskPriority_${prio.key}_u"</c:if> ><img <c:if test="${entry.priority == prio.key}"> src="<html:imagesPath/>icons/prio_${prio.key}.png" </c:if><c:if test="${entry.priority != prio.key}">src="<html:imagesPath/>pics/1pix.gif"</c:if>	alt="${prio.value}" title="${prio.value}"></a></c:forEach>
							</ssf:ifEntryModifiable>
						</c:if>
					</td>
					<td class="ss_due<c:if test="${overdue}"> ss_overdue</c:if>" id="ss_tasks_${renderResponse.namespace}_${entry._docId}_due">
						<c:if test="${!empty entry['start_end#LogicalEndDate']}">
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
						</c:if>
					</td>
					<td class="ss_iconsContainer" id="ss_tasks_${renderResponse.namespace}_${entry._docId}_status">
						<ssf:ifEntryModifiable entryMap = "${entry}">
							<c:if test="${! empty entry.status}"><c:forEach var="status" items="${entry.ssEntryDefinitionElementData.status.values}"><a <c:if test="${entry.status == status.key}">href="javascript: //" class="ss_taskStatus" </c:if><c:if test="${entry.status != status.key}">href="javascript:  myTasks_${renderResponse.namespace}.changeStatus(${entry._docId}, '${status.key}');" class="ss_taskStatus ss_taskStatus_${status.key}_u" </c:if>><img <c:if test="${entry.status == status.key}"> src="<html:imagesPath/>icons/status_${status.key}.gif" </c:if><c:if test="${entry.status != status.key}"> src="<html:imagesPath/>pics/1pix.gif" </c:if> alt="${status.value}" title="${status.value}"></a></c:forEach></c:if>
						</ssf:ifEntryModifiable>
						<ssf:ifEntryModifiable entryMap = "${entry}" invert="true">
							<c:if test="${! empty entry.status}"><c:forEach var="status" items="${entry.ssEntryDefinitionElementData.status.values}"><a href="javascript: //" <c:if test="${entry.status == status.key}"> class="ss_taskStatus" </c:if><c:if test="${entry.status != status.key}"> class="ss_taskStatus ss_taskStatus_${status.key}_u" </c:if>><img <c:if test="${entry.status == status.key}"> src="<html:imagesPath/>icons/status_${status.key}.gif" </c:if><c:if test="${entry.status != status.key}"> src="<html:imagesPath/>pics/1pix.gif" </c:if> alt="${status.value}" title="${status.value}"></a></c:forEach></c:if>
						</ssf:ifEntryModifiable>
					</td>
					<td <c:if test="${ !empty ssCurrentFolderModeType && ssCurrentFolderModeType == 'VIRTUAL' }">class="ss_assigned_virtual"</c:if><c:if test="${ empty ssCurrentFolderModeType || ssCurrentFolderModeType != 'VIRTUAL' }">class="ss_assigned"</c:if>>
						<c:set var="assignment" value='<%= org.kablink.teaming.util.ResolveIds.getPrincipals(entry.get("assignment"), false) %>' />
						<c:if test="${!empty assignment}">
							<ul>
								<c:forEach var="assigned" items="${assignment}">
									<li><ssf:showUser user="${assigned}"/></li>
								</c:forEach>
							</ul>
						</c:if>
						
						<c:set var="assignment_groups" value='<%= org.kablink.teaming.util.ResolveIds.getPrincipals(entry.get("assignment_groups"), false) %>' />
						<c:if test="${!empty assignment_groups}">
							<ul>
								<c:forEach var="assigned" items="${assignment_groups}">
									<li><ssf:showGroup group="${assigned}"/></li>
								</c:forEach>
							</ul>
						</c:if>		
						
						<c:set var="assignment_teams" value='<%= org.kablink.teaming.util.ResolveIds.getBinders(entry.get("assignment_teams")) %>' />
						<c:if test="${!empty assignment_teams}">
							<ul>
								<c:forEach var="assigned" items="${assignment_teams}">
									<li><ssf:showTeam team="${assigned}"/></li>
								</c:forEach>
							</ul>
						</c:if>									
					
					<td id="ss_tasks_${renderResponse.namespace}_${entry._docId}_completed">
						<c:if test="${! empty entry.completed}">
							<ssf:ifEntryModifiable entryMap = "${entry}">
								<ssf:progressBar currentValue="${entry.completed}" 
									valuesMap="${entry.ssEntryDefinitionElementData.completed.values}" 
									namespace="${renderResponse.namespace}" 
									entryId="${entry._docId}}" />
							</ssf:ifEntryModifiable>

							<ssf:ifEntryModifiable entryMap = "${entry}" invert="true">
								<ssf:progressBar currentValue="${entry.completed}" 
									valuesMap="${entry.ssEntryDefinitionElementData.completed.values}" 
									namespace="${renderResponse.namespace}" 
									entryId="${entry._docId}}"
									readOnly="true" />
							</ssf:ifEntryModifiable>
						</c:if>
					</td>
					<c:if test="${ !empty ssCurrentFolderModeType && ssCurrentFolderModeType == 'VIRTUAL' }">
						<td id="ss_tasks_${renderResponse.namespace}_${entry._docId}_location">
							<c:set var="path" value=""/>
				
							<c:if test="${!empty ssFolderList}">
								<c:forEach var="folder" items="${ssFolderList}">
									<c:if test="${folder.id == entry._binderId}">
										<c:set var="path" value="${folder}"/>
										<c:set var="title" value="${folder.title} (${folder.parentWorkArea.title})"/>
									</c:if>
								</c:forEach>
							</c:if>
				
							<c:if test="${!empty path}">
					    		<a href="javascript: ;"
									onclick="return ss_gotoPermalink('${entry._binderId}', '${entry._binderId}', 'folder', '${ss_namespace}', 'yes');"
									title="${path}"
									><span class="ss_bold">${title}</span></a>
							</c:if>
						</td>
					</c:if>
				</tr>
			
			</c:forEach>
		</c:if>
	</tbody>
</table>

<c:set var="ss_useDefaultViewEntryPopup" value="1" scope="request"/>
