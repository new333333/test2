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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.jobs.DefaultEmailNotification.JobDescription;
import org.kablink.teaming.jobs.SSCronTriggerJob.CronJobDescription;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.mail.MailModule;
import org.kablink.teaming.util.SpringContextUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 */
public class DefaultFileVersionAgingDelete extends SSCronTriggerJob 
	implements FileVersionAging {
	 
    public void doExecute(JobExecutionContext context) throws JobExecutionException {
    	Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
    	Long fileVersionsMaxAge = getCoreDao().loadZoneConfig(zoneId).getFileVersionsMaxAge();

    	//Get the list of potential file versions to delete
		List fileAtts = new ArrayList();
		Date ageDate = new Date();
		Date now = new Date();
		if (fileVersionsMaxAge != null) {
			//Calculate the "aged" date. If any file version was created before this date, then it is time to delete it
			ageDate.setTime(now.getTime() - fileVersionsMaxAge*24*60*60*1000);	//Subtract the "max age days" from today's date
	    	fileAtts.addAll(getCoreDao().getOldFileVersions(zoneId, ageDate));
		}
    	fileAtts.addAll(getCoreDao().getOldBinderFileVersions(zoneId, now));
    	List entitiesProcessed = new ArrayList();
    	long totalDelCount = 0;
    	for (Object o : fileAtts) {
    		Object[] fAttObj = (Object []) o;
    		String fAttId = (String)fAttObj[0];
    		DefinableEntity entity = (DefinableEntity)fAttObj[1];
    		if (!entitiesProcessed.contains(entity)) {
    			//Purge old versions
    			entitiesProcessed.add(entity);
    			try {
    				long delCount = fileModule.deleteAgedFileVersions(entity, ageDate);
    				totalDelCount = totalDelCount + delCount;
    			} catch(Exception e) {
    				logger.error("Could not delete old file versions from " + 
    						entity.getEntityType().name() + ": " + entity.getTitle() + " (" + 
    						entity.getId().toString() + ")", e);
    			}
    		}
    	}
    	logger.info("Old versions deleted via aging: " + String.valueOf(totalDelCount));
    }

	public ScheduleInfo getScheduleInfo(Long zoneId) {
		return getScheduleInfo(new CronJobDescription(zoneId, zoneId.toString(), 
				FILE_VERSION_AGING_GROUP, zoneId.toString()));
	}
	public void setScheduleInfo(ScheduleInfo info) {
		setScheduleInfo(new CronJobDescription(info.getZoneId(), info.getZoneId().toString(), 
				FILE_VERSION_AGING_GROUP, info.getZoneId().toString()), info);

	}

	public void enable(boolean enable, Long zoneId) {
		//get rid of old functionality when
		if (enable == false) {
			unscheduleJob(zoneId.toString(), FILE_VERSION_AGING_GROUP);
		} else {
			enable(enable, new CronJobDescription(zoneId, zoneId.toString(), 
				FILE_VERSION_AGING_GROUP, FILE_VERSION_AGING_DESCRIPTION));
		}
 	}

	public class JobDescription extends CronJobDescription {
		JobDescription(Long zoneId) {
			super(zoneId, zoneId.toString(), FILE_VERSION_AGING_GROUP, FILE_VERSION_AGING_DESCRIPTION);
		}
     	public ScheduleInfo getDefaultScheduleInfo() {
    		ScheduleInfo info = super.getDefaultScheduleInfo();
    		info.getDetails().put("lastFileVersionAgingCheck", new Date());
    		info.getDetails().put("zoneId", zoneId);
    		return info;
    	}
       	
	}
}