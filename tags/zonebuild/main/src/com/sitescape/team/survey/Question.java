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
	
	public boolean isAlreadyVotedCurrentUser() {
		Iterator<Answer> it = answers.iterator();
		while (it.hasNext()) {
			if (it.next().isAlreadyVotedCurrentUser()) {
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

	public List<Answer> vote(String[] value) {
		List<Answer> answers = new ArrayList();
		if (isAlreadyVotedCurrentUser()) {
			return answers;
		}
		if (value == null || value.length == 0) {
			return answers;
		}
		
		for (int i = 0; i < value.length; i++) {
			if (this.type.equals(Type.multiple) || this.type.equals(Type.single)) {
				Answer answer = getAnswerByIndex(Integer.parseInt(value[i]));
				answer.vote();
				answers.add(answer);
			} else {
				Answer answer = addInputAnswer(value[i]);
				answers.add(answer);
			}
		}
		this.totalResponses++;
		
		this.jsonObj.remove("totalResponses");
		this.jsonObj.put("totalResponses", this.totalResponses);
		
		return answers;
	}

	private Answer addInputAnswer(String txt) {
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
		
		Answer answer = new Answer(newInputAnswer, this);
		this.answers.add(answer);
		
		return answer;
	}

}
