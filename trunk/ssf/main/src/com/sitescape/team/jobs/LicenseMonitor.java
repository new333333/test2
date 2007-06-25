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

public interface LicenseMonitor {
	public final static String LICENSE_MONITOR_GROUP="license-monitor";
	public final static String LICENSE_MONITOR_DESCRIPTION="gather licensing statistics";
	public final static String LICENSE_JOB="license.job";
	public final static String LICENSE_HOURS="timeout.hours";
	public void schedule(Long zoneId, int hours);
}
