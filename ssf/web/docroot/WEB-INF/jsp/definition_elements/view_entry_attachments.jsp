<% // View entry attachments %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssFolderEntry" type="com.sitescape.ef.domain.FolderEntry" scope="request" />

<c:if test="${!empty ssFolderEntry.fileAttachments}">
<div class="ss_entryContent">
<span class="ss_labelLeft"><c:out value="${property_caption}"/></span>

<span class="ss_content">
<c:forEach var="selection" items="${ssFolderEntry.fileAttachments}" >
<a target="_blank" 
  href="<ssf:url 
    webPath="viewFile"
    folderId="${ssFolderEntry.parentFolder.id}"
    entryId="${ssFolderEntry.id}" >
    <ssf:param name="fileId" value="${selection.id}"/>
    </ssf:url>"><c:out value="${selection.fileItem.name}"/></a><br>
 </c:forEach>
 </span>
</div>
</c:if>
