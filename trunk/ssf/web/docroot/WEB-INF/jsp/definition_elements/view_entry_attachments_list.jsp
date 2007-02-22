<%@ page import="com.sitescape.util.BrowserSniffer" %>
<div id="${ss_viewEntryAttachmentDivId}">
	<%
	boolean isIECheck = BrowserSniffer.is_ie(request);
	String strBrowserType = "nonie";
	if (isIECheck) strBrowserType = "ie";
	%>
	<table width="100%" border="0">
	<tbody>
	<tr>
	<td width="100%">

	<c:forEach var="selection" items="${ssDefinitionEntry.fileAttachments}" >
	
		<table width="100%" border="0">
		<tbody>
			<tr>
				<td width="60%" align="left">
					<a style="text-decoration: none;" href="<ssf:url 
					    webPath="viewFile"
					    folderId="${ssDefinitionEntry.parentBinder.id}"
					    entryId="${ssDefinitionEntry.id}" >
					    <ssf:param name="fileId" value="${selection.id}"/>
					    </ssf:url>" 
					<c:if test="${ssConfigJspStyle != 'mail'}">    
					    onClick="return ss_launchUrlInNewWindow(this, '${selection.fileItem.name}');"
					</c:if>
					     ><c:out value="${selection.fileItem.name} "/></a>
				</td>
				<td width="40%" align="left">
				
					<c:if test="${ssConfigJspStyle != 'mail'}">
						<c:if test="${ssEntryAttachmentAllowEdit == 'true'}">
							<ssf:ifSupportsEditInPlace relativeFilePath="${selection.fileItem.name}" browserType="<%=strBrowserType%>">
								<% if (isIECheck) { %>
									<c:choose>
									<c:when test="${ssEntryAttachmentEditTypeForIE == 'applet'}">
										<c:if test="${ssIsAppletSupported == 'true'}">
											<a href="javascript: ;" onClick="javascript:ss_openWebDAVFile${ssDefinitionEntry.id}${ss_namespace_attach}('<ssf:ssfsInternalAttachmentUrl 
													binder="${ssDefinitionEntry.parentBinder}"
													entity="${ssDefinitionEntry}"
													fileAttachment="${selection}"/>'); return false;"><span class="ss_edit_button ss_smallprint">[<ssf:nlt tag="Edit"/>]</span></a>
										</c:if>
									</c:when>
									<c:otherwise>
										<a href="<ssf:ssfsInternalAttachmentUrl 
												binder="${ssDefinitionEntry.parentBinder}"
												entity="${ssDefinitionEntry}"
												fileAttachment="${selection}"/>">
												<span class="ss_edit_button ss_smallprint">[<ssf:nlt tag="Edit"/>]</span></a>
									</c:otherwise>
									</c:choose>							
								<% } else { %>
									<c:choose>
									<c:when test="${ssEntryAttachmentEditTypeForNonIE == 'webdav'}">
										<a href="<ssf:ssfsInternalAttachmentUrl 
												binder="${ssDefinitionEntry.parentBinder}"
												entity="${ssDefinitionEntry}"
												fileAttachment="${selection}"/>">
												<span class="ss_edit_button ss_smallprint">[<ssf:nlt tag="Edit"/>]</span></a>
									</c:when>
									<c:otherwise>
										<c:if test="${ssIsAppletSupported == 'true'}">
											<a href="javascript: ;" onClick="javascript:ss_openWebDAVFile${ssDefinitionEntry.id}${ss_namespace_attach}('<ssf:ssfsInternalAttachmentUrl 
													binder="${ssDefinitionEntry.parentBinder}"
													entity="${ssDefinitionEntry}"
													fileAttachment="${selection}"/>'); return false;"><span class="ss_edit_button ss_smallprint">[<ssf:nlt tag="Edit"/>]</span></a>
										</c:if>
									</c:otherwise>
									</c:choose>							
								<% } %>
							</ssf:ifSupportsEditInPlace>						
						</c:if>
					</c:if>
					
				</td>
			</tr>
		</tbody>
		</table>
	
		<table class="ss_compact20" width="100%" border="0">
		<tbody>
			<tr>
				<td class="ss_compact20" width="30%"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				     value="${selection.modification.date}" type="both" 
					 timeStyle="short" dateStyle="short" /></td>
				<td class="ss_compact20" width="5%"><span class="ss_smallprint">(${selection.fileItem.lengthKB}KB)</span></td>
				<ssf:ifSupportsViewAsHtml relativeFilePath="${selection.fileItem.name}" browserType="<%=strBrowserType%>">
					<td class="ss_compact20" width="65%"><span class="ss_smallprint">
						<a target="_blank" style="text-decoration: none;" href="<ssf:url 
						    webPath="viewFile"
						    folderId="${ssDefinitionEntry.parentBinder.id}"
					   	 	entryId="${ssDefinitionEntry.id}" >
					    	<ssf:param name="fileId" value="${selection.id}"/>
					    	<ssf:param name="viewType" value="html"/>
					    	</ssf:url>" >[<ssf:nlt tag="entry.HTML" />]</a>
						</span>
					</td>
				</ssf:ifSupportsViewAsHtml>
			</tr>
		</tbody>
		</table>
			
		<c:set var="versionCount" value="0"/>
		
		<c:forEach var="fileVersion" items="${selection.fileVersions}">
			<c:set var="versionCount" value="${versionCount + 1}"/>
		</c:forEach>

		<c:if test="${!empty selection.fileVersions && versionCount > 1}">
			<div class="ss_indent_medium" width="100%">
				<span class="ss_bold"><ssf:nlt tag="entry.PreviousVersions"/></span>
				<br />
				<c:set var="versionCount" value="0"/>
				<table class="ss_compact20" width="100%">
				<tbody>
				<c:forEach var="fileVersion" items="${selection.fileVersions}">
					<c:if test="${versionCount > 0}">
						<tr>
						<td class="ss_compact20" width="20%"><a style="text-decoration: none;"
						  href="<ssf:url 
						    webPath="viewFile"
						    folderId="${ssDefinitionEntry.parentBinder.id}"
						    entryId="${ssDefinitionEntry.id}" >
						    <ssf:param name="fileId" value="${selection.id}"/>
						    <ssf:param name="versionId" value="${fileVersion.id}"/>
						    </ssf:url>"><ssf:nlt tag="entry.version"/> ${fileVersion.versionNumber}</a></td>
						<td class="ss_compact20" width="30%"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
						     value="${fileVersion.modification.date}" type="both" 
							 timeStyle="short" dateStyle="short" /></td>
						<td class="ss_compact20" width="5%"><span class="ss_smallprint">(${fileVersion.fileItem.lengthKB}KB)</span></td>
						<td class="ss_compact20" width="45%"><span class="ss_smallprint">
							</span>
						</td>
						</tr>
					</c:if>
					<c:set var="versionCount" value="${versionCount + 1}"/>
				</c:forEach>
				</tbody>
				</table>
			</div>
		</c:if>	
		<br />	
	
	</c:forEach>

	</td>
	</tr>
	</tbody>
	</table>

</div>
