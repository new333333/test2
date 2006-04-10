<% //Graphic view %>

<c:if test="${!empty ssBinder.customAttributes[property_name]}">
<c:set var="selections" value="${ssBinder.customAttributes[property_name].value}" />
<c:forEach var="selection" items="${selections}">
<img border="0" src="<ssf:url 
    webPath="viewFile"
    folderId="${ssBinder.id}" >
    <ssf:param name="fileId" value="${selection.id}"/>
    <ssf:param name="viewType" value="scaled"/>
    </ssf:url>" alt="${property_caption}" />
</c:forEach>
</c:if>
