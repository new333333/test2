function ss_newSurveyQuestion(type, questionText, withDefaultAnswers) {
	if (!ss_questionsArray[ss_questionsCounter] || ss_questionsArray[ss_questionsCounter] == 'undefined') {
		ss_questionsArray[ss_questionsCounter] = new Array();
	}
	ss_questionsArray[ss_questionsCounter].type=type;

	var questionContainer = document.createElement('div');
	dojo.html.setClass(questionContainer, "questionContainer");
	questionContainer.id = "question"+ss_questionsCounter;
	dojo.byId('ss_surveyForm_questions').appendChild(questionContainer);
	ss_addQuestionHeader(questionContainer);
	ss_addQuestionDescription(questionContainer, questionText);
	ss_addQuestionAnswers(type, ss_questionsCounter, withDefaultAnswers);
	ss_refreshAllHeaders();
	ss_questionsCounter++;
}
function ss_addQuestionHeader(questionContainer) {
	var questionHeader = document.createElement('h4');
	questionHeader.id = "questionHeader"+ss_questionsCounter;
	var removerLink = document.createElement('a');
	removerLink.href = "javascript: //;";
	dojo.event.connect(removerLink, "onclick", ss_callRemoveQuestion(ss_questionsCounter));
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
function ss_callRemoveQuestion(index) {
	return function(evt) {ss_removeQuestion(index);};
}
function ss_removeQuestion(index) {
	if (confirm(ss_nlt_surveyConfirmRemove)) {
		ss_questionsArray[index]='undefined';
		var questionContainer = dojo.byId("question"+index);
		questionContainer.parentNode.removeChild(questionContainer);
		ss_refreshAllHeaders();
	}
}
function ss_addQuestionDescription(questionContainer,questionText) {
	var question = document.createElement('textarea');
	if (questionText) {
		question.value = questionText;
	}
	dojo.html.setClass(question, "mceEditable");
	question.id = "questionText"+ss_questionsCounter;
	questionContainer.appendChild(question);
	tinyMCE.execCommand("mceAddControl", false, question.id);
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
	dojo.event.connect(more, "onclick", ss_callAddAnswerOption(index));
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
function ss_callAddAnswerOption(index) {
	return function(evt) {ss_addAnswerOption(index);};
}
function ss_addAnswerOption(index, value) {
	var lastAnswerNo = 0;
	if (ss_questionsArray[index] && ss_questionsArray[index].answersNo && ss_questionsArray[index].answersNo != 'undefined') {
		lastAnswerNo = ss_questionsArray[index].answersNo;
	}
	var answer = document.createElement('li');
	answer.id="option_question"+index+"answer"+lastAnswerNo;

	var removerLink = document.createElement('a');
	removerLink.href = "javascript: //;";
	dojo.event.connect(removerLink, "onclick", ss_callRemoveAnswer(index, lastAnswerNo));
	var removerImg = document.createElement('img');
	removerImg.setAttribute("src", ss_imagesPath + "pics/delete.gif");
	removerLink.appendChild(removerImg);
	answer.appendChild(removerLink);

	var newOption = document.createElement('input');
	newOption.name = "question"+index+"answer"+lastAnswerNo;
	newOption.id = "question"+index+"answer"+lastAnswerNo;
	if (value) {
		newOption.value = value;
	}
	lastAnswerNo++;
	ss_questionsArray[index].answersNo = lastAnswerNo;
	answer.appendChild(newOption);
	dojo.byId('answers'+index).appendChild(answer);
}
function ss_callRemoveAnswer(questionNo, answerNo) {
	return function(evt) {ss_removeAnswer(questionNo, answerNo);};
}
function ss_removeAnswer(questionNo, answerNo) {
	var li = dojo.byId("option_question"+questionNo+"answer"+answerNo);
	li.parentNode.removeChild(li);
}

function ss_prepareSubmit(obj) {
	var ss_toSend = new Array();
	var ind = 0;
	var aCounter = 0;
	for (var i=0; i<ss_questionsArray.length;i++){
		if (ss_questionsArray[i].type && ss_questionsArray[i].type != 'undefined') {
			ss_toSend[ind] = new Array();
			ss_toSend[ind].type = ss_questionsArray[i].type;
			ss_toSend[ind].question = dojo.byId("questionText"+i).value;
			if (ss_questionsArray[i].type == 'multiple' || ss_questionsArray[i].type == 'single') {
				ss_toSend[ind].answers = new Array();
				aCounter = 0;
				for (var j=0; j<ss_questionsArray[i].answersNo; j++) {
					if (dojo.byId("question"+i+"answer"+j)) {
						ss_toSend[ind].answers[aCounter] = dojo.byId("question"+i+"answer"+j).value;
						aCounter++;
					}
				}
			}
			ind++;
		}
	}
	return ss_onSubmit(obj);
}

function ss_initSurveyQuestions(questionsArray) {
	for (var i=0; i<questionsArray.length; i++) {
		ss_newSurveyQuestion(questionsArray[i].type, questionsArray[i].question, false);
		
		if (questionsArray[i].type == 'multiple' || questionsArray[i].type == 'single') {
			for (var j=0; j<questionsArray[i].answers.length; j++) {
				ss_addAnswerOption(ss_questionsCounter-1, questionsArray[i].answers[j]);
			}
		}
	}
	// alert("Done");
}

