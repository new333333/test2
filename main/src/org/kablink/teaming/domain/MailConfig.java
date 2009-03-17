package org.kablink.teaming.domain;
//component of zoneConfig
public class MailConfig  {
	protected boolean sendMailEnabled = true;
	protected boolean postingEnabled = false;
	protected boolean simpleUrlPostingEnabled = false;
	
	public MailConfig()	{
	}
	public MailConfig(MailConfig src) {
		this.sendMailEnabled = src.sendMailEnabled;
		this.postingEnabled = src.postingEnabled;
		this.simpleUrlPostingEnabled = src.simpleUrlPostingEnabled;
	}
	public boolean isSendMailEnabled() {
		return sendMailEnabled;
	}
	public void setSendMailEnabled(boolean sendMailEnabled) {
		this.sendMailEnabled = sendMailEnabled;
	}
	public boolean isPostingEnabled() {
		return postingEnabled;
	}
	public void setPostingEnabled(boolean postingEnabled) {
		this.postingEnabled = postingEnabled;
	}
	public boolean isSimpleUrlPostingEnabled() {
		return simpleUrlPostingEnabled;
	}
	public void setSimpleUrlPostingEnabled(boolean simpleUrlPostingEnabled) {
		this.simpleUrlPostingEnabled = simpleUrlPostingEnabled;
	}

}
