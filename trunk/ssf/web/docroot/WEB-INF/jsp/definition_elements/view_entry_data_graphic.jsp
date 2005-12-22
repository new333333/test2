<% //Graphic view %>

<c:if test="${!empty ssEntry.customAttributes[property_name]}">
<c:set var="selections" value="${ssEntry.customAttributes[property_name].value}" />
<c:set var="selectionCount" value="first"/>
<c:forEach var="selection" items="${selections}">
<c:if test="${selectionCount == 'first'}">
<img border="0" src="<ssf:url 
    webPath="viewFile"
    folderId="${ssEntry.parentBinder.id}"
    entryId="${ssEntry.id}" >
    <ssf:param name="fileId" value="${selection.id}"/>
    </ssf:url>" alt="${property_caption}" />
</c:if>
<c:set var="selectionCount" value="notfirst"/>
</c:forEach>
</c:if>
