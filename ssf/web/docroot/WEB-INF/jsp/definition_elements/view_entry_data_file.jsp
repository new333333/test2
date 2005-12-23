<% //File view %>

<c:if test="${!empty ssDefinitionEntry.customAttributes[property_name]}">
<div class="ss_entryContent">
<span class="ss_contentbold"><c:out value="${property_caption}" /></span>

<span class="ss_content">
<c:set var="selections" value="${ssDefinitionEntry.customAttributes[property_name].value}" />
<c:forEach var="selection" items="${selections}">
<a target="_blank" 
  href="<ssf:url 
    webPath="viewFile"
    folderId="${ssDefinitionEntry.parentBinder.id}"
    entryId="${ssDefinitionEntry.id}" >
    <ssf:param name="fileId" value="${selection.id}"/>
    </ssf:url>"><c:out value="${selection.fileItem.name}"/></a><br>

</c:forEach>
</span>
</div>
</c:if>
