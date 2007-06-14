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
<% //View the listing part of a survey folder %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<table class="ss_surveys_list">
	<tr>
		<th><ssf:nlt tag="survey.title"/></th>
		<th><ssf:nlt tag="survey.creator"/></th>
		<th><ssf:nlt tag="survey.dueDate"/></th>
	</tr>
<c:forEach var="entry" items="${ssFolderEntries}" >
	<jsp:useBean id="entry" type="java.util.HashMap" />
	<% boolean overdue = com.sitescape.team.util.DateComparer.isOverdue((Date)entry.get("due_date")); %>
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
			<ssf:showUser user="${entry._principal}" />
		</td>
		<td ${tdClass}>
			<fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry.due_date}" type="both" dateStyle="medium" timeStyle="short" />
			<c:if test="${overdue}">
				<ssf:nlt tag="survey.overdue"/>
			</c:if>		
		</td>
	</tr>
</c:forEach>
</table>

<ssf:menuLink displayDiv="true" menuDivId="ss_emd_${renderResponse.namespace}" linkMenuObjIdx="${renderResponse.namespace}" 
	namespace="${renderResponse.namespace}">
</ssf:menuLink>