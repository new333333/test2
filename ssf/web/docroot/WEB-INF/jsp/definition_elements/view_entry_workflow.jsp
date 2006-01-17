<% // View entry workflow %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssConfigDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ssConfigJspStyle" type="String" scope="request" />
<jsp:useBean id="ssDefinitionEntry" type="com.sitescape.ef.domain.Entry" scope="request" />

<c:if test="${!empty ssDefinitionEntry.workflowStates}">
<div class="ss_workflow">
<table class="ss_style">
<%
	String column1 = "<span><b>" + NLT.get("workflow", "Workflow") + ":</b></span>";
%>
<c:set var="lastWorkflowTitle" value=""/>
<c:forEach var="workflow" items="${ssDefinitionEntry.workflowStates}">
  <c:if test="${!empty workflow.definition}">
  <c:set var="workflowTitle" value="${workflow.definition.title}"/>
  <tr>
    <td valign="top"><%= column1 %></td>
    <c:if test="${workflowTitle != lastWorkflowTitle}">
      <td valign="top"><c:out value="${workflow.definition.title}"/></td>
    </c:if>
    <c:if test="${workflowTitle == lastWorkflowTitle}">
      <td valign="top"><c:out value=""/></td>
    </c:if>
    <td>&nbsp;&nbsp;</td>
    <td valign="top">${workflow.stateCaption}</td>
    <td>&nbsp;&nbsp;&nbsp;</td>
    <c:if test="${!empty workflow.manualTransitions}">
      <td valign="top" align="right"><b><ssf:nlt tag="workflow.transitionTo" 
        text="Transition to:"/></b></td>
      <td valign="top">
	  <form class="ss_style" method="post" action="" style="display:inline;">
	  <input type="hidden" name="tokenId" value="${workflow.tokenId}">
	  <select name="toState">
	  <c:forEach var="transition" items="${workflow.manualTransitions}">
	    <option value="${transition.key}">${transition.value}</option>
	  </c:forEach>
	  </select><input type="submit" name="changeStateBtn" 
	   value="<ssf:nlt tag="button.ok" text="OK"/>">
	  </form>
	  </td>
	</c:if>
	<c:if test="${empty workflow.manualTransitions}">
	  <td></td><td></td>
	</c:if>
  </tr>
  <%	column1 = "";  %>
  <c:set var="lastWorkflowTitle" value="${workflow.definition.title}"/>
  </c:if>
</c:forEach>

</table>
</div>
</c:if>
