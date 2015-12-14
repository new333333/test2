<%
/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<div id="ss_para" class="ss_paraC">
	<div id="ss_today">
		<div id="ss_tasks_para" style="padding-left: 0px;">

			<div id="ss_title" class="ss_pt_title ss_green">
				<ssf:nlt tag="relevance.tasksDue"/>
				<span class="col-nextback-but">
					<c:if test="${ss_myTasksPage > '0'}">
					<a href="javascript: ;" 
					  onclick="ss_showDashboardPage('${ssBinder.id}', '${ssRDCurrentTab}', 'tasks', '${ss_myTasksPage}', 'previous', 'ss_dashboardTasks', '${ss_relevanceDashboardNamespace}');return false;">
					<img align="absmiddle" src="<html:imagesPath/>pics/sym_arrow_left_.png" 
					  title="<ssf:nlt tag="general.previousPage"/>" <ssf:alt/>/>
					</a>
					</c:if>
					<c:if test="${empty ss_myTasksPage || ss_myTasksPage <= '0'}">
					<img align="absmiddle" src="<html:imagesPath/>pics/sym_arrow_left_g.png" <ssf:alt/>/>
					</c:if>
					<c:if test="${!empty ss_myTasks}">
					<a href="javascript: ;" 
					  onclick="ss_showDashboardPage('${ssBinder.id}', '${ssRDCurrentTab}', 'tasks', '${ss_myTasksPage}', 'next', 'ss_dashboardTasks', '${ss_relevanceDashboardNamespace}');return false;">
					<img align="absmiddle" src="<html:imagesPath/>pics/sym_arrow_right_.png"
					  title="<ssf:nlt tag="general.nextPage"/>" <ssf:alt/>/>
					</a>
					</c:if>
					<c:if test="${empty ss_myTasks}">
					<img align="absmiddle" src="<html:imagesPath/>pics/sym_arrow_right_g.png" <ssf:alt/>/>
					</c:if>
				</span>
			</div>
			


			<c:forEach var="entry" items="${ss_myTasks}">
			<jsp:useBean id="entry" type="java.util.HashMap" />
			
			<div class="item">
				<div>
					<c:set var="isDashboard" value="yes"/>				
					<ssf:titleLink hrefClass="ss_link_2"
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
				</div>
			  	<div class="item-sub margintop2" style="margin-left:5px">
					<div>
						<c:choose>
							<c:when test="${!empty entry['start_end#LogicalEndDate']}">
								<ssf:nlt tag="task.dueDate"/>:
								<c:choose>
									<c:when test="${!empty entry['start_end#TimeZoneID']}">
										<span class="ss_prioValue"><fmt:formatDate 
												timeZone="${ssUser.timeZone.ID}"
												value="${entry['start_end#LogicalEndDate']}" type="date" 
												dateStyle="medium" /></span>
									</c:when>	
									<c:otherwise>
										<span class="ss_prioValue"><fmt:formatDate 
												timeZone="GMT"
												value="${entry['start_end#LogicalEndDate']}" type="date" 
												dateStyle="medium"/></span>
									</c:otherwise>
								</c:choose>
								<c:if test="${overdue}">
									<span class="ss_overdue"><ssf:nlt tag="milestone.overdue"/></span>
								</c:if>
							</c:when>
						</c:choose>				  
					</div>
					<div>
						<c:if test="${! empty entry.priority}">
						  <c:forEach var="prio" items="${entry.ssEntryDefinitionElementData.priority.values}">
								<c:if test="${entry.priority == prio.key}">
									<ssf:nlt tag="relevance.taskPriority">
										<ssf:param name="value" useBody="true">
											<span class="ss_prioValue">${prio.value}</span>
										</ssf:param>
									</ssf:nlt>	
								</c:if>
						  </c:forEach>
						</c:if>
					</div>
					<div>
						<c:if test="${! empty entry.status}">
						  <c:forEach var="status" items="${entry.ssEntryDefinitionElementData.status.values}">
							  <c:if test="${entry.status == status.key}">
								<ssf:nlt tag="relevance.taskStatus">
								  <ssf:param name="value" useBody="true">
									<span class="ss_prioValue">${status.value}</span>
								  </ssf:param>
								</ssf:nlt>
							  </c:if>
							</c:forEach>
						  </c:if>
					</div>
					<c:set var="path" value=""/>
					<c:if test="${!empty ss_myTasksFolders[entry._binderId]}">
						<c:set var="path" value="${ss_myTasksFolders[entry._binderId]}"/>
						<c:set var="title" value="${ss_myTasksFolders[entry._binderId].parentBinder.title} // ${ss_myTasksFolders[entry._binderId].title}"/>
					</c:if>
	
					<c:if test="${!empty path}">
						<span>
							<c:set var="isDashboard" value="yes"/>
							<c:if test="${!empty path}">
								<a href="javascript: ;"
									onclick="return ss_gotoPermalink('${entry._binderId}', '${entry._binderId}', 'folder', '${ss_namespace}', 'yes');"
									title="${path}"
									><span class="s_prioValue">${title}</a>
							</c:if>
						</span>
					</c:if>
				</div>	
			</div>
										
			  </c:forEach>
			  <c:if test="${empty ss_myTasks && ss_pageNumber > '0'}">
				<span class="ss_italic"><ssf:nlt tag="whatsnew.noMoreEntriesFound"/></span>
			  </c:if>
		</div><!-- end of para -->
	</div><!-- end of today -->
</div><!-- end of ss_para -->
