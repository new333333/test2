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
import java.util.Arrays;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.User;
import com.sun.star.text.SetVariableType;

public class Answer {

	private JSONObject jsonObj;
	
	private String text;
	
	private int index;
	
	private int votesCount;
	
	private List votedUserIds;
	
	public int getIndex() {
		return index;
	}

	public Answer(JSONObject jsonObj, Question question) {
		this.jsonObj = jsonObj;
		
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

	public void vote() {
		this.votesCount++;
		if (this.votedUserIds == null) {
			this.votedUserIds = new ArrayList();
		}
		User currentUser = RequestContextHolder.getRequestContext().getUser();
		this.votedUserIds.add(currentUser.getId().toString());
		
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

	public void removeVote() {
		User currentUser = RequestContextHolder.getRequestContext().getUser();
		String currentUserId = currentUser.getId().toString();
		if (this.votedUserIds == null || !this.votedUserIds.contains(currentUserId)) {
			return;
		}
		
		this.votedUserIds.remove(currentUserId);
		
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
		User currentUser = RequestContextHolder.getRequestContext().getUser();
		return this.votedUserIds != null && this.votedUserIds.contains(currentUser.getId().toString());
	}
	
	public boolean isAlreadyVoted() {
		return this.votedUserIds != null && 
			!this.votedUserIds.isEmpty();
	}

	public List getVotedUserIds() {
		return votedUserIds;
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
