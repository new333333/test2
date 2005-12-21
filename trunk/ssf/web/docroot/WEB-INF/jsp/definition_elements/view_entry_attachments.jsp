<% // View entry attachments %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssEntry" type="com.sitescape.ef.domain.Entry" scope="request" />

<c:if test="${!empty ssEntry.fileAttachments}">
<div class="ss_entryContent">
<span class="ss_labelLeft"><c:out value="${property_caption}"/></span>

<span class="ss_content">
<c:forEach var="selection" items="${ssEntry.fileAttachments}" >
<a target="_blank" 
  href="<ssf:url 
    webPath="viewFile"
    folderId="${ssEntry.parentBinder.id}"
    entryId="${ssEntry.id}" >
    <ssf:param name="fileId" value="${selection.id}"/>
    </ssf:url>"><c:out value="${selection.fileItem.name} (${selection.repositoryServiceName})"/></a><br>
 </c:forEach>
 </span>
</div>
</c:if>
