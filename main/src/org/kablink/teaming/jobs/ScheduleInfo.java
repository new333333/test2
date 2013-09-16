/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.jobs;

import java.util.Map;

import org.kablink.util.Validator;
import org.quartz.JobDataMap;

public class ScheduleInfo {
	private Schedule schedule;
	protected Map details;
	private boolean enabled;
	private Long folderId;
	private Long zoneId;
	
	public ScheduleInfo(Long zoneId) {
		this.zoneId = zoneId;
	}
	public ScheduleInfo(Map details) {
		this.details = details;
	}
	public ScheduleInfo(Long folderId, Map details) {
		this.folderId = folderId;
		this.details = details;
	}
	public Schedule getSchedule() {
		if (schedule == null) { 
			getDetails();
			try {
				String q = (String)details.get("schedule");
				if (!Validator.isNull(q))
					schedule = new Schedule((String)details.get("schedule"));
				else schedule = new Schedule();
			} catch (Exception ex) {
				schedule = new Schedule();
			}
		}
		return schedule;
	}
	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}
	public void setFolderId(Long folderId) {
		this.folderId = folderId;
	}
	public Long getFolderId() {
		return folderId;
	}
	public void setZoneId(Long zoneId) {
		this.zoneId = zoneId;
	}
	public Long getZoneId() {
		if (zoneId == null) {
			getDetails();
			zoneId = (Long)details.get("zoneId");
		}
		return zoneId;
	}
	public Map getDetails() {
		if (details == null) details = new JobDataMap();
		if (schedule != null) {
			details.put("schedule", schedule.getQuartzSchedule());
		}
		if (zoneId != null) {
			details.put("zoneId", zoneId);
		}
		return details;
	}
	public void setDetails(Map details) {
		if (details instanceof JobDataMap)
			this.details = details;
		else
			this.details = new JobDataMap(details);
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled.booleanValue();
	}
}
