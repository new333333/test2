/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */

package org.kablink.teaming.jobs;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.jobs.SimpleTriggerJob.SimpleJobDescription;
import org.kablink.teaming.module.mail.JavaMailSender;
import org.kablink.teaming.module.mail.MailModule;
import org.kablink.teaming.module.mail.MimeMapPreparator;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.util.Validator;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.MailSendException;


/**
 * @author Janet McCann
 *
 */
public class DefaultSendEmail extends SimpleTriggerJob implements SendEmail {
	protected Log logger = LogFactory.getLog(getClass());

	/* (non-Javadoc)
	 * @see org.kablink.teaming.jobs.SSStatefulJob#doExecute(org.quartz.JobExecutionContext)
	 */
    public void doExecute(JobExecutionContext context) throws JobExecutionException {
		Object msg = jobDataMap.get("mailMessage");
		if (msg instanceof Map) {
			doExecuteV1(context, (Map) msg);
			return;
		} 
		MailModule mail = (MailModule)SpringContextUtil.getBean("mailModule");
		String fileName = (String)msg;
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
				Exception[] exceptions = sx.getMessageExceptions();
	 			if (exceptions != null && exceptions.length > 0) {
	 				//some users send, others not
	 				logger.error(sx.toString()); 	
	 				next = null;
	 			} else {
	 				//try again
	 				logger.error("Error sending mail:" + sx.getLocalizedMessage());
	 			}
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
    //handle mail left from v1.  This format is obsolete
    protected void doExecuteV1(JobExecutionContext context, Map message) throws JobExecutionException {
		MailModule mail = (MailModule)SpringContextUtil.getBean("mailModule");
			
		String name = (String)jobDataMap.get("mailSender");
		Date next = context.getNextFireTime();
		try {
			if (message.containsKey("mimeMessage")) {
				ByteArrayInputStream ios = new ByteArrayInputStream((byte[])message.get("mimeMessage"));
				//	send pre-composed message
				mail.sendMail(name, ios);
			} else {
				MimeMapPreparator helper = new MimeMapPreparator(message, logger, SPropsUtil.getBoolean("mail.sendVTODO", true));
				mail.sendMail(name, helper);
			} 
			context.put(CleanupJobListener.CLEANUPSTATUS, CleanupJobListener.DeleteJob);
			context.setResult("Success");
			return;
	   	} catch (MailSendException sx) {
 			Exception[] exceptions = sx.getMessageExceptions();
 			if (exceptions != null && exceptions.length > 0) {
 				//some users send, others not
 				logger.error(sx.toString()); 	
 				next = null;
 			} else {
 				logger.error("Error sending mail:" + sx.getLocalizedMessage());
 			}
    	} catch (MailAuthenticationException ax) {
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
			context.setResult("Failed");
		} else {
		//will be rescheduled
		context.setResult("Failed");
		}
    }

    public void schedule(JavaMailSender mailSender, MimeMessage message, String comment, File fileDir, boolean now) {
    	schedule(mailSender, null, null, message, comment, fileDir, now);
    }
    public void schedule(JavaMailSender mailSender, String account, String password, MimeMessage message, String comment, File fileDir, boolean now) {
		//each job is new 
		GregorianCalendar start = new GregorianCalendar();
		int secondsDelay = 60;
		try {
			secondsDelay = SPropsUtil.getInt("mail.seconds.delay", secondsDelay);
		} catch(Exception e) {}
		if (now) start.add(Calendar.SECOND, secondsDelay);
		else start.add(Calendar.HOUR_OF_DAY, 1);

		JobDataMap data = new JobDataMap();
		data.put("mailSender", mailSender.getName());
		data.put("zoneId",RequestContextHolder.getRequestContext().getZoneId());
		if (Validator.isNotNull(account)) {
			data.put("mailAccount", account);
			data.put("mailPwd", password);
		}
		if(!fileDir.exists())
			fileDir.mkdirs();
		File file = new File(fileDir, String.valueOf(start.getTime().getTime()) + ".mail");
		FileOutputStream fo=null;
		try {
			file.createNewFile();
			fo = new FileOutputStream(file);
			message.writeTo(fo);
		} catch (MessagingException io) {
			throw new MailPreparationException(NLT.get("errorcode.sendMail.cannotSerialize", new Object[] {io.getLocalizedMessage()}));
		} catch (IOException io) {
			throw new MailPreparationException(NLT.get("errorcode.sendMail.cannotSerialize", new Object[] {io.getLocalizedMessage()}));
		} catch (Exception ex) {
			logger.error("Unable to queue mail: " + ex.getLocalizedMessage());
			return;
		} finally {
			try {
				fo.close();
			} catch (Exception ex) {};
		}
		data.put("mailMessage", file.getPath());
		
		schedule(new JobDescription(RequestContextHolder.getRequestContext().getZoneId(), comment, start.getTime(), data));
    }	
	public class JobDescription extends SimpleJobDescription {
		Date startDate;
		JobDataMap data;
		JobDescription(Long zoneId, String description, Date startDate, JobDataMap data) {
			super(zoneId, "sendMail" + "-" + startDate.getTime(), SEND_MAIL_GROUP, description, 60*60);
			this.data = data;
			this.startDate = startDate;
		}
		protected Date getStartDate() {
			return startDate;
		}
		protected int getRepeatCount() { //every hour for 1 day
			return 24;
		}

		protected JobDataMap getData() {
			return data;
		}		
	}    
}

