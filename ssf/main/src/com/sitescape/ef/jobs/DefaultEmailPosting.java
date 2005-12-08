package com.sitescape.ef.jobs;

import java.text.ParseException;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.StatefulJob;
import org.quartz.SchedulerException;

import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.module.mail.MailModule;
import com.sitescape.ef.util.SessionUtil;
import com.sitescape.ef.util.SpringContextUtil;

public class DefaultEmailPosting implements StatefulJob, EmailPosting {
	protected Log logger = LogFactory.getLog(getClass());
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		SessionUtil.sessionStartup();
    	MailModule mail = (MailModule)SpringContextUtil.getBean("mailModule");
		try {
			mail.receivePostings();
		} catch (ConfigurationException cf) {
			throw new JobExecutionException(cf);
		} catch (Exception e){
			logger.error(e.getMessage());
    		throw new JobExecutionException(e,false);
    	} finally {
    		SessionUtil.sessionStop();
    	}
    }

	public void disable(Scheduler scheduler) {
		try {
			scheduler.unscheduleJob(EmailPosting.POSTING_NAME, EmailPosting.POSTING_GROUP);
		} catch (SchedulerException se) {			
		}
	}
	public void enable(Scheduler scheduler, Schedule schedule) {
   		try {
   			JobDetail jobDetail=scheduler.getJobDetail(EmailPosting.POSTING_NAME, EmailPosting.POSTING_NAME);
   			CronTrigger trigger = (CronTrigger)scheduler.getTrigger(EmailPosting.POSTING_NAME, EmailPosting.POSTING_NAME);
		   			
		   	//haven't been scheduled yet
		   	if (jobDetail == null) {
		   		trigger = buildCronTrigger(schedule);
	 
		  		//volitility(not stored in db),durablilty(remains after trigger removed),recover(after recover or fail-over)
		   		jobDetail = new JobDetail(EmailPosting.POSTING_NAME, EmailPosting.POSTING_NAME,
						this.getClass(),false, false, false);
				jobDetail.setDescription(EmailPosting.POSTING_NAME);
				scheduler.scheduleJob(jobDetail, trigger);				
		   	} else if (trigger != null) {
				//replace with new trigger if necessary
				String cSched = trigger.getCronExpression();
			 	String nSched = schedule.getQuartzSchedule();
				if (!nSched.equals(cSched)) {
			 		trigger = buildCronTrigger(schedule);
					//replace existing trigger with new one
					scheduler.rescheduleJob(EmailPosting.POSTING_NAME, EmailPosting.POSTING_NAME, trigger);
				}
		   	} else {
		   		trigger = buildCronTrigger(schedule);
		   		scheduler.rescheduleJob(EmailPosting.POSTING_NAME, EmailPosting.POSTING_NAME, trigger); 
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
