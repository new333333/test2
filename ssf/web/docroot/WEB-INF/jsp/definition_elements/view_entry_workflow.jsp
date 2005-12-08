<% // View entry workflow %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssConfigDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ssConfigJspStyle" type="String" scope="request" />

<h1 class="ss_entryTitle"><c:out value="${property_caption}"/></h1>

<c:forEach var="workflow" items="ssDefinitionEntry.workflowStates">
Workflow state: <c:out value="${workflow.state}"/>
<br>
<br>
</c:forEach>