<% //Workspace title view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<h3>
 <a style="text-decoration: none;" href="<ssf:url 
    folderId="${ssBinder.id}" 
    action="view_workspace"/>">
<c:if test="${empty ssBinder.title}">
    <span class="ss_gray">--no title--</span>
    </c:if><c:out value="${ssBinder.title}"/></a>
</h3>
