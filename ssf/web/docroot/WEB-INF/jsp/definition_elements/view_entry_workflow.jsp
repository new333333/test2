<% // View entry workflow %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<c:if test="${!empty ssDefinitionEntry.workflowStates}">
<div class="ss_workflow">
<table>
<%
	String column1 = "<span><b>" + NLT.get("workflow", "Workflow") + ":</b></span>";
%>
<c:set var="lastWorkflowTitle" value=""/>
<c:forEach var="workflow" items="${ssDefinitionEntry.workflowStates}">
  <c:if test="${!empty workflow.definition}">
    <c:if test="${empty workflow.threadName}">
	  <tr>
	    <td valign="top"><%= column1 %></td>
	    <c:if test="${workflowTitle != lastWorkflowTitle}">
	      <td valign="top"><c:out value="${workflow.definition.title}"/></td>
	    </c:if>
	    <c:if test="${workflowTitle == lastWorkflowTitle}">
	      <td valign="top"><c:out value=""/></td>
	    </c:if>
	    <td>&nbsp;&nbsp;</td>
	    <td></td>
	    <td>&nbsp;&nbsp;</td>
	    <td valign="top">${ssWorkflowCaptions[workflow.id]}</td>
	    <td>&nbsp;&nbsp;&nbsp;</td>
	    <c:if test="${!empty ssWorkflowTransitions[workflow.id]}">
	      <td valign="top" align="right"><b><ssf:nlt tag="workflow.transitionTo" 
	        text="Transition to:"/></b></td>
	      <td valign="top">
		  <form class="ss_style ss_form" method="post" action="" style="display:inline;">
		  <input type="hidden" name="tokenId" value="${workflow.id}">
		  <input type="hidden" name="replyId" value="${ssDefinitionEntry.id}">
		  <select name="toState">
		  <c:forEach var="transition" items="${ssWorkflowTransitions[workflow.id]}">
		    <option value="${transition.key}">${transition.value}</option>
		  </c:forEach>
		  </select><input type="submit" class="ss_submit" name="changeStateBtn" 
		   value="<ssf:nlt tag="button.ok" text="OK"/>">
		  </form>
		  </td>
		</c:if>
		<c:if test="${empty ssWorkflowTransitions[workflow.id]}">
		  <td></td><td></td>
		</c:if>
	    <c:if test="${!empty ssWorkflowQuestions[workflow.id]}">
	      <td valign="top" align="right">&nbsp;&nbsp;&nbsp;</td>
	      <td valign="top">
		  <c:forEach var="question" items="${ssWorkflowQuestions[workflow.id]}">
		    <form class="ss_style ss_form" method="post" action="" style="display:inline;">
		    <input type="hidden" name="tokenId" value="${workflow.id}">
		    <input type="hidden" name="replyId" value="${ssDefinitionEntry.id}">
		    <span class="ss_bold"><c:out value="${question.value.ssWorkflowQuestionText}"/></span><br/>
		    <select name="${question.key}">
		    <c:forEach var="response" items="${question.value.ssWorkflowQuestionResponses}">
		      <option value="${response.key}">${response.value}</option>
		    </c:forEach>
		    </select><input type="submit" class="ss_submit" name="respondBtn" 
		     value="<ssf:nlt tag="button.ok" text="OK"/>">
		    </form>
		  </c:forEach>
		  </td>
		</c:if>
		<c:if test="${empty ssWorkflowQuestions[workflow.id]}">
		  <td></td><td></td>
		</c:if>
	  </tr>
	  <%	column1 = "";  %>
	  <c:set var="lastWorkflowTitle" value="${workflow.definition.title}"/>

      <c:forEach var="workflow2" items="${ssDefinitionEntry.workflowStates}">
        <% //??? This next check needs to be fixed if multiple workflow porcesses are allowed %>
        <c:if test="${workflow2.definition.id == workflow.definition.id}">
          <c:if test="${!empty workflow2.threadName}">
			  <tr>
			    <td valign="top"></td>
			    <td valign="top"></td>
			    <td>&nbsp;&nbsp;</td>
			    <td valign="top"><c:out value="${workflow2.threadName}"/></td>
			    <td>&nbsp;&nbsp;</td>
			    <td valign="top">${ssWorkflowCaptions[workflow2.id]}</td>
			    <td>&nbsp;&nbsp;&nbsp;</td>
			    <c:if test="${!empty ssWorkflowTransitions[workflow2.id]}">
			      <td valign="top" align="right"><b><ssf:nlt tag="workflow2.transitionTo" 
			        text="Transition to:"/></b></td>
			      <td valign="top">
				  <form class="ss_style ss_form" method="post" action="" style="display:inline;">
				  <input type="hidden" name="tokenId" value="${workflow2.id}">
				  <input type="hidden" name="replyId" value="${ssDefinitionEntry.id}">
				  <select name="toState">
				  <c:forEach var="transition" items="${ssWorkflowTransitions[workflow2.id]}">
				    <option value="${transition.key}">${transition.value}</option>
				  </c:forEach>
				  </select><input type="submit" class="ss_submit" name="changeStateBtn" 
				   value="<ssf:nlt tag="button.ok" text="OK"/>">
				  </form>
				  </td>
				</c:if>
				<c:if test="${empty ssWorkflowTransitions[workflow2.id]}">
				  <td></td><td></td>
				</c:if>
			  </tr>
          </c:if>
        </c:if>
      </c:forEach>
	  <c:if test="${!empty workflow.description}">
	    <tr>
	      <td colspan="8">
			<div class="ss_entryContent">
			 <span><c:out value="${workflow.description}" escapeXml="false"/></span>
			</div>
	      </td>
	    </tr>
	  </c:if>
    </c:if>
  <c:set var="workflowTitle" value="${workflow.definition.title}"/>
  </c:if>
</c:forEach>
</table>
</div>
</c:if>
