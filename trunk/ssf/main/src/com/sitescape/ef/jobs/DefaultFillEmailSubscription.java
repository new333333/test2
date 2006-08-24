
package com.sitescape.ef.jobs;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;

import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.mail.MailManager;
import com.sitescape.ef.util.SpringContextUtil;

/**
 * @author Janet McCann
 *
 */
public class DefaultFillEmailSubscription extends SSStatefulJob implements FillEmailSubscription {
	protected Log logger = LogFactory.getLog(getClass());

	/* (non-Javadoc)
	 * @see com.sitescape.ef.jobs.SSStatefulJob#doExecute(org.quartz.JobExecutionContext)
	 */
    public void doExecute(JobExecutionContext context) throws JobExecutionException {
    	MailManager mail = (MailManager)SpringContextUtil.getBean("mailManager");
		Long folderId = new Long(jobDataMap.getLong("binder"));
		Long entryId = new Long(jobDataMap.getLong("entry"));
		Date stamp = (Date)jobDataMap.get("when");
		try {
			mail.fillSubscription(folderId, entryId, stamp);
			context.put(CleanupJobListener.CLEANUPSTATUS, CleanupJobListener.DeleteJob);
			context.setResult("Success");
		} catch (Exception ex) {
			//remove job
			context.put(CleanupJobListener.CLEANUPSTATUS, CleanupJobListener.DeleteJobOnError);
			throw new JobExecutionException(ex);
		}
    }

		
    public void schedule(Long folderId, Long entryId, Date changeDate) {
		Scheduler scheduler = (Scheduler)SpringContextUtil.getBean("scheduler");	 
		//each job is new = don't use verify schedule, cause this unique
		GregorianCalendar start = new GregorianCalendar();
		start.add(Calendar.MINUTE, 1);
		
		//add time to jobName - may have multiple 
	 	String jobName =  "fillEmailSubscription" + "-" + entryId + "-" + changeDate.getTime();
	 	String className = this.getClass().getName();
	  	try {		
			JobDetail jobDetail = new JobDetail(jobName, ENTRY_SUBSCRIPTION_GROUP, 
					Class.forName(className),false, false, false);
			jobDetail.setDescription("Fill subscription");
			JobDataMap data = new JobDataMap();
			data.put("binder", folderId);
			data.put("zoneName",RequestContextHolder.getRequestContext().getZoneName());
			data.put("entry", entryId);
			data.put("when", changeDate);
			
			jobDetail.setJobDataMap(data);
			jobDetail.addJobListener(getDefaultCleanupListener());
			//retry every hour
	  		SimpleTrigger trigger = new SimpleTrigger(jobName, ENTRY_SUBSCRIPTION_GROUP, jobName, ENTRY_SUBSCRIPTION_GROUP, start.getTime(), null, 24, 1000*60*60);
  			trigger.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT);
  			trigger.setDescription("Fill subscription");
  			trigger.setVolatility(false);
			scheduler.scheduleJob(jobDetail, trigger);				
 		} catch (Exception e) {
   			throw new ConfigurationException("Cannot start (job:group) " + jobName 
   					+ ":" + ENTRY_SUBSCRIPTION_GROUP, e);
   		}
    }
}

