<% // View entry attachments %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<c:if test="${!empty ssDefinitionEntry.fileAttachments}">
<div class="ss_entryContent">
<br/>
<span class="ss_labelLeft"><c:out value="${property_caption}"/></span>
<br>
<c:forEach var="selection" items="${ssDefinitionEntry.fileAttachments}" >
<div style="margin:0px; padding:0px;">
<a style="text-decoration: none;" href="<ssf:url 
    webPath="viewFile"
    folderId="${ssDefinitionEntry.parentBinder.id}"
    entryId="${ssDefinitionEntry.id}" >
    <ssf:param name="fileId" value="${selection.id}"/>
    </ssf:url>" 
    onClick="return ss_launchUrlInNewWindow(this, '${selection.fileItem.name}');"
    ><c:out value="${selection.fileItem.name} "/></a>
    
<ssf:ifSupportsEditInPlace relativeFilePath="${selection.fileItem.name}">
<a style="text-decoration: none;"
	href="<ssf:ssfsAttachmentUrl 
		binder="${ssDefinitionEntry.parentBinder}"
		entity="${ssDefinitionEntry}"
		fileAttachment="${selection}"/>">
		<span class="ss_edit_button ss_smallprint">[<ssf:nlt tag="Edit"/>]</span></a>
</ssf:ifSupportsEditInPlace>
<div class="ss_indent_medium">
<table class="ss_compact20">
<tr>
<td class="ss_compact20"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
     value="${selection.modification.date}" type="both" 
	 timeStyle="short" dateStyle="short" /></td>
<td class="ss_compact20"><span class="ss_smallprint">(${selection.fileItem.lengthKB}KB)</span></td>
</tr>
</table>
</div>
</div>
<c:set var="versionCount" value="0"/>
<c:forEach var="fileVersion" items="${selection.fileVersions}">
<c:set var="versionCount" value="${versionCount + 1}"/>
</c:forEach>
<c:if test="${!empty selection.fileVersions && versionCount > 1}">
<div class="ss_indent_medium">
<span class="ss_bold"><ssf:nlt tag="entry.PreviousVersions"/></span>
<br>
<c:set var="versionCount" value="0"/>
<table class="ss_compact20">
<c:forEach var="fileVersion" items="${selection.fileVersions}">
<c:if test="${versionCount > 0}">
<tr>
<td class="ss_compact20"><a style="text-decoration: none;"
  href="<ssf:url 
    webPath="viewFile"
    folderId="${ssDefinitionEntry.parentBinder.id}"
    entryId="${ssDefinitionEntry.id}" >
    <ssf:param name="fileId" value="${selection.id}"/>
    <ssf:param name="versionId" value="${fileVersion.id}"/>
    </ssf:url>"><ssf:nlt tag="entry.version"/> ${fileVersion.versionNumber}</a></td>
<td class="ss_compact20"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
     value="${fileVersion.modification.date}" type="both" 
	 timeStyle="short" dateStyle="short" /></td>
<td class="ss_compact20"><span class="ss_smallprint">(${fileVersion.fileItem.lengthKB}KB)</span></td>
</tr>
</c:if>
<c:set var="versionCount" value="${versionCount + 1}"/>
</c:forEach>
</table>
</div>
<br>
</c:if>
</c:forEach>
</div>
</c:if>
