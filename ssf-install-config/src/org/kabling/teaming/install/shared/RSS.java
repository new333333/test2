package org.kabling.teaming.install.shared;

import java.io.Serializable;

/**
 * RSS Configuration Settings
 * 
 * RSS feeds are inherently insecure as they do not use the standard authentication mechanism. If you do not want to have users create RSS
 * subscriptions, set the enable property to "false".
 * 
 * If you have RSS feeds enabled, you can tune the maximum number of days to keep in the feed and the maximum number of days to keep
 * updating a feed when there are no clients reading it.
 **/
public class RSS implements Serializable
{

	private static final long serialVersionUID = -645027619353219289L;
	private boolean enabled;
	private int maxElapsedDays;
	private int maxInactiveDays;

	public RSS()
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

	public int getMaxElapsedDays()
	{
		return maxElapsedDays;
	}

	public void setMaxElapsedDays(int maxElapsedDays)
	{
		this.maxElapsedDays = maxElapsedDays;
	}

	public int getMaxInactiveDays()
	{
		return maxInactiveDays;
	}

	public void setMaxInactiveDays(int maxInactiveDays)
	{
		this.maxInactiveDays = maxInactiveDays;
	}
}
