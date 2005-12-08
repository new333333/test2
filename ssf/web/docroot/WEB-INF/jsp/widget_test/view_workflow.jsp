<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
Current State:<c:out value="${workflowState}"/><br/>
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

