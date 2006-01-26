
package com.sitescape.ef.jobs;

import java.util.Map;
import java.util.List;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Date;


import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.JobDataMap;
import org.quartz.Scheduler;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.module.mail.MailModule;
import com.sitescape.ef.module.mail.MimeMessagePreparator;
import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.util.Validator;

/**
 * @author Janet McCann
 *
 */
public class DefaultSendEmail extends SSStatefulJob implements SendEmail {
	protected Log logger = LogFactory.getLog(getClass());

	/* (non-Javadoc)
	 * @see com.sitescape.ef.jobs.SSStatefulJob#doExecute(org.quartz.JobExecutionContext)
	 */
    public void doExecute(JobExecutionContext context) throws JobExecutionException {
    	MailModule mail = (MailModule)SpringContextUtil.getBean("mailModule");
		Map message = (Map)jobDataMap.get("mailMessage");
		MimeHelper helper = new MimeHelper(message);
		String name = (String)jobDataMap.get("mailSender");
		
		Date next = context.getNextFireTime();
		try {
			if (mail.sendMail(name, helper) == true) {
				context.put(CleanupJobListener.CLEANUPSTATUS, CleanupJobListener.DeleteJob);
				context.setResult("Success");
			} else if (next == null) {
				//end of schedule
				context.put(CleanupJobListener.CLEANUPSTATUS, CleanupJobListener.DeleteJob);
				context.setResult("Failed");
			} else {
				//will be rescheduled
				context.setResult("Failed");				
			}
				
		} catch (Exception ex) {
			//remove job
			context.put(CleanupJobListener.CLEANUPSTATUS, CleanupJobListener.DeleteJobOnError);
			throw new JobExecutionException(ex);
		}
    }

		
    public void schedule(String mailSenderName, Map message, String comment) {
		Scheduler scheduler = (Scheduler)SpringContextUtil.getBean("scheduler");	 
		//each job is new = don't use verify schedule, cause this a unique
		GregorianCalendar start = new GregorianCalendar();
		start.add(Calendar.MINUTE, 1);
		
		//add time to jobName - may have multiple 
	 	String jobName =  "sendMail" + "-" + start.getTime().getTime();
	 	String className = this.getClass().getName();
	  	try {		
			JobDetail jobDetail = new JobDetail(jobName, SEND_MAIL_GROUP, 
					Class.forName(className),false, false, false);
			jobDetail.setDescription(comment);
			JobDataMap data = new JobDataMap();
			data.put("mailSender", mailSenderName);
			data.put("zoneName",RequestContextHolder.getRequestContext().getZoneName());
			data.put("mailMessage", message);
			
			jobDetail.setJobDataMap(data);
			jobDetail.addJobListener(getDefaultCleanupListener());
			//retry every hour
	  		SimpleTrigger trigger = new SimpleTrigger(jobName, SEND_MAIL_GROUP, jobName, SEND_MAIL_GROUP, start.getTime(), null, 24, 1000*60*60);
  			trigger.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT);
  			trigger.setDescription(comment);
  			trigger.setVolatility(false);
			scheduler.scheduleJob(jobDetail, trigger);				
 		} catch (Exception e) {
   			throw new ConfigurationException("Cannot start (job:group) " + jobName 
   					+ ":" + SEND_MAIL_GROUP, e);
   		}
    }	
	 private class MimeHelper implements MimeMessagePreparator {
			MimeMessage message;
			String from;
			Map details;
			
			private MimeHelper(Map details) {
				this.details = details;
			}
			public MimeMessage getMessage() {
				return message;
			}
			public void setDefaultFrom(String from) {
				this.from = from;
			}
			public void prepare(MimeMessage mimeMessage) throws MessagingException {
				//make sure nothing saved yet
				message = null;
				MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
				helper.setSubject((String)details.get(SendEmail.SUBJECT));
				if (details.containsKey(SendEmail.FROM)) 
					helper.setFrom((InternetAddress)details.get(SendEmail.FROM));
				else
					helper.setFrom(from);
				
				List addrs = (List)details.get(SendEmail.TO);
				for (int i=0; i<addrs.size(); ++i) {
					helper.addTo((InternetAddress)addrs.get(i));
				}
				String text = (String)details.get(SendEmail.TEXT_MSG);
				String html = (String)details.get(SendEmail.HTML_MSG);
				if (Validator.isNull(html))
					helper.setText(text);
				else
					helper.setText(text, html);

				mimeMessage.addHeader("Content-Transfer-Encoding", "8bit");
				
				//save message incase cannot connect and need to resend;
				message = mimeMessage;
			}

		}
    
}

