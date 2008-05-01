<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
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
  <c:set var="workflowTitle" value="${workflow.definition.title}"/>
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
		    <span class="ss_bold"><c:out value="${question.value.workflow_questionText}"/></span><br/>
		    <select name="${question.key}">
		    <c:forEach var="response" items="${question.value.workflow_questionResponses}">
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
				    <span class="ss_bold"><c:out value="${question.value.workflow_questionText}"/></span><br/>
				    <select name="${question.key}">
				    <c:forEach var="response" items="${question.value.workflow_questionResponses}">
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

  </c:if>
</c:forEach>
</table>
</div>
</c:if>
<c:if test="${ssConfigJspStyle == 'mail'}">
<% // The mail support is in "definition_elements/mail" %>
</c:if>

</c:if>
