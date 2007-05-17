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
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<table width="100%">
	<tr>
		<th><ssf:nlt tag="task.status"/></th>
		<th><ssf:nlt tag="task.priority"/></th>
		<th><ssf:nlt tag="task.title"/></th>
		<th><ssf:nlt tag="task.creator"/></th>
		<th><ssf:nlt tag="task.creationDate"/></th>
	</tr>
<c:forEach var="entry" items="${ssFolderEntries}" >
	<tr>
		<td>
			<c:if test="${! empty entry.status}">
				<c:out value="${entry.status}"/>
			</c:if>
		</td>
		<td>
			<c:if test="${! empty entry.priority_select}">
				<c:out value="${entry.priority_select}"/>
			</c:if>
		</td>
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
		<td><c:out value="${entry._principal.title}"/></td>
		<td>
			<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
			      value="${entry._creationDate}" type="both" 
				  timeStyle="short" dateStyle="short" />
		</td>
	</tr>
</c:forEach>
</table>

<ssf:menuLink displayDiv="true" menuDivId="ss_emd_${renderResponse.namespace}" linkMenuObjIdx="${renderResponse.namespace}" 
	namespace="${renderResponse.namespace}">
</ssf:menuLink>