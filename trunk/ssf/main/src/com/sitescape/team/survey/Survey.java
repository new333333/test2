package com.sitescape.team.survey;

import java.io.Serializable;
import java.util.List;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class Survey implements Serializable {

	private String jsonStringRepresentation;

	private transient JSONObject jsonObj;

	public Survey(String jsonRepresentation) {
		super();
		this.jsonStringRepresentation = jsonRepresentation;
		if (this.jsonStringRepresentation != null) {
			this.jsonObj = JSONObject.fromString(this.jsonStringRepresentation);
		} else {
			this.jsonObj = new JSONObject();
		}
	}
	
	public Object[] getQuestions() {
		return jsonObj.getJSONArray("definition").toArray();
	}

}
