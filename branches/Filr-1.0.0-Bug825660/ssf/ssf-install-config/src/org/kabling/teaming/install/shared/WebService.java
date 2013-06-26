package org.kabling.teaming.install.shared;

import java.io.Serializable;

public class WebService implements Serializable
{

	private static final long serialVersionUID = -645027619353219289L;

	private boolean enabled;
	private boolean basicEnabled;
	private boolean tokenEnabled;
	private boolean anonymousEnabled;

	public WebService()
	{
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public boolean isBasicEnabled()
	{
		return basicEnabled;
	}

	public void setBasicEnabled(boolean basicEnabled)
	{
		this.basicEnabled = basicEnabled;
	}

	public boolean isTokenEnabled()
	{
		return tokenEnabled;
	}

	public void setTokenEnabled(boolean tokenEnabled)
	{
		this.tokenEnabled = tokenEnabled;
	}

	public boolean isAnonymousEnabled() {
		return anonymousEnabled;
	}

	public void setAnonymousEnabled(boolean anonymousEnabled) {
		this.anonymousEnabled = anonymousEnabled;
	}
}
