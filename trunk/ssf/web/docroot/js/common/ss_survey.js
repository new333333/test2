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

if (!window.ssSurvey) {
	function ssSurvey(hiddenInputValueId, surveyContainerId, prefix) {
		
		var inputId = hiddenInputValueId;
		
		var surveyContainerId = surveyContainerId;
	
		var ss_questionsArray = new Array();
		
		var ss_questionsCounter = 0;
		
		var prefix = prefix;
	
		var that = this;
		
		var currentSurvey;
		
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
			var removerLink = document.createElement('a');
			removerLink.href = "javascript: //;";
			dojo.connect(removerLink, "onclick", ss_callRemoveQuestion(that, ss_questionsCounter));
			var removerImg = document.createElement('img');
			removerImg.setAttribute("src", ss_imagesPath + "pics/delete.gif");
			removerLink.appendChild(removerImg);
			questionHeader.appendChild(removerLink);
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
			if (confirm(this.locale.confirmRemove)) {
				ss_questionsArray[index]='undefined';
				var questionContainer = dojo.byId(prefix + "question" + index);
				questionContainer.parentNode.removeChild(questionContainer);
				ss_refreshAllHeaders();
			}
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
			for (var i=0; i<ss_questionsArray.length; i++) {
				if (ss_questionsArray[i].type && ss_questionsArray[i].type != 'undefined') {
					totalQC++;
				}
			}
			var counter=0;
			for (var j=0; j<ss_questionsArray.length; j++) {
				if (ss_questionsArray[j].type && ss_questionsArray[j].type != 'undefined') {
					counter++;
					dojo.byId(prefix + "questionHeaderLabel"+j).innerHTML = that.locale.questionHeader+" "+counter+"/"+totalQC;
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
			ss_setClass(more, "ss_button");
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
			for (var i=0; i<ss_questionsArray.length;i++){
				if (ss_questionsArray[i].type && ss_questionsArray[i].type != 'undefined') {
					ss_toSend[ind] = {};
					ss_toSend[ind].type = ss_questionsArray[i].type;
					content = tinyMCE.getContent(prefix + "questionText"+i).replace(/\+/g, "&#43");
					ss_toSend[ind].question = content;
					questionIndexInput = dojo.byId(prefix + "questionText"+i+"_index");
					if (questionIndexInput) {
						ss_toSend[ind].index = questionIndexInput.value;
					}
					requiredAnswer = dojo.byId(prefix + "answerRequired" + i);
					ss_toSend[ind].answerRequired = requiredAnswer && requiredAnswer.checked;
					if (ss_questionsArray[i].type == 'multiple' || ss_questionsArray[i].type == 'single') {
						ss_toSend[ind].answers = new Array();
						aCounter = 0;
						for (var j=0; j<ss_questionsArray[i].answersNo; j++) {
							if (dojo.byId(prefix + "question"+i+"answer"+j)) {
								var answerIndexInput = dojo.byId(prefix + "question"+i+"answer"+j+"_index");
								ss_toSend[ind].answers[aCounter] = {
									'text' : dojo.byId(prefix + "question"+i+"answer"+j).value
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
			var inputObj = document.getElementById(inputId);
			if (inputObj) {
				inputObj.value = dojo.toJson(
						{
							'questions' : ss_toSend, 
							'viewBeforeDueTime' : viewBeforeDueTime,
							'viewAfterDueTime' : viewAfterDueTime,
							'viewDetails' : viewDetails,
							'allowChange' : allowObj && allowObj.checked
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
			removerImg.setAttribute("src", ss_imagesPath + "pics/delete.gif");
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

ssSurvey.vote = function(formId, binderId, entryId, requiredQuestions, prefix) {
	var missingAnswers = false;
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
		if (!hasAnswer) {
			missingAnswers = true;
			var questionConteinerObj = dojo.byId(prefix + "_question_" + qId);
			dojo.addClass(questionConteinerObj, "ss_survey_required");
		}
	}
	
	if (missingAnswers) {
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

ssSurvey.removeVote = function(formId, binderId, entryId) {
	var urlParams = {operation:"vote_survey_remove", binderId:binderId, entryId:entryId};
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams);

	dojo.xhrGet({
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
		formNode: document.getElementById(formId)
	});
}