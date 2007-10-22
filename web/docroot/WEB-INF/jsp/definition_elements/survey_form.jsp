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
