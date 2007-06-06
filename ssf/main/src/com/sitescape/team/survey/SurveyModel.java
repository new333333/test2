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
	
	protected SurveyModel(JSONObject survey) {
		super();
		this.survey = survey;
		
		questions = new ArrayList();
		Iterator<JSONObject> questionsIt = this.survey.getJSONArray("questions").iterator();
		while (questionsIt.hasNext()) {
			JSONObject question = questionsIt.next();
			questions.add(new Question(question));
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
}
