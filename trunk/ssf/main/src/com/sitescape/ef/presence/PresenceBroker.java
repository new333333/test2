package com.sitescape.ef.presence;

import com.sitescape.ef.domain.User;

public interface PresenceBroker {

	public boolean getScreenNameExists(String zonName);
	public void sendIm(String from, String recipient, String message);
	public String getCommunityId(String communityname);
}
