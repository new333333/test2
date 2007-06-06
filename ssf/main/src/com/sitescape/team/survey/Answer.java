package com.sitescape.team.survey;

import net.sf.json.JSONObject;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Answer {

	private JSONObject jsonObj;
	
	private String text;
	
	public Answer(JSONObject jsonObj) {
		this.jsonObj = jsonObj;
		
		this.text = jsonObj.getString("answer");
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("text", text).toString();
	}

	public String getText() {
		return text;
	}
	
	
}
