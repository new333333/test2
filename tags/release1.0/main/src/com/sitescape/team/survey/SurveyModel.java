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
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
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
	
	public boolean isAlreadyVoted() {
		if (this.questions == null) {
			return false;
		}
		
		Iterator it = this.questions.iterator();
		while (it.hasNext()) {
			Question question = (Question)it.next();
			if (question.isAlreadyVoted()) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("questions", questions)
				.toString();
	}

	protected int getNextIndex() {
		return ++maxLastIndex;
	}
	
	protected void reportIndexInUse(int index) {
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
	
	public void setVoteRequest() {
		if (survey != null) {
			survey.put("voteRequest", true);
		}
	}
	
	public void removeVoteRequest() {
		if (survey == null) {
			return;
		}
		
		try {
			survey.remove("voteRequest");
		} catch (JSONException e) {
		}
	}
	
	public boolean isVoteRequest() {
		if (survey == null) {
			return false;
		}
		
		try {
			return survey.getBoolean("voteRequest");
		} catch (JSONException e) {}
		
		return false;
	}
}
