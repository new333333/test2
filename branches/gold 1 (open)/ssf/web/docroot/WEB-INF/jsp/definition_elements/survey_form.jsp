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
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<script type="text/javascript" src="<html:rootPath/>js/common/ss_survey.js"></script>

<input type="hidden" name="${property_name}" value="" id="survey_${ss_namespace}_${property_name}" />
<div id="ss_surveyForm_questions_${ss_namespace}_${property_name}">
</div>

<div class="ss_more">
	<a class="ss_button" href="javascript: ss_survey_${ss_namespace}_${property_name}.ss_newSurveyQuestion('multiple', '', null, true);"><ssf:nlt tag="survey.addQuestion.multiple"/></a>
	<a class="ss_button" href="javascript: ss_survey_${ss_namespace}_${property_name}.ss_newSurveyQuestion('single', '', null, true);"><ssf:nlt tag="survey.addQuestion.single"/></a>
	<a class="ss_button" href="javascript: ss_survey_${ss_namespace}_${property_name}.ss_newSurveyQuestion('input', '', null, true);"><ssf:nlt tag="survey.addQuestion.input"/></a>
</div>
<script type="text/javascript">
	// labels
	var ss_nlt_surveyMoreAnswers = "<ssf:nlt tag='survey.answer.more'/>";
	var ss_nlt_surveyQuestionHeader = "<ssf:nlt tag='survey.question.header'/>";
	var ss_nlt_surveyConfirmRemove = "<ssf:nlt tag='survey.question.confirmRemove'/>";
	var ss_nlt_surveyModifyNotAllowed_alreadyVoted = "<ssf:nlt tag="survey.modify.not.allowed.already.voted"/>";
	
	var ss_survey_${ss_namespace}_${property_name} = new ssSurvey("survey_${ss_namespace}_${property_name}", "ss_surveyForm_questions_${ss_namespace}_${property_name}");
	dojo.addOnLoad(function () {
		ss_survey_${ss_namespace}_${property_name}.initialize("<ssf:escapeJavaScript value="${ssDefinitionEntry.customAttributes[property_name].value}" />");
		ssSurvey.addToOnSubmit(ss_survey_${ss_namespace}_${property_name});
	});
</script>
