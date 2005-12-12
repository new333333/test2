package com.sitescape.ef.jobs;

import java.text.ParseException;
import java.util.TimeZone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.StatefulJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;

import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.ef.util.SessionUtil;
import com.sitescape.ef.util.SZoneConfig;

import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.context.request.RequestContext;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.NoUserByTheIdException;
import com.sitescape.ef.domain.NoUserByTheNameException;

/**
 * @author Janet McCann
 * Do common setup for all jobs.
 * Return result objects to be handled by CleanupJobListener:
 * delete a job on unrecoverable error
 * delete a job that is complete
 * do nothing
 */
public abstract class SSStatefulJob implements StatefulJob {
	protected Log logger = LogFactory.getLog(getClass());
	protected JobDataMap jobDataMap;
	protected CoreDao coreDao;
	protected User user;
	protected String zoneName;
	public static int JOBNAME_MAX = 120; 
	public static int DESCRIPTION_MAX = 120; 
	public static String trimJobName(String jobName) {
		if (jobName.length() > JOBNAME_MAX)
			return jobName.substring(0, Math.max(jobName.length(), JOBNAME_MAX));
		return jobName;
	}
	public static String trimDescription(String description) {
		if (description.length() > DESCRIPTION_MAX)
			return description.substring(0, Math.max(description.length(), DESCRIPTION_MAX));
		return description;
	}
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		SessionUtil.sessionStartup();
    	coreDao = (CoreDao)SpringContextUtil.getBean("coreDao");
    	jobDataMap = context.getJobDetail().getJobDataMap();
		context.setResult("Success");
		try {  
	           	//zone required
           	if (!jobDataMap.containsKey("zoneName")) {			
           		throw new SchedulerException(context.getJobDetail().getFullName() + " : zoneName missing from jobData");
           	}
           	zoneName = jobDataMap.getString("zoneName");
           	//Validate user and zone are compatible
           	if (jobDataMap.containsKey("user")) {
           		Long id = new Long(jobDataMap.getLong("user"));
           		user = coreDao.loadUser(id, zoneName);
           		if (user.isDisabled()) throw new NoUserByTheIdException(id);
           	} else {
        		String name = SZoneConfig.getString(zoneName, "property[@name='adminUser']");
           		user = coreDao.findUserByNameOnlyIfEnabled(name, zoneName);
           	}
    	
           	//Setup thread context expected by business logic
           	RequestContext rc = new RequestContext(user.getZoneName(), user.getName());
           	RequestContextHolder.setRequestContext(rc);
           	//	do the real work
           	doExecute(context);

		} catch (NoUserByTheIdException nu) {
			unscheduleJobOnError(context, nu);
		} catch (NoUserByTheNameException nn) {
			unscheduleJobOnError(context, nn);
		} catch (JobExecutionException je) {
			context.setResult("Failed");
			//re-throw
			throw je;
		} catch (ConfigurationException cf) {
			context.setResult("Failed");
			throw new JobExecutionException(cf);
		} catch (Exception e){
			context.setResult("Failed");
    		throw new JobExecutionException(e);
    	} finally {
    		SessionUtil.sessionStop();
    		RequestContextHolder.clear();
    	}

	}   
	/**
	 * Job failed due to missing domain objects.  Return exception that will remove the
	 * job triggers and the job if durablility=false;
	 * @param context
	 * @param e
	 * @throws JobExecutionException
	 */
	protected void removeJobOnError(JobExecutionContext context, Exception e) throws JobExecutionException {
		context.put(CleanupJobListener.CLEANUPSTATUS, CleanupJobListener.DeleteJobOnError);
		context.setResult("Failed");
		throw new JobExecutionException(e);
	}
	/**
	 * Job failed due to missing domain objects.  Return exception that will unschedule the
	 * job triggers.  JobDetails will remain. 
	 * @param context
	 * @param e
	 * @throws JobExecutionException
	 */
	protected void unscheduleJobOnError(JobExecutionContext context, Exception e) throws JobExecutionException {
		context.put(CleanupJobListener.CLEANUPSTATUS, CleanupJobListener.DeleteJobOnError);
		context.setResult("Failed");
		throw new JobExecutionException(e);
	}
	
	protected abstract void doExecute(JobExecutionContext context) throws JobExecutionException;
	/**
	 * Get scheduler information for a job
	 * @param job
	 * @return
	 */
	public ScheduleInfo getScheduleInfo(JobDescription job) {
		try {
			Scheduler scheduler = (Scheduler)SpringContextUtil.getBean("scheduler");		
			JobDetail jobDetail=scheduler.getJobDetail(job.getName(), job.getGroup());
			if (jobDetail == null) {
				return job.getDefaultScheduleInfo();
			}
			
			ScheduleInfo info = new ScheduleInfo(job.getZoneName());
			int state = scheduler.getTriggerState(job.getName(), job.getGroup());
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
	/**
	 * Sync quartz with new scheduler information..  Always register job, even if disabled.
	 * @param info
	 * @throws ParseException
	 */
	public void setScheduleInfo(JobDescription job, ScheduleInfo info) {
		try {
			Scheduler scheduler = (Scheduler)SpringContextUtil.getBean("scheduler");	 
		 	JobDetail jobDetail=scheduler.getJobDetail(job.getName(), job.getGroup());
		 	//never been scheduled -start now
		 	if (jobDetail == null) {
 				//volitility(not stored in db),durablilty(remains after trigger removed),recover(after recover or fail-over)
		 		jobDetail = new JobDetail(job.getName(), job.getGroup(),
		 				this.getClass(),false, true, false);
				jobDetail.setDescription(job.getDescription());
				jobDetail.setJobDataMap((JobDataMap)info.getDetails());
				jobDetail.addJobListener(job.getCleanupListener());
				scheduler.addJob(jobDetail, true);
		 	} else {
		 		//update data if necessary
		 		if (!jobDetail.equals(info.getDetails())) {
			 		jobDetail.setJobDataMap((JobDataMap)info.getDetails());	 			
		 			scheduler.addJob(jobDetail, true);
		 		}
		 	}
  			CronTrigger trigger = (CronTrigger)scheduler.getTrigger(job.getName(), job.getGroup());
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
  				 		scheduler.rescheduleJob(job.getName(), job.getGroup(), trigger);
 	 				} else {
 	 					int state = scheduler.getTriggerState(job.getName(), job.getGroup());
 	 					if ((state == Trigger.STATE_PAUSED) || (state == Trigger.STATE_NONE)) {
 	 						scheduler.resumeJob(job.getName(), job.getGroup());
 	 					}
 	 				}
  				} else {
			 		scheduler.unscheduleJob(job.getName(), job.getGroup());  					
  				}
				
  			}
		} catch (SchedulerException se) {			
			throw new ConfigurationException(se.getLocalizedMessage());
		} catch (ParseException pe) {
			throw new ConfigurationException("Error parsing schedule", pe);
		}
		
	}
	/**
	 * Enable or disable a job
	 * @param job
	 */
	public void enable(boolean enable, JobDescription job) {
		ScheduleInfo info = getScheduleInfo(job); 
		if (enable && info.isEnabled()) return;
		if (!enable && !info.isEnabled()) return;
		info.setEnabled(enable);
		setScheduleInfo(job,info);
 	}

	public String getDefaultCleanupListener() {
		return com.sitescape.ef.jobs.CleanupJobListener.name;
	}
  	public TimeZone getDefaultTimeZone() {
   		try {
			return RequestContextHolder.getRequestContext().getUser().getTimeZone();
		} catch (Exception e) {
			return TimeZone.getDefault();
		}
	}

	public CronTrigger buildCronTrigger(JobDescription job, Schedule schedule) throws ParseException{
    	
   		CronTrigger trigger = new CronTrigger(job.getName(), job.getGroup(), job.getName(), 
   					job.getGroup(), schedule.getQuartzSchedule(), job.getTimeZone());
   		trigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW);
   		trigger.setVolatility(false);
 
		return trigger;    	
    }
    protected interface JobDescription {
    	public  String getDescription();
    	public String getZoneName();
    	public String getName();
    	public String getGroup();
    	public TimeZone getTimeZone();
    	public String getCleanupListener();
    	public ScheduleInfo getDefaultScheduleInfo();
    }
}
