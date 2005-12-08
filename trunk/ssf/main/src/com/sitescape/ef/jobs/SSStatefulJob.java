package com.sitescape.ef.jobs;

import java.text.ParseException;
import java.util.TimeZone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.JobDetail;
import org.quartz.StatefulJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.sitescape.ef.util.SpringContextUtil;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.context.request.RequestContext;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.NoUserByTheIdException;

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
	protected ApplicationContext ctx;
	protected JobDataMap jobDataMap;
	protected CoreDao coreDao;
	protected User user;
	protected String zoneName;
	
	public void execute(final JobExecutionContext context) throws JobExecutionException {
    	coreDao = (CoreDao)SpringContextUtil.getBean("coreDao");
    	jobDataMap = context.getJobDetail().getJobDataMap();
    	SessionFactory sessionFactory = (SessionFactory)ctx.getBean("sessionFactory");
		//open shared session
		Session session = SessionFactoryUtils.getSession(sessionFactory, true);
		TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
		try {  
	           	//zone required
           	if (!jobDataMap.containsKey("zoneName")) {			
           		throw new SchedulerException(context.getJobDetail().getFullName() + " : zoneName missing from jobData");
           	}
           	zoneName = jobDataMap.getString("zoneName");
           	//	default user to wf_admin if not supplied
           	//Validate user and zone are compatible
           	if (jobDataMap.containsKey("user")) {
           		user = coreDao.loadUser(new Long(jobDataMap.getLong("user")), zoneName);
           	} else {
           		user = coreDao.findUserByNameOnlyIfEnabled("wf_admin", zoneName);
           	}
    	
           	//Setup thread context expected by business logic
           	RequestContext rc = new RequestContext(user.getZoneName(), user.getName());
           	RequestContextHolder.setRequestContext(rc);
           	//	do the real work
           	doExecute(context);

		} catch (NoUserByTheIdException nu) {
			removeJobOnError(context, nu);
		} catch (JobExecutionException je) {
			//re-throw
			throw je;
		} catch (Exception e){
			logger.error(e.getMessage());
    		throw new JobExecutionException(e,false);
    	} finally {
			TransactionSynchronizationManager.unbindResource(sessionFactory);
			SessionFactoryUtils.releaseSession(session, sessionFactory);    		
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
		logger.error(e.getMessage());
		logger.error("Removing triggers for " + context.getJobDetail().getFullName());
		context.setResult(CleanupJobListener.DeleteJobOnError);
		throw new JobExecutionException(e, false);
	}

	protected abstract void doExecute(JobExecutionContext context) throws JobExecutionException;

	public void verifySchedule(Scheduler scheduler, JobDescription job) {
   		try {
   			if (!job.isEnabled()) {
   				scheduler.unscheduleJob(job.getName(), job.getGroup());
   				return;
   			}
   			JobDetail jobDetail=scheduler.getJobDetail(job.getName(), job.getGroup());
   			CronTrigger trigger = (CronTrigger)scheduler.getTrigger(job.getName(), job.getGroup());
   			
   			//haven't been scheduled yet
   			if (jobDetail == null) {
   				trigger = buildTrigger(job);
  				//volitility(not stored in db),durablilty(remains after trigger removed),recover(after recover or fail-over)
   				jobDetail = new JobDetail(job.getName(), job.getGroup(),
						this.getClass(),false, false, false);
				jobDetail.setDescription(job.getDescription());
				jobDetail.setJobDataMap(job.getData());
				jobDetail.addJobListener(com.sitescape.ef.jobs.CleanupJobListener.name);
				scheduler.scheduleJob(jobDetail, trigger);				
   			} else if (trigger != null) {
				//replace with new trigger if necessary
				String cSched = trigger.getCronExpression();
	   			String nSched = job.getSchedule();
				if (!nSched.equals(cSched)) {
	   				trigger = buildTrigger(job);
					//replace existing trigger with new one
					scheduler.rescheduleJob(job.getName(), job.getGroup(), trigger);
				}
   			} else {
   				trigger = buildTrigger(job);
   				scheduler.rescheduleJob(job.getName(), job.getGroup(), trigger); 
   			}    	
   		} catch (Exception e) {
   			throw new ConfigurationException("Cannot start scheduler", e);
   		}		
	}
    public CronTrigger buildTrigger(JobDescription job) throws ParseException{
    	
   		CronTrigger trigger = new CronTrigger(job.getName(), job.getGroup(), job.getName(), 
   					job.getGroup(), job.getSchedule(), job.getTimeZone());
   		trigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW);
   		trigger.setVolatility(false);
 
		return trigger;    	
    }
    protected interface JobDescription {
    	public  String getSchedule();
    	public  String getDescription();
    	public  JobDataMap getData();
    	public  boolean isEnabled();
    	public String getName();
    	public String getGroup();
    	public TimeZone getTimeZone();
    }
}
