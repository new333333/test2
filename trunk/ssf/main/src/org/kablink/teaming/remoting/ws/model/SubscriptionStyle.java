package org.kablink.teaming.remoting.ws.model;
import java.io.Serializable;
public class SubscriptionStyle implements Serializable {
	private int style;
	private String[] emailTypes;
	
	public SubscriptionStyle(int style, String[] emailTypes) {
		this.style = style;
		this.emailTypes = emailTypes;
	}
	public int getStyle() {
		return style;
	}
	public void setStyle(int style) {
		this.style = style;
	}
	public String[] getEmailTypes() {
		return emailTypes;
	}
	public void setEmailTypes(String[] emailTypes) {
		this.emailTypes = emailTypes;
	}
}
