<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

Current State:<c:out value="${workflowState}"/><br/>
				<a href="<portlet:actionURL windowState="maximized">
						<portlet:param name="action" value="workflow" />
						<portlet:param name="operation" value="create" />
						<portlet:param name="workflowId" value="${workflowId}" />
					</portlet:actionURL>">Create new definition</a><br>

				<a href="<portlet:actionURL windowState="maximized">
						<portlet:param name="action" value="workflow" />
						<portlet:param name="operation" value="new" />
						<portlet:param name="workflowId" value="${workflowId}" />
					</portlet:actionURL>">Create new instance</a><br>

				<a href="<portlet:actionURL windowState="maximized">
						<portlet:param name="action" value="workflow" />
						<portlet:param name="operation" value="proceed" />
						<portlet:param name="processId" value="${processId}" />
					</portlet:actionURL>">Move to next state</a><br>

				<a href="<portlet:actionURL windowState="maximized">
						<portlet:param name="action" value="workflow" />
						<portlet:param name="operation" value="orphan" />
						<portlet:param name="processId" value="${processId}" />
					</portlet:actionURL>">Move to orphan state</a><br>

				<a href="<portlet:actionURL windowState="maximized">
						<portlet:param name="action" value="workflow" />
						<portlet:param name="operation" value="listDef" />
						<portlet:param name="workflowId" value="${workflowId}" />
						<portlet:param name="processId" value="${processId}" />
					</portlet:actionURL>">List definitions</a><br>

				<a href="<portlet:actionURL windowState="maximized">
						<portlet:param name="action" value="workflow" />
						<portlet:param name="operation" value="listInst" />
						<portlet:param name="workflowId" value="${workflowId}" />
						<portlet:param name="processId" value="${processId}" />
					</portlet:actionURL>">List instances</a><br>
					
<c:if test="${!empty definitions}">
<br>
Definitions:
<br>
<c:forEach var="definition" items="${definitions}">
<c:out value="${definition.name}"/> 
 (Id: <c:out value="${definition.id}"/>, 
 Version: <c:out value="${definition.version}"/>)<br>
 Nodes:<br>
 <c:forEach var="nodeList" items="${nodeMap[definition.id].value}">
 <c:out value="${nodeList}"/><br>
 </c:forEach>
 <br><br>
</c:forEach>
</c:if>
<c:if test="${!empty instances}">
<br>
Instances:
<br>
<c:forEach var="instance" items="${instances}">
Id: <c:out value="${instance.id}"/>, 
 Definition: <c:out value="${instance.processDefinition.name}"/>
 (Id: <c:out value="${instance.processDefinition.id}"/>, 
 Version: <c:out value="${instance.processDefinition.version}"/>)<br>
</c:forEach>
</c:if>



