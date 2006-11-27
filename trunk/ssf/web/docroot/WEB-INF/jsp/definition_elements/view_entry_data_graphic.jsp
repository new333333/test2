<% //Graphic view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<c:if test="${!empty ssDefinitionEntry.customAttributes[property_name]}">
<c:set var="selections" value="${ssDefinitionEntry.customAttributes[property_name].value}" />
<c:forEach var="selection" items="${selections}">
<img border="0" src="<ssf:url 
    webPath="viewFile"
    folderId="${ssDefinitionEntry.parentBinder.id}"
    entryId="${ssDefinitionEntry.id}" >
    <ssf:param name="fileId" value="${selection.id}"/>
    <ssf:param name="viewType" value="scaled"/>
    </ssf:url>" alt="${property_caption}" />
</c:forEach>
</c:if>
