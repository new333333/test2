<% // View entry attachments %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<c:if test="${!empty ssDefinitionEntry.fileAttachments}">
<div class="ss_entryContent">
<br/>
<span class="ss_labelLeft"><c:out value="${property_caption}"/></span>
<br>
<span>
<c:forEach var="selection" items="${ssDefinitionEntry.fileAttachments}" >
<a style="text-decoration: none;"
  href="<ssf:url 
    webPath="viewFile"
    folderId="${ssDefinitionEntry.parentBinder.id}"
    entryId="${ssDefinitionEntry.id}" >
    <ssf:param name="fileId" value="${selection.id}"/>
    </ssf:url>"><c:out value="${selection.fileItem.name} "/></a>
    
<ssf:ifSupportsEditInPlace relativeFilePath="${selection.fileItem.name}">
<a style="text-decoration: none;"
	href="<ssf:ssfsAttachmentUrl 
		binder="${ssDefinitionEntry.parentBinder}"
		entity="${ssDefinitionEntry}"
		fileAttachment="${selection}"/>">
		<span class="ss_edit_button ss_smallprint">[<ssf:nlt tag="Edit"/>]</span></a>
</ssf:ifSupportsEditInPlace>
<br>
<c:set var="versionCount" value="0"/>
<c:forEach var="fileVersion" items="${selection.fileVersions}">
<c:if test="${versionCount > 0}">
&nbsp;&nbsp;&nbsp;<a style="text-decoration: none;"
  href="<ssf:url 
    webPath="viewFile"
    folderId="${ssDefinitionEntry.parentBinder.id}"
    entryId="${ssDefinitionEntry.id}" >
    <ssf:param name="fileId" value="${selection.id}"/>
    <ssf:param name="versionId" value="${fileVersion.id}"/>
    </ssf:url>">v${fileVersion.versionNumber}</a>
<br>
</c:if>
<c:set var="versionCount" value="${versionCount + 1}"/>
</c:forEach>
</c:forEach>
</span>
</div>
</c:if>
