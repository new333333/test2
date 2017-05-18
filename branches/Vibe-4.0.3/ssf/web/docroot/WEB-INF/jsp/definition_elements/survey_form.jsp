<%
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
<c:set var="ss_fieldModifyDisabled" value=""/>
<c:set var="ss_fieldModifyStyle" value=""/>
<c:if test="${ss_accessControlMap['ss_modifyEntryRightsSet']}">
  <c:if test="${(!ss_accessControlMap['ss_modifyEntryFieldsAllowed'] && !ss_accessControlMap['ss_modifyEntryAllowed']) || 
			(!ss_accessControlMap['ss_modifyEntryAllowed'] && !ss_fieldModificationsAllowed == 'true')}">
    <c:set var="ss_fieldModifyStyle" value="ss_modifyDisabled"/>
    <c:set var="ss_fieldModifyInputAttribute" value=" disabled='disabled' "/>
    <c:set var="ss_fieldModifyDisabled" value="true"/>
  </c:if>
</c:if>

<c:set var="surveyModel" value="${ssDefinitionEntry.customAttributes[property_name].value.surveyModel}"/>

<script type="text/javascript" src="<html:rootPath/>js/common/ss_survey.js"></script>

<script type="text/javascript">
var ss_survey_cannotMoveAfterVoting = '<ssf:escapeJavaScript><ssf:nlt tag="survey.cannotMoveAfterVoting.warning" /></ssf:escapeJavaScript>';
</script>

<div class="ss_entryContent ${ss_fieldModifyStyle}">
	<div class="ss_labelAbove ss_normal margintop2" style="font-size: 18px; border-bottom: 1px solid #b8b8b8;"><c:out value="${property_caption}"/></div>


<c:if test="${empty ss_fieldModifyDisabled}">
	<input type="hidden" name="${property_name}" value="" id="survey_${ss_namespace}_${property_name}" />
	
	<div class="margintop3" id="ss_surveyForm_questions_${ss_namespace}_${property_name}">
	</div>	

	<div class="ss_more margintop3">
		<a class="ss_tinyButton" href="javascript: ss_survey_${ss_namespace}_${property_name}.ss_newSurveyQuestion('multiple', '', null, true);"><ssf:nlt tag="survey.addQuestion.multiple"/></a>
		<a class="ss_tinyButton" href="javascript: ss_survey_${ss_namespace}_${property_name}.ss_newSurveyQuestion('single', '', null, true);"><ssf:nlt tag="survey.addQuestion.single"/></a>
		<a class="ss_tinyButton" href="javascript: ss_survey_${ss_namespace}_${property_name}.ss_newSurveyQuestion('input', '', null, true);"><ssf:nlt tag="survey.addQuestion.input"/></a>
	</div>

	<div class="margintop3 ss_padleft5">
		<span class="ss_bold"><ssf:nlt tag="survey.view.results.beforeDue"/></span>
			<div class="margintop1 marginleft1">
				<input type="radio" checked="true" name="beforeDueTime"  id="${ss_namespace}_${property_name}_viewBeforeDueTime_all" /><label for="${ss_namespace}_${property_name}_viewBeforeDueTime_all"><ssf:nlt tag="survey.view.results.beforeDue.all" /></label></div>
			<div class="margintop1 marginleft1">
				<input type="radio" name="beforeDueTime" id="${ss_namespace}_${property_name}_viewBeforeDueTime_voters" /><label for="${ss_namespace}_${property_name}_viewBeforeDueTime_voters"><ssf:nlt tag="survey.view.results.beforeDue.voters" /></label></div>
			<div class="margintop1 marginleft1">
				<input type="radio" name="beforeDueTime" id="${ss_namespace}_${property_name}_viewBeforeDueTime_moderator" /><label for="${ss_namespace}_${property_name}_viewBeforeDueTime_moderator"><ssf:nlt tag="survey.view.results.beforeDue.moderators" /></label></div>
	</div>

	<div class="margintop3 ss_padleft5">
		<span class="ss_bold"><ssf:nlt tag="survey.view.results.afterDue"/></span>
		<div class="margintop1 marginleft1">
			<input type="radio" checked="true" name="afterDueTime" id="${ss_namespace}_${property_name}_viewAfterDueTime_all" /><label for="${ss_namespace}_${property_name}_viewAfterDueTime_all"><ssf:nlt tag="survey.view.results.afterDue.all" /></label></div>
		<div class="margintop1 marginleft1">
			<input type="radio" name="afterDueTime" id="${ss_namespace}_${property_name}_viewAfterDueTime_voters" /><label for="${ss_namespace}_${property_name}_viewAfterDueTime_voters"><ssf:nlt tag="survey.view.results.afterDue.voters" /></label></div>
		<div class="margintop1 marginleft1">
			<input type="radio" name="afterDueTime" id="${ss_namespace}_${property_name}_viewAfterDueTime_moderator" /><label for="${ss_namespace}_${property_name}_viewAfterDueTime_moderator"><ssf:nlt tag="survey.view.results.afterDue.moderators" /></label></div>
	</div>

	<div class="margintop3 ss_padleft5">
		<span class="ss_bold"><ssf:nlt tag="survey.view.results.details"/></span>
		<div class="margintop1 marginleft1">
			<input type="radio" name="resultDetails" id="${ss_namespace}_${property_name}_viewDetails_all" /><label for="${ss_namespace}_${property_name}_viewDetails_all"><ssf:nlt tag="survey.view.results.details.all"/></label></div>
		<div class="margintop1 marginleft1">
			<input type="radio" name="resultDetails" id="${ss_namespace}_${property_name}_viewDetails_voters" /><label for="${ss_namespace}_${property_name}_viewDetails_voters"><ssf:nlt tag="survey.view.results.details.voters"/></label></div>
		<div class="margintop1 marginleft1">
			<input type="radio" checked="true" name="resultDetails" id="${ss_namespace}_${property_name}_viewDetails_moderator" /><label for="${ss_namespace}_${property_name}_viewDetails_moderator"><ssf:nlt tag="survey.view.results.details.moderators"/></label></div>
	</div>

	<div class="margintop3 marginleft1 ss_padleft5">
		<input type="checkbox" name="allowChange" id="${ss_namespace}_${property_name}_allowChange"/> 
		<label for="${ss_namespace}_${property_name}_allowChange"><ssf:nlt tag="survey.modify.label"/></label>
	</div>

	<div class="margintop3 marginleft1 ss_padleft5">
		<input type="checkbox" name="allowMultipleGuestVotes" 
		  id="${ss_namespace}_${property_name}_allowMultipleGuestVotes"/> 
		<label for="${ss_namespace}_${property_name}_allowMultipleGuestVotes"><ssf:nlt tag="survey.multipleGuestVotes.label"/></label>
	</div>

	
	<script type="text/javascript">
		var ss_survey_${ss_namespace}_${property_name} = new ssSurvey("survey_${ss_namespace}_${property_name}", 
				"ss_surveyForm_questions_${ss_namespace}_${property_name}", 
				"${ss_namespace}_${property_name}",
				"${surveyModel.alreadyVoted}");
		// labels
		ss_survey_${ss_namespace}_${property_name}.locale.moreAnswers = "<ssf:nlt tag='survey.answer.more'/>";
		ss_survey_${ss_namespace}_${property_name}.locale.questionHeader = "<ssf:nlt tag='survey.question.header'/>";
		ss_survey_${ss_namespace}_${property_name}.locale.confirmRemove = "<ssf:nlt tag='survey.question.confirmRemove'/>";
		ss_survey_${ss_namespace}_${property_name}.locale.required = " <ssf:nlt tag='survey.required'/>";
		ss_survey_${ss_namespace}_${property_name}.locale.modifySurveyWarning = "<ssf:nlt tag='survey.modify.warning'/>";
		dojo.addOnLoad(function () {
			ss_survey_${ss_namespace}_${property_name}.initialize("<ssf:escapeJavaScript value="${ssDefinitionEntry.customAttributes[property_name].value}" />");
			ssSurvey.addToOnSubmit(ss_survey_${ss_namespace}_${property_name});
		});
	</script>
</c:if>

</div>