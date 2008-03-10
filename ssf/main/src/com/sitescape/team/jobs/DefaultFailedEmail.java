/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */

package com.sitescape.team.jobs;

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
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.module.mail.JavaMailSender;
import com.sitescape.team.module.mail.MailModule;
import com.sitescape.team.util.SpringContextUtil;
import com.sitescape.util.Validator;

/**
 * @author Janet McCann
 *
 */
public class DefaultFailedEmail extends SSStatefulJob implements FailedEmail {
	protected Log logger = LogFactory.getLog(getClass());

	/* (non-Javadoc)
	 * @see com.sitescape.team.jobs.SSStatefulJob#doExecute(org.quartz.JobExecutionContext)
	 */
    public void doExecute(JobExecutionContext context) throws JobExecutionException {
    	MailModule mail = (MailModule)SpringContextUtil.getBean("mailModule");
		String fileName = (String)jobDataMap.get("mailMessage");
		//set file name for delete when trigger complete
    	File file = new File(fileName);
		FileInputStream fs=null;
		Date next = context.getNextFireTime();
		context.setResult("Failed");
		try {
			fs = new FileInputStream(file);
			String name = (String)jobDataMap.get("mailSender");
			String account = (String)jobDataMap.get("mailAccount");
			String password = (String)jobDataMap.get("mailPwd");
			try {
				mail.sendMail(name, account, password, fs);
				context.put(CleanupJobListener.CLEANUPSTATUS, CleanupJobListener.DeleteJob);
				try {
					fs.close();
					fs = null;
					file.delete();
				} catch (IOException io) {
					logger.error("Mail file error (" + file.getName() + ") " + io);
				} finally {
					context.setResult("Success");
				}
				return;
				
		   	} catch (MailSendException sx) {
		   		//try again
	    		logger.error("Error sending mail:" + sx.getLocalizedMessage());
	    	} catch (MailAuthenticationException ax) {
		   		//try again
	       		logger.error("Authentication Exception:" + ax.getLocalizedMessage());				
			} catch (Exception ex) {
				//remove job
				context.put(CleanupJobListener.CLEANUPSTATUS, CleanupJobListener.DeleteJobOnError);
				throw new JobExecutionException(ex);
			}
			//see if we should give up
			if (next == null) {
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

		
    public void schedule(Binder binder, JavaMailSender mailSender, MimeMessage mail, File fileDir) {
    	schedule(binder, mailSender, null, null, mail, fileDir);
    }
    public void schedule(Binder binder, JavaMailSender mailSender, String account, String password, MimeMessage mail, File fileDir) {
		Scheduler scheduler = (Scheduler)SpringContextUtil.getBean("scheduler");	 
		//each job is new = don't use verify schedule, cause this a unique
		GregorianCalendar start = new GregorianCalendar();
		start.add(Calendar.HOUR_OF_DAY, 1);
			
		//add time to jobName - may have multple from same binder
	 	String jobName =  binder.getId() + "-" + start.getTime().getTime();
	   	String description = SSStatefulJob.trimDescription(start.getTime() + ":" + binder.getTitle());
	 	String className = this.getClass().getName();
	  	try {		
			JobDetail jobDetail = new JobDetail(jobName, RETRY_GROUP, 
					Class.forName(className),false, false, false);
			jobDetail.setDescription(description);
			JobDataMap data = new JobDataMap();
			data.put("binder",binder.getId());
			data.put("zoneId",binder.getZoneId());
			
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
			if (Validator.isNotNull(account)) {
				data.put("mailAccount", account);
				data.put("mailPwd", password);
			}
			jobDetail.setJobDataMap(data);
			jobDetail.addJobListener(getDefaultCleanupListener());
	  		SimpleTrigger trigger = new SimpleTrigger(jobName, RETRY_GROUP, jobName, RETRY_GROUP, start.getTime(), null, 24, 1000*60*60);
  			trigger.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT);
  			trigger.setDescription(description);
  			trigger.setVolatility(false);
			scheduler.scheduleJob(jobDetail, trigger);				
 		} catch (Exception e) {
   			throw new ConfigurationException("Cannot start (job:group) " + jobName 
   					+ ":" + RETRY_GROUP, e);
   		}
    }	
}
