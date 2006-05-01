
package com.sitescape.ef.jobs;

import java.util.Iterator;
import java.util.Date;

import org.jbpm.db.JbpmSession;
import org.jbpm.db.SchedulerSession;
import org.jbpm.scheduler.exe.Timer;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

import com.sitescape.ef.module.workflow.WorkflowModule;
import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.ef.module.workflow.impl.WorkflowFactory;

/**
 *
 * @author Jong Kim
 */
public class DefaultWorkflowTimeout extends SSStatefulJob implements WorkflowTimeout {
	 
    /*
     * The bulk of this code is taken from org.jbpm.scheduler.impl.SchedulerThread
     * @see com.sitescape.ef.jobs.SSStatefulJob#doExecute(org.quartz.JobExecutionContext)
     */
	public void doExecute(JobExecutionContext context) throws JobExecutionException {	
		WorkflowModule work = (WorkflowModule)SpringContextUtil.getBean("workflowModule");
    	JbpmSession jbpmSession = WorkflowFactory.getSession();
   		SchedulerSession schedulerSession = new SchedulerSession(jbpmSession);
    	      
   		logger.debug("checking for timers");
   		Iterator iter = schedulerSession.findTimersByDueDate();
   		boolean isDueDateInPast=true; 
   		while( (iter.hasNext()) && (isDueDateInPast)) {
    		Timer timer = (Timer) iter.next();
    	    logger.debug("found timer "+timer);
    	    //Do work inside a transaction in the workflowModule
    	    // if this timer is due
    	    if (timer.isDue()) {
    	    	logger.debug("executing timer '"+timer+"'");
    	        work.modifyWorkflowStateOnTimeout(new Long(timer.getId()));  

    	    } else { // this is the first timer that is not yet due
    	      isDueDateInPast = false;
    		}
   	      }
    }
/**
 * Save for jbpm 3.1
	public void doExecute(JobExecutionContext context) throws JobExecutionException {	
		WorkflowModule work = (WorkflowModule)SpringContextUtil.getBean("workflowModule");
    	JbpmContext jContext = WorkflowFactory.getContext();
    	try {
    		SchedulerSession schedulerSession = jContext.getSchedulerSession();
    	      
    		logger.debug("checking for timers");
    		Iterator iter = schedulerSession.findTimersByDueDate();
    		boolean isDueDateInPast=true; 
    		while( (iter.hasNext()) && (isDueDateInPast)) {
    			Timer timer = (Timer) iter.next();
    			logger.debug("found timer "+timer);
    			//Do work inside a transaction in the workflowModule
    			// if this timer is due
    			if (timer.isDue()) {
    				logger.debug("executing timer '"+timer+"'");
    				work.modifyWorkflowStateOnTimeout(new Long(timer.getId()));  

    			} else { // this is the first timer that is not yet due
    				isDueDateInPast = false;
    			}
   	      	}
    	} finally {
    		jContext.close();
    	}
    }
	**/
    public void schedule(String zoneName, int seconds) {
		Scheduler scheduler = (Scheduler)SpringContextUtil.getBean("scheduler");	 
		try {
			JobDetail jobDetail=scheduler.getJobDetail(zoneName, WORKFLOW_TIMER_GROUP);
			if (jobDetail == null) {
				jobDetail = new JobDetail(zoneName, WORKFLOW_TIMER_GROUP, 
						Class.forName(this.getClass().getName()),false, false, false);
				jobDetail.setDescription(WORKFLOW_TIMER_DESCRIPTION);
				JobDataMap data = new JobDataMap();
				data.put("zoneName",zoneName);
				jobDetail.setJobDataMap(data);
				jobDetail.addJobListener(getDefaultCleanupListener());
				scheduler.addJob(jobDetail, true);
			}
			SimpleTrigger trigger = (SimpleTrigger)scheduler.getTrigger(zoneName, WORKFLOW_TIMER_GROUP);
			//	see if job exists
			if (trigger == null) {
				trigger = new SimpleTrigger(zoneName, WORKFLOW_TIMER_GROUP, zoneName, WORKFLOW_TIMER_GROUP, new Date(), null, 
						SimpleTrigger.REPEAT_INDEFINITELY, seconds*1000);
				trigger.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT);
				trigger.setDescription(WORKFLOW_TIMER_DESCRIPTION);
				trigger.setVolatility(false);
				scheduler.scheduleJob(trigger);				
    	
			} else {
				int state = scheduler.getTriggerState(zoneName, WORKFLOW_TIMER_GROUP);
				if ((state == Trigger.STATE_PAUSED) || (state == Trigger.STATE_NONE)) {
					scheduler.resumeJob(zoneName, WORKFLOW_TIMER_GROUP);
				}
				if (trigger.getRepeatInterval() != seconds*1000) {
					trigger.setRepeatInterval(seconds*1000);
					scheduler.rescheduleJob(zoneName, WORKFLOW_TIMER_GROUP, trigger);
				}
			} 
		} catch (SchedulerException se) {			
			throw new ConfigurationException(se.getLocalizedMessage());			
  		} catch (ClassNotFoundException cf) {
			throw new ConfigurationException(cf.getLocalizedMessage());			
  		}
    }


}
