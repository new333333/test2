/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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

dojo.require("dojox.validate.web");
dojo.require("dojo.cookie");

if (!window.ssSurvey) {
	function ssSurvey(hiddenInputValueId, surveyContainerId, prefix, votesExist) {
		
		var inputId = hiddenInputValueId;
		
		var surveyContainerId = surveyContainerId;
	
		var ss_questionsArray = new Array();
		var ss_orderArray = new Array();
		
		var ss_questionsCounter = 0;
		
		var prefix = prefix;
	
		var that = this;
		
		var currentSurvey;
		
		var usersHaveAlreadyVoted = votesExist;
		
		this.locale = {
			moreAnswers: "Add more answers",
			questionHeader: "Question",
			confirmRemove: "Do you want remove the question with answers?",
			modifySurveyWarning: "Be carefull with modifing survey! Some people have already voted in this survey. If you remove a questions or an answers you'll lose votes.",
			required: "Answer to this question is required."
		};
		
		this.initialize = function(currentSurveyAsJSONString) {
			if (currentSurveyAsJSONString) {
				currentSurvey = dojo.fromJson(currentSurveyAsJSONString);
				dojo.byId(surveyContainerId).innerHTML = that.locale.modifySurveyWarning;
				ss_initSurveySettings(currentSurvey);
				ss_initSurveyQuestions(currentSurvey.questions);
			}
		}
		
		this.ss_newSurveyQuestion = function(type, questionText, questionIndex, withDefaultAnswers, requiredAnswer) {
			ss_newSurveyQuestion(type, questionText, questionIndex, withDefaultAnswers, requiredAnswer);
		}
		
		function alreadyVoted () {
			if (currentSurvey && currentSurvey.questions) {
				for (var i = 0; i < currentSurvey.questions.length; i++) {
					if (currentSurvey.questions[i].answers) {
						for (var j = 0; j < currentSurvey.questions[i].answers.length; j++) {
							if (currentSurvey.questions[i].answers[j].votedBy &&
									currentSurvey.questions[i].answers[j].votedBy.length > 0) {
								return true;
							}
						}
					}
				}
			}
			return false;
		}

		function ss_newSurveyQuestion(type, questionText, questionIndex, withDefaultAnswers, requiredAnswer) {
			if (!ss_questionsArray[ss_questionsCounter] || ss_questionsArray[ss_questionsCounter] == 'undefined') {
				ss_questionsArray[ss_questionsCounter] = new Array();
				ss_orderArray[ss_questionsCounter] = ss_questionsCounter;
				questionIndex = "" + (ss_questionsCounter+1);
			}
			ss_questionsArray[ss_questionsCounter].type=type;
		
			var questionContainer = document.createElement('div');
			ss_setClass(questionContainer, "ss_questionContainer");
			questionContainer.id = prefix + "question" + ss_questionsCounter;
			dojo.byId(surveyContainerId).appendChild(questionContainer);
			ss_addQuestionHeader(questionContainer);
			ss_addQuestionDescription(questionContainer, questionText, questionIndex);
			ss_addAnswerRequired(questionContainer, questionText, questionIndex, requiredAnswer);
			ss_addQuestionAnswers(type, ss_questionsCounter, withDefaultAnswers);
			ss_refreshAllHeaders();
			ss_questionsCounter++;
		}
		
		function ss_addQuestionHeader (questionContainer) {
			var questionHeader = document.createElement('h4');
			questionHeader.id = prefix + "questionHeader"+ss_questionsCounter;
			if (1 == 1 || !usersHaveAlreadyVoted) {
				//Show the links. But they will give an error message saying that you cannot move questions after anyone has voted
				var removerLink = document.createElement('a');
				removerLink.href = "javascript: //;";
				dojo.connect(removerLink, "onclick", ss_callRemoveQuestion(that, ss_questionsCounter));
				var removerImg = document.createElement('img');
				removerImg.setAttribute("src", ss_imagesPath + "pics/delete.png");
				removerImg.setAttribute("align", "absmiddle");
				removerLink.appendChild(removerImg);
				questionHeader.appendChild(removerLink);
				
				var upLink = document.createElement('a');
				upLink.href = "javascript: //;";
				dojo.connect(upLink, "onclick", ss_callMoveQuestion(that, ss_questionsCounter, true));
				var upImg = document.createElement('img');
				upImg.setAttribute("src", ss_imagesPath + "pics/sym_s_up.gif");
				upImg.setAttribute("align", "absmiddle");
				upLink.appendChild(upImg);
				questionHeader.appendChild(upLink);
	
				var downLink = document.createElement('a');
				downLink.href = "javascript: //;";
				dojo.connect(downLink, "onclick", ss_callMoveQuestion(that, ss_questionsCounter, false));
				var downImg = document.createElement('img');
				downImg.setAttribute("src", ss_imagesPath + "pics/sym_s_down.gif");
				downImg.setAttribute("align", "absmiddle");
				downLink.appendChild(downImg);
				questionHeader.appendChild(downLink);
			}

			var label = document.createElement('span');
			label.id = prefix + "questionHeaderLabel"+ss_questionsCounter;
			label.appendChild(document.createTextNode(that.locale.questionHeader));
			questionHeader.appendChild(label);
			questionContainer.appendChild(questionHeader);
		}
		
		function ss_callRemoveQuestion(obj, index) {
			return function(evt) {obj.ss_removeQuestion(index);};
		}
		
		this.ss_removeQuestion = function(index) {
			if (usersHaveAlreadyVoted && usersHaveAlreadyVoted != "false") {
				alert(ss_survey_cannotMoveAfterVoting);
				return;
			}
			if (confirm(this.locale.confirmRemove)) {
				ss_questionsArray[index]='undefined';
				var questionContainer = dojo.byId(prefix + "question" + index);
				questionContainer.parentNode.removeChild(questionContainer);
				ss_refreshAllHeaders();
			}
		}

		function ss_callMoveQuestion(obj, index, directionUp) {
			return function(evt) {obj.ss_moveQuestion(index, directionUp);};
		}

		this.ss_moveQuestion = function(index, directionUp) {
			if (usersHaveAlreadyVoted && usersHaveAlreadyVoted != "false") {
				alert(ss_survey_cannotMoveAfterVoting);
				return;
			}
			var orderIndex = -1;
			//Find where this question is in the order
			for (i=0; i<ss_orderArray.length; i++) {
				if (ss_orderArray[i] == index) {
					orderIndex = i;
					break;
				}
			}
			//Make sure the question isn't at the top or bottom
			if (orderIndex < 0 || (directionUp && orderIndex <= 0)) return;
			if (!directionUp && orderIndex >= ss_questionsArray.length - 1) return;
			//It is ok to move
			if (directionUp) {
				var firstItem = ss_orderArray[(orderIndex-1)];
				ss_orderArray[(orderIndex-1)] = ss_orderArray[(orderIndex)];
				ss_orderArray[(orderIndex)] = firstItem;
			} else {
				var firstItem = ss_orderArray[(orderIndex)];
				ss_orderArray[(orderIndex)] = ss_orderArray[(orderIndex+1)];
				ss_orderArray[(orderIndex+1)] = firstItem;
			}
			ss_refreshAllHeaders();
		}

		function ss_addQuestionDescription(questionContainer, questionText, questionIndex) {
			var question = document.createElement('textarea');
			if (questionText) {
				question.value = questionText;
			}
			question.id = prefix + "questionText"+ss_questionsCounter;
			questionContainer.appendChild(question);
			tinyMCE.execCommand("mceAddControl", false, question.id);
			if (questionIndex) {
				var questionIndexInput = document.createElement('input');
				questionIndexInput.setAttribute("type", "hidden");
				questionIndexInput.id = prefix + "questionText"+ss_questionsCounter+"_index";
				questionIndexInput.value = questionIndex;
				questionContainer.appendChild(questionIndexInput);
			}
		}
		
		function ss_addAnswerRequired(questionContainer, questionText, questionIndex, value) {
			var id = prefix + "answerRequired"+ss_questionsCounter;
			var requiredObj = document.createElement('input');
			requiredObj.type = "checkbox";
			requiredObj.style.width = "auto";
			requiredObj.id = id;
			questionContainer.appendChild(requiredObj);
			requiredObj.checked = value;
			var labelObj = document.createElement('label');
			labelObj.htmlFor = id;
			labelObj.appendChild(document.createTextNode(that.locale.required));
			questionContainer.appendChild(labelObj);
		}
		
		
		function ss_refreshAllHeaders() {
			var totalQC =0;
			for (var i=0; i<ss_orderArray.length; i++) {
				var index = ss_orderArray[i];
				if (ss_questionsArray[index].type && ss_questionsArray[index].type != 'undefined') {
					totalQC++;
				}
			}
			var questionNum = 1;
			for (var j=0; j<ss_orderArray.length; j++) {
				var index = ss_orderArray[j];
				if (ss_questionsArray[index].type && ss_questionsArray[index].type != 'undefined') {
					dojo.byId(prefix + "questionHeaderLabel"+index).innerHTML = that.locale.questionHeader+" "+(questionNum)+"/"+totalQC;
					questionNum++;
				}
			}
		}
		
		function ss_addQuestionAnswers(type, index, withDefaultOptions) {
			if (type == "multiple" || type == "single") {
				ss_addDefaultAnswers(index, withDefaultOptions);
			}
		}
		
		function ss_addDefaultAnswers(index, withDefaultOptions) {
			var more = document.createElement('a');
			ss_setClass(more, "ss_tinyButton");
			dojo.connect(more, "onclick", ss_callAddAnswerOption(that, index));
			more.appendChild(document.createTextNode(that.locale.moreAnswers));
			var answersList = document.createElement('ol');
			answersList.id = prefix + "answers"+index;
			dojo.byId(prefix + 'question'+index).appendChild(answersList);
			if (withDefaultOptions) {
				ss_addAnswerOption(index);
				ss_addAnswerOption(index);
				ss_addAnswerOption(index);
			}
			dojo.byId(prefix + 'question'+index).appendChild(more);	
		}
		
		function ss_callAddAnswerOption(obj, index) {
			return function(evt) {obj.ss_addAnswerOption(index);};
		}

		
		function ss_callRemoveAnswer(obj, questionNo, answerNo) {
			return function(evt) {obj.ss_removeAnswer(questionNo, answerNo);};
		}
		
		this.ss_removeAnswer = function(questionNo, answerNo) {
			var li = dojo.byId(prefix + "option_question"+questionNo+"answer"+answerNo);
			li.parentNode.removeChild(li);
		}
		
		this.prepareSubmit = function(obj) {
			var ss_toSend = new Array();
			var ind = 0;
			var aCounter = 0;
			var content;
			var questionIndexInput;
			var requiredAnswer;
			for (var i=0; i<ss_orderArray.length;i++){
				var index = ss_orderArray[i];
				if (ss_questionsArray[index].type && ss_questionsArray[index].type != 'undefined') {
					ss_toSend[ind] = {};
					ss_toSend[ind].type = ss_questionsArray[index].type;
					content = tinyMCE.get(prefix + "questionText"+index).getContent().replace(/\+/g, "&#43");
					ss_toSend[ind].question = content;
					questionIndexInput = dojo.byId(prefix + "questionText"+index+"_index");
					if (questionIndexInput) {
						ss_toSend[ind].index = questionIndexInput.value;
					}
					requiredAnswer = dojo.byId(prefix + "answerRequired" + index);
					ss_toSend[ind].answerRequired = requiredAnswer && requiredAnswer.checked;
					if (ss_questionsArray[index].type == 'multiple' || ss_questionsArray[index].type == 'single') {
						ss_toSend[ind].answers = new Array();
						aCounter = 0;
						for (var j=0; j<ss_questionsArray[index].answersNo; j++) {
							var answerObj = dojo.byId(prefix + "question"+index+"answer"+j);
							if (answerObj && answerObj.value && trim(answerObj.value)) {
								var answerIndexInput = dojo.byId(prefix + "question"+index+"answer"+j+"_index");
								ss_toSend[ind].answers[aCounter] = {
									'text' : trim(answerObj.value)
								};
								if (answerIndexInput) {
									ss_toSend[ind].answers[aCounter].index = answerIndexInput.value;
								}
								aCounter++;
							}
						}
					}
					ind++;
				}
			}
			
			var ids = ["all", "voters", "moderator"];
			for (var i = 0; i < ids.length; i++) {
				var rights = dojo.byId(prefix + "_viewBeforeDueTime_" + ids[i]);
				if (rights && rights.checked) {
					var viewBeforeDueTime = ids[i];
				}
			}
					
			var ids = ["all", "voters", "moderator"];
			for (var i = 0; i < ids.length; i++) {
				var rights = dojo.byId(prefix + "_viewAfterDueTime_" + ids[i]);
				if (rights && rights.checked) {
					var viewAfterDueTime = ids[i];
				}
			}
			
			var ids = ["all", "voters", "moderator"];
			for (var i = 0; i < ids.length; i++) {
				var rights = dojo.byId(prefix + "_viewDetails_" + ids[i]);
				if (rights && rights.checked) {
					var viewDetails = ids[i];
				}
			}	
			
			var allowObj = dojo.byId(prefix + "_allowChange");		
			var allowMultipleGuestVotesObj = dojo.byId(prefix + "_allowMultipleGuestVotes");		
			//alert(allowMultipleGuestVotesObj)
			//alert(allowMultipleGuestVotesObj.checked)
			var inputObj = document.getElementById(inputId);
			if (inputObj) {
				inputObj.value = dojo.toJson(
						{
							'questions' : ss_toSend, 
							'viewBeforeDueTime' : viewBeforeDueTime,
							'viewAfterDueTime' : viewAfterDueTime,
							'viewDetails' : viewDetails,
							'allowChange' : allowObj && allowObj.checked,
							'allowMultipleGuestVotes' : allowMultipleGuestVotesObj && allowMultipleGuestVotesObj.checked
						}
				);
			}
			return ss_onSubmit(obj);
		}
		
		function ss_initSurveyQuestions(questionsArray) {
			for (var i=0; i<questionsArray.length; i++) {
				ss_newSurveyQuestion(questionsArray[i].type, questionsArray[i].question, questionsArray[i].index,  false, questionsArray[i].answerRequired);
				
				if (questionsArray[i].type == 'multiple' || questionsArray[i].type == 'single') {
					for (var j=0; j<questionsArray[i].answers.length; j++) {
						ss_addAnswerOption(ss_questionsCounter-1, questionsArray[i].answers[j]);
					}
				}
			}
		}
		
		function ss_initSurveySettings(currentSurvey) {
			var rights = dojo.byId(prefix + "_viewBeforeDueTime_" + currentSurvey.viewBeforeDueTime);
			if (rights) {
				rights.checked = true;
			}
					
			rights = dojo.byId(prefix + "_viewAfterDueTime_" + currentSurvey.viewAfterDueTime);
			if (rights) {
				rights.checked = true;
			}
			
			rights = dojo.byId(prefix + "_viewDetails_" + currentSurvey.viewDetails);
			if (rights) {
				rights.checked = true;
			}
			
			rights = dojo.byId(prefix + "_allowChange");		
			if (rights) {
				rights.checked = currentSurvey.allowChange;
			}
			
			rights = dojo.byId(prefix + "_allowMultipleGuestVotes");		
			if (rights) {
				rights.checked = currentSurvey.allowMultipleGuestVotes;
			}
		}		
		
		function ss_addAnswerOption(index, value) {
			that.ss_addAnswerOption(index, value);
		}
		
		this.ss_addAnswerOption = function(index, value) {
			var lastAnswerNo = 0;
			if (ss_questionsArray[index] && ss_questionsArray[index].answersNo && ss_questionsArray[index].answersNo != 'undefined') {
				lastAnswerNo = ss_questionsArray[index].answersNo;
			}
			var answer = document.createElement('li');
			answer.id = prefix + "option_question"+index+"answer"+lastAnswerNo;
		
			var removerLink = document.createElement('a');
			removerLink.href = "javascript: //;";
			dojo.connect(removerLink, "onclick", ss_callRemoveAnswer(that, index, lastAnswerNo));
			var removerImg = document.createElement('img');
			removerImg.setAttribute("src", ss_imagesPath + "pics/delete.png");
			removerLink.appendChild(removerImg);
			answer.appendChild(removerLink);
		
			var newOption = document.createElement('input');
			newOption.name = "question"+index+"answer"+lastAnswerNo;
			newOption.id = prefix + "question"+index+"answer"+lastAnswerNo;
			
			if (value) {
				newOption.value = value.text;
			}
			
			
			
			answer.appendChild(newOption);
			if (value && value.index) {
				var newOptionIndex = document.createElement('input');
				newOptionIndex.setAttribute("type", "hidden");
				newOptionIndex.id = prefix + "question"+index+"answer"+lastAnswerNo+"_index";
				newOptionIndex.value = value.index;
				answer.appendChild(newOptionIndex);
			}
			dojo.byId(prefix + 'answers'+index).appendChild(answer);
			
			lastAnswerNo++;
			ss_questionsArray[index].answersNo = lastAnswerNo;
		}
	}
	


}

if (!window["ssCurrentFormSurveys"]) {
	var ssCurrentFormSurveys = new Array();	
}

ssSurvey.addToOnSubmit = function(surveyObj) {
	ssCurrentFormSurveys.push(surveyObj);
}

ssSurvey.prepareSubmit = function(formObj) {
	for (var i = 0; i < ssCurrentFormSurveys.length; i++) {
		ssCurrentFormSurveys[i].prepareSubmit(formObj);
	}
}

ssSurvey.clearAnswers = function(questionIndex, answerIndexes, prefix) {
	if (answerIndexes.length > 0) {
		for (var i = 0; i < answerIndexes.length; i++) {
			var answerObj = dojo.byId(prefix + "_answer_" + questionIndex + "_" + answerIndexes[i]);
			answerObj.checked = false;
		}
	} else {
		var answerObj = dojo.byId(prefix + "_answer_" + questionIndex);
		if (answerObj) {
			answerObj.value = '';
		}
	}
}

ssSurvey.voteViaEnter = function(evt, formId, binderId, entryId, requiredQuestions, prefix) {
    evt = (evt) ? evt : event;
    var charCode = (evt.charCode) ? evt.charCode : ((evt.which) ? evt.which : evt.keyCode);
    if (charCode == 13) {
		window.setTimeout(function() {
			ssSurvey.vote(formId, binderId, entryId, requiredQuestions, prefix);
		}, 100);
		return false;
    }
    return true;
}

ssSurvey.vote = function(formId, binderId, entryId, requiredQuestions, prefix) {
	var missingAnswers = false;
	
	var guestUserEmail = dojo.byId(prefix + "_guest_email");
	if (guestUserEmail && (!guestUserEmail.value || guestUserEmail.value == "" || !dojox.validate.isEmailAddress(guestUserEmail.value))) {
		missingAnswers = true;
		var guestUserEmailContainer = dojo.byId(prefix + "_guest_email_container");
		if (guestUserEmailContainer) {
			dojo.addClass(guestUserEmailContainer, "ss_survey_required");
		}
	}
	
	for (qId in requiredQuestions) {
		var hasAnswer = false;
		if (requiredQuestions[qId].length > 0) {
			for (var i = 0; i < requiredQuestions[qId].length; i++) {
				var answerObj = dojo.byId(prefix + "_answer_" + qId + "_" + requiredQuestions[qId][i]);
				if (answerObj && answerObj.checked) {
					hasAnswer = true;
				}
			}
		} else {
			var answerObj = dojo.byId(prefix + "_answer_" + qId);
			if (answerObj && answerObj.value != '') {
				hasAnswer = true;
			}
		}
		var questionConteinerObj = dojo.byId(prefix + "_question_" + qId);
		if (!hasAnswer) {
			missingAnswers = true;
			dojo.addClass(questionConteinerObj, "ss_survey_required");
			break;
		}
		dojo.removeClass(questionConteinerObj, "ss_survey_required")
	}
	
	if (missingAnswers) {
		alert(ss_survey_requiredMissingWarning);
		return false;
	}


	var urlParams = {operation:"vote_survey", binderId:binderId, entryId:entryId};
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams);

	dojo.xhrGet({
    	url: url,
		handleAs: "json-comment-filtered",
		error: function(err) {
			alert(ss_not_logged_in);
		},
		load: function(data) {
			if (data && data.notLoggedIn) {
				alert(ss_not_logged_in);
			} else if (data && data.status == "ok") {
				if (guestUserEmail && guestUserEmail.value) {
					dojo.cookie("Vote-" + binderId + "-" + entryId, guestUserEmail.value, { expires: 10000 });				
				}
				if (window["ss_reloadUrl"]) {
					document.location.href = ss_replaceSubStr(ss_reloadUrl, "ss_randomPlaceholder", ss_random++);
				}
			} else {
				alert(data.statusMsg);
			}
		},
		preventCache: true,
		form: document.getElementById(formId)
	});
}

ssSurvey.removeVote = function(formId, binderId, entryId) {
	var urlParams = {operation:"vote_survey_remove", binderId:binderId, entryId:entryId};
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams);

	dojo.xhrPost({
    	url: url,
		handleAs: "json-comment-filtered",		
		error: function(err) {
			alert(ss_not_logged_in);
		},
		load: function(data) {
			if (data.notLoggedIn) {
				alert(ss_not_logged_in);
			} else if (window["ss_reloadUrl"]) {
				ss_random++;
				url = ss_replaceSubStr(ss_reloadUrl, "ss_randomPlaceholder", ss_random);
				document.location.href = ss_reloadUrl;
			}
		},
		preventCache: true,
		form: document.getElementById(formId)
	});
}

function trim(stringToTrim) {
	return stringToTrim.replace(/^\s+|\s+$/g,"");
}
