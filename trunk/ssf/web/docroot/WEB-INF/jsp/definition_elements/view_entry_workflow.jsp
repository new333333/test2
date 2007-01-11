<% // View entry workflow %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<c:if test="${!empty ssDefinitionEntry.workflowStates}">
<c:if test="${ssConfigJspStyle != 'mail'}">
<div class="ss_workflow">
<table border="0" cellspacing="0" cellpadding="0">
<tr><th align="left" colspan="4" style="padding:0px 0px 6px 2px;"><ssf:nlt tag="workflow"/></th></tr>
<tr>
  <th align="left" class="ss_fineprint ss_underline ss_light" style="padding:0px 0px 4px 30px; font-weight:normal;">
    <ssf:nlt tag="workflow.process"/></th>
  <th align="left" class="ss_fineprint ss_underline ss_light" style="padding:0px 0px 4px 30px; font-weight:normal;">
    <ssf:nlt tag="workflow.state"/></th>
  <th align="left" class="ss_fineprint ss_underline ss_light" style="padding:0px 0px 4px 30px; font-weight:normal;" 
    colspan="2">
    <ssf:nlt tag="workflow.action"/></th>
</tr>
<c:set var="lastWorkflowTitle" value=""/>
<c:forEach var="workflow" items="${ssDefinitionEntry.workflowStates}">
  <c:if test="${!empty workflow.definition}">
    <c:if test="${empty workflow.threadName}">
	  <tr>
	    <td valign="top" style="padding:0px 0px 4px 30px;">
	      <c:if test="${workflowTitle != lastWorkflowTitle}">
	        <c:out value="${workflow.definition.title}"/>
	      </c:if>
	    </td>
	    <td valign="top" style="padding:0px 0px 4px 30px;">${ssWorkflowCaptions[workflow.id]}</td>
	    <td valign="top" align="right" style="padding:0px 0px 4px 30px;">
	    <c:if test="${!empty ssWorkflowTransitions[workflow.id]}">
	      <b><ssf:nlt tag="workflow.transitionTo" 
	        text="Transition to:"/></b>
	      </td>
	      <td valign="top" style="padding:0px 0px 4px 4px;">
		  <form class="ss_style ss_form" method="post" action="" 
		    style="display:inline; background: inherit !important;">
		  <input type="hidden" name="tokenId" value="${workflow.id}">
		  <input type="hidden" name="replyId" value="${ssDefinitionEntry.id}">
		  <select name="toState">
		  <c:forEach var="transition" items="${ssWorkflowTransitions[workflow.id]}">
		    <option value="${transition.key}">${transition.value}</option>
		  </c:forEach>
		  </select><input type="submit" class="ss_submit" name="changeStateBtn" 
		   style="background: inherit !important;"
		   value="<ssf:nlt tag="button.ok" text="OK"/>">
		  </form>
		</c:if>
		</td>
		<c:if test="${empty ssWorkflowTransitions[workflow.id]}">
		  <td style="padding:0px 0px 4px 30px;"></td>
		</c:if>
	  </tr>
	  <c:if test="${!empty ssWorkflowQuestions[workflow.id]}">
	    <tr>
	      <td valign="top" colspan="2" style="padding:0px 0px 4px 30px;"></td>
	      <td valign="top" colspan="2" style="padding:0px 0px 4px 30px;">
		  <c:forEach var="question" items="${ssWorkflowQuestions[workflow.id]}">
		    <form class="ss_style ss_form" method="post" action="" 
		      style="display:inline; background: inherit !important;">
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
		</tr>
	  </c:if>
	  <c:set var="lastWorkflowTitle" value="${workflow.definition.title}"/>

      <c:forEach var="workflow2" items="${ssDefinitionEntry.workflowStates}">
        <% //??? This next check needs to be fixed if multiple workflow porcesses are allowed %>
        <c:if test="${workflow2.definition.id == workflow.definition.id}">
          <c:if test="${!empty workflow2.threadName}">
			  <tr>
			    <td valign="top" style="padding:0px 0px 4px 30px;"><c:out value="${workflow2.threadName}"/></td>
			    <td valign="top" style="padding:0px 0px 4px 30px;">${ssWorkflowCaptions[workflow2.id]}</td>
			    <c:if test="${!empty ssWorkflowTransitions[workflow2.id]}">
			      <td valign="top" align="right" style="padding:0px 0px 4px 30px;">
			        <b><ssf:nlt tag="workflow.transitionTo" 
			        text="Transition to:"/></b></td>
			      <td valign="top" style="padding:0px 0px 4px 4px;">
				  <form class="ss_style ss_form" method="post" action="" 
				    style="display:inline; background: inherit !important;">
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
				  <td style="padding:0px 0px 4px 30px;"></td><td style="padding:0px 0px 4px 30px;"></td>
				</c:if>
			  </tr>
			  <c:if test="${!empty ssWorkflowQuestions[workflow.id]}">
			    <tr>
			      <td valign="top" colspan="2" style="padding:0px 0px 4px 30px;"></td>
			      <td valign="top" colspan="2" style="padding:0px 0px 4px 30px;">
				  <c:forEach var="question" items="${ssWorkflowQuestions[workflow.id]}">
				    <form class="ss_style ss_form" method="post" action="" 
				      style="display:inline; background: inherit !important;">
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
				</tr>
			  </c:if>
          </c:if>
        </c:if>
      </c:forEach>
    </c:if>

  <c:set var="workflowTitle" value="${workflow.definition.title}"/>
  </c:if>
</c:forEach>
</table>
</div>
</c:if>
<c:if test="${ssConfigJspStyle == 'mail'}">
<div class="ss_workflow">
<table border="0" cellspacing="0" cellpadding="0">
<tr><th align="left" colspan="2" style="padding:0px 0px 6px 2px;"><ssf:nlt tag="workflow"/></th></tr>
<tr>
  <th align="left" class="ss_fineprint ss_underline ss_light" style="padding:0px 0px 4px 30px; font-weight:normal;">
    <ssf:nlt tag="workflow.process"/></th>
  <th align="left" class="ss_fineprint ss_underline ss_light" style="padding:0px 0px 4px 30px; font-weight:normal;">
    <ssf:nlt tag="workflow.state"/></th>
 </tr>
 <c:set var="lastWorkflowTitle" value=""/>
 
<c:forEach var="workflow" items="${ssDefinitionEntry.workflowStates}">
  <c:if test="${!empty workflow.definition}">
    <c:if test="${empty workflow.threadName}">
	  <tr>
	    <td valign="top" style="padding:0px 0px 4px 30px;">
	      <c:if test="${workflowTitle != lastWorkflowTitle}">
	        <c:out value="${workflow.definition.title}"/>
	      </c:if>
	    </td>
	    <td valign="top" style="padding:0px 0px 4px 30px;">${ssWorkflowCaptions[workflow.id]}</td>
	  </tr>
    </c:if>

    <c:set var="workflowTitle" value="${workflow.definition.title}"/>
    <c:forEach var="workflow2" items="${ssDefinitionEntry.workflowStates}">
        <% //??? This next check needs to be fixed if multiple workflow porcesses are allowed %>
        <c:if test="${workflow2.definition.id == workflow.definition.id}">
          <c:if test="${!empty workflow2.threadName}">
			  <tr>
			    <td valign="top" style="padding:0px 0px 4px 30px;"><c:out value="${workflow2.threadName}"/></td>
			    <td valign="top" style="padding:0px 0px 4px 30px;">${ssWorkflowCaptions[workflow2.id]}</td>
			  </tr>
    	  </c:if>
  		</c:if>
  	</c:forEach>
  <c:set var="workflowTitle" value="${workflow.definition.title}"/>
  </c:if>

</c:forEach>
</table>
</div>
</c:if>

</c:if>
