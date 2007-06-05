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

<script type="text/javascript">
// labels
var ss_nlt_surveyMoreAnswers = "<ssf:nlt tag='survey.answer.more'/>";
var ss_nlt_surveyQuestionHeader = "<ssf:nlt tag='survey.question.header'/>";
var ss_nlt_surveyConfirmRemove = "<ssf:nlt tag='survey.question.confirmRemove'/>";

var ss_questionsArray = new Array();
var ss_questionsCounter = 0;
</script>

		
<div id="ss_surveyForm_questions">
</div>
<div class="ss_more">
	<a class="ss_button" href="javascript: ss_newSurveyQuestion('multiple', '', true);"><ssf:nlt tag="survey.addQuestion.multiple"/></a>
	<a class="ss_button" href="javascript: ss_newSurveyQuestion('single', '', true);"><ssf:nlt tag="survey.addQuestion.single"/></a>
	<a class="ss_button" href="javascript: ss_newSurveyQuestion('input', '', true);"><ssf:nlt tag="survey.addQuestion.input"/></a>
</div>

