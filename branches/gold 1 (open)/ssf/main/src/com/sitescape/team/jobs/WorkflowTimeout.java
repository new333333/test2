/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.jobs;

public interface WorkflowTimeout {
	public final static String WORKFLOW_TIMER_GROUP="workflow-timer";
	public final static String WORKFLOW_TIMER_DESCRIPTION="process timed out workflows";
	public final static String TIMEOUT_JOB="timeout.job";
	public final static String TIMEOUT_SECONDS="timeout.seconds";
	public void schedule(Long zoneId, int seconds);
}
