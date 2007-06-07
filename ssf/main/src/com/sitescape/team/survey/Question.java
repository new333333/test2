package com.sitescape.team.survey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.User;

public class Question {
	
	public static enum Type {
		multiple, single, input;
	}
	
	private JSONObject jsonObj;
	
	private Type type;
	
	private String question;
	
	private int index;
	
	private List<Answer> answers;
	
	private int totalResponses = 0;
	
	private int maxLastAnswerIndex = 0;
	
	private SurveyModel parent;

	public Question(JSONObject jsonObj, SurveyModel surveyModel) {
		this.jsonObj = jsonObj;
		this.parent = surveyModel;
		
		this.type = Type.valueOf(jsonObj.getString("type"));
		this.question = jsonObj.getString("question");
		try {
			this.index = jsonObj.getInt("index");
			 surveyModel.reportIndexInUse(this.index);
		} catch (JSONException e) {
			this.index = surveyModel.getNextIndex();
			this.jsonObj.put("index", this.index);
		}
		
		this.answers = new ArrayList();
		try {
			Iterator<JSONObject> answersIt = jsonObj.getJSONArray("answers").iterator();
			while (answersIt.hasNext()) {
				JSONObject answer = answersIt.next();
				this.answers.add(new Answer(answer, this));
			}
		} catch (JSONException e) { 
			// input has no answers
		}
		
		try {
			this.totalResponses = jsonObj.getInt("totalResponses");
		} catch (JSONException e) {}
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("type", type).append(
				"question", question).append("index", index).append("answers", answers).toString();
	}

	public List<Answer> getAnswers() {
		return answers;
	}

	public String getQuestion() {
		return question;
	}

	public String getType() {
		return type.name();
	}
	
	public boolean isAlreadyVoted() {
		Iterator<Answer> it = answers.iterator();
		while (it.hasNext()) {
			if (it.next().isAlreadyVoted()) {
				return true;
			}
		}
		return false;
	}
	
	public int getTotalResponses() {
		return this.totalResponses;
	}

	public int getIndex() {
		return index;
	}
	
	public int getNextAnswerIndex() {
		return ++maxLastAnswerIndex;
	}
	
	public void reportAnswerIndexInUse(int index) {
		if (index > maxLastAnswerIndex) {
			maxLastAnswerIndex = index;
		}
	}

	public Answer getAnswerByIndex(int i) {
		Iterator<Answer> it = answers.iterator();
		while (it.hasNext()) {
			Answer answer = it.next();
			if (answer.getIndex() == index) {
				return answer;
			}
		}
		return null;
	}

	public void vote(String[] value) {
		if (isAlreadyVoted()) {
			return;
		}
		if (value == null || value.length == 0) {
			return;
		}
		
		for (int i = 0; i < value.length; i++) {
			if (this.type.equals(Type.multiple) || this.type.equals(Type.single)) {
				Answer answer = getAnswerByIndex(Integer.parseInt(value[i]));
				answer.vote();
			} else {
				addInputAnswer(value[i]);
			}
		}
		this.totalResponses++;
		
		this.jsonObj.remove("totalResponses");
		this.jsonObj.put("totalResponses", this.totalResponses);
	}

	private void addInputAnswer(String txt) {
		JSONObject newInputAnswer = new JSONObject();
		newInputAnswer.put("text", txt);
		
		User currentUser = RequestContextHolder.getRequestContext().getUser();
		newInputAnswer.put("votedBy", Collections.singletonList( currentUser.getId().toString() ));
		newInputAnswer.put("votesCount", 1);
		
		try {
			jsonObj.get("answers");
			jsonObj.accumulate("answers", newInputAnswer);
		} catch (JSONException e) {
			JSONArray a = new JSONArray();
			a.put(newInputAnswer);
			jsonObj.put("answers", a);
		}
		
		this.answers.add(new Answer(newInputAnswer, this));
	}

}
