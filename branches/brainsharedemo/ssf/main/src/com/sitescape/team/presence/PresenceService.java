package com.sitescape.team.presence;

import com.sitescape.team.domain.User;

public interface PresenceService {

	public int getPresenceInfo(User user);
	public int getPresenceInfo(String user);
	
}
