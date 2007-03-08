<% // View entry workflow %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<c:if test="${!empty ssDefinitionEntry.workflowStates}">
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
