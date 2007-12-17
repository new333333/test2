package com.sitescape.team.jobs;

import com.sitescape.team.domain.Workspace;

public interface ZoneSchedule {
	   public void startScheduledJobs(Workspace zone);
	   public void stopScheduledJobs(Workspace zone);

}
