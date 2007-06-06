package com.sitescape.team.survey;

import java.io.Serializable;

import net.sf.json.JSONObject;

public class Survey implements Serializable {

	private String jsonStringRepresentation;

	private transient JSONObject jsonObj;

	private transient SurveyModel survey;

	public Survey(String jsonRepresentation) {
		super();
		this.jsonStringRepresentation = jsonRepresentation;
		init();
	}

	private void init() {
		if (jsonObj == null && this.jsonStringRepresentation != null) {
			this.jsonObj = JSONObject.fromString(this.jsonStringRepresentation);
			this.survey = new SurveyModel(this.jsonObj);
		}
	}

	public SurveyModel getSurveyModel() {
		init();
		return this.survey;
	}

}
