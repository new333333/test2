<% //View the listing in the search view format %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<table class="ss_blog" width="100%" border="0">

	<c:forEach var="entry" items="${ssFolderEntries}" >
		<jsp:useBean id="entry" type="java.util.HashMap" />
		<c:set var="entryBinderId" value="${entry._binderId}"/>
		<c:if test="${entry._entityType == 'folder' || entry._entityType == 'workspace'}">
		  <c:set var="entryBinderId" value="${entry._docId}"/>
		</c:if>
		
		<tr>
			<td class="ss_searchviewContainer">xxx${entry._entityType}xxx
				<a href="<ssf:url adapter="true" portletName="ss_forum" folderId="${entry._binderId}" 
					action="view_folder_entry" entryId="${entry._docId}" actionUrl="true" />" 
					onClick="ss_loadEntry(this,'<c:out value="${entry._docId}"/>');return false;" ><span class="ss_entryTitle">
					<c:out value="${entry._docNum}" escapeXml="false"/>.&nbsp;<c:out value="${entry.title}" escapeXml="false"/>
					</span>
				</a>
			</td>
		</tr>

		<c:if test="${!empty entry._desc}">
			<tr>
				<td class="ss_searchviewContainer">
					<ssf:markup type="view" binderId="${entryBinderId}" entryId="${entry._docId}">
						<ssf:textFormat textContent="${entry._desc}" formatAction="limitedDescription" textMaxWords="60" />
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
		</tr>				
	</c:forEach>
</table>