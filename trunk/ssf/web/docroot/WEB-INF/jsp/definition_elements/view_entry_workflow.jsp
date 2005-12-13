<% // View entry workflow %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssConfigDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ssConfigJspStyle" type="String" scope="request" />
<jsp:useBean id="ssDefinitionEntry" type="com.sitescape.ef.domain.Entry" scope="request" />

<c:if test="${!empty ssDefinitionEntry.workflowStates}">
<div class="ss_workflow">
<table>
<%
	String column1 = "<span><b>" + NLT.get("workflow", "Workflow") + ":</b></span>";
%>
<c:forEach var="workflow" items="${ssDefinitionEntry.workflowStates}">
<c:if test="${!empty workflow.definition}">
<tr><td valign="top"><%= column1 %></td>
<td valign="top"><c:out value="${workflow.definition.title}"/> - ${workflow.stateCaption}</td>
<td>&nbsp;&nbsp;&nbsp;</td>
<td valign="top" align="right"><b>Transition to:</b></td>
<td valign="top">
<form method="post" action="" style="display:inline;">
<input type="hidden" name="tokenId" value="${workflow.tokenId}">
<select name="toState">
<c:forEach var="transition" items="${workflow.manualTransitions}">
<option value="${transition.key}">${transition.value}</option>
</c:forEach>
</select><input type="submit" name="changeStateBtn" 
 value="<ssf:nlt tag="button.ok" text="OK"/>">
</form>
</td>
</tr>
<%	column1 = "";  %>
</c:if>
</c:forEach>

</table>
</div>
</c:if>
