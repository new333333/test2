<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<div class="ss_portlet">
	<table border="0" cellpadding="0" cellspacing="0" width="95%">
	<tr>
		<td>
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
						<portlet:param name="workflowId" value="${workflowId}" />
					</portlet:actionURL>">Move to next state</a><br>

				<a href="<portlet:actionURL windowState="maximized">
						<portlet:param name="action" value="workflow" />
						<portlet:param name="operation" value="orphan" />
						<portlet:param name="processId" value="${processId}" />
						<portlet:param name="workflowId" value="${workflowId}" />
					</portlet:actionURL>">Move to orphan state</a><br>

				<a href="<portlet:actionURL windowState="maximized">
						<portlet:param name="action" value="workflow" />
						<portlet:param name="operation" value="deleteNode" />
						<portlet:param name="processId" value="${processId}" />
						<portlet:param name="workflowId" value="${workflowId}" />
			  		</portlet:actionURL>">Delete node (deletes orphan or orphan3)</a><br>
			  		
				<a href="<portlet:actionURL windowState="maximized">
						<portlet:param name="action" value="workflow" />
						<portlet:param name="operation" value="addNode" />
						<portlet:param name="processId" value="${processId}" />
						<portlet:param name="workflowId" value="${workflowId}" />
			  		</portlet:actionURL>">Add node (adds orphan)</a><br>
			  		
				<a href="<portlet:actionURL windowState="maximized">
						<portlet:param name="action" value="workflow" />
						<portlet:param name="operation" value="modifyNodeName" />
						<portlet:param name="processId" value="${processId}" />
						<portlet:param name="workflowId" value="${workflowId}" />
			  		</portlet:actionURL>">Modify Node Name(orphan->orphan3) (orphan3->orphan)</a><br>
			  		
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
					
				<a href="<portlet:actionURL windowState="maximized">
						<portlet:param name="action" value="workflow" />
						<portlet:param name="operation" value="deleteAll" />
						<portlet:param name="processId" value="${processId}" />
						<portlet:param name="workflowId" value="${workflowId}" />
					</portlet:actionURL>">Delete all</a><br>
					
<c:if test="${!empty definitions}">
<br>
<hr>
<br>
<b>Definitions:</b>
<br>
<c:forEach var="definition" items="${definitions}">
	<c:out value="${definition.name}"/> 
	 (Id: <c:out value="${definition.id}"/>, 
	 Version: <c:out value="${definition.version}"/>)<br>
	 Nodes:<br>
	 <c:forEach var="nodeList" items="${definition.nodes}">
		 <c:out value="${nodeList.name}"/><br>
		 <c:forEach var="transition" items="${nodeList.leavingTransitions}">
			 &nbsp;&nbsp;&nbsp;to: <c:out value="${transition.to.name}"/><br>
		 </c:forEach>
	 </c:forEach>
	 <br><br>
</c:forEach>
</c:if>
<c:if test="${!empty instances}">
<br>
<hr>
<br>
<b>Instances:</b>
<br>
<c:forEach var="instance" items="${instances}">
Id: <c:out value="${instance.id}"/>, 
 Definition: <c:out value="${instance.processDefinition.name}"/>
 (Id: <c:out value="${instance.processDefinition.id}"/>, 
 Version: <c:out value="${instance.processDefinition.version}"/>)<br>
</c:forEach>
</c:if>

<c:if test="${!empty definitionXml}">
<br>
<hr>
<br>
<b>Definition xml:</b>
<br>
<pre>
<c:out value="${definitionXml}"/>
</pre>
</c:if>


</td>
</tr>
</table>
</div>

