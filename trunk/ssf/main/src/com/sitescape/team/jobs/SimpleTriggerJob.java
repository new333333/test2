package com.sitescape.team.jobs;

import java.util.Date;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;

import com.sitescape.team.ConfigurationException;

public abstract class SimpleTriggerJob extends SSStatefulJob {
	
	public void schedule(SimpleJobDescription job) {
		Scheduler scheduler = getScheduler();		
		try {
			JobDetail jobDetail=scheduler.getJobDetail(job.getJobName(), job.getJobGroup());
			if (jobDetail == null) {
				jobDetail = new JobDetail(job.getJobName(), job.getJobGroup(), 
						Class.forName(this.getClass().getName()),false, false, false);
				jobDetail.setDescription(job.getJobDescription());
				jobDetail.setJobDataMap(job.getData());
				jobDetail.addJobListener(getDefaultCleanupListener());
				scheduler.addJob(jobDetail, true);
			} else {
		 		//update data if necessary
		 		if (!jobDetail.getJobDataMap().equals(job.getData())) {
			 		jobDetail.setJobDataMap(job.getData());	 			
		 			scheduler.addJob(jobDetail, true);
		 		}
			}
			SimpleTrigger trigger = (SimpleTrigger)scheduler.getTrigger(job.getTriggerName(), job.getTriggerGroup());
			//	see if job exists
			if (trigger == null) {
				trigger = new SimpleTrigger(job.getJobName(), job.getJobGroup(), job.getTriggerName(), job.getTriggerGroup(), job.getStartDate(), job.getEndDate(), 
						job.getRepeatCount(),  job.getRepeatSeconds()*1000);
				trigger.setMisfireInstruction(job.getMisfireInstruction());
				trigger.setDescription(job.getTriggerDescription());
				trigger.setVolatility(false);
				scheduler.scheduleJob(trigger);				
		
			} else {
				if (trigger.getRepeatInterval() != job.getRepeatSeconds()*1000) {
					trigger.setRepeatInterval(job.getRepeatSeconds()*1000);
					scheduler.rescheduleJob(job.getJobName(), job.getJobGroup(), trigger);
				}
			} 
		} catch (SchedulerException se) {			
			throw new ConfigurationException(se.getLocalizedMessage());			
		} catch (ClassNotFoundException cf) {
			throw new ConfigurationException(cf.getLocalizedMessage());			
		}
	}
	public class SimpleJobDescription {
		protected Long zoneId;
		protected String jobName, jobGroup, jobDescription;
		int seconds;
		SimpleJobDescription(Long zoneId) {
			this.zoneId = zoneId;
		}
		SimpleJobDescription(Long zoneId, String jobName, String jobGroup, String jobDescription, int seconds) {
			this.zoneId = zoneId;
			this.jobName = jobName;
			this.jobGroup = jobGroup;
			this.jobDescription = SSStatefulJob.trimDescription(jobDescription);

			this.seconds = seconds;
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
		protected int getRepeatSeconds() {
			return seconds;
		}
		
		protected JobDataMap getData() {
			JobDataMap data = new JobDataMap();
			data.put("zoneId",zoneId);
			return data;
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
		protected int getRepeatCount() {
			return SimpleTrigger.REPEAT_INDEFINITELY;
		}
		protected int getMisfireInstruction() {
			return SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT;
		}
	}
}
