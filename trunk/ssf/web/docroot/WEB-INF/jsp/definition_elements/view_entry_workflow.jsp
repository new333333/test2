<% // View entry workflow %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssConfigDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ssConfigJspStyle" type="String" scope="request" />

<c:if test="${!empty ssDefinitionEntry.workflowStates}">
<table>
<c:forEach var="workflow" items="${ssDefinitionEntry.workflowStates}">
<tr><td>Workflow state: <c:out value="${workflow.state}"/></td></tr>
</c:forEach>
</table>
</c:if>
