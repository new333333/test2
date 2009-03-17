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

<div class="ss_entryContent">
	<div class="ss_labelAbove"><c:out value="${property_caption}"/></div>

	<p>
		<ssf:nlt tag="survey.view.results.beforeDue"/><br>
		<input type="radio" checked="true" name="beforeDueTime"  id="${ss_namespace}_${property_name}_viewBeforeDueTime_all" /><label for="${ss_namespace}_${property_name}_viewBeforeDueTime_all"><ssf:nlt tag="survey.view.results.beforeDue.all" /></label>
		<input type="radio" name="beforeDueTime" id="${ss_namespace}_${property_name}_viewBeforeDueTime_voters" /><label for="${ss_namespace}_${property_name}_viewBeforeDueTime_voters"><ssf:nlt tag="survey.view.results.beforeDue.voters" /></label>
		<input type="radio" name="beforeDueTime" id="${ss_namespace}_${property_name}_viewBeforeDueTime_moderator" /><label for="${ss_namespace}_${property_name}_viewBeforeDueTime_moderator"><ssf:nlt tag="survey.view.results.beforeDue.moderators" /></label>
	</p>

	<p>
		<ssf:nlt tag="survey.view.results.afterDue"/><br>
		<input type="radio" checked="true" name="afterDueTime" id="${ss_namespace}_${property_name}_viewAfterDueTime_all" /><label for="${ss_namespace}_${property_name}_viewAfterDueTime_all"><ssf:nlt tag="survey.view.results.afterDue.all" /></label>
		<input type="radio" name="afterDueTime" id="${ss_namespace}_${property_name}_viewAfterDueTime_voters" /><label for="${ss_namespace}_${property_name}_viewAfterDueTime_voters"><ssf:nlt tag="survey.view.results.afterDue.voters" /></label>
		<input type="radio" name="afterDueTime" id="${ss_namespace}_${property_name}_viewAfterDueTime_moderator" /><label for="${ss_namespace}_${property_name}_viewAfterDueTime_moderator"><ssf:nlt tag="survey.view.results.afterDue.moderators" /></label>
	</p>

	<p>
		<ssf:nlt tag="survey.view.results.details"/><br>
		<input type="radio" name="resultDetails" id="${ss_namespace}_${property_name}_viewDetails_all" /><label for="${ss_namespace}_${property_name}_viewDetails_all"><ssf:nlt tag="survey.view.results.details.all"/></label>
		<input type="radio" name="resultDetails" id="${ss_namespace}_${property_name}_viewDetails_voters" /><label for="${ss_namespace}_${property_name}_viewDetails_voters"><ssf:nlt tag="survey.view.results.details.voters"/></label>
		<input type="radio" checked="true" name="resultDetails" id="${ss_namespace}_${property_name}_viewDetails_moderator" /><label for="${ss_namespace}_${property_name}_viewDetails_moderator"><ssf:nlt tag="survey.view.results.details.moderators"/></label>
	</p>

	<p>
		<input type="checkbox" name="allowChange" id="${ss_namespace}_${property_name}_allowChange"/> <label for="${ss_namespace}_${property_name}_allowChange"><ssf:nlt tag="survey.modify.label"/>
	</p>

	<input type="hidden" name="${property_name}" value="" id="survey_${ss_namespace}_${property_name}" />
	<div id="ss_surveyForm_questions_${ss_namespace}_${property_name}">
	</div>
	
	<div class="ss_more">
		<a class="ss_button" href="javascript: ss_survey_${ss_namespace}_${property_name}.ss_newSurveyQuestion('multiple', '', null, true);"><ssf:nlt tag="survey.addQuestion.multiple"/></a>
		<a class="ss_button" href="javascript: ss_survey_${ss_namespace}_${property_name}.ss_newSurveyQuestion('single', '', null, true);"><ssf:nlt tag="survey.addQuestion.single"/></a>
		<a class="ss_button" href="javascript: ss_survey_${ss_namespace}_${property_name}.ss_newSurveyQuestion('input', '', null, true);"><ssf:nlt tag="survey.addQuestion.input"/></a>
	</div>
	
	
	<script type="text/javascript">
		var ss_survey_${ss_namespace}_${property_name} = new ssSurvey("survey_${ss_namespace}_${property_name}", "ss_surveyForm_questions_${ss_namespace}_${property_name}", "${ss_namespace}_${property_name}");
		// labels
		ss_survey_${ss_namespace}_${property_name}.locale.moreAnswers = "<ssf:nlt tag='survey.answer.more'/>";
		ss_survey_${ss_namespace}_${property_name}.locale.questionHeader = "<ssf:nlt tag='survey.question.header'/>";
		ss_survey_${ss_namespace}_${property_name}.locale.confirmRemove = "<ssf:nlt tag='survey.question.confirmRemove'/>";
		ss_survey_${ss_namespace}_${property_name}.locale.required = "<ssf:nlt tag='survey.required'/>";
		ss_survey_${ss_namespace}_${property_name}.locale.modifySurveyWarning = "<ssf:nlt tag='survey.modify.warning'/>";
		dojo.addOnLoad(function () {
			ss_survey_${ss_namespace}_${property_name}.initialize("<ssf:escapeJavaScript value="${ssDefinitionEntry.customAttributes[property_name].value}" />");
			ssSurvey.addToOnSubmit(ss_survey_${ss_namespace}_${property_name});
		});
	</script>

</div>