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
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.kablink.teaming.NoObjectByTheIdException;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SpringContextUtil;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


/**
 * Background job to change ACL's on Binders and entries by reindexing incrementally
 * 
 * @author Roy Klein
 */
public class DefaultBinderReindex extends SimpleTriggerJob implements BinderReindex {
	 
	protected static int MAX_IDS=1000;
	public void doExecute(JobExecutionContext context) throws JobExecutionException {
	    BinderModule binderModule = (BinderModule)SpringContextUtil.getBean("binderModule");
    	List<Long> binderIds = (List)jobDataMap.get("binderIds");
    	Boolean includeEntries = (Boolean)jobDataMap.get("includeEntries");
    	List<Long>retryBinderIds = new ArrayList<Long>();
    	for (Long id:binderIds) {
    		//index binder and it's entries
			try {
				binderModule.indexBinderIncremental(id, (includeEntries==null)? true : includeEntries.booleanValue());
			} catch (NoObjectByTheIdException ex) {
				//gone, skip it
			} catch (Exception ex) {
				//try again
				logger.error(NLT.get("profile.titlechange.index.error") + " (binder " + id.toString() + ") ", ex);
				retryBinderIds.add(id);
			}
    	}
    	if (retryBinderIds.isEmpty() ) {
    		context.put(CleanupJobListener.CLEANUPSTATUS, CleanupJobListener.DeleteJob);
    		context.setResult("Success");
    	} else {
    		jobDataMap.put("binderIds", retryBinderIds);
    		context.setResult("Failed");
    	}

	}

    public void scheduleNonBlocking( List<Long> binderIds, User user, boolean includeEntries) { 
		//the number of changes could be large, and some databases won't accept it (mssql packet size 1M)
		int count = 0;
		int binderIndex=0;
		GregorianCalendar now = new GregorianCalendar();
		long ms = now.getTimeInMillis();
		String tms = String.valueOf(now.getTimeInMillis());
		while (binderIndex < binderIds.size()) { 
			String userIdString = user.getId().toString() + " " + user.getModification().getDate().toString() + " " + tms + " " + String.valueOf(count);
			//each job is new
			JobDataMap data = new JobDataMap();
			data.put(ZONEID,user.getZoneId());
			if (binderIndex < binderIds.size()) {
				data.put("binderIds", new ArrayList<Long>(binderIds.subList(binderIndex, Math.min(binderIndex+MAX_IDS, binderIds.size()))));
				binderIndex+= MAX_IDS;
					
			} else {
				data.put("binderIds", Collections.EMPTY_LIST);
			}
			data.put("includeEntries", Boolean.valueOf(includeEntries));

			GregorianCalendar start = new GregorianCalendar();
			start.add(Calendar.SECOND, 3+count);
			scheduleNonBlocking(new JobDescription(user.getZoneId(), userIdString, start.getTime(), data));
		
			++count;
		}
    }
	public class JobDescription extends SimpleJobDescription {
		Date startDate;
		JobDataMap data;
		JobDescription(Long zoneId, String jobName, Date startDate, JobDataMap data) {
			super(zoneId, jobName, BINDER_REINDEX_GROUP, BINDER_REINDEX_DESCRIPTION, 5*60);
			this.startDate = startDate;
			this.data = data;
		}
		@Override
		protected Date getStartDate() {
			return startDate;
		}
		@Override
		protected JobDataMap getData() {
			return data;
		}
		
	}    

}
