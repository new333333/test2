<% //File view %>

<c:if test="${!empty ssDefinitionEntry.customAttributes[property_name]}">
<div class="ss_entryContent">
<span class="ss_bold"><c:out value="${property_caption}" /></span>

<span>
<c:set var="selections" value="${ssDefinitionEntry.customAttributes[property_name].value}" />
<c:forEach var="selection" items="${selections}">
<a target="_blank" 
  href="<ssf:url 
    webPath="viewFile"
    folderId="${ssDefinitionEntry.parentBinder.id}"
    entryId="${ssDefinitionEntry.id}" >
    <ssf:param name="fileId" value="${selection.id}"/>
    </ssf:url>"><c:out value="${selection.fileItem.name}"/>
</a>
<ssf:ifSupportsEditInPlace relativeFilePath="${selection.fileItem.name}">
<a 
	href="<ssf:ssfsFileUrl 
		binder="${ssDefinitionEntry.parentBinder}"
		entity="${ssDefinitionEntry}"
		elemName="${property_name}"
		fileAttachment="${selection}"/>"><b><font color="#FF0000">Edit</font></b>
</a>
</ssf:ifSupportsEditInPlace>
<br>
</c:forEach>
</span>
</div>
</c:if>
