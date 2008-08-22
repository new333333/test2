package com.sitescape.team.remoting.ws.model;

import java.io.Serializable;

public class WorkflowResponse implements Serializable {
	protected String question;
	protected String response;
	protected Timestamp responser;
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getResponse() {
		return this.response;
	}
	public void setResponse(String response) {
		this.response = response;
	}  
    public Timestamp getResponder() {
    	return responser;
    }
    public void setResponder(Timestamp responser) {
    	this.responser = responser;
    }

}
