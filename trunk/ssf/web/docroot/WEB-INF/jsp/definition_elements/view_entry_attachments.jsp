<% // View entry attachments %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<c:if test="${!empty ssDefinitionEntry.fileAttachments}">
<div class="ss_entryContent">
<br/>
<span class="ss_labelLeft"><c:out value="${property_caption}"/></span>
<br>
<span>
<c:forEach var="selection" items="${ssDefinitionEntry.fileAttachments}" >
<a target="_blank" style="text-decoration: none;"
  href="<ssf:url 
    webPath="viewFile"
    folderId="${ssDefinitionEntry.parentBinder.id}"
    entryId="${ssDefinitionEntry.id}" >
    <ssf:param name="fileId" value="${selection.id}"/>
    </ssf:url>"><c:out value="${selection.fileItem.name} "/>
</a>
<ssf:ifSupportsEditInPlace relativeFilePath="${selection.fileItem.name}">
<a style="text-decoration: none;"
	href="<ssf:ssfsAttachmentUrl 
		binder="${ssDefinitionEntry.parentBinder}"
		entity="${ssDefinitionEntry}"
		fileAttachment="${selection}"/>">
		<span class="ss_edit_button ss_smallprint">[<ssf:nlt tag="Edit"/>]</span></a>
</ssf:ifSupportsEditInPlace>
<br>
<c:forEach var="fileVersion" items="${selection.fileVersions}">
&nbsp;&nbsp;&nbsp;<a target="_blank" style="text-decoration: none;"
  href="<ssf:url 
    webPath="viewFile"
    folderId="${ssDefinitionEntry.parentBinder.id}"
    entryId="${ssDefinitionEntry.id}" >
    <ssf:param name="fileId" value="${selection.id}"/>
    <ssf:param name="versionId" value="${fileVersion.id}"/>
    </ssf:url>">v${fileVersion.versionNumber}</a>
<br>
</c:forEach>
</c:forEach>
</span>
</div>
</c:if>
