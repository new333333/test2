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
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.User;


public class SurveyModel {
	
	public static final String ALL_USERS = "all";

	public static final String VOTERS = "voters";
	
	public static final String MODERATOR = "moderator";
	
	private JSONObject survey;
	
	private List<Question> questions;
	
	private String allowedToViewBeforeDueDate;
	
	private String allowedToViewAfterDueDate;
	
	private String allowedToViewDetails;
	
	private Boolean allowedToChangeVote;
	
	private Boolean allowMultipleGuestVotes;
	
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
		try {
			this.allowedToViewBeforeDueDate = this.survey.getString("viewBeforeDueTime");
		} catch (JSONException e) {}
		try {
			this.allowedToViewAfterDueDate = this.survey.getString("viewAfterDueTime");
		} catch (JSONException e) {}
		try {
			this.allowedToViewDetails = this.survey.getString("viewDetails");
		} catch (JSONException e) {}
		try {		
			this.allowedToChangeVote = this.survey.getBoolean("allowChange");
		} catch (JSONException e) {}
		try {		
			this.allowMultipleGuestVotes = this.survey.getBoolean("allowMultipleGuestVotes");
		} catch (JSONException e) {}
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
	
	public boolean isAlreadyVotedCurrentUser() {
		return isAlreadyVotedCurrentUser(null);
	}
	
	public boolean isAlreadyVotedCurrentUser(String guestEmail) {
		if (this.questions == null) {
			return false;
		}
		
		Iterator it = this.questions.iterator();
		while (it.hasNext()) {
			Question question = (Question)it.next();
			if (question.isAlreadyVotedCurrentUser(guestEmail)) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("questions", questions)
			.append("allowedToChangeVote", this.allowedToChangeVote)
			.append("allowMultipleGuestVotes", this.allowMultipleGuestVotes)
			.append("allowedToViewAfterDueDate", this.allowedToViewAfterDueDate)
			.append("allowedToViewBeforeDueDate", this.allowedToViewBeforeDueDate)
			.append("allowedToViewDetails", this.allowedToViewDetails)
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

	public boolean isAllowedToChangeVote() {
		User currentUser = RequestContextHolder.getRequestContext().getUser();
		if (currentUser.isShared() || allowedToChangeVote == null) {
			return false;
		}
		
		return allowedToChangeVote;
	}

	public String getAllowedToViewAfterDueDate() {
		return allowedToViewAfterDueDate!=null?allowedToViewAfterDueDate:ALL_USERS;
	}

	public String getAllowedToViewBeforeDueDate() {
		return allowedToViewBeforeDueDate!=null?allowedToViewBeforeDueDate:ALL_USERS;
	}

	public String getAllowedToViewDetails() {
		return allowedToViewDetails!=null?allowedToViewDetails:MODERATOR;
	}
	
	public boolean isAllowedToViewDetailsCurrentUser() {
		return isAllowedToViewDetailsCurrentUser(null);
	}
	public boolean isAllowedToViewDetailsCurrentUser(String guestEmail) {
		if (this.allowedToViewDetails == null) {
			// v1.0 compatibility
			return true;
		}
		
		if (ALL_USERS.equals(this.allowedToViewDetails)) {
			return true;
		}
		
		if (VOTERS.equals(this.allowedToViewDetails) &&
				this.isAlreadyVotedCurrentUser(guestEmail)) {
			return true;
		}
		
		return false;
	}	
	
	public boolean isAllowedModeratorToViewDetails() {
		if (MODERATOR.equals(this.allowedToViewDetails)) {
			return true;
		}	
		return false;
	}	
	
	public boolean isAllowedToViewBeforeDueDateCurrentUser() {
		return isAllowedToViewBeforeDueDateCurrentUser(null);
	}
	
	public boolean isAllowedToViewBeforeDueDateCurrentUser(String guestEmail) {
		if (this.allowedToViewBeforeDueDate == null) {
			// v1.0 compatibility
			return true;
		}
		
		if (ALL_USERS.equals(this.allowedToViewBeforeDueDate)) {
			return true;
		}
		
		if (VOTERS.equals(this.allowedToViewBeforeDueDate) &&
				this.isAlreadyVotedCurrentUser(guestEmail)) {
			return true;
		}
		
		return false;
	}
	
	public boolean isAllowedModeratorToViewBeforeDueDate() {
		if (MODERATOR.equals(this.allowedToViewBeforeDueDate)) {
			return true;
		}	
		return false;
	}
	
	public boolean isAllowedToViewAfterDueDateCurrentUser() {
		return isAllowedToViewAfterDueDateCurrentUser(null);
	}
	
	public boolean isAllowedToViewAfterDueDateCurrentUser(String guestEmail) {
		if (this.allowedToViewAfterDueDate == null) {
			// v1.0 compatibility
			return true;
		}
		
		if (ALL_USERS.equals(this.allowedToViewAfterDueDate)) {
			return true;
		}
		
		if (VOTERS.equals(this.allowedToViewAfterDueDate) &&
				this.isAlreadyVotedCurrentUser(guestEmail)) {
			return true;
		}
		
		return false;
	}
	
	public boolean isAllowedModeratorToViewAfterDueDate() {
		if (MODERATOR.equals(this.allowedToViewAfterDueDate)) {
			return true;
		}	
		return false;
	}

	public boolean isAllowMultipleGuestVotes() {
		if (allowMultipleGuestVotes == null) {
			return false;
		}
		
		return allowMultipleGuestVotes;
	}

	public void removeVote() {
		if (this.questions == null) {
			return;
		}
		
		Iterator it = this.questions.iterator();
		while (it.hasNext()) {
			Question question = (Question)it.next();
			question.removeVote();
		}
	}
	
	public void removeAllVotes() {
		if (this.questions == null) {
			return;
		}
		
		Iterator it = this.questions.iterator();
		while (it.hasNext()) {
			Question question = (Question)it.next();
			question.removeAllVotes();
		}
	}
	
	public void updateFrom(SurveyModel surveyModel) {
		Iterator<Question> newQuestionsIt = this.questions.iterator();
		while (newQuestionsIt.hasNext()) {
			Question newQuestion = newQuestionsIt.next();
			Question oldQuestion = surveyModel.getQuestionByIndex(newQuestion.getIndex());
			if (oldQuestion != null) {
				newQuestion.updateFrom(oldQuestion);
			}
		}
	}

}
