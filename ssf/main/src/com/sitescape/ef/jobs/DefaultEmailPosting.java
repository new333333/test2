package com.sitescape.ef.jobs;

import java.text.ParseException;
import java.util.TimeZone;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronTrigger;
import org.quartz.Trigger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.StatefulJob;
import org.quartz.SchedulerException;
import org.quartz.JobDataMap;

import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.module.mail.MailModule;
import com.sitescape.ef.module.mail.PostingConfig;
import com.sitescape.ef.util.SessionUtil;
import com.sitescape.ef.util.SpringContextUtil;

public class DefaultEmailPosting implements StatefulJob, EmailPosting {
	protected Log logger = LogFactory.getLog(getClass());
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		SessionUtil.sessionStartup();
    	MailModule mail = (MailModule)SpringContextUtil.getBean("mailModule");
		try {
			ScheduleInfo config = new ScheduleInfo((Map)(context.getJobDetail().getJobDataMap()));
			mail.receivePostings(config);
		} catch (ConfigurationException cf) {
			throw new JobExecutionException(cf);
		} catch (Exception e){
			logger.error(e.getMessage());
    		throw new JobExecutionException(e,false);
    	} finally {
    		SessionUtil.sessionStop();
    	}
    }
	public ScheduleInfo getScheduleInfo() {
		ScheduleInfo info = new ScheduleInfo();
		try {
			Scheduler scheduler = (Scheduler)SpringContextUtil.getBean("scheduler");		
			JobDetail jobDetail=scheduler.getJobDetail(EmailPosting.POSTING_NAME, EmailPosting.POSTING_NAME);
			if (jobDetail == null) return info;
			
			int state = scheduler.getTriggerState(EmailPosting.POSTING_NAME, EmailPosting.POSTING_NAME);
			if ((state == Trigger.STATE_PAUSED) || (state == Trigger.STATE_NONE))
				info.setEnabled(false);
			else
				info.setEnabled(true);
			info.setDetails(jobDetail.getJobDataMap());
			return info;
		} catch (SchedulerException se) {
			return info;
		}
	}
	public void setScheduleInfo(ScheduleInfo info) throws ParseException {
		try {
			Scheduler scheduler = (Scheduler)SpringContextUtil.getBean("scheduler");	 
		 	JobDetail jobDetail=scheduler.getJobDetail(EmailPosting.POSTING_NAME, EmailPosting.POSTING_NAME);
		 	//never been scheduled -start now
		 	if (jobDetail == null) {
 				//volitility(not stored in db),durablilty(remains after trigger removed),recover(after recover or fail-over)
		 		jobDetail = new JobDetail(EmailPosting.POSTING_NAME, EmailPosting.POSTING_NAME,
		 				this.getClass(),false, true, false);
		 		jobDetail.setDescription(EmailPosting.POSTING_NAME);
		 		jobDetail.setJobDataMap((JobDataMap)info.getDetails());
				scheduler.addJob(jobDetail, true);
		 	} else {
		 		//update data if necessary
		 		if (!jobDetail.equals(info.getDetails())) {
			 		jobDetail.setJobDataMap((JobDataMap)info.getDetails());	 			
		 			scheduler.addJob(jobDetail, true);
		 		}
		 	}
  			CronTrigger trigger = (CronTrigger)scheduler.getTrigger(EmailPosting.POSTING_NAME, EmailPosting.POSTING_NAME);
  			//see if stopped
  			if (trigger == null) {
  				if (info.isEnabled()) {
  					trigger = buildCronTrigger(info.getSchedule());
  					scheduler.scheduleJob(trigger);
  				} 
  			} else {
  				//make sure schedule is the same
  				if (info.isEnabled()) {
  					String cSched = trigger.getCronExpression();
  					String nSched = info.getSchedule().getQuartzSchedule();
  					if (!nSched.equals(cSched)) {
  						trigger = buildCronTrigger(info.getSchedule());
  				 		scheduler.rescheduleJob(EmailPosting.POSTING_NAME, EmailPosting.POSTING_NAME, trigger);
 	 				} 
  				} else {
			 		scheduler.unscheduleJob(EmailPosting.POSTING_NAME, EmailPosting.POSTING_NAME);  					
  				}
				
  			}
		} catch (SchedulerException se) {			
			throw new ConfigurationException(se.getLocalizedMessage());
		}
		
	}

	public void enable(boolean enable) {
  		try {
			Scheduler scheduler = (Scheduler)SpringContextUtil.getBean("scheduler");		
			if (!enable) {
  				scheduler.unscheduleJob(EmailPosting.POSTING_NAME, EmailPosting.POSTING_GROUP);
   				return;
			}
   			JobDetail jobDetail=scheduler.getJobDetail(EmailPosting.POSTING_NAME, EmailPosting.POSTING_NAME);
   			CronTrigger trigger = (CronTrigger)scheduler.getTrigger(EmailPosting.POSTING_NAME, EmailPosting.POSTING_NAME);
		   	//haven't been scheduled yet -start with defaults
		   	if (jobDetail == null) {
		   		ScheduleInfo info = new ScheduleInfo();
		   		info.setEnabled(true);
		   		setScheduleInfo(info);

		   	} else if (trigger != null) {
			 	if (scheduler.getTriggerState(EmailPosting.POSTING_NAME, EmailPosting.POSTING_NAME) == Trigger.STATE_PAUSED) {
			 		scheduler.resumeJob(EmailPosting.POSTING_NAME, EmailPosting.POSTING_NAME);
				}
		   	} else {
		   		JobDataMap data = jobDetail.getJobDataMap();
		   		
		   		trigger = buildCronTrigger(new Schedule((String)data.get("schedule")));
		   		scheduler.scheduleJob(trigger);
		   	}    	
   		} catch (Exception e) {
		 	throw new ConfigurationException("Cannot start (job:group) " + EmailPosting.POSTING_NAME
					+ ":" + EmailPosting.POSTING_NAME, e);
		}		
	}
	private CronTrigger buildCronTrigger(Schedule schedule) throws ParseException {
		TimeZone tz;
	  	try {
	  		tz = RequestContextHolder.getRequestContext().getUser().getTimeZone();
	  	} catch (Exception e) {
	  		tz = TimeZone.getDefault();
	  	}
	  	CronTrigger trigger = new CronTrigger(EmailPosting.POSTING_NAME, EmailPosting.POSTING_NAME,EmailPosting.POSTING_NAME, 
   				EmailPosting.POSTING_NAME, schedule.getQuartzSchedule(), tz);
   		trigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW);
		trigger.setVolatility(false);
		return trigger;

	}


}
