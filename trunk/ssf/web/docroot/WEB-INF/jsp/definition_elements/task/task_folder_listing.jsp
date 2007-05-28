<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<% //View the listing part of a tasks folder %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<table class="ss_tasks_list" id="ss_tasks_list_<portlet:namespace/>">
	<thead>
		<tr>
			<th><ssf:nlt tag="task.dueDate"/></th>
			<th><ssf:nlt tag="task.priority"/></th>
			<th><ssf:nlt tag="task.title"/></th>
			<th><ssf:nlt tag="task.status"/></th>
			<th><ssf:nlt tag="task.assigned"/></th>
			<th><ssf:nlt tag="task.done"/></th>		
		</tr>
	</thead>
	<tbody>
	</tbody>
</table>
<script type="text/javascript">
	var myTasks_<portlet:namespace/> = new ss_tasks ('ss_tasks_list_<portlet:namespace/>', ${ssFolder.id}, '<portlet:namespace/>');
</script>

<ssf:menuLink displayDiv="true" menuDivId="ss_emd_${renderResponse.namespace}" linkMenuObjIdx="${renderResponse.namespace}" 
	namespace="${renderResponse.namespace}">
</ssf:menuLink>