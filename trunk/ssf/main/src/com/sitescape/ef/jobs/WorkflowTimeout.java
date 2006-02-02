package com.sitescape.ef.jobs;

public interface WorkflowTimeout {
	public final static String WORKFLOW_TIMER_GROUP="workflow-timer";
	public final static String WORKFLOW_TIMER_DESCRIPTION="process timed out workflows";
	public final static String TIMEOUT_JOB="timeout.job";
	public void schedule(String zoneName);
}
