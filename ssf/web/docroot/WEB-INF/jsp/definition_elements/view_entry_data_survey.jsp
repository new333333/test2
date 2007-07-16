<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<%@ page import="java.util.Date" %>
<c:set var="ss_namespace" value="${renderResponse.namespace}" scope="request"/>
<%
	boolean overdue = false;
	if (request.getAttribute("ssDefinitionEntry") != null &&
		((DefinableEntity)request.getAttribute("ssDefinitionEntry")).getCustomAttribute("due_date") != null) {
		Date dueDate = (Date) ((DefinableEntity)request.getAttribute("ssDefinitionEntry")).getCustomAttribute("due_date").getValue();
		overdue = com.sitescape.team.util.DateComparer.isOverdue(dueDate);
	}
%>
<c:set var="overdue" value="<%= overdue %>" />

<script type="text/javascript" src="<html:rootPath/>js/common/ss_survey.js"></script>
<script type="text/javascript">
	ssSurvey.votedLabel = "<ssf:nlt tag="survey.vote.successfull"/>";
</script>

<div class="ss_entryContent">
<span class="ss_labelLeft"><c:out value="${property_caption}" /></span>


<c:set var="alreadyVotedCurrentUser" value="false"/>
<c:set var="hasRightsToVote" value="${ss_accessControlMap[ssDefinitionEntry.id]['addReply']}"/>
<form id="ssSurveyForm_${property_name}" method="post">
	<input type="hidden" name="attributeName" value="${property_name}" />
	<c:set var="hasAnyQuestion" value="false" />
	<c:forEach var="question" items="${ssDefinitionEntry.customAttributes[property_name].value.surveyModel.questions}" >
		<c:set var="hasAnyQuestion" value="true" />
		<div class="ss_questionContainer">
			<p class="ss_survey_question" style="zoom:1;"><c:out value="${question.question}" escapeXml="false"/></p>
			<c:choose>
				<c:when test="${overdue || question.alreadyVotedCurrentUser || !hasRightsToVote}">
					<ol>
					<c:forEach var="answer" items="${question.answers}">
						<li>
							<c:if test="${question.type == 'multiple' || question.type == 'single'}">
								<ssf:drawChart count="${answer.votesCount}" total="${question.totalResponses}"/>
							</c:if>
							<span class="ss_survey_answer" style="zoom:1;"><c:out value="${answer.text}" escapeXml="false"/></span>
							<div class="ss_clear"></div>
						</li>
					</c:forEach>
					</ol>
					<c:if test="${question.type == 'multiple'}">
						<p class="ss_legend"><ssf:nlt tag="survey.vote.multiple.legend"/></p>
					</c:if>
				</c:when>
				<c:otherwise>
					<c:if test="${question.type == 'multiple'}">
						<ol>
						<c:forEach var="answer" items="${question.answers}">
							<li>
								<input type="checkbox" style="width: 19px;" name="answer_${question.index}" id="${ss_namespace}_${property_name}_answer_${question.index}_${answer.index}" value="${answer.index}" />
								<label for="${ss_namespace}_${property_name}_answer_${question.index}_${answer.index}"><c:out value="${answer.text}" escapeXml="false"/></label>
							</li>
						</c:forEach>
						</ol>
					</c:if>
					<c:if test="${question.type == 'single'}">
						<ol>
						<c:forEach var="answer" items="${question.answers}">
							<li>
								<input type="radio" style="width: 19px;" name="answer_${question.index}" value="${answer.index}" id="${ss_namespace}_${property_name}_answer_${question.index}_${answer.index}"/>
								<label for="${ss_namespace}_${property_name}_answer_${question.index}_${answer.index}"><c:out value="${answer.text}" escapeXml="false"/></label>
							</li>
						</c:forEach>
						</ol>
					</c:if>
					<c:if test="${question.type == 'input'}">
						<input type="text" name="answer_${question.index}">
					</c:if>
				</c:otherwise>
			</c:choose>
		</div>
		<c:set var="alreadyVotedCurrentUser" value="${question.alreadyVotedCurrentUser}"/>
	</c:forEach>
	
	<c:choose>
		<c:when test="${!hasAnyQuestion}">
			<ssf:nlt tag="survey.vote.notAllowed.empty"/>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${alreadyVotedCurrentUser}">
					<ssf:nlt tag="survey.vote.notAllowed.alreadyVoted"/>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${overdue}">
							<ssf:nlt tag="survey.vote.notAllowed.overdue"/>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${hasRightsToVote}">
									<input type="button" value="Vote!" 
										onclick="ssSurvey.vote('ssSurveyForm_${property_name}', ${ssBinder.id}, ${ssDefinitionEntry.id});"/>
								</c:when>
								<c:otherwise>
									<ssf:nlt tag="survey.vote.notAllowed.accessRights"/>
								</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>

</form>



</div>