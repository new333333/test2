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
		return new ToStringBuilder(this).append("text", text).append("index", index).toString();
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
		
		this.jsonObj.remove("votesCount");
		this.jsonObj.put("votesCount", this.votesCount);
		
		this.jsonObj.remove("votedBy");
		this.jsonObj.put("votedBy", this.votedUserIds);
	}
	
	public boolean isAlreadyVotedCurrentUser() {
		User currentUser = RequestContextHolder.getRequestContext().getUser();
		return this.votedUserIds != null && this.votedUserIds.contains(currentUser.getId().toString());
	}
	
	public boolean isAlreadyVoted() {
		return this.votedUserIds != null && 
			!this.votedUserIds.isEmpty();
	}
	
}
