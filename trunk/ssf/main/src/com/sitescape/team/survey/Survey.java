package com.sitescape.team.survey;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import net.sf.json.JSONObject;

public class Survey {

	private JSONObject jsonObj;

	private SurveyModel survey;

	public Survey(String jsonStringRepresentation) {
		super();
		this.jsonObj = JSONObject.fromString(jsonStringRepresentation);
		this.survey = new SurveyModel(this.jsonObj);
	}

	public SurveyModel getSurveyModel() {
		return this.survey;
	}

	public String toString() {
		return jsonObj.toString();
	}

}
