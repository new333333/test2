<% // View entry attachments %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssFolderEntry" type="com.sitescape.ef.domain.FolderEntry" scope="request" />

<br><b><c:out value="${property_caption}"/></b><br>

<c:forEach var="selection" items="${ssFolderEntry.fileAttachments}" >
<a class="bg" target="_blank" 
  href="<ssf:url 
    webPath="viewFile"
    folderId="${ssFolderEntry.parentFolder.id}"
    entryId="${ssFolderEntry.id}" >
    <ssf:param name="fileId" value="${selection.id}"/>
    </ssf:url>"><c:out value="${selection.fileItem.name}"/></a><br>
 </c:forEach>
 
<br>
