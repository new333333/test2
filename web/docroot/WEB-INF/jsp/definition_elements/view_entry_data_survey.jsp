<%
/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="java.util.Date" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:if test="${empty ss_surveyFormCounter}"><c:set var="ss_surveyFormCounter" value="0" scope="request"/></c:if>
<c:set var="ss_surveyFormCounter" value="${ss_surveyFormCounter + 1}" scope="request"/>
<c:if test="${property_required}"><c:set var="ss_someFieldsRequired" value="true" scope="request"/></c:if>
<%
	String caption1 = (String) request.getAttribute("property_caption");
	String caption2 = NLT.get("general.required.caption", new Object[]{caption1});
%>
<c:if test="${empty ss_survey_randomizer}" >
  <c:set var="ss_survey_randomizer" value="0" scope="request" />
</c:if>
<c:set var="ss_survey_randomizer" value="${ss_survey_randomizer + 1}" scope="request" />
<c:set var="ss_survey_prefix" value="${renderResponse.namespace}_${ss_survey_randomizer}"/>
<%
	boolean overdue = false;
	Date dueDate = null;
	if (request.getAttribute("ssDefinitionEntry") != null &&
		((DefinableEntity)request.getAttribute("ssDefinitionEntry")).getCustomAttribute("due_date") != null) {
		dueDate = (Date) ((DefinableEntity)request.getAttribute("ssDefinitionEntry")).getCustomAttribute("due_date").getValue();
		overdue = org.kablink.teaming.util.DateComparer.isOverdue(dueDate);
	}
%>
<c:set var="dueDate" value="<%= dueDate %>" />
<c:set var="overdue" value="<%= overdue %>" />

<script type="text/javascript" src="<html:rootPath/>js/common/ss_survey.js"></script>
<script type="text/javascript">
	var ss_survey_requiredMissingWarning = "<ssf:nlt tag='survey.required.missing.warning'/>";
</script>

<div class="ss_entryContent">
<div class="margintop2" style="border-bottom: 1px solid #b8b8b8; font-size: 18px;"><c:out value="${property_caption}" /></div>

<c:set var="hasRightsToVote" value="${ss_accessControlMap[ssDefinitionEntry.id]['addReply']}"/>
<c:set var="isModerator" value="${ss_accessControlMap[ssDefinitionEntry.id]['modifyEntry']}"/>
<c:set var="canClearAllVotes" value="${ss_accessControlMap[ssDefinitionEntry.id]['deleteEntry']}"/>
<c:set var="surveyModel" value="${ssDefinitionEntry.customAttributes[property_name].value.surveyModel}"/>

<c:set var="operationChangeVote" value="${operation=='changeVote'}" />
<c:set var="operationViewResults" value="${operation=='viewResults'}" />
<c:set var="operationViewDetails" value="${operation=='viewDetails'}" />

<jsp:useBean id="ssBinder" type="org.kablink.teaming.domain.Binder" scope="request" />
<jsp:useBean id="ssEntry" type="org.kablink.teaming.domain.FolderEntry" scope="request"/>

<c:set var="alreadyVotedByGuest" value="false" />
<c:if test="${ssUser.shared && !surveyModel.allowMultipleGuestVotes}">
	<%
	String cookieName = "Vote-" + ssBinder.getId() + "-" + ssEntry.getId();
	String cookieValue = null;
	Cookie cookies [] = request.getCookies ();
	if (cookies != null) {
		for (int i = 0; i < cookies.length; i++) {
			if (cookies [i].getName().equals (cookieName)) {
				cookieValue = cookies[i].getValue();
				break;
			}
		}
	}
	%>
	<c:set var="cookieValue" value="<%=cookieValue%>" />
	<c:if test="${!empty cookieValue}">
		<c:set var="alreadyVotedByGuest" value="true" />
	</c:if>
</c:if>

<form class="ss_surveyForm" id="ssSurveyForm_${property_name}${ss_surveyFormCounter}" method="post"
  action="<ssf:url adapter="true" portletName="ss_forum"    
	actionUrl="true"
	action="view_folder_entry" 
	binderId="${mashupEntry.parentFolder.id}"
	entryId="${mashupEntry.id}" />"
>
	<input type="hidden" name="attributeName" value="${property_name}" />
	<c:set var="hasAnyQuestion" value="${fn:length(surveyModel.questions) > 0}" />

	<c:set var="showSurveyForm" value="${!overdue && 
			((ssUser.shared && surveyModel.allowMultipleGuestVotes) || !surveyModel.alreadyVotedCurrentUser) && 
			!alreadyVotedByGuest && hasRightsToVote && !(operationViewResults || operationViewDetails)}" />
	<c:set var="showSurveyModifyForm" value="${operationChangeVote && !overdue && surveyModel.alreadyVotedCurrentUser && !alreadyVotedByGuest && surveyModel.allowedToChangeVote && hasRightsToVote}" />	
						
	<c:set var="showResults" value="${((!overdue && !surveyModel.alreadyVotedCurrentUser && !hasRightsToVote && (surveyModel.allowedToViewBeforeDueDateCurrentUser || isModerator)) ||
									(!overdue && (surveyModel.alreadyVotedCurrentUser || alreadyVotedByGuest) && !operationChangeVote && (surveyModel.allowedToViewBeforeDueDateCurrentUser || isModerator)) ||
									(overdue && (surveyModel.allowedToViewAfterDueDateCurrentUser || isModerator)) ||
									(!overdue && !surveyModel.alreadyVotedCurrentUser && (surveyModel.allowedToViewBeforeDueDateCurrentUser || isModerator) && hasRightsToVote && (operationViewResults || operationViewDetails)))}" />
	
	<c:if test="${!overdue && !surveyModel.alreadyVotedCurrentUser && !hasRightsToVote && !(surveyModel.allowedToViewBeforeDueDateCurrentUser || isModerator) && !(surveyModel.allowedToViewAfterDueDateCurrentUser || isModerator)}" >
		<ssf:nlt tag="survey.vote.status.noRightsToVoteAndSeeResults"/>
	</c:if>
	<c:if test="${!overdue && !surveyModel.alreadyVotedCurrentUser && !hasRightsToVote && !(surveyModel.allowedToViewBeforeDueDateCurrentUser || isModerator) && (surveyModel.allowedToViewAfterDueDateCurrentUser || isModerator)}" >
	    <ssf:nlt tag="survey.vote.status.noRightsToVote">
	    	<ssf:param name="value" useBody="true"><fmt:formatDate value="${dueDate}" 
					    		 										timeZone="${ssUser.timeZone.ID}" type="both" 
																	  timeStyle="short" dateStyle="medium"/></ssf:param></ssf:nlt>
	</c:if>
	<c:if test="${!overdue && surveyModel.alreadyVotedCurrentUser && operationChangeVote && !surveyModel.allowedToChangeVote}" >
	    <ssf:nlt tag="survey.vote.status.noRightsToChangeVote"/>
	</c:if>		
	<c:if test="${!overdue && (alreadyVotedByGuest || surveyModel.alreadyVotedCurrentUser) && !operationChangeVote && !(surveyModel.allowedToViewBeforeDueDateCurrentUser || isModerator) && surveyModel.allowedToChangeVote}" >
		<ssf:nlt tag="survey.vote.status.voted"/>
	    <a href="<ssf:url adapter="true" portletName="ss_forum" folderId="${ssBinder.id}" 
						action="view_folder_entry" entryId="${ssEntry.id}" actionUrl="true"><ssf:param name="operation" value="changeVote" /></ssf:url>"><ssf:nlt tag="survey.title.changeVote"/></a>
	</c:if>
	<c:if test="${!overdue && (surveyModel.alreadyVotedCurrentUser || alreadyVotedByGuest) && !(surveyModel.allowedToViewBeforeDueDateCurrentUser || isModerator) && !surveyModel.allowedToChangeVote && (surveyModel.allowedToViewAfterDueDateCurrentUser || isModerator)}" >
		<ssf:nlt tag="survey.vote.status.alreadyVoted">
	    	<ssf:param name="value" useBody="true"><fmt:formatDate value="${dueDate}" 
					    		 										timeZone="${ssUser.timeZone.ID}" type="both" 
																	  timeStyle="short" dateStyle="medium"/></ssf:param></ssf:nlt>
	</c:if>	
	<c:if test="${!overdue && surveyModel.alreadyVotedCurrentUser && !(surveyModel.allowedToViewBeforeDueDateCurrentUser || isModerator) && !surveyModel.allowedToChangeVote && !(surveyModel.allowedToViewAfterDueDateCurrentUser || isModerator)}" >
	    <ssf:nlt tag="survey.vote.status.alreadyVotedNoResult"/>
	</c:if>
	<c:if test="${overdue && !(surveyModel.allowedToViewAfterDueDateCurrentUser || isModerator)}" >
		<ssf:nlt tag="survey.vote.status.noRightsToSeeResults"/>
	</c:if>

	<c:if test="${showResults}">
		<c:forEach var="question" items="${surveyModel.questions}" >
			
			<div class="ss_questionContainer" id="${ss_survey_prefix}_${property_name}_question_${question.index}">
					<c:if test="${question.requiredAnswer && !showResults}">
						<span id="ss_required_${property_name}" title="<%= caption2 %>" class="ss_required" style="zoom:1;">*</span><span class="ss_required ss_smallprint"><ssf:nlt tag="survey.answer.required"/></span>
					</c:if>
					<c:out value="${question.question}" escapeXml="false"/>

				<ul>
				<c:forEach var="answer" items="${question.answers}" varStatus="status">
					<c:if test="${question.type != 'input' || !empty answer.text}">
						<li style="vertical-align:top;list-style-type:none;">
						  <table cellspacing="0" cellpadding="0">
						  <tr><td valign="top">${status.count}.&nbsp;&nbsp;</td>
						  <td valign="top">
							<div style="display:inline-block;">
								<c:if test="${question.type == 'multiple' || question.type == 'single'}">
									<ssf:drawChart count="${answer.votesCount}" total="${question.totalResponses}"/>
								</c:if>
								<span class="ss_survey_answer" style="zoom:1;"><c:out value="${answer.text}" escapeXml="false"/></span>
							</div>
							<c:if test="${question.type == 'multiple' || question.type == 'single'}">
								<div class="ss_clear"></div>
							</c:if>
							<c:if test="${!empty answer.votedUserIds && operationViewDetails && (surveyModel.allowedToViewDetailsCurrentUser || isModerator)}">
								<div>
									<ul class="ss_survey_users_list">
										<c:set var="users_list" value="${answer.votedUserIds}"/>
										<jsp:useBean id="users_list" type="java.util.ArrayList" />
										<c:forEach var="voter" items="<%= org.kablink.teaming.util.ResolveIds.getPrincipals(users_list, false) %>" >
											<li><ssf:showUser user="${voter}" /></li>
										</c:forEach>
										<c:forEach var="email" items="${answer.votedGuestsEmails}" >
											<li>${email}</li>
										</c:forEach>										
									</ul>
								</div>								
							</c:if>
							</td>
							</tr>
							</table>
						</li>
					</c:if>
				</c:forEach>
				</ul>
				<c:if test="${question.type == 'multiple'}">
					<p class="ss_legend"><ssf:nlt tag="survey.vote.multiple.legend"/></p>
				</c:if>

			</div>
		</c:forEach>
		
	</c:if>
		
		<c:if test="${operationViewDetails}">
			<a href="<ssf:url adapter="true" portletName="ss_forum" folderId="${ssBinder.id}" 
						action="view_folder_entry" feature="survey" entryId="${ssEntry.id}" actionUrl="true" />"><ssf:nlt tag="survey.title.shortResults"/></a>
		</c:if>
	
		<c:if test="${showResults}">
			<c:if test="${((!overdue && (surveyModel.allowedToViewBeforeDueDateCurrentUser || isModerator) ||
							(overdue && (surveyModel.allowedToViewAfterDueDateCurrentUser || isModerator))) &&
							!operationViewDetails && (surveyModel.allowedToViewDetailsCurrentUser || isModerator))}">
				<a href="<ssf:url adapter="true" portletName="ss_forum" folderId="${ssBinder.id}" 
							action="view_folder_entry" feature="survey" entryId="${ssEntry.id}" actionUrl="true"><ssf:param name="operation" value="viewDetails" /></ssf:url>"><ssf:nlt tag="survey.title.details"/></a>
			</c:if>
		</c:if>
		
		<c:if test="${!overdue && surveyModel.alreadyVotedCurrentUser && !operationChangeVote && (surveyModel.allowedToViewBeforeDueDateCurrentUser || isModerator) && surveyModel.allowedToChangeVote}">
			<a style="padding: 0 10px;" href="<ssf:url adapter="true" portletName="ss_forum" folderId="${ssBinder.id}" 
						action="view_folder_entry" entryId="${ssEntry.id}" actionUrl="true"><ssf:param name="operation" value="changeVote" /></ssf:url>"><ssf:nlt tag="survey.title.changeVote"/></a>
		</c:if>
		
		<c:if test="${overdue}">
		  <div>
		    <ssf:nlt tag="survey.vote.notAllowed.overdue"/>
		  </div>
		</c:if>

		<c:if test="${showResults}">
			<c:if test="${!overdue && (surveyModel.alreadyVotedCurrentUser || alreadyVotedByGuest) && !operationChangeVote}">
				<div>
				    <ssf:nlt tag="survey.vote.status.alreadyVotedStillOpen">
	    			<ssf:param name="value" useBody="true"><fmt:formatDate value="${dueDate}" 
					    		 										timeZone="${ssUser.timeZone.ID}" type="both" 
																	  timeStyle="short" dateStyle="medium"/></ssf:param></ssf:nlt>
				</div>
			</c:if>
		</c:if>
		
		<c:if test="${!overdue && !surveyModel.alreadyVotedCurrentUser && !hasRightsToVote && (surveyModel.allowedToViewBeforeDueDateCurrentUser || isModerator)}">
			<ssf:nlt tag="survey.vote.status.noRightsToVoteRightsToViewBefore"/>
		</c:if>

		<c:if test="${isModerator && canClearAllVotes}">
		  <div>
			<a href="<ssf:url adapter="true" portletName="ss_forum" folderId="${ssBinder.id}" 
					action="view_folder_entry" entryId="${ssEntry.id}" actionUrl="true"><ssf:param name="operation" value="vote_survey_remove_all" 
			/></ssf:url>"><ssf:nlt tag="survey.title.clearAllVotes"/></a>
		  </div>
		</c:if>

		
	<c:if test="${showSurveyForm || (showSurveyModifyForm && !ssUser.shared)}">
	
		<c:if test="${ssUser.shared}">
			<div id="${ss_survey_prefix}_${property_name}_guest_email_container">
				<label for="${ss_survey_prefix}_${property_name}_guest_email"><ssf:nlt tag="survey.vote.guest.email"/></label> 
				<input type="text" name="guest_email" id="${ss_survey_prefix}_${property_name}_guest_email" />
			</div>
		</c:if>

		<c:forEach var="question" items="${surveyModel.questions}" >
			<div class="ss_questionContainer" id="${ss_survey_prefix}_${property_name}_question_${question.index}">
				<div style="position: absolute; top: 10px; right: 10px;">
					<a href="javascript: //" onclick="ssSurvey.clearAnswers(${question.index}, [<c:forEach var="answer" items="${question.answers}" varStatus="status">${answer.index}<c:if test="${!status.last}">,</c:if></c:forEach>], '${ss_survey_prefix}_${property_name}')"><ssf:nlt tag="survey.clear"/></a>
				</div>
					<c:if test="${question.requiredAnswer && !showResults}">
						<span id="ss_required_${property_name}" title="<%= caption2 %>" class="ss_required" style="zoom:1;">*</span><span class="ss_required ss_smallprint"><ssf:nlt tag="survey.answer.required"/></span>
					</c:if>
					<c:out value="${question.question}" escapeXml="false"/>
				
				<c:if test="${question.type == 'multiple'}">
					<ul>
					<c:forEach var="answer" items="${question.answers}" varStatus="status">
						<li style="list-style-type:none;">
						  <table cellspacing="0" cellpadding="0">
						    <tr>
						      <td valign="top">${status.count}.&nbsp;&nbsp;</td>
						      <td valign="top">
							    <input type="checkbox" style="width: 19px;" name="answer_${question.index}" id="${ss_survey_prefix}_${property_name}_answer_${question.index}_${answer.index}" value="${answer.index}" 
									<c:if test="${showSurveyModifyForm && answer.alreadyVotedCurrentUser}">checked="true"</c:if>
								    />
							    <label for="${ss_survey_prefix}_${property_name}_answer_${question.index}_${answer.index}"><c:out value="${answer.text}" escapeXml="false"/></label>
							  </td>
							</tr>
						  </table>
						</li>
					</c:forEach>
					</ul>
				</c:if>
				<c:if test="${question.type == 'single'}">
					<ul>
					<c:forEach var="answer" items="${question.answers}" varStatus="status">
						<li style="list-style-type:none;">
						  <table cellspacing="0" cellpadding="0">
						    <tr>
						      <td valign="top">${status.count}.&nbsp;&nbsp;</td>
						      <td valign="top">
							    <input type="radio" style="width: 19px;" name="answer_${question.index}" value="${answer.index}" id="${ss_survey_prefix}_${property_name}_answer_${question.index}_${answer.index}"
									<c:if test="${showSurveyModifyForm && answer.alreadyVotedCurrentUser}">checked="true"</c:if>
								    />
							    <label for="${ss_survey_prefix}_${property_name}_answer_${question.index}_${answer.index}"><c:out value="${answer.text}" escapeXml="false"/></label>
							  </td>
							</tr>
						  </table>
						</li>
					</c:forEach>
					</ul>
				</c:if>
				<c:if test="${question.type == 'input'}">
					<c:if test="${showSurveyModifyForm}">
						<c:set var="currentUserAnswer" value="" />
						<c:forEach var="answer" items="${question.answers}">
								<c:if test="${showSurveyModifyForm && answer.alreadyVotedCurrentUser}">
									<c:set var="currentUserAnswer" value="${answer.text}" />
								</c:if>
						</c:forEach>
					</c:if>
					<input type="text" name="answer_${question.index}" id="${ss_survey_prefix}_${property_name}_answer_${question.index}" 
						<c:if test="${showSurveyModifyForm && !empty currentUserAnswer}">value="<c:out value="${currentUserAnswer}" escapeXml="false"/>"</c:if>
						onkeypress="return ssSurvey.voteViaEnter(event,'ssSurveyForm_${property_name}${ss_surveyFormCounter}', ${ssBinder.id}, ${ssDefinitionEntry.id}, {<% int qraCount = 0; %><c:forEach var="question" items="${surveyModel.questions}"><c:if test="${question.requiredAnswer}"><% qraCount += 1; if (1 < qraCount) { %>,<% } %>${question.index}:[<c:if test="${question.type != 'input'}"><c:forEach var="answer" items="${question.answers}" varStatus="status">${answer.index}<c:if test="${!status.last}">,</c:if></c:forEach></c:if>]</c:if></c:forEach>}, '${ss_survey_prefix}_${property_name}');"/>
				</c:if>
			</div>
		</c:forEach>
				
		<c:choose>
			<c:when test="${!hasAnyQuestion}">
				<ssf:nlt tag="survey.vote.notAllowed.empty"/>
			</c:when>
			<c:otherwise>
				<input class="marginleft1" type="button" value="<ssf:nlt tag="survey.vote"/>"
					onclick="ssSurvey.vote('ssSurveyForm_${property_name}${ss_surveyFormCounter}', ${ssBinder.id}, ${ssDefinitionEntry.id}, {<% int qraCount = 0; %><c:forEach var="question" items="${surveyModel.questions}"><c:if test="${question.requiredAnswer}"><% qraCount += 1; if (1 < qraCount) { %>,<% } %>${question.index}:[<c:if test="${question.type != 'input'}"><c:forEach var="answer" items="${question.answers}" varStatus="status">${answer.index}<c:if test="${!status.last}">,</c:if></c:forEach></c:if>]</c:if></c:forEach>}, '${ss_survey_prefix}_${property_name}');"/>
					
				<c:if test="${showSurveyModifyForm}">
					<input class="marginleft1" type="button" value="<ssf:nlt tag="survey.vote.remove"/>" 
						onclick="ssSurvey.removeVote('ssSurveyForm_${property_name}${ss_surveyFormCounter}', ${ssBinder.id}, ${ssDefinitionEntry.id});"/>
					
				</c:if>
			</c:otherwise>		
		</c:choose>

		<c:if test="${(surveyModel.allowedToViewBeforeDueDateCurrentUser || isModerator)}">
			<span style="padding-left: 15px;"><a href="<ssf:url adapter="true" portletName="ss_forum" folderId="${ssBinder.id}" 
						action="view_folder_entry" entryId="${ssEntry.id}" actionUrl="true"><ssf:param name="operation" value="viewResults" /></ssf:url>"><ssf:nlt tag="survey.title.results"/></a></span>
		</c:if>	


	</c:if>

</form>



</div>