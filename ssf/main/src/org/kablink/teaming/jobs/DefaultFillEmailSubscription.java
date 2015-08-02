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

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.jobs.SimpleTriggerJob.SimpleJobDescription;
import org.kablink.teaming.module.mail.MailModule;
import org.kablink.teaming.util.SpringContextUtil;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;


/**
 * @author Janet McCann
 *
 */
public class DefaultFillEmailSubscription extends SimpleTriggerJob implements FillEmailSubscription {
	protected Log logger = LogFactory.getLog(getClass());

	/* (non-Javadoc)
	 * @see org.kablink.teaming.jobs.SSStatefulJob#doExecute(org.quartz.JobExecutionContext)
	 */
    public void doExecute(JobExecutionContext context) throws JobExecutionException {
		//assume old job from v1 where each changed entry registered a job
		if (!zoneId.toString().equals(context.getTrigger().getJobName())) {
				deleteJob(context);
		} else {			
			if (!getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId()).getMailConfig().isSendMailEnabled()) {
				logger.debug("Sending mail is not enabled for zone " + RequestContextHolder.getRequestContext().getZoneName());
				return;
			}
			MailModule mail = (MailModule)SpringContextUtil.getBean("mailModule");
			Date begin = (Date)jobDataMap.get("lastNotification");
			Date end = mail.fillSubscriptions(begin);
			if (end == null) return;
			jobDataMap.put("lastNotification", end);
		}
    }

	public void remove(Long zoneId) {
		unscheduleJob(zoneId.toString(), SUBSCRIPTION_GROUP);		
	}
	
    public void schedule(Long zoneId, Date changeDate, int minutes) {
		schedule(new JobDescription(zoneId, changeDate, minutes*60));
    }
    protected class JobDescription extends SimpleJobDescription {
     	Date changeDate;
    	JobDescription(Long zoneId, Date changeDate, int seconds) {
    		super(zoneId, zoneId.toString(),SUBSCRIPTION_GROUP, SUBSCRIPTION_DESCRIPTION, seconds );
    		this.changeDate = changeDate;
    	}
    	protected JobDataMap getData() {
    		JobDataMap data = super.getData();
    		if (changeDate != null) {
    			data.put("lastNotification", changeDate);
    		} else {
    			data.put("lastNotification", new Date());					
    		}
    		return data;
		}
    }
	    
}

