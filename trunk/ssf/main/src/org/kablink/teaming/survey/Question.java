/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.survey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.User;


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
	
	private Boolean requiredAnswer = false;
	
	private SurveyModel survey;

	public Question(JSONObject jsonObj, SurveyModel surveyModel) {
		this.jsonObj = jsonObj;
		this.survey = surveyModel;
		
		this.type = Type.valueOf(jsonObj.getString("type"));
		this.question = jsonObj.getString("question");
		try {
			this.index = jsonObj.getInt("index");
			 surveyModel.reportIndexInUse(this.index);
		} catch (JSONException e) {
			this.index = surveyModel.getNextIndex();
			this.jsonObj.put("index", this.index);
		}
		try {
			this.requiredAnswer = jsonObj.getBoolean("answerRequired");
		} catch (JSONException e) {}
		
		this.answers = new ArrayList();
		try {
			Iterator<JSONObject> answersIt = jsonObj.getJSONArray("answers").iterator();
			while (answersIt.hasNext()) {
				JSONObject answer = answersIt.next();
				this.answers.add(new Answer(answer, this, this.survey));
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
				"question", question).append("index", index).append("answers", answers)
				.append("requiredAnswer", this.requiredAnswer)
				.append("totalResponses", this.totalResponses)
				.toString();
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
	
	public boolean isAlreadyVotedCurrentUser(String guestEmail) {
		Iterator<Answer> it = answers.iterator();
		while (it.hasNext()) {
			if (it.next().isAlreadyVotedCurrentUser(guestEmail)) {
				return true;
			}
		}
		return false;
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
			if (answer.getIndex() == i) {
				return answer;
			}
		}
		return null;
	}

	public List<Answer> vote(String[] value, String guestEmail) {
		List<Answer> answers = new ArrayList();
		if (isAlreadyVotedCurrentUser(guestEmail)) {
			return answers;
		}
		if (value == null || value.length == 0) {
			return answers;
		}
		
		for (int i = 0; i < value.length; i++) {
			if (this.type.equals(Type.multiple) || this.type.equals(Type.single)) {
				Answer answer = getAnswerByIndex(Integer.parseInt(value[i]));
				answer.vote(guestEmail);
				answers.add(answer);
			} else {
				Answer answer = addInputAnswer(value[i], guestEmail);
				answers.add(answer);
			}
		}
		
		setTotalResponses(++this.totalResponses);
				
		return answers;
	}

	public void removeVote() {
		User currentUser = RequestContextHolder.getRequestContext().getUser();
		if (currentUser.isShared()) {
			return;
		}
		removeVote(currentUser);
	}
	private void removeVote(User user) {
		boolean hasVoted = false;
		Iterator<Answer> it = answers.iterator();
		while (it.hasNext()) {
			Answer answer = (Answer)it.next();
			if (answer.isAlreadyVotedCurrentUser(null)) {
				hasVoted = true;
				if (this.type.equals(Type.multiple) || this.type.equals(Type.single)) {
					answer.removeVote();
				} else {
					removeInputAnswer(answer);
					it.remove();
				}
			}
		}

		if (hasVoted) {
			setTotalResponses(--this.totalResponses);
		}
	}

	public void removeAllVotes() {
		if (this.type==Type.input) {
			this.removeInputAnswerAll();
		} else {
			Iterator<Answer> it = answers.iterator();
			while (it.hasNext()) {
				Answer answer = (Answer) it.next();
				answer.removeAllVotes();
			}
		}

		setTotalResponses(0);
	}

	private Answer addInputAnswer(String txt, String guestEmail) {
		JSONObject newInputAnswer = new JSONObject();
		newInputAnswer.put("text", txt);
		
		User currentUser = RequestContextHolder.getRequestContext().getUser();
		String userId = currentUser.getId().toString();
		if (currentUser.isShared()) {
			userId = guestEmail;
		}
		newInputAnswer.put("votedBy", Collections.singletonList(userId));
		newInputAnswer.put("votesCount", 1);
		
		try {
			jsonObj.get("answers");
			jsonObj.accumulate("answers", newInputAnswer);
		} catch (JSONException e) {
			JSONArray a = new JSONArray();
			a.put(newInputAnswer);
			jsonObj.put("answers", a);
		}
		
		Answer answer = new Answer(newInputAnswer, this, this.survey);
		this.answers.add(answer);
		
		return answer;
	}
	
	private void removeInputAnswer(Answer answer) {
		try {
			JSONArray filteredAnswers = new JSONArray();
			Iterator<JSONObject> answersIt = jsonObj.getJSONArray("answers").iterator();
			while (answersIt.hasNext()) {
				JSONObject jsonAnswer = answersIt.next();
				int answerIndex = jsonAnswer.getInt("index");
				if (answerIndex != answer.getIndex()) {
					filteredAnswers.put(jsonAnswer);
				}
			}
			
			jsonObj.remove("answers");
			if (filteredAnswers.length() > 0) {
				jsonObj.put("answers", filteredAnswers);
			}
		} catch (JSONException e) { 
			// input has no answers
		}
	}

	private void removeInputAnswerAll() {
		if (type==Type.input) {
			try {
				jsonObj.remove("answers");
			} catch (JSONException e) {
				// input has no answers
			}
			this.answers.clear();
		}
	}

	public boolean isRequiredAnswer() {
		return requiredAnswer;
	}
	
	public void setTotalResponses(int newTotalResponses) {
		this.totalResponses = newTotalResponses;
		this.jsonObj.remove("totalResponses");
		this.jsonObj.put("totalResponses", newTotalResponses);
	}

	public void updateFrom(Question oldQuestion) {
		if (this.index != oldQuestion.index) {
			return;
		}
		
		setTotalResponses(oldQuestion.totalResponses);
		
		if (oldQuestion.answers == null || oldQuestion.answers.isEmpty()) {
			return;
		}
		
		if (this.type.equals(Type.single) || this.type.equals(Type.multiple)) {
			Iterator<Answer> newAnswersIt = this.answers.iterator();
			while (newAnswersIt.hasNext()) {
				Answer newAnswer = newAnswersIt.next();
				Answer oldAnswer = oldQuestion.getAnswerByIndex(newAnswer.getIndex());
				if (oldAnswer != null) {
					newAnswer.updateFrom(oldAnswer);	
				}
			}
		} else {
			this.answers.addAll(oldQuestion.getAnswers());
			jsonObj.put("answers", oldQuestion.jsonObj.getJSONArray("answers"));
		}
	}
}
