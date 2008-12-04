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
<%@ page import="java.util.Date" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ss_namespace" value="${renderResponse.namespace}" scope="request"/>
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

<div class="ss_entryContent">
<span class="ss_labelLeft"><c:out value="${property_caption}" /></span>

<c:set var="hasRightsToVote" value="${ss_accessControlMap[ssDefinitionEntry.id]['addReply']}"/>
<c:set var="isModerator" value="${ss_accessControlMap[ssDefinitionEntry.id]['modifyEntry']}"/>
<c:set var="surveyModel" value="${ssDefinitionEntry.customAttributes[property_name].value.surveyModel}"/>

<c:set var="operationChangeVote" value="${operation=='changeVote'}" />
<c:set var="operationViewResults" value="${operation=='viewResults'}" />
<c:set var="operationViewDetails" value="${operation=='viewDetails'}" />

<jsp:useBean id="ssBinder" type="org.kablink.teaming.domain.Binder" scope="request" />
<jsp:useBean id="ssEntry" type="org.kablink.teaming.domain.FolderEntry" scope="request"/>

<c:set var="alreadyVotedByGuest" value="false" />
<c:if test="${ssUser.shared}">
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


<form id="ssSurveyForm_${property_name}" method="post">
	<input type="hidden" name="attributeName" value="${property_name}" />
	<c:set var="hasAnyQuestion" value="${fn:length(surveyModel.questions) > 0}" />

	<c:set var="showSurveyForm" value="${!overdue && !surveyModel.alreadyVotedCurrentUser &&!alreadyVotedByGuest && hasRightsToVote && !(operationViewResults || operationViewDetails)}" />
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
			
			<div class="ss_questionContainer" id="${ss_namespace}_${property_name}_question_${question.index}">
				<p class="ss_survey_question" style="zoom:1;">
					<c:out value="${question.question}" escapeXml="false"/><c:if test="${question.requiredAnswer && !showResults}"><span class=\"ss_required\">*</span></c:if>
				</p>

				<ol>
				<c:forEach var="answer" items="${question.answers}">
					<c:if test="${question.type != 'input' || !empty answer.text}">
						<li>
							<c:if test="${question.type == 'multiple' || question.type == 'single'}">
								<ssf:drawChart count="${answer.votesCount}" total="${question.totalResponses}"/>
							</c:if>
							<span class="ss_survey_answer" style="zoom:1;"><c:out value="${answer.text}" escapeXml="false"/></span>
							<c:if test="${question.type == 'multiple' || question.type == 'single'}">
								<div class="ss_clear"></div>
							</c:if>
							<c:if test="${!empty answer.votedUserIds && operationViewDetails && (surveyModel.allowedToViewDetailsCurrentUser || isModerator)}">
								<div>
									<ul class="ss_survey_users_list">
										<c:set var="users_list" value="${answer.votedUserIds}"/>
										<jsp:useBean id="users_list" type="java.util.ArrayList" />
										<c:forEach var="voter" items="<%= org.kablink.teaming.util.ResolveIds.getPrincipals(users_list) %>" >
											<li><ssf:showUser user="${voter}" /></li>
										</c:forEach>
										<c:forEach var="email" items="${answer.votedGuestsEmails}" >
											<li>${email}</li>
										</c:forEach>										
									</ul>
								</div>								
							</c:if>
						</li>
					</c:if>
				</c:forEach>
				</ol>
				<c:if test="${question.type == 'multiple'}">
					<p class="ss_legend"><ssf:nlt tag="survey.vote.multiple.legend"/></p>
				</c:if>

			</div>
		</c:forEach>
		
	</c:if>
		
		<c:if test="${operationViewDetails}">
			<a href="<ssf:url adapter="true" portletName="ss_forum" folderId="${ssBinder.id}" 
						action="view_folder_entry" entryId="${ssEntry.id}" actionUrl="true" />"><ssf:nlt tag="survey.title.shortResults"/></a>
		</c:if>
	
		<c:if test="${showResults}">
			<c:if test="${((!overdue && (surveyModel.allowedToViewBeforeDueDateCurrentUser || isModerator) ||
							(overdue && (surveyModel.allowedToViewAfterDueDateCurrentUser || isModerator))) &&
							!operationViewDetails && (surveyModel.allowedToViewDetailsCurrentUser || isModerator))}">
				<a href="<ssf:url adapter="true" portletName="ss_forum" folderId="${ssBinder.id}" 
							action="view_folder_entry" entryId="${ssEntry.id}" actionUrl="true"><ssf:param name="operation" value="viewDetails" /></ssf:url>"><ssf:nlt tag="survey.title.details"/></a>
			</c:if>
		</c:if>
		
		<c:if test="${!overdue && surveyModel.alreadyVotedCurrentUser && !operationChangeVote && (surveyModel.allowedToViewBeforeDueDateCurrentUser || isModerator) && surveyModel.allowedToChangeVote}">
			<a href="<ssf:url adapter="true" portletName="ss_forum" folderId="${ssBinder.id}" 
						action="view_folder_entry" entryId="${ssEntry.id}" actionUrl="true"><ssf:param name="operation" value="changeVote" /></ssf:url>"><ssf:nlt tag="survey.title.changeVote"/></a>
		</c:if>
		
		<c:if test="${overdue}">
		    <ssf:nlt tag="survey.vote.notAllowed.overdue"/>
		</c:if>

		<c:if test="${showResults}">
			<c:if test="${!overdue && (surveyModel.alreadyVotedCurrentUser || alreadyVotedByGuest) && !operationChangeVote}">
					    <ssf:nlt tag="survey.vote.status.alreadyVotedStillOpen">
		    	<ssf:param name="value" useBody="true"><fmt:formatDate value="${dueDate}" 
						    		 										timeZone="${ssUser.timeZone.ID}" type="both" 
																		  timeStyle="short" dateStyle="medium"/></ssf:param></ssf:nlt>
			</c:if>
		</c:if>
		
		<c:if test="${!overdue && !surveyModel.alreadyVotedCurrentUser && !hasRightsToVote && (surveyModel.allowedToViewBeforeDueDateCurrentUser || isModerator)}">
			<ssf:nlt tag="survey.vote.status.noRightsToVoteRightsToViewBefore"/>
		</c:if>

		
	<c:if test="${showSurveyForm || (showSurveyModifyForm && !ssUser.shared)}">
	
		<c:if test="${ssUser.shared}">
			<div id="${ss_namespace}_${property_name}_guest_email_container">
				<label for="${ss_namespace}_${property_name}_guest_email"><ssf:nlt tag="survey.vote.guest.email"/></label> 
				<input type="text" name="guest_email" id="${ss_namespace}_${property_name}_guest_email" />
			</div>
		</c:if>

		<c:forEach var="question" items="${surveyModel.questions}" >
			<div class="ss_questionContainer" id="${ss_namespace}_${property_name}_question_${question.index}">
				<p class="ss_survey_question" style="zoom:1;">
					<c:out value="${question.question}" escapeXml="false"/><c:if test="${question.requiredAnswer && !showResults}"><span class=\"ss_required\">*</span></c:if>
					<a href="javascript: //" onclick="ssSurvey.clearAnswers(${question.index}, [<c:forEach var="answer" items="${question.answers}" varStatus="status">${answer.index}<c:if test="${!status.last}">,</c:if></c:forEach>], '${ss_namespace}_${property_name}')"><ssf:nlt tag="survey.clear"/></a>
				</p>
				
				<c:if test="${question.type == 'multiple'}">
					<ol>
					<c:forEach var="answer" items="${question.answers}">
						<li>
							<input type="checkbox" style="width: 19px;" name="answer_${question.index}" id="${ss_namespace}_${property_name}_answer_${question.index}_${answer.index}" value="${answer.index}" 
									<c:if test="${showSurveyModifyForm && answer.alreadyVotedCurrentUser}">checked="true"</c:if>
								/>
							<label for="${ss_namespace}_${property_name}_answer_${question.index}_${answer.index}"><c:out value="${answer.text}" escapeXml="false"/></label>
						</li>
					</c:forEach>
					</ol>
				</c:if>
				<c:if test="${question.type == 'single'}">
					<ol>
					<c:forEach var="answer" items="${question.answers}">
						<li>
							<input type="radio" style="width: 19px;" name="answer_${question.index}" value="${answer.index}" id="${ss_namespace}_${property_name}_answer_${question.index}_${answer.index}"
									<c:if test="${showSurveyModifyForm && answer.alreadyVotedCurrentUser}">checked="true"</c:if>
								/>
							<label for="${ss_namespace}_${property_name}_answer_${question.index}_${answer.index}"><c:out value="${answer.text}" escapeXml="false"/></label>
						</li>
					</c:forEach>
					</ol>
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
					<input type="text" name="answer_${question.index}" id="${ss_namespace}_${property_name}_answer_${question.index}" 
						<c:if test="${showSurveyModifyForm && !empty currentUserAnswer}">value="<c:out value="${currentUserAnswer}" escapeXml="false"/>"</c:if> />
				</c:if>
			</div>
		</c:forEach>
		
		
		<c:if test="${(surveyModel.allowedToViewBeforeDueDateCurrentUser || isModerator)}">
			<a href="<ssf:url adapter="true" portletName="ss_forum" folderId="${ssBinder.id}" 
						action="view_folder_entry" entryId="${ssEntry.id}" actionUrl="true"><ssf:param name="operation" value="viewResults" /></ssf:url>"><ssf:nlt tag="survey.title.results"/></a>
		</c:if>	
		
		<c:choose>
			<c:when test="${!hasAnyQuestion}">
				<ssf:nlt tag="survey.vote.notAllowed.empty"/>
			</c:when>
			<c:otherwise>
				<input type="button" value="<ssf:nlt tag="survey.vote"/>" 
					onclick="ssSurvey.vote('ssSurveyForm_${property_name}', ${ssBinder.id}, ${ssDefinitionEntry.id}, {<c:forEach var="question" items="${surveyModel.questions}" varStatus="status"><c:if test="${question.requiredAnswer}">${question.index}:[<c:if test="${question.type != 'input'}"><c:forEach var="answer" items="${question.answers}" varStatus="aStatus">${answer.index}<c:if test="${!aStatus.last}">,</c:if></c:forEach></c:if>]<c:if test="${!status.last}">,</c:if></c:if></c:forEach>}, '${ss_namespace}_${property_name}');"/>
					
				<c:if test="${showSurveyModifyForm}">
					<input type="button" value="<ssf:nlt tag="survey.vote.remove"/>" 
						onclick="ssSurvey.removeVote('ssSurveyForm_${property_name}', ${ssBinder.id}, ${ssDefinitionEntry.id});"/>
					
				</c:if>
			</c:otherwise>		
		</c:choose>
	</c:if>

</form>



</div>