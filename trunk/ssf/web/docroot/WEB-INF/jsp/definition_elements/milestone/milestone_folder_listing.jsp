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
<% //View the listing part of a milestone folder %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<table class="ss_milestones_list">
	<tr>
		<th><ssf:nlt tag="milestone.title"/></th>
		<th><ssf:nlt tag="milestone.responsible"/></th>
		<th><ssf:nlt tag="milestone.tasks"/></th>
		<th><ssf:nlt tag="milestone.status"/></th>
		<th><ssf:nlt tag="milestone.dueDate"/></th>
	</tr>
	<c:forEach var="entry" items="${ssFolderEntries}" >
		<jsp:useBean id="entry" type="java.util.HashMap" />
		
		<c:set var="tdClass" value="" />
		<c:if test="${entry.status == 'completed'}">
			<c:set var="tdClass" value="class='ss_completed'" />
		</c:if>
		<%
			boolean overdue = com.sitescape.team.util.DateComparer.isOverdueDate((Date)entry.get("due_date"));
		%>
		<c:set var="overdue" value="<%= overdue %>"/>
		<c:if test="${overdue && entry.status != 'completed'}">
			<c:set var="tdClass" value="class='ss_overdue'" />
		</c:if>
		
		<tr>
			<td>
				<span class="ss_entryTitle ss_normalprint">
					<ssf:menuLink displayDiv="false" action="view_folder_entry" adapter="true" entryId="${entry._docId}" 
					binderId="${entry._binderId}" entityType="${entry._entityType}" 
					imageId='menuimg_${entry._docId}_${renderResponse.namespace}' 
				    menuDivId="ss_emd_${renderResponse.namespace}"
					linkMenuObjIdx="${renderResponse.namespace}" 
					namespace="${renderResponse.namespace}"
					entryCallbackRoutine="${showEntryCallbackRoutine}">
					
						<ssf:param name="url" useBody="true">
							<ssf:url adapter="true" portletName="ss_forum" folderId="${entry._binderId}" 
							action="view_folder_entry" entryId="${entry._docId}" actionUrl="true" />
						</ssf:param>
					
						<c:out value="${entry.title}" escapeXml="false"/>
					</ssf:menuLink>
				</span>
			</td>
			<td>
				<ul class="ss_nobullet">
				<c:forEach var="principal" items="<%= com.sitescape.team.util.ResolveIds.getPrincipals(entry.get("responsible")) %>" >
					<li><ssf:showUser user="${principal}" /></li>
				</c:forEach>
				</ul>
			</td>
			<td>
				<ul class="ss_nobullet">
					<c:forEach var="selection" items="<%= com.sitescape.team.util.ResolveIds.getBinderTitlesAndIcons(entry.get("tasks")) %>" varStatus="status">
						<li><a href="<ssf:url 
			  				folderId="${selection.key}" 
			  				action="view_folder_listing">
			  				<ssf:param name="binderId" value="${selection.key}"/>
			  				<ssf:param name="newTab" value="1"/>
			  				</ssf:url>"><c:out value="${selection.value.title}" escapeXml="false"/></a>
			  			</li>
					</c:forEach>
				</ul>
			</td>
			<td ${tdClass}>
				<ssf:nlt tag="__milestone_status_${entry.status}"/>
			</td>
			<td ${tdClass}>
				<fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry.due_date}" type="date" dateStyle="medium" />
				<c:if test="${overdue && entry.status != 'completed'}">
					<ssf:nlt tag="milestone.overdue"/>
				</c:if>
			</td>
		</tr>
	</c:forEach>
</table>

<ssf:menuLink displayDiv="true" menuDivId="ss_emd_${renderResponse.namespace}" linkMenuObjIdx="${renderResponse.namespace}" 
	namespace="${renderResponse.namespace}">
</ssf:menuLink>