package com.sitescape.team.remoting.ws.model;
import java.io.Serializable;
public class SubscriptionStyle implements Serializable {
	private Integer style;
	private String[] emailTypes;
	
	public SubscriptionStyle(Integer style, String[] emailTypes) {
		this.style = style;
		this.emailTypes = emailTypes;
	}
	public Integer getStyle() {
		return style;
	}
	public void setStyle(Integer style) {
		this.style = style;
	}
	public String[] getEmailTypes() {
		return emailTypes;
	}
	public void setEmailTypes(String[] emailTypes) {
		this.emailTypes = emailTypes;
	}
}
