<% //Workspace title view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<h3>
 <a style="text-decoration: none;" href="<ssf:url 
    folderId="${ssDefinitionEntry.id}" 
    action="view_workspace"/>">
<c:if test="${empty ssDefinitionEntry.title}">
    <span class="ss_gray">--no title--</span>
    </c:if><c:out value="${ssDefinitionEntry.title}"/></a>
</h3>
