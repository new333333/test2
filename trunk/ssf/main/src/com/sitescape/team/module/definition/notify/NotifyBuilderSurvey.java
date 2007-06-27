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
package com.sitescape.team.module.definition.notify;

import java.util.Iterator;
import java.util.Map;

import org.dom4j.Element;

import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.survey.Answer;
import com.sitescape.team.survey.Question;
import com.sitescape.team.survey.Survey;
import com.sitescape.team.survey.SurveyModel;

public class NotifyBuilderSurvey extends AbstractNotifyBuilder {

    protected boolean build(Element element, Notify notifyDef, CustomAttribute attribute, Map args) {
     	Object obj = attribute.getValue();
    	if (obj instanceof Survey) {
    		Survey survey = (Survey)obj;
    		SurveyModel surveyModel = survey.getSurveyModel();
    		if (surveyModel != null) {
    			Iterator questionsIt = surveyModel.getQuestions().iterator();
    			while (questionsIt.hasNext()) {
    				Question question = (Question)questionsIt.next();
    				Element questionEl = element.addElement("question");
    				
    				Element questionTextEl = questionEl.addElement("text");
    				questionTextEl.setText(question.getQuestion());
    				
    				Iterator answersIt = question.getAnswers().iterator();
    				while (answersIt.hasNext()) {
    					Answer answer = (Answer)answersIt.next();
    					Element answerEl = questionEl.addElement("answer");
    					
    					Element answerTextEl = answerEl.addElement("text");
    					answerTextEl.setText(answer.getText());
    					
    					Element answerVotesCountEl = answerEl.addElement("votesCount");
    					answerVotesCountEl.setText(Integer.toString(answer.getVotesCount()));
    				}
    			}
    		}
    	} else if (obj != null) {
	    	element.setText(obj.toString());
    	}
    	return true;
   }
}
