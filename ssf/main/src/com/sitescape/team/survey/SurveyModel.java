package com.sitescape.team.survey;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.builder.ToStringBuilder;

public class SurveyModel {

	private JSONObject survey;
	
	private List<Question> questions;
	
	private int maxLastIndex = 0;
	
	protected SurveyModel(JSONObject survey) {
		super();
		this.survey = survey;
		
		questions = new ArrayList();
		Iterator<JSONObject> questionsIt = this.survey.getJSONArray("questions").iterator();
		while (questionsIt.hasNext()) {
			JSONObject question = questionsIt.next();
			questions.add(new Question(question, this));
		}
	}	

	public List<Question> getQuestions() {
		return this.questions;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("questions", questions)
				.toString();
	}

	public int getNextIndex() {
		return ++maxLastIndex;
	}
	
	public void reportIndexInUse(int index) {
		if (index > maxLastIndex) {
			maxLastIndex = index;
		}
	}

	public Question getQuestionByIndex(int index) {
		Iterator<Question> it = questions.iterator();
		while (it.hasNext()) {
			Question question = it.next();
			if (question.getIndex() == index) {
				return question;
			}
		}
		return null;
	}
}
