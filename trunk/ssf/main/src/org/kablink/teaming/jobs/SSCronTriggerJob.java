package org.kablink.teaming.jobs;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import org.kablink.teaming.ConfigurationException;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;


public abstract class SSCronTriggerJob extends SSStatefulJob {
	/**
	 * Sync quartz with new scheduler information..  Always register job, even if disabled.
	 * @param info
	 * @throws ParseException
	 */
	public void setScheduleInfo(CronJobDescription job, ScheduleInfo info) {
		try {
			Scheduler scheduler = getScheduler();		
		 	JobDetail jobDetail=scheduler.getJobDetail(job.getJobName(), job.getJobGroup());
		 	//never been scheduled -start now
		 	if (jobDetail == null) {
 				//volitility(not stored in db),durablilty(remains after trigger removed),recover(after recover or fail-over)
		 		jobDetail = new JobDetail(job.getJobName(), job.getJobGroup(),
		 				this.getClass(),false, true, false);
				jobDetail.setDescription(job.getJobDescription());
				jobDetail.setJobDataMap((JobDataMap)info.getDetails());
				jobDetail.addJobListener(getDefaultCleanupListener());
				scheduler.addJob(jobDetail, true);
		 	} else {
		 		//update data if necessary
		 		if (!jobDetail.getJobDataMap().equals(info.getDetails())) {
			 		jobDetail.setJobDataMap((JobDataMap)info.getDetails());	 			
		 			scheduler.addJob(jobDetail, true);
		 		}
		 	}
  			CronTrigger trigger = (CronTrigger)scheduler.getTrigger(job.getTriggerName(), job.getTriggerGroup());
  			//see if stopped
  			if (trigger == null) {
  				if (info.isEnabled()) {
  					trigger = buildCronTrigger(job, info.getSchedule());
  					scheduler.scheduleJob(trigger);
  				} 
  			} else {
  				//make sure schedule is the same
  				if (info.isEnabled()) {
  					String cSched = trigger.getCronExpression();
  					String nSched = info.getSchedule().getQuartzSchedule();
  					if (!nSched.equals(cSched)) {
  						trigger = buildCronTrigger(job, info.getSchedule());
  				 		scheduler.rescheduleJob(job.getJobName(), job.getJobGroup(), trigger);
 	 				} else {
 	 					int state = scheduler.getTriggerState(job.getTriggerName(), job.getTriggerGroup());
 	 					if ((state == Trigger.STATE_PAUSED) || (state == Trigger.STATE_NONE)) {
 	 						scheduler.resumeJob(job.getJobName(), job.getJobGroup());
 	 					}
 	 				}
  				} else {
			 		scheduler.unscheduleJob(job.getJobName(), job.getJobGroup());  					
  				}
				
  			}
		} catch (SchedulerException se) {			
			throw new ConfigurationException(se.getLocalizedMessage());
		} catch (ParseException pe) {
			throw new ConfigurationException("Error parsing schedule", pe);
		}
		
	}
	/**
	 * Get scheduler information for a job
	 * @param job
	 * @return
	 */
	public ScheduleInfo getScheduleInfo(CronJobDescription job) {
		try {
			Scheduler scheduler = getScheduler();		
			JobDetail jobDetail=scheduler.getJobDetail(job.getJobName(), job.getJobGroup());
			if (jobDetail == null) {
				return job.getDefaultScheduleInfo();
			}
			
			ScheduleInfo info = new ScheduleInfo(job.getZoneId());
			int state = scheduler.getTriggerState(job.getTriggerName(), job.getTriggerGroup());
			if ((state == Trigger.STATE_PAUSED) || (state == Trigger.STATE_NONE))
				info.setEnabled(false);
			else
				info.setEnabled(true);
			info.setDetails(jobDetail.getJobDataMap());
			return info;
		} catch (SchedulerException se) {
			return job.getDefaultScheduleInfo();
		}
	}


	public CronTrigger buildCronTrigger(CronJobDescription job, Schedule schedule) throws ParseException{
    	
   		CronTrigger trigger = new CronTrigger(job.getJobName(), job.getJobGroup(), job.getTriggerName(), 
   					job.getTriggerGroup(), schedule.getQuartzSchedule(), job.getTimeZone());
   		trigger.setMisfireInstruction(job.getMisfireInstruction());
   		trigger.setVolatility(false);
 
		return trigger;    	
    }
	/**
	 * Enable or disable a job
	 * @param job
	 */
	public void enable(boolean enable, CronJobDescription job) {
		ScheduleInfo info = getScheduleInfo(job); 
		if (enable && info.isEnabled()) return;
		if (!enable && !info.isEnabled()) return;
		info.setEnabled(enable);
		setScheduleInfo(job,info);
 	}

 	public class CronJobDescription {
		protected Long zoneId;
		protected String jobName, jobGroup, jobDescription;
		CronJobDescription(Long zoneId) {
			this.zoneId = zoneId;
		}
		CronJobDescription(Long zoneId, String jobName, String jobGroup, String jobDescription) {
			this.zoneId = zoneId;
			this.jobName = jobName;
			this.jobGroup = jobGroup;
			this.jobDescription = trimDescription(jobDescription);
		}
		protected Long getZoneId() {
			return zoneId;
		}
		protected String getJobName() {
			return jobName;
		}
		protected String getJobGroup() {
			return jobGroup;
		}
		protected String getJobDescription() {
			return jobDescription;
		}
     	public TimeZone getTimeZone() {
    		return getDefaultTimeZone();
     	}
 		
		protected String getTriggerName() {
			return getJobName();
		}
		protected String getTriggerGroup() {
			return getJobGroup();
		}
		protected String getTriggerDescription() {
			return getJobDescription();
		}
		protected Date getStartDate() {
			return new Date();
		}
		protected Date getEndDate() {
			return null;
		}
		protected int getMisfireInstruction() {
			return CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW;
		}
		protected ScheduleInfo getDefaultScheduleInfo() {
	   		return new ScheduleInfo(zoneId);   	 
		}

	}
}
