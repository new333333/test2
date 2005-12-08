
package com.sitescape.ef.jobs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import javax.mail.internet.MimeMessage;
import java.util.GregorianCalendar;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.JobDataMap;
import org.quartz.Scheduler;

import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.module.mail.MailModule;
import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.module.mail.JavaMailSender;
import com.sitescape.ef.util.SPropsUtil;
/**
 * @author Janet McCann
 *
 */
public class DefaultFailedEmail extends SSStatefulJob implements FailedEmail{
	protected Log logger = LogFactory.getLog(getClass());

	/* (non-Javadoc)
	 * @see com.sitescape.ef.jobs.SSStatefulJob#doExecute(org.quartz.JobExecutionContext)
	 */
    public void doExecute(JobExecutionContext context) throws JobExecutionException {
    	MailModule mail = (MailModule)SpringContextUtil.getBean("mailModule");
		File file = new File((String)jobDataMap.get("mailMessage"));
		try {
			FileInputStream fs = new FileInputStream(file);
			String name = (String)jobDataMap.get("mailSender");
			if (mail.sendMail(name, fs) == true) {
				context.setResult(CleanupJobListener.DeleteJob);
			}
		} catch (ConfigurationException cf) {
			throw new JobExecutionException(cf);
		} catch (FileNotFoundException fe) {
			//remove job
			context.setResult(CleanupJobListener.DeleteJob);
			logger.error("Mail file missing (" + file.getName() + ") - job cancelled");
		}
    }
    public void schedule(Folder folder, JavaMailSender mailSender, MimeMessage mail) {
		Scheduler scheduler = (Scheduler)SpringContextUtil.getBean("scheduler");	 
		//each job is new = don't use verify schedule, cause this a unique
		GregorianCalendar start = new GregorianCalendar();
		//	start.add(Calendar.HOUR_OF_DAY, 1);
			
		start.add(Calendar.MINUTE, 2);
		//add time to jobName - may have multple from same folder
	 	String jobName =  folder.getId() + "-" + start.getTime().getTime();
	   	String description = SSStatefulJob.trimDescription(start.getTime() + ":" + folder.toString());
	 	String className = this.getClass().getName();
	  	try {		
			JobDetail jobDetail = new JobDetail(jobName, RETRY_NOTIFICATION_GROUP, 
					Class.forName(className),false, false, false);
			jobDetail.setDescription(description);
			JobDataMap data = new JobDataMap();
			data.put("folder",folder.getId());
			data.put("zoneName",folder.getZoneName());
			
			File file = new File(SPropsUtil.getString("default.temp.mail.dir") + "/mail-" + folder.getId() + "-" + start.getTime().getTime() + ".mail");
			try {
				file.createNewFile();
				FileOutputStream fo = new FileOutputStream(file);
				mail.writeTo(fo);
			} catch (Exception ex) {
				logger.error("Unable to queue mail: " + ex.getLocalizedMessage());
				return;
			}
			data.put("mailMessage", file.getPath());
			data.put("mailSender", mailSender.getName());
			jobDetail.setJobDataMap(data);
			jobDetail.addJobListener(getDefaultCleanupListener());
	  		SimpleTrigger trigger = new SimpleTrigger(jobName, RETRY_NOTIFICATION_GROUP, jobName, RETRY_NOTIFICATION_GROUP, start.getTime(), null, 24, 1000*60);
  			trigger.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT);
  			trigger.setDescription(description);
  			trigger.setVolatility(false);
			scheduler.scheduleJob(jobDetail, trigger);				
 		} catch (Exception e) {
   			throw new ConfigurationException("Cannot start (job:group) " + jobName 
   					+ ":" + RETRY_NOTIFICATION_GROUP, e);
   		}
    }	
}
