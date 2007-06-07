package com.sitescape.team.survey;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONObject;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Question {
	
	private JSONObject jsonObj;
	
	private String type;
	private String question;
	private List<Answer> answers;

	public Question(JSONObject jsonObj) {
		this.jsonObj = jsonObj;
		
		this.type = jsonObj.getString("type");
		this.question = jsonObj.getString("question");
		
		this.answers = new ArrayList();
		Iterator<JSONObject> answersIt = jsonObj.getJSONArray("answers").iterator();
		while (answersIt.hasNext()) {
			JSONObject answer = answersIt.next();
			this.answers.add(new Answer(answer));
		}
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("type", type).append(
				"question", question).append("answers", answers).toString();
	}

	public List<Answer> getAnswers() {
		return answers;
	}

	public String getQuestion() {
		return question;
	}

	public String getType() {
		return type;
	}
	
	public boolean isAlreadyVoted() {
		// TODO: implement
		return false;
	}
	public int getTotalResponses() {
		// TODO implement
		return 100;
	}
}
