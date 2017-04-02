<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
<% // View entry workflow %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<c:if test="${!empty ssDefinitionEntry.workflowStates}">
<div class="entry-content">
  <div id="workflow" class="entry-caption">
    <ssf:nlt tag="workflow"/>
    <c:set var="lastWorkflowTitle" value=""/>
  </div>
 
  <div class="workflow entry-element">
	<c:forEach var="workflow" items="${ssDefinitionEntry.workflowStates}">
	  <c:if test="${!empty workflow.definition}">
	      <c:if test="${workflowTitle != lastWorkflowTitle}">
	        <span style="padding-right: 5px;"><ssf:nlt tag="${workflow.definition.title}" checkIfTag="true"/></span>
	      </c:if>
	      <c:if test="${empty workflow.threadName}">
		    <span class="ss_mobile_workflow_state">${ssWorkflowCaptions[workflow.id]}</span>
	      </c:if>
	
	    <c:set var="workflowTitle" value="${workflow.definition.title}"/>
	    <c:forEach var="workflow2" items="${ssDefinitionEntry.workflowStates}">
	        <% //??? This next check needs to be fixed if multiple of the same workflow porcess is allowed %>
	        <c:if test="${workflow2.definition.id == workflow.definition.id}">
	          <c:if test="${!empty workflow2.threadName}">
				<br/><span class="ss_mobile_light ss_mobile_workflow_state">${workflow2.threadName}
			    ${ssWorkflowCaptions[workflow2.id]}</span>
	    	  </c:if>
	
		      <c:if test="${!empty ssWorkflowTransitions[workflow2.id]}">
			      <div class="margintop2"><ssf:nlt tag="workflow.transitionTo" /></div>
				  <form class="ss_style ss_form" method="post" 
				    action="<ssf:url adapter="true" portletName="ss_forum" 
						folderId="${ssBinder.id}" 
						entryId="${ssDefinitionEntry.id}"
						action="__ajax_mobile" 
						operation="mobile_show_entry" 
						actionUrl="true" />" 
				    style="display:inline; background: inherit !important;">
				  <input type="hidden" name="tokenId" value="${workflow.id}">
				  <input type="hidden" name="replyId" value="${ssDefinitionEntry.id}">
				  <select name="toState">
				  <c:forEach var="transition" items="${ssWorkflowTransitions[workflow2.id]}">
				    <option value="${transition.key}"><ssf:nlt tag="${transition.value}" checkIfTag="true"/></option>
				  </c:forEach>
				  </select><input type="submit" class="ss_submit" name="changeStateBtn" 
				   value="<ssf:nlt tag="button.ok" text="OK"/>">
						<sec:csrfInput />
				  </form>
			  </c:if>
	  		</c:if>
	  	</c:forEach>
	  <c:set var="workflowTitle" value="${workflow.definition.title}"/>
      <c:if test="${workflowTitle != lastWorkflowTitle}">
        <br/>
	  </c:if>	    	  
	  </c:if>
	</c:forEach>
   </div>
  </div>

</c:if>
