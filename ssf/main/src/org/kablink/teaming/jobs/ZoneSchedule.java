package org.kablink.teaming.jobs;

import org.kablink.teaming.domain.Workspace;

public interface ZoneSchedule {
	   public void startScheduledJobs(Workspace zone);
	   public void stopScheduledJobs(Workspace zone);

}
