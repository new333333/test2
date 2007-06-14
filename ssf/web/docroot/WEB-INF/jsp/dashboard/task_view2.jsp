<%
// The dashboard "search" component
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
  //this is used by penlets and portlets
 
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<c:set var="hitCount" value="0"/>
<c:set var="componentId" value="${ssComponentId}"/>
<c:if test="${empty ssComponentId}">
<c:set var="componentId" value="${ssDashboard.ssComponentId}" />
</c:if>
<c:set var="portletNamespace" value=""/>
<ssf:ifnotadapter>
<c:set var="portletNamespace" value="${renderResponse.namespace}"/>
</ssf:ifnotadapter>

<style>
a.ss_taskPriority_least_u:hover img, a.ss_taskPriority_low_u:hover img, a.ss_taskPriority_medium_u:hover img, a.ss_taskPriority_high_u:hover img, a.ss_taskPriority_critical_u:hover img {
	background-position: left top; 
}
a.ss_taskStatus_inProcess_u:hover img, a.ss_taskStatus_needsAction_u:hover img, a.ss_taskStatus_cancelled_u:hover img, a.ss_taskStatus_completed_u:hover img {
	background-position: left top; 
}
</style>

<div class="ss_blog">
<table class="ss_tasks_list">
	<tr>
		<th><ssf:nlt tag="task.dueDate"/></th>
		<th><ssf:nlt tag="task.priority"/></th>
		<th><ssf:nlt tag="task.title"/></th>
		<th><ssf:nlt tag="task.status"/></th>
		<th><ssf:nlt tag="task.assigned"/></th>
		<th><ssf:nlt tag="task.done"/></th>		
	</tr>

<c:forEach var="entry" items="${ssDashboard.beans[componentId].ssSearchFormData.searchResults}" >
	<jsp:useBean id="entry" type="java.util.HashMap" />
  <c:set var="hitCount" value="${hitCount + 1}"/>

	<tr>
		<td>
			<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
			      value="<%= (java.util.Date)entry.get("start_end#EndDate") %>" type="both" 
				  dateStyle="medium" timeStyle="short" />
		</td>
		<td>
			<c:if test="${! empty entry.priority}">
				<c:forEach var="prio" items="${ssEntryDefinitionElementData.priority.values}">
					<a href="javascript: // ;"
						<c:if test="${entry.priority == prio.key}">
							class="ss_taskPriority"
						</c:if>
						<c:if test="${entry.priority != prio.key}">
							class="ss_taskPriority ss_taskPriority_${prio.key}_u"
						</c:if>
						><img 
						<c:if test="${entry.priority == prio.key}">
						src="<html:imagesPath/>icons/prio_${prio.key}.gif"
						</c:if>
						<c:if test="${entry.priority != prio.key}">
						src="<html:imagesPath/>pics/1pix.gif"
						</c:if>
						alt="${prio.value}" title="${prio.value}"
					>
				</c:forEach>
			</c:if>
		</td>
		<td>
			<span class="ss_entryTitle ss_normalprint">
				<c:set var="isDashboard" value="yes"/>
				
			<ssf:menuLink 
				displayDiv="false" entryId="${entry._docId}" binderId="${entry._binderId}" 
				entityType="${entry._entityType}" imageId='menuimg_${entry._docId}_${renderResponse.namespace}' 
		    	menuDivId="ss_emd_${renderResponse.namespace}_${componentId}" linkMenuObjIdx="${renderResponse.namespace}_${componentId}" 
				namespace="${renderResponse.namespace}" entryCallbackRoutine="${showEntryCallbackRoutine}" 
				useBinderFunction="no" isDashboard="${isDashboard}" dashboardType="${ssDashboard.scope}">

				
					<ssf:param name="url" useBody="true">
						<ssf:url adapter="true" portletName="ss_forum" folderId="${entry._binderId}" 
						action="view_folder_entry" entryId="${entry._docId}" actionUrl="true" />
					</ssf:param>
				
					<c:out value="${entry.title}" escapeXml="false"/>
				</ssf:menuLink>
			</span>
		</td>
		<td>
			<c:if test="${! empty entry.status}">
				<c:forEach var="status" items="${ssEntryDefinitionElementData.status.values}">
					<a href="javascript: //"
						<c:if test="${entry.status == status.key}">
							class="ss_taskStatus"
						</c:if>
						<c:if test="${entry.status != status.key}">
							class="ss_taskStatus ss_taskStatus_${status.key}_u"
						</c:if>>
						<img 
						<c:if test="${entry.status == status.key}">
							src="<html:imagesPath/>icons/status_${status.key}.gif" 
						</c:if>
						<c:if test="${entry.status != status.key}">
							src="<html:imagesPath/>pics/1pix.gif" 
						</c:if>
						alt="${status.value}" title="${status.value}">
					</a>
				</c:forEach>
				
			</c:if>
		</td>
		<td>
			<ul>
				<c:forEach var="assigned" items="<%= com.sitescape.team.util.ResolveIds.getPrincipals(entry.get("assignment")) %>">
					<li><ssf:showUser user="${assigned}" /></li>
				</c:forEach>
			</ul>
		</td>
		<td>
			<c:if test="${! empty entry.completed}">
			<div class="ss_c_">
				<div class="ss_${entry.completed} ss_smallprint">
					<c:forEach var="done" items="${ssEntryDefinitionElementData.completed.values}">
						<c:if test="${entry.completed == done.key}">
							${done.value}
						</c:if>
					</c:forEach>
				</div>
			<div>
			<div class="ss_clear_float"></div>
			</c:if>			
		</td>		
	</tr>

</c:forEach>
</table>
			
</div>

<div>
  <table width="100%">
   <tr>
    <td valign="top">
<c:if test="${hitCount > 0}">
      <span class="ss_light ss_fineprint">
	    [<ssf:nlt tag="folder.Results">
	    <ssf:param name="value" value="${ss_pageNumber * ss_pageSize + 1}"/>
	    <ssf:param name="value" value="${ss_pageNumber * ss_pageSize + hitCount}"/>
	    <ssf:param name="value" value="${ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchCount}"/>
	    </ssf:nlt>]
	  </span>
</c:if>
<c:if test="${hitCount == 0}">
    <span class="ss_light ss_fineprint">
	  [<ssf:nlt tag="dashboard.noEntriesFound"/>]
	</span>
</c:if>
	</td>
	<td align="right">
	<c:if test="${ssDashboard.scope != 'portlet'}">
		<c:set var="binderId" value="${ssBinder.id}"/>
	</c:if>
	<c:if test="${ssDashboard.scope == 'portlet'}">
		<c:set var="binderId" value="${ssDashboardPortlet.id}"/>
	</c:if>
	  <c:if test="${ss_pageNumber > 0}">
	    <span>
	      <a onClick="ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber - 1}', '${ss_pageSize}', '${ss_divId}', '${componentId}', 'task'); return false;"
	        href="#" >&lt;&lt;&lt;&nbsp;<ssf:nlt tag="general.previousPage"/></a>&nbsp;&nbsp;&nbsp;
	    </span>
	  </c:if>
	  <c:if test="${(ss_pageNumber * ss_pageSize + hitCount) < ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchCount}">
	    <span>&nbsp;&nbsp;
	      <a onClick="ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber + 1}', '${ss_pageSize}', '${ss_divId}', '${componentId}', 'task'); return false;"
	        href="#" ><ssf:nlt tag="general.nextPage"/>&nbsp;&gt;&gt;&gt;</a>
	    </span>
	  </c:if>
    </td>
   </tr>
  </table>
</div>

<ssf:menuLink displayDiv="true" menuDivId="ss_emd_${renderResponse.namespace}_${componentId}" 
	linkMenuObjIdx="${renderResponse.namespace}_${componentId}" 
	namespace="${renderResponse.namespace}" dashboardType="${ssDashboard.scope}">
</ssf:menuLink>	
