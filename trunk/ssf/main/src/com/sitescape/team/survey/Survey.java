package com.sitescape.team.survey;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.UpdateAttributeSupport;

import net.sf.json.JSONObject;

public class Survey implements UpdateAttributeSupport {

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

	public boolean update(Object obj) {
		Survey newSurvey = (Survey)obj;
		if (getSurveyModel().isAlreadyVoted() && 
				!newSurvey.getSurveyModel().isVoteRequest()) {
			return false;
		}
		
		newSurvey.getSurveyModel().removeVoteRequest();
		
		throw new ClassCastException();
	}

}
