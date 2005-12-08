
package com.sitescape.ef.module.mail.impl;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.ArrayList;
import javax.mail.MessagingException;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.SimpleTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.ConfigurationException;

import com.sitescape.ef.domain.NotificationDef;
import com.sitescape.ef.domain.Notification;
import com.sitescape.ef.domain.UserNotification;
import com.sitescape.ef.modelprocessor.ProcessorManager;
import com.sitescape.ef.module.mail.MailModule;
import com.sitescape.ef.module.mail.FolderEmailFormatter;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailAuthenticationException;

import com.sitescape.util.GetterUtil;
import com.sitescape.util.Validator;
/**
 * @author Janet McCann
 *
 */
public class MailModuleImpl implements MailModule {
	private Log logger = LogFactory.getLog(getClass());
	private CoreDao coreDao;
	private ProcessorManager processorManager;
	private static String RETRY_NOTIFICATION_GROUP="retry-send-email-notification";
	private Scheduler scheduler; 
	private SimpleMailMessage sampleMessage;
	private MailSender mailSender;
	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}
	public void setMessage(SimpleMailMessage message) {
		this.sampleMessage = message;
	}
   /**
     * This method is used only by the IoC container. 
     * @param commonService
     */
    public void setCoreDao(CoreDao coreDao) {
        this.coreDao = coreDao;
    }

    public void setProcessorManager(ProcessorManager processorManager) {
        this.processorManager = processorManager;
    }
    public void setScheduler(Scheduler scheduler) {
    	this.scheduler = scheduler;
    }

    public Date sendNotifications(Long forumId) {
        String zoneName = RequestContextHolder.getRequestContext().getZoneName();
 		Folder forum = (Folder)coreDao.loadBinder(forumId, zoneName); 
		Date current = new Date();
		//get forum specific helper to build messages
  		FolderEmailFormatter helper = (FolderEmailFormatter)processorManager.getProcessor(forum,FolderEmailFormatter.PROCESSOR_KEY);
		NotificationDef nDef = forum.getNotificationDef();
  		 
		List entries = coreDao.loadChangedEntries(forum, nDef.getLastNotification(),
				current, helper.getLookupOrder());
 		nDef.setLastNotification(current);
		if (entries.isEmpty()) {
			return current;
		}
		
		List notifications = nDef.getDistribution(); 
		String [] us = nDef.getEmailAddress();
		if ((us.length ==0) && (notifications.size() == 0)) return current;
		//All users
		Set userIds = new TreeSet();
		// Users wanting individual, message style email
		Set indivUserIds = new TreeSet(); 
		buildToLists(forum, userIds, indivUserIds);
		
		//TODO: check read access - remove users who don't have acess
		//TODO: need to check access for each user against each entry
		Object[] results = helper.validateIdList(entries, userIds);
		String subject = helper.getNotificationSubject(forum);
		
		for (int i=0; i<results.length; ++i) {
			Object row[] = (Object [])results[i];
			//Eventually need to remove individual users from user list and make
			//2 calls - one for digest, one for full
			String message = helper.buildNotificationMessage(forum, (Collection)row[0]);
			String [] mailTo = buildMailToList((Collection)row[1], zoneName, us);
			SimpleMailMessage mailMsg = new SimpleMailMessage(sampleMessage);
			mailMsg.setTo(mailTo);
			mailMsg.setSubject(subject);
			mailMsg.setText(message);
			if (!sendMail(mailMsg)) {
				scheduleMailRetry(forum, mailMsg);
			}
		}
	
		return current;
	}
    public boolean sendMail(SimpleMailMessage mailMsg) {
    	try {
			mailSender.send(mailMsg);
		} catch (MailParseException px) {
       		logger.error(px.getMessage());
    		return false;
    		
    	} catch (MailSendException sx) {
    		logger.error("Error sending mail:" + sx.getMessage());
    		return false;
    	} catch (MailAuthenticationException ax) {
       		logger.error("Authentication Exception:" + ax.getMessage());
    		return false;    		
    	}
    	return true;
    }
    /*
     * Parse the distribution list.  Explode all groups in the list to
     * their user member ids.  Keep list of users requesting individual style
     * email.  
     * 
     */
	private void buildToLists(Folder forum, Set userIds, Set indivUserIds) {
		List notifications = forum.getNotificationDef().getDistribution();
		//Build id set to build user list
		for (int i=0; i<notifications.size();++i) {
			Notification notify = (Notification)notifications.get(i);
			if (!(notify instanceof UserNotification))
				userIds.add(notify.getSendTo().getId());
		}
        //turn list of users and groups into list of only users
		Set newIds = coreDao.explodeGroups(userIds);
		userIds.clear();
		userIds.addAll(newIds);
		for (int i=0; i<notifications.size();++i) {
			Notification notify = (Notification)notifications.get(i);			
			if (notify instanceof UserNotification) {
				UserNotification userNotify = (UserNotification)notify;
				if (userNotify.isDisabled()) {
					userIds.remove(userNotify.getSendTo().getId());
				} else if (userNotify.getStyle() == UserNotification.DIGEST_STYLE_EMAIL_NOTIFICATION) {
					userIds.add(userNotify.getSendTo().getId());
				} else { //may have to check if this option is enabled zone wide?
					indivUserIds.add(userNotify.getSendTo().getId());
					//add user to main list for access checks. - remove later
					userIds.add(userNotify.getSendTo().getId());
				}
			}
		}		
		//remove disabled users
		List users = coreDao.loadEnabledUsers(userIds);
		userIds.clear();
		for (int i=0;i<users.size(); ++i) {
			userIds.add(((User)users.get(i)).getId());
		}
		indivUserIds.retainAll(userIds);
	}
		
	private String [] buildMailToList(Collection userIds, String zoneName, String[] addrsOnly) {
		Set addresses = new HashSet(userIds.size());
		for (Iterator iter=userIds.iterator();iter.hasNext();) {
			User user = coreDao.loadUser((Long)iter.next(), zoneName);
			String email = user.getEmailAddress();
			try {
				if (!Validator.isNull(email)) {
					//validate address
					new InternetAddress(email);
					addresses.add(email);
				}
			} catch (AddressException ae) {
				logger.error("Skipping email notifications for " + user.getTitle() + " Bad email address");
			}
		}
		for (int i=0; i<addrsOnly.length; ++i) {
			String email = addrsOnly[i];
			try {
				if (!Validator.isNull(email)) {
					//validate address
					new InternetAddress(email);
					addresses.add(email);
				}
			} catch (AddressException ae) {
				logger.error("Skipping email notifications for " + email + " Bad email address");
			}
		}
		return (String[])addresses.toArray(new String[0]);
	}
	//Create a new job to retry the mail
	private void scheduleMailRetry(Folder forum, SimpleMailMessage mail) throws ConfigurationException {
		GregorianCalendar start = new GregorianCalendar();
		start.add(Calendar.HOUR_OF_DAY, 1);
		//add time to jobName - may have multple from same forum
 		String jobName = forum.toString() + ":" + start.getTime();
   		
 		String className = "com.sitescape.ef.jobs.SendMail";
  		try {		
			JobDetail jobDetail = new JobDetail(jobName, RETRY_NOTIFICATION_GROUP, 
					Class.forName(className),false, false, false);
			jobDetail.setDescription("Send email notification for " + forum);
			JobDataMap data = new JobDataMap();
			data.put("forum",forum.getId());
			data.put("zoneName",forum.getZoneName());
			data.put("mailMessage", mail);
			jobDetail.setJobDataMap(data);
			jobDetail.addJobListener(com.sitescape.ef.jobs.CleanupJobListener.name);

  			SimpleTrigger trigger = new SimpleTrigger(jobName, RETRY_NOTIFICATION_GROUP, jobName, RETRY_NOTIFICATION_GROUP, start.getTime(), null, 24, 1000*60*60);
  			trigger.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT);
  			trigger.setVolatility(false);
			scheduler.scheduleJob(jobDetail, trigger);				
		} catch (ClassNotFoundException cnf) {
			throw new ConfigurationException("Send email notification class " + className + "not found");		
  		} catch (SchedulerException se) {
   			throw new ConfigurationException("Cannot start forum scheduler", se);
   		}
  			
	}

}
