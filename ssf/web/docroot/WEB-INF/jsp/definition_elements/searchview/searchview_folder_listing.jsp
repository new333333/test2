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
<% //View the listing in the search view format %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<%
	Map entriesSeen = new HashMap();
%>
<table class="ss_blog" width="100%" border="0">
	<c:forEach var="entry" items="${ssFolderEntries}" >
		<jsp:useBean id="entry" type="java.util.HashMap" />
		<%
			if (!entriesSeen.containsKey(entry.get("_docId"))) {
		%>
		<c:set var="entryBinderId" value="${entry._binderId}"/>
		<c:set var="entryDocId" value="${entry._docId}"/>	
		<c:if test="${entry._entityType == 'folder' || entry._entityType == 'workspace'}">
		  <c:set var="entryBinderId" value="${entry._docId}"/>
		  <c:set var="entryDocId" value=""/>
		</c:if>
		
		<tr>
			<td class="ss_searchviewContainer">
				<span class="ss_entryTitle">
					<c:out value="${entry._docNum}" escapeXml="false"/>.
					<ssf:menuLink 
						displayDiv="false" action="view_folder_entry" 
						adapter="true" entryId="${entry._docId}" 
						folderId="${entry._binderId}" binderId="${entry._binderId}" 
						entityType="${entry._entityType}" imageId='menuimg_${entry._docId}_${renderResponse.namespace}' 
				    	menuDivId="ss_emd_${renderResponse.namespace}" linkMenuObjIdx="${renderResponse.namespace}" 
						namespace="${renderResponse.namespace}" entryCallbackRoutine="${showEntryCallbackRoutine}">
						<ssf:param name="url" useBody="true">
							<ssf:url adapter="true" portletName="ss_forum" folderId="${entry._binderId}" 
							action="view_folder_entry" entryId="${entry._docId}" actionUrl="true" />
						</ssf:param>
						<c:out value="${entry.title}" escapeXml="false"/>
					</ssf:menuLink>
				</span>
			</td>
		</tr>

		<c:if test="${!empty entry._desc}">
			<tr>
				<td class="ss_searchviewContainer">
					<ssf:markup type="view" binderId="${entryBinderId}" entryId="${entryDocId}">
						<ssf:textFormat formatAction="limitedDescription" textMaxWords="30">
							${entry._desc}
						</ssf:textFormat>
					</ssf:markup>
				</td>
			</tr>
		</c:if>
		
		<c:if test="${!empty entry._workflowStateCaption}">
			<tr>
				<td class="ss_searchviewContainer">
					<span class="ss_entrySignature">
						<ssf:nlt tag="entry.workflowState" />: <c:out value="${entry._workflowStateCaption}" />
					</span>
				</td>
			</tr>
		</c:if>
				
		<tr>
			<td class="ss_searchviewContainer">
				<table width="100%">
				<tr>
					<td width="50%">
						<span class="ss_entrySignature">
							<ssf:nlt tag="entry.createdBy" />: <ssf:showUser user="<%=(User)entry.get("_principal")%>" />
						</span>
					</td>
					<td width="50%">
						<span class="ss_entrySignature">
							<ssf:nlt tag="entry.modifiedDate" />: <fmt:formatDate timeZone="${fileEntry._principal.timeZone.ID}"
						 		value="${entry._modificationDate}" type="both" 
						 		timeStyle="long" dateStyle="long" />
						</span>
					</td>
				</tr>
				</table>
			</td>
		</tr>

		<tr>
			<td class="ss_searchviewContainer">
				<div class="ss_line"></div>
			</td>
		</tr>		<%
			}
			entriesSeen.put(entry.get("_docId"), "1");
		%>
	</c:forEach>
</table>

<ssf:menuLink displayDiv="true" menuDivId="ss_emd_${renderResponse.namespace}" linkMenuObjIdx="${renderResponse.namespace}" 
	namespace="${renderResponse.namespace}">
</ssf:menuLink>