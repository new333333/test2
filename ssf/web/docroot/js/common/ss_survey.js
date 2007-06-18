dojo.require("dojo.json");

if (!window.ssSurvey) {

	function ssSurvey(hiddenInputValueId, surveyContainerId) {
		
		var inputId = hiddenInputValueId;
		
		var surveyContainerId = surveyContainerId;
	
		var ss_questionsArray = new Array();
		
		var ss_questionsCounter = 0;
	
		var that = this;
		
		this.initialize = function(currentSurveyAsJSONString) {
			if (currentSurveyAsJSONString) {
				var currentSurvey = dojo.json.evalJson(currentSurveyAsJSONString);
				ss_initSurveyQuestions(currentSurvey.questions);
			}
		}
		
		this.ss_newSurveyQuestion = function(type, questionText, questionIndex, withDefaultAnswers) {
			ss_newSurveyQuestion(type, questionText, questionIndex, withDefaultAnswers);
		}

		function ss_newSurveyQuestion(type, questionText, questionIndex, withDefaultAnswers) {
			if (!ss_questionsArray[ss_questionsCounter] || ss_questionsArray[ss_questionsCounter] == 'undefined') {
				ss_questionsArray[ss_questionsCounter] = new Array();
			}
			ss_questionsArray[ss_questionsCounter].type=type;
		
			var questionContainer = document.createElement('div');
			dojo.html.setClass(questionContainer, "ss_questionContainer");
			questionContainer.id = "question"+ss_questionsCounter;
			dojo.byId(surveyContainerId).appendChild(questionContainer);
			ss_addQuestionHeader(questionContainer);
			ss_addQuestionDescription(questionContainer, questionText, questionIndex);
			ss_addQuestionAnswers(type, ss_questionsCounter, withDefaultAnswers);
			ss_refreshAllHeaders();
			ss_questionsCounter++;
		}
		
		function ss_addQuestionHeader (questionContainer) {
			var questionHeader = document.createElement('h4');
			questionHeader.id = "questionHeader"+ss_questionsCounter;
			var removerLink = document.createElement('a');
			removerLink.href = "javascript: //;";
			dojo.event.connect(removerLink, "onclick", ss_callRemoveQuestion(that, ss_questionsCounter));
			var removerImg = document.createElement('img');
			removerImg.setAttribute("src", ss_imagesPath + "pics/delete.gif");
			removerLink.appendChild(removerImg);
			questionHeader.appendChild(removerLink);
			var label = document.createElement('span');
			label.id = "questionHeaderLabel"+ss_questionsCounter;
			label.appendChild(document.createTextNode(ss_nlt_surveyQuestionHeader));
			questionHeader.appendChild(label);
			questionContainer.appendChild(questionHeader);
		}
		
		function ss_callRemoveQuestion(obj, index) {
			return function(evt) {obj.ss_removeQuestion(index);};
		}
		
		this.ss_removeQuestion = function(index) {
			if (confirm(ss_nlt_surveyConfirmRemove)) {
				ss_questionsArray[index]='undefined';
				var questionContainer = dojo.byId("question"+index);
				questionContainer.parentNode.removeChild(questionContainer);
				ss_refreshAllHeaders();
			}
		}
		
		function ss_addQuestionDescription(questionContainer, questionText, questionIndex) {
			var question = document.createElement('textarea');
			if (questionText) {
				question.value = questionText;
			}
			question.id = "questionText"+ss_questionsCounter;
			questionContainer.appendChild(question);
			tinyMCE.execCommand("mceAddControl", false, question.id);
			if (questionIndex) {
				var questionIndexInput = document.createElement('input');
				questionIndexInput.setAttribute("type", "hidden");
				questionIndexInput.id = "questionText"+ss_questionsCounter+"_index";
				questionIndexInput.value = questionIndex;
				questionContainer.appendChild(questionIndexInput);
			}
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
					dojo.byId("questionHeaderLabel"+j).innerHTML = ss_nlt_surveyQuestionHeader+" "+counter+"/"+totalQC;
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
			dojo.html.setClass(more, "ss_button");
			dojo.event.connect(more, "onclick", ss_callAddAnswerOption(that, index));
			more.appendChild(document.createTextNode(ss_nlt_surveyMoreAnswers));
			var answersList = document.createElement('ol');
			answersList.id = "answers"+index;
			dojo.byId('question'+index).appendChild(answersList);
			if (withDefaultOptions) {
				ss_addAnswerOption(index);
				ss_addAnswerOption(index);
				ss_addAnswerOption(index);
			}
			dojo.byId('question'+index).appendChild(more);	
		}
		
		function ss_callAddAnswerOption(obj, index) {
			return function(evt) {obj.ss_addAnswerOption(index);};
		}

		
		function ss_callRemoveAnswer(obj, questionNo, answerNo) {
			return function(evt) {obj.ss_removeAnswer(questionNo, answerNo);};
		}
		
		this.ss_removeAnswer = function(questionNo, answerNo) {
			var li = dojo.byId("option_question"+questionNo+"answer"+answerNo);
			li.parentNode.removeChild(li);
		}
		
		this.prepareSubmit = function(obj) {
			var ss_toSend = new Array();
			var ind = 0;
			var aCounter = 0;
			var content;
			var questionIndexInput;
			for (var i=0; i<ss_questionsArray.length;i++){
				if (ss_questionsArray[i].type && ss_questionsArray[i].type != 'undefined') {
					ss_toSend[ind] = {};
					ss_toSend[ind].type = ss_questionsArray[i].type;
					content = tinyMCE.getContent("questionText"+i).replace(/\+/g, "&#43");
					ss_toSend[ind].question = content;
					questionIndexInput = dojo.byId("questionText"+i+"_index");
					if (questionIndexInput) {
						ss_toSend[ind].index = questionIndexInput.value;
					}
					if (ss_questionsArray[i].type == 'multiple' || ss_questionsArray[i].type == 'single') {
						ss_toSend[ind].answers = new Array();
						aCounter = 0;
						for (var j=0; j<ss_questionsArray[i].answersNo; j++) {
							if (dojo.byId("question"+i+"answer"+j)) {
								var answerIndexInput = dojo.byId("question"+i+"answer"+j+"_index");
								ss_toSend[ind].answers[aCounter] = {
									'text' : dojo.byId("question"+i+"answer"+j).value
								};
								if (dojo.byId("question"+i+"answer"+j+"_index")) {
									ss_toSend[ind].answers[aCounter].index = dojo.byId("question"+i+"answer"+j+"_index").value;
								}
								aCounter++;
							}
						}
					}
					ind++;
				}
			}
			var inputObj = document.getElementById(inputId);
			if (inputObj) {
				inputObj.value = dojo.json.serialize({'questions' : ss_toSend});
			}
			return ss_onSubmit(obj);
		}
		
		function ss_initSurveyQuestions(questionsArray) {
			for (var i=0; i<questionsArray.length; i++) {
				ss_newSurveyQuestion(questionsArray[i].type, questionsArray[i].question, questionsArray[i].index,  false);
				
				if (questionsArray[i].type == 'multiple' || questionsArray[i].type == 'single') {
					for (var j=0; j<questionsArray[i].answers.length; j++) {
						ss_addAnswerOption(ss_questionsCounter-1, questionsArray[i].answers[j]);
					}
				}
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
			answer.id="option_question"+index+"answer"+lastAnswerNo;
		
			var removerLink = document.createElement('a');
			removerLink.href = "javascript: //;";
			dojo.event.connect(removerLink, "onclick", ss_callRemoveAnswer(that, index, lastAnswerNo));
			var removerImg = document.createElement('img');
			removerImg.setAttribute("src", ss_imagesPath + "pics/delete.gif");
			removerLink.appendChild(removerImg);
			answer.appendChild(removerLink);
		
			var newOption = document.createElement('input');
			newOption.name = "question"+index+"answer"+lastAnswerNo;
			newOption.id = "question"+index+"answer"+lastAnswerNo;
			
			var newOptionIndex = document.createElement('input');
			newOptionIndex.setAttribute("type", "hidden");
			newOptionIndex.id = "question"+index+"answer"+lastAnswerNo+"_index";
			
			if (value) {
				newOption.value = value.text;
				newOptionIndex.value = value.index;
			}
			
			lastAnswerNo++;
			ss_questionsArray[index].answersNo = lastAnswerNo;
			answer.appendChild(newOption);
			answer.appendChild(newOptionIndex);
			dojo.byId('answers'+index).appendChild(answer);
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
	for (var i in ssCurrentFormSurveys) {
		ssCurrentFormSurveys[i].prepareSubmit(formObj);
	}
}

ssSurvey.vote = function(formId, binderId, entryId) {
	var url = ss_AjaxBaseUrl + "&operation=vote_survey";
	url += "\&binderId=" + binderId;
	url += "\&entryId=" + entryId;
	
	dojo.io.bind({
    	url: url,
		error: function(type, data, evt) {
			alert(ss_not_logged_in);
		},
		load: function(type, data, evt) {
			alert(ssSurvey.votedLabel);
			try { window.close(); } catch (e){}
			try { parent.ss_hideEntryDiv(); } catch (e){}
		},
		mimetype: "text/json",
		formNode: document.getElementById(formId)
	});
}
