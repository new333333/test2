<% //File view %>
<jsp:useBean id="ssEntry" type="com.sitescape.ef.domain.Entry" scope="request" />

<c:if test="${!empty ssEntry.customAttributes[property_name]}">
<div class="ss_entryContent">
<span class="ss_contentbold"><c:out value="${property_caption}" /></span>

<span class="ss_content">
<c:set var="selections" value="${ssEntry.customAttributes[property_name].value}" />
<c:forEach var="selection" items="${selections}">
<a target="_blank" 
  href="<ssf:url 
    webPath="viewFile"
    folderId="${ssEntry.parentBinder.id}"
    entryId="${ssEntry.id}" >
    <ssf:param name="fileId" value="${selection.id}"/>
    </ssf:url>"><c:out value="${selection.fileItem.name}"/></a><br>
</c:forEach>
</span>
</div>
</c:if>
