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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.User;

import com.sun.star.text.SetVariableType;

public class Answer {

	private JSONObject jsonObj;
	
	private String text;
	
	private int index;
	
	private int votesCount;
	
	private List votedUserIds;
	
	private SurveyModel survey;
	
	public int getIndex() {
		return index;
	}

	public Answer(JSONObject jsonObj, Question question, SurveyModel surveyModel) {
		this.jsonObj = jsonObj;
		this.survey = surveyModel;
		
		this.text = jsonObj.getString("text");
		try {
			this.index = jsonObj.getInt("index");
			question.reportAnswerIndexInUse(this.index);
		} catch (JSONException e) {
			this.index = question.getNextAnswerIndex();
			this.jsonObj.put("index", this.index);
		}
		
		try {
			this.votesCount = jsonObj.getInt("votesCount");
		} catch (JSONException e) {}
		
		try {
			this.votedUserIds = JSONArray.toList(jsonObj.getJSONArray("votedBy"));
		} catch (JSONException e) {}
	}
		

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("text", text).append("index", index)
					.append("votedUserIds", this.votedUserIds)
					.append("votesCount", this.votesCount)
					.toString();
	}

	public String getText() {
		return text;
	}
	
	public int getVotesCount() {
		return this.votesCount;
	}

	public void vote(String guestEmail) {
		this.votesCount++;
		if (this.votedUserIds == null) {
			this.votedUserIds = new ArrayList();
		}
		User currentUser = RequestContextHolder.getRequestContext().getUser();
		if (currentUser.isShared()) {
			this.votedUserIds.add(guestEmail);
		} else {
			this.votedUserIds.add(currentUser.getId().toString());
		}
		
		setVotesCount(this.votesCount);
		setVotedBy(this.votedUserIds);
	}
	
	private void setVotedBy(List newVotedUserIds) {
		this.votedUserIds = newVotedUserIds;
		this.jsonObj.remove("votedBy");
		this.jsonObj.put("votedBy", newVotedUserIds);
	}

	private void setVotesCount(int newVotesCount) {
		this.votesCount = newVotesCount;
		this.jsonObj.remove("votesCount");
		this.jsonObj.put("votesCount", newVotesCount);
	}

	public void removeAllVotes() {
		while (this.votedUserIds != null && !this.votedUserIds.isEmpty()) {
			String userId = (String)this.votedUserIds.get(0);
			removeVote(userId);
		}
	}
	
	public void removeVote() {
		User currentUser = RequestContextHolder.getRequestContext().getUser();
		if (currentUser.isShared()) {
			return;
		}
		String currentUserId = currentUser.getId().toString();
		removeVote(currentUserId);
	}
	private void removeVote(String userId) {
		if (this.votedUserIds == null || !this.votedUserIds.contains(userId)) {
			return;
		}
		
		this.votedUserIds.remove(userId);
		
		this.votesCount--;
		
		this.jsonObj.remove("votesCount");
		if (this.votesCount > 0) {
			this.jsonObj.put("votesCount", this.votesCount);
		}
		
		this.jsonObj.remove("votedBy");
		if (this.votedUserIds != null && !this.votedUserIds.isEmpty()) {
			this.jsonObj.put("votedBy", this.votedUserIds);
		}
	}
	
	public boolean isAlreadyVotedCurrentUser() {
		return isAlreadyVotedCurrentUser(null);
	}
	
	public boolean isAlreadyVotedCurrentUser(String guestEmail) {
		User currentUser = RequestContextHolder.getRequestContext().getUser();
		return isAlreadyVotedUser(currentUser, guestEmail);
	}
	private boolean isAlreadyVotedUser(User user, String guestEmail) {
		String userId = user.getId().toString();
		if (user.isShared()) {
			userId = guestEmail;
		}
		
		return this.votedUserIds != null && this.votedUserIds.contains(userId);
	}
	
	public boolean isAlreadyVoted() {
		return this.votedUserIds != null && 
			!this.votedUserIds.isEmpty();
	}

	public List getVotedUserIds() {
		return votedUserIds;
	}
	
	public List getVotedGuestsEmails() {
		if (this.votedUserIds == null || this.votedUserIds.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		List result = new ArrayList();
		Iterator it = this.votedUserIds.iterator();
		while (it.hasNext()) {
			String userId = (String)it.next();
			try {
				Long.parseLong(userId);
			} catch (NumberFormatException e) {
				result.add(userId);
			}
		}
		return result;
	}	

	public void updateFrom(Answer oldAnswer) {
		if (this.index != oldAnswer.index) {
			return;
		}
		
		if (oldAnswer.votesCount > 0) {
			setVotesCount(oldAnswer.votesCount);
		}
		if (oldAnswer.votedUserIds != null && !oldAnswer.votedUserIds.isEmpty()) {
			setVotedBy(oldAnswer.votedUserIds);
		}
	}
}
