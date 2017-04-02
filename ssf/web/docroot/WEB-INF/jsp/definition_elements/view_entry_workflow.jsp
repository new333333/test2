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
<script type="text/javascript">
function ss_checkForWorkflowStateSelection(obj) {
	var formObj = ss_findOwningElement(obj, "form");
	if (formObj.toState.value == "") {
		alert("<ssf:nlt tag="workflow.selectStatePlease"/>");
		return false;
	}
	return true;
}
</script>
<div class="ss_workflow">
<table border="0" cellspacing="0" cellpadding="0">
<tr><th align="left" colspan="4" style="padding:0px 0px 6px 2px;"><ssf:nlt tag="workflow"/></th></tr>
<tr>
  <th align="left" class="ss_fineprint ss_underline ss_light" style="padding:0px 0px 4px 20px; font-weight:normal;">
    <ssf:nlt tag="workflow.process"/></th>
  <th align="left" class="ss_fineprint ss_underline ss_light" style="padding:0px 0px 4px 20px; font-weight:normal;">
    <ssf:nlt tag="workflow.state"/></th>
  <th align="left" class="ss_fineprint ss_underline ss_light" style="padding:0px 0px 4px 20px; font-weight:normal;" 
    colspan="2">
    <ssf:nlt tag="workflow.action"/></th>
</tr>
<c:set var="lastWorkflowTitle" value=""/>
<c:forEach var="workflow" items="${ssDefinitionEntry.workflowStates}">
  <c:if test="${!empty workflow.definition}">
  <c:set var="workflowTitle" value="${workflow.definition.title}"/>
    <c:if test="${empty workflow.threadName}">
	  <tr>
	    <td valign="top" style="padding:0px 0px 4px 20px;">
	      <c:if test="${workflowTitle != lastWorkflowTitle}">
	        <ssf:nlt tag="${workflow.definition.title}" checkIfTag="true"/>
	      </c:if>
	    </td>
	    <td valign="top" style="padding:0px 0px 4px 20px;">${ssWorkflowCaptions[workflow.id]}</td>
	    <c:if test="${!empty ssWorkflowTransitions[workflow.id]}">
	      <td valign="top" align="right" style="padding:0px 0px 4px 20px;">
	      <b><ssf:nlt tag="workflow.transitionTo" /></b>
	      </td>
	      <td valign="top" style="padding:0px 0px 4px 4px;">
		  <form class="ss_style ss_form" method="post" 
		    action="<ssf:url adapter="true" 
				        portletName="ss_forum" 
				        folderId="${ssDefinitionEntry.parentFolder.id}" 
						action="view_folder_entry" 
						entryId="${ssDefinitionEntry.id}" 
						actionUrl="true" />" 
		    style="display:inline; background: inherit !important;">
		  <input type="hidden" name="tokenId" value="${workflow.id}">
		  <input type="hidden" name="replyId" value="${ssDefinitionEntry.id}">
		  <select name="toState">
		  <c:if test="${fn:length(ssWorkflowTransitions[workflow.id]) > 1}">
		    <option value=""><ssf:nlt tag="workflow.selectState"/></option>
		  </c:if>
		  <c:forEach var="transition" items="${ssWorkflowTransitions[workflow.id]}">
		    <option value="${transition.key}"><ssf:nlt tag="${transition.value}" checkIfTag="true"/></option>
		  </c:forEach>
		  </select><input type="submit" class="ss_submit" name="changeStateBtn" 
		   value="<ssf:nlt tag="button.ok" text="OK"/>"
		   onClick="return ss_checkForWorkflowStateSelection(this)">
				<sec:csrfInput />
		  </form>
		  </td>
		</c:if>
		<c:if test="${empty ssWorkflowTransitions[workflow.id]}">
		  <td colspan="2" style="padding:0px 0px 4px 20px;"></td>
		</c:if>
	  </tr>
	  <tr>
	    <td colspan="2" valign="top">
	      <c:if test="${!empty ssWorkflowDescriptions[workflow.id]}">
	        <div class="ss_entryDescription" style="margin:2px 2px 2px 20px; padding:4px;">${ssWorkflowDescriptions[workflow.id]}</div>
	      </c:if>
	    </td>
	    <td colspan="2" valign="top"></td>
	  </tr>
	  <c:if test="${!empty ssWorkflowQuestions[workflow.id]}">
	    <tr>
	      <td valign="top" colspan="2" style="padding:0px 0px 4px 20px;"></td>
	      <td valign="top" colspan="2" style="padding:0px 0px 4px 20px;">
	      <table>
		  <c:forEach var="question" items="${ssWorkflowQuestions[workflow.id]}">
		    <tr>
		    <td valign="top">
		    <form class="ss_style ss_form" method="post" 
		      action="<ssf:url adapter="true" 
				        portletName="ss_forum" 
				        folderId="${ssDefinitionEntry.parentFolder.id}" 
						action="view_folder_entry" 
						entryId="${ssDefinitionEntry.id}" 
						actionUrl="true" />" 
		      style="display:inline; background: inherit !important;">
		    <input type="hidden" name="tokenId" value="${workflow.id}">
		    <input type="hidden" name="replyId" value="${ssDefinitionEntry.id}">
		    <span class="ss_bold"><ssf:nlt tag="${question.value.workflow_questionText}" checkIfTag="true"/></span><br/>
		    <select name="${question.key}">
		    <c:forEach var="response" items="${question.value.workflow_questionResponses}">
		      <option value="${response.key}"><ssf:nlt tag="${response.value}" checkIfTag="true"/></option>
		    </c:forEach>
		    </select><input type="submit" class="ss_submit" name="respondBtn" 
		     value="<ssf:nlt tag="button.ok" text="OK"/>"><br/>
					<sec:csrfInput />
		    </form>
		    </td>
		    <td valign="top" style="padding-left:10px;">
		     <c:if test="${!empty question.value.workflow_questionResponses}">
		      <c:set var="hasResponded" value="false"/>
		      <c:set var="responderCount" value="0"/>
		      <c:forEach var="responderId" items="${question.value.workflow_questionResponders}">
		        <c:if test="${responderId == ssUser.id}">
		          <div><ssf:nlt tag="workflow.question.alreadyResponded"/></div>
		          <c:set var="hasResponded" value="true"/>
		        </c:if>
		        <c:set var="responderCount" value="${responderCount + 1}"/>
		      </c:forEach>
		      <c:if test="${!hasResponded}">
		        <div><ssf:nlt tag="workflow.question.hasNotResponded"/></div>
		      </c:if>
		      <c:set var="totalResponderCount" value="0"/>
		      <c:forEach items="${ssWorkflowQuestionResponders[question.key]}" varStatus="s">
				<c:if test="${s.last}">
				<c:set var="totalResponderCount" value="${s.count}"/>
				</c:if>
			  </c:forEach>
			  <c:if test="${totalResponderCount > 1}">
			    <div>
			      <ssf:nlt tag="workflow.question.waiting">
			        <ssf:param name="value" value="${responderCount}"/>
			        <ssf:param name="value" value="${totalResponderCount}"/>
			      </ssf:nlt>
			    </div>
			  </c:if>
			  <div>
			    <a href="javascript: ;" onClick="ss_showHide('ss_workflowResponsesDiv${ssDefinitionEntry.id}');return false;">
			      <span class="ss_smallprint"><ssf:nlt tag="workflow.viewResponses"/></span>
			    </a>
			  </div>
			 </c:if>
		    </td>
		    </tr>
		  </c:forEach>
		  </table>
		  </td>
		</tr>
	  </c:if>
	  <c:set var="lastWorkflowTitle" value="${workflow.definition.title}"/>

      <c:forEach var="workflow2" items="${ssDefinitionEntry.workflowStates}">
        <% //??? This next check needs to be fixed if multiple workflow porcesses are allowed %>
        <c:if test="${workflow2.definition.id == workflow.definition.id}">
          <c:if test="${!empty workflow2.threadName}">
			  <tr>
			    <td valign="top" style="padding:10px 0px 4px 20px;">${ssWorkflowThreadCaptions[workflow2.id]}</td>
			    <td valign="top" style="padding:10px 0px 4px 20px;">${ssWorkflowCaptions[workflow2.id]}</td>
			    <c:if test="${!empty ssWorkflowTransitions[workflow2.id]}">
			      <td valign="top" align="right" style="padding:10px 0px 4px 20px;">
			        <b><ssf:nlt tag="workflow.transitionTo" 
			        text="Transition to:"/></b></td>
			      <td valign="top" style="padding:10px 0px 4px 4px;">
				  <form class="ss_style ss_form" method="post" 
				    action="<ssf:url adapter="true" 
				        portletName="ss_forum" 
				        folderId="${ssDefinitionEntry.parentFolder.id}" 
						action="view_folder_entry" 
						entryId="${ssDefinitionEntry.id}" 
						actionUrl="true" />" 
				    style="display:inline; background: inherit !important;">
				  <input type="hidden" name="tokenId" value="${workflow2.id}">
				  <input type="hidden" name="replyId" value="${ssDefinitionEntry.id}">
				  <select name="toState">
				  <c:if test="${fn:length(ssWorkflowTransitions[workflow2.id]) > 1}">
				    <option value=""><ssf:nlt tag="workflow.selectState"/></option>
				  </c:if>
				  <c:forEach var="transition" items="${ssWorkflowTransitions[workflow2.id]}">
				    <option value="${transition.key}"><ssf:nlt tag="${transition.value}" checkIfTag="true"/></option>
				  </c:forEach>
				  </select><input type="submit" class="ss_submit" name="changeStateBtn" 
				   value="<ssf:nlt tag="button.ok" text="OK"/>">
						<sec:csrfInput />
					</form>
				  </td>
				</c:if>
				<c:if test="${empty ssWorkflowTransitions[workflow2.id]}">
				  <td style="padding:10px 0px 4px 20px;"></td><td style="padding:10px 0px 4px 20px;"></td>
				</c:if>
			  </tr>
			  <c:if test="${!empty ssWorkflowQuestions[workflow2.id]}">
			    <tr>
			      <td valign="top" colspan="2" style="padding:0px 0px 4px 20px;">
			        <c:if test="${!empty ssWorkflowDescriptions[workflow2.id]}">
			          <div class="ss_entryDescription" style="margin:2px 2px 2px 20px; padding:4px;">${ssWorkflowDescriptions[workflow2.id]}</div>
			        </c:if>
			      </td>
			      <td valign="top" colspan="2" style="padding:0px 0px 4px 20px;">
				  <table>
				  <c:forEach var="question" items="${ssWorkflowQuestions[workflow2.id]}">
		    		<tr>
		    		<td valign="top">
				    <form class="ss_style ss_form" method="post" 
				      action="<ssf:url adapter="true" 
				        portletName="ss_forum" 
				        folderId="${ssDefinitionEntry.parentFolder.id}" 
						action="view_folder_entry" 
						entryId="${ssDefinitionEntry.id}" 
						actionUrl="true" />" 
				      style="display:inline; background: inherit !important;">
				    <input type="hidden" name="tokenId" value="${workflow2.id}">
				    <input type="hidden" name="replyId" value="${ssDefinitionEntry.id}">
				    <span class="ss_bold"><ssf:nlt tag="${question.value.workflow_questionText}" checkIfTag="true"/></span><br/>
				    <select name="${question.key}">
				    <c:forEach var="response" items="${question.value.workflow_questionResponses}">
				      <option value="${response.key}"><ssf:nlt tag="${response.value}" checkIfTag="true"/></option>
				    </c:forEach>
				    </select><input type="submit" class="ss_submit" name="respondBtn" 
				     value="<ssf:nlt tag="button.ok" text="OK"/>">
							<sec:csrfInput />
						</form>
				    </td>
				    <td valign="top" style="padding-left:10px;">
				     <c:if test="${!empty question.value.workflow_questionResponses}">
				      <c:set var="hasResponded" value="false"/>
				      <c:set var="responderCount" value="0"/>
				      <c:forEach var="responderId" items="${question.value.workflow_questionResponders}">
				        <c:if test="${responderId == ssUser.id}">
				          <div><ssf:nlt tag="workflow.question.alreadyResponded"/></div>
				          <c:set var="hasResponded" value="true"/>
				        </c:if>
				        <c:set var="responderCount" value="${responderCount + 1}"/>
				      </c:forEach>
				      <c:if test="${!hasResponded}">
				        <div><ssf:nlt tag="workflow.question.hasNotResponded"/></div>
				      </c:if>
				      <c:set var="totalResponderCount" value="0"/>
				      <c:forEach items="${ssWorkflowQuestionResponders[question.key]}" varStatus="s">
						<c:if test="${s.last}">
						<c:set var="totalResponderCount" value="${s.count}"/>
						</c:if>
					  </c:forEach>
					  <c:if test="${totalResponderCount > 1}">
					    <div>
					      <ssf:nlt tag="workflow.question.waiting">
					        <ssf:param name="value" value="${responderCount}"/>
					        <ssf:param name="value" value="${totalResponderCount}"/>
					      </ssf:nlt>
					    </div>
					  </c:if>
					  <div>
					    <a href="javascript: ;" onClick="ss_showHide('ss_workflowResponsesDiv${ssDefinitionEntry.id}');return false;">
					      <span class="ss_smallprint"><ssf:nlt tag="workflow.viewResponses"/></span>
					    </a>
					  </div>
					 </c:if>
				    </td>
				    </tr>
				  </c:forEach>
				  </table>
				  </td>
				</tr>
			  </c:if>
			  <c:if test="${empty ssWorkflowQuestions[workflow2.id]}">
			    <tr>
			      <td valign="top" colspan="2" style="padding:0px 0px 4px 20px;">
			        <c:if test="${!empty ssWorkflowDescriptions[workflow2.id]}">
			          <div class="ss_entryDescription" style="margin:2px 2px 2px 20px; padding:4px;">${ssWorkflowDescriptions[workflow2.id]}</div>
			        </c:if>
			      </td>
			      <td valign="top" colspan="2" style="padding:0px 0px 4px 20px;">
				  </td>
				</tr>
			  </c:if>
          </c:if>
        </c:if>
      </c:forEach>
    </c:if>

  </c:if>
</c:forEach>
</table>
</div>
<div id="ss_workflowResponsesDiv${ssDefinitionEntry.id}" class="ss_workflow" style="display:none;">
  <c:forEach var="workflow3" items="${ssDefinitionEntry.workflowStates}">
  <c:forEach var="question" items="${ssWorkflowQuestions[workflow3.id]}">
    <% java.util.Map haveResponded = new java.util.HashMap(); %>
    <c:set var="thoseWhoHaveResponded" value="<%= haveResponded %>"/>
    <span class="ss_bold"><ssf:nlt tag="${question.value.workflow_questionText}" checkIfTag="true"/></span><br/>
    <table style="padding-left:10px;">
    <c:forEach var="response" items="${question.value.workflow_questionResponses}">
      <tr>
      <td valign="top">
        <span><ssf:nlt tag="${response.value}" checkIfTag="true"/></span>
      </td>
      <td valign="top" style="padding-left:10px;">
        <c:forEach var="workflowResponderId" items="${question.value.workflow_questionReponseResponders[response.key]}">
          <jsp:useBean id="workflowResponderId" type="Long" />
          <% haveResponded.put(workflowResponderId, workflowResponderId); %>
          <c:if test="${!empty ssWorkflowQuestionResponders[question.key][workflowResponderId]}">
            <div><ssf:showUser user="${ssWorkflowQuestionResponders[question.key][workflowResponderId]}"/></div>
          </c:if>
        </c:forEach>
      </td>
      </tr>
    </c:forEach>
    <tr>
      <td valign="top">
        <span><ssf:nlt tag="workflow.noResponse" /></span>
      </td>
      <td valign="top" style="padding-left:10px;">
        <c:forEach var="workflowResponder" items="${ssWorkflowQuestionResponders[question.key]}">
          <c:if test="${empty thoseWhoHaveResponded[workflowResponder.key]}">
            <div><ssf:showUser user="${workflowResponder.value}"/></div>
          </c:if>
        </c:forEach>
      </td>
    </tr>
    </table>
  </c:forEach>
  </c:forEach>
  <br/>
  <br/>
  <a class="ss_linkButton" href="javascript: ;" onClick="ss_showHide('ss_workflowResponsesDiv${ssDefinitionEntry.id}');return false;" 
  ><ssf:nlt tag="button.close"/></a>
</div>

<c:set var="workflowStateColumnText" value=""/>
<c:forEach var="workflow" items="${ssDefinitionEntry.workflowStates}">
  <c:if test="${!empty workflowStateColumnText}">
    <c:set var="workflowStateColumnText">${workflowStateColumnText}, ${ssWorkflowCaptions[workflow.id]}</c:set>
  </c:if>
  <c:if test="${empty workflowStateColumnText}">
    <c:set var="workflowStateColumnText">${ssWorkflowCaptions[workflow.id]}</c:set>
  </c:if>
</c:forEach>
</c:if>
<script type="text/javascript">
ss_createOnLoadObj("ss_resetFolderWorkflowStateColumn", ss_resetFolderWorkflowStateColumn);
function ss_resetFolderWorkflowStateColumn() {
	var spanObj = document.getElementById("ss_workflowState${ssBinder.id}_${ssDefinitionEntry.id}");
	if (spanObj == null) {
		var folderIframe = self.parent.document.getElementById("contentControl")
		if (folderIframe != null) {
			spanObj = folderIframe.contentDocument.getElementById("ss_workflowState${ssBinder.id}_${ssDefinitionEntry.id}");
		}
	}
	if (spanObj != null) {
		spanObj.innerHTML = "${workflowStateColumnText}";
	}
}
</script>
