package org.kablink.teaming.domain;
//component of zoneConfig
public class MailConfig  {
	protected boolean sendMailEnabled = true;
	protected boolean postingEnabled = true;
	protected boolean simpleUrlPostingEnabled = false;
	protected int simpleUrlPort=2525;

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
