<% // View entry attachments %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssDefinitionEntry" type="com.sitescape.ef.domain.Entry" scope="request" />

<c:if test="${!empty ssDefinitionEntry.fileAttachments}">
<div class="ss_entryContent">
<span class="ss_labelLeft"><c:out value="${property_caption}"/></span>

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
