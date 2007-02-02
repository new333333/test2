
package com.sitescape.ef.jobs;

import java.util.Iterator;
import java.util.Date;

import org.jbpm.JbpmContext;
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
import com.sitescape.ef.module.workflow.impl.WorkflowFactory;
import com.sitescape.team.util.SpringContextUtil;

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

    public void schedule(Long zoneId, int seconds) {
		Scheduler scheduler = (Scheduler)SpringContextUtil.getBean("scheduler");	 
		try {
			JobDetail jobDetail=scheduler.getJobDetail(zoneId.toString(), WORKFLOW_TIMER_GROUP);
			if (jobDetail == null) {
				jobDetail = new JobDetail(zoneId.toString(), WORKFLOW_TIMER_GROUP, 
						Class.forName(this.getClass().getName()),false, false, false);
				jobDetail.setDescription(WORKFLOW_TIMER_DESCRIPTION);
				JobDataMap data = new JobDataMap();
				data.put("zoneId",zoneId);
				jobDetail.setJobDataMap(data);
				jobDetail.addJobListener(getDefaultCleanupListener());
				scheduler.addJob(jobDetail, true);
			}
			SimpleTrigger trigger = (SimpleTrigger)scheduler.getTrigger(zoneId.toString(), WORKFLOW_TIMER_GROUP);
			//	see if job exists
			if (trigger == null) {
				trigger = new SimpleTrigger(zoneId.toString(), WORKFLOW_TIMER_GROUP, zoneId.toString(), WORKFLOW_TIMER_GROUP, new Date(), null, 
						SimpleTrigger.REPEAT_INDEFINITELY, seconds*1000);
				trigger.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT);
				trigger.setDescription(WORKFLOW_TIMER_DESCRIPTION);
				trigger.setVolatility(false);
				scheduler.scheduleJob(trigger);				
    	
			} else {
				int state = scheduler.getTriggerState(zoneId.toString(), WORKFLOW_TIMER_GROUP);
				if ((state == Trigger.STATE_PAUSED) || (state == Trigger.STATE_NONE)) {
					scheduler.resumeJob(zoneId.toString(), WORKFLOW_TIMER_GROUP);
				}
				if (trigger.getRepeatInterval() != seconds*1000) {
					trigger.setRepeatInterval(seconds*1000);
					scheduler.rescheduleJob(zoneId.toString(), WORKFLOW_TIMER_GROUP, trigger);
				}
			} 
		} catch (SchedulerException se) {			
			throw new ConfigurationException(se.getLocalizedMessage());			
  		} catch (ClassNotFoundException cf) {
			throw new ConfigurationException(cf.getLocalizedMessage());			
  		}
    }


}
