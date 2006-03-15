<% // View entry attachments %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<c:if test="${!empty ssDefinitionEntry.fileAttachments}">
<div class="ss_entryContent">
<span class="ss_labelLeft"><c:out value="${property_caption}"/></span>
<br>
<span>
<c:forEach var="selection" items="${ssDefinitionEntry.fileAttachments}" >
<a target="_blank" 
  href="<ssf:url 
    webPath="viewFile"
    folderId="${ssDefinitionEntry.parentBinder.id}"
    entryId="${ssDefinitionEntry.id}" >
    <ssf:param name="fileId" value="${selection.id}"/>
    </ssf:url>"><c:out value="${selection.fileItem.name} "/></a><br>
 </c:forEach>
 </span>
</div>
</c:if>
