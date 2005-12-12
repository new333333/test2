
package com.sitescape.ef.jobs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.mail.internet.MimeMessage;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Date;

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

/**
 * @author Janet McCann
 *
 */
public class DefaultFailedEmail extends SSStatefulJob implements FailedEmail {
	protected Log logger = LogFactory.getLog(getClass());

	/* (non-Javadoc)
	 * @see com.sitescape.ef.jobs.SSStatefulJob#doExecute(org.quartz.JobExecutionContext)
	 */
    public void doExecute(JobExecutionContext context) throws JobExecutionException {
    	MailModule mail = (MailModule)SpringContextUtil.getBean("mailModuleTarget");
		String fileName = (String)jobDataMap.get("mailMessage");
		//set file name for delete when trigger complete
    	File file = new File(fileName);
		FileInputStream fs=null;
		Date next = context.getNextFireTime();
		context.setResult("Failed");
		try {
			fs = new FileInputStream(file);
			String name = (String)jobDataMap.get("mailSender");
			if (mail.sendMail(name, fs) == true)  {
				context.put(CleanupJobListener.CLEANUPSTATUS, CleanupJobListener.DeleteJob);
				try {
					fs.close();
					fs = null;
					context.setResult("Success");
					file.delete();
				} catch (IOException io) {
					logger.error("Mail file error (" + file.getName() + ") " + io);
				}
				
			} else if (next == null) {
				//end of schedule
				context.put(CleanupJobListener.CLEANUPSTATUS, CleanupJobListener.DeleteJob);
				try {
					fs.close();
					fs = null;
					name = file.getName();
		            int dotIdx = name.indexOf('.');
		            if (dotIdx >= 0)
		                name = name.substring(0, dotIdx);
		            name = name + ".dead";
		            String path = file.getAbsolutePath();
		            int lastSep = path.lastIndexOf(File.separator);
		            if (lastSep > 0)
		                path = path.substring(0,lastSep);
		            File dead = new File(path + File.separator + name);
					file.renameTo(dead);
				} catch (IOException io) {
					logger.error("Mail file error (" + file.getName() + ") " + io);
				}
					
			}

		} catch (FileNotFoundException fe) {
			//remove job
			context.put(CleanupJobListener.CLEANUPSTATUS, CleanupJobListener.DeleteJobOnError);
			throw new JobExecutionException(fe);
		} finally {
			if (fs != null) {
				try {
					fs.close();
				} catch (IOException io) {}
			}
		}
    }

		
    public void schedule(Folder folder, JavaMailSender mailSender, MimeMessage mail, File fileDir) {
		Scheduler scheduler = (Scheduler)SpringContextUtil.getBean("scheduler");	 
		//each job is new = don't use verify schedule, cause this a unique
		GregorianCalendar start = new GregorianCalendar();
		start.add(Calendar.HOUR_OF_DAY, 1);
			
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
			
			if(!fileDir.exists())
				fileDir.mkdirs();
			File file = new File(fileDir, String.valueOf(start.getTime().getTime()) + ".mail");
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
	  		SimpleTrigger trigger = new SimpleTrigger(jobName, RETRY_NOTIFICATION_GROUP, jobName, RETRY_NOTIFICATION_GROUP, start.getTime(), null, 24, 1000*60*60);
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
