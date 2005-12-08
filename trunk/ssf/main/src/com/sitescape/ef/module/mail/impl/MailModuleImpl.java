
package com.sitescape.ef.module.mail.impl;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.HashMap;

import javax.mail.MessagingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.quartz.JobDataMap;
import org.quartz.SimpleTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.dao.FolderDao;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.OrderBy;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.ConfigurationException;

import com.sitescape.ef.domain.NotificationDef;
import com.sitescape.ef.domain.Notification;
import com.sitescape.ef.domain.UserNotification;
import com.sitescape.ef.modelprocessor.ProcessorManager;
import com.sitescape.ef.module.impl.AbstractModuleImpl;
import com.sitescape.ef.module.mail.MailModule;
import com.sitescape.ef.module.mail.FolderEmailFormatter;
import com.sitescape.ef.util.XmlClassPathConfigFiles;

import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailAuthenticationException;

import com.sitescape.util.Validator;
/**
 * @author Janet McCann
 *
 */
public class MailModuleImpl extends AbstractModuleImpl implements MailModule {
	private Log logger = LogFactory.getLog(getClass());
	private static String RETRY_NOTIFICATION_GROUP="retry-send-email-notification";
	private JavaMailSender mailSender;
	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

    public Date sendNotifications(Long folderId) {
        String zoneName = RequestContextHolder.getRequestContext().getZoneName();
 		Folder folder = (Folder)coreDao.loadBinder(folderId, zoneName); 
		Date current = new Date();
		//get folder specific helper to build messages
  		FolderEmailFormatter processor = (FolderEmailFormatter)processorManager.getProcessor(folder,FolderEmailFormatter.PROCESSOR_KEY);
		NotificationDef nDef = folder.getNotificationDef();
		Folder top = folder.getTopFolder();
		if (top == null) top = folder;
		List entries = folderDao.loadFolderTreeUpdates(top, nDef.getLastNotification(),current, processor.getLookupOrder(folder));
 		if (entries.isEmpty()) {
 			nDef.setLastNotification(current);
			return current;
		}
		
		List notifications = nDef.getDistribution(); 
		String [] us = nDef.getEmailAddress();
		if ((us.length == 0) && (notifications.size() == 0)) return current;
		//All users
		Set userIds = new TreeSet();
		// Users wanting individual, message style email
		Set indivUserIds = new TreeSet(); 
		buildToLists(folder, userIds, indivUserIds);
		//TODO: need to further seperate list for languages
		//TODO: check read access - remove users who don't have acess
		//TODO: need to check access for each user against each entry
		//expect results will maintain order used to do lookup
		Object[] results = processor.validateIdList(entries, userIds);
		MimeHelper mHelper = new MimeHelper(processor, folder);
		mHelper.setAddrs(us);
		for (int i=0; i<results.length; ++i) {
			Object row[] = (Object [])results[i];
			//Eventually need to remove individual users from user list and make
			//2 calls - one for digest, one for full
			
			//Use spring callback to wrap exceptions into something more useful than javas 
			try {
				mHelper.setEntries((Collection)row[0]);
				mHelper.setToIds((Collection)row[1]);
				mailSender.send(mHelper);
			} catch (MailSendException sx) {
	    		logger.error("Error sending mail:" + sx.getMessage());
				scheduleMailRetry(folder, mHelper.getMessage());
	    	} catch (Exception ex) {
	       		logger.error(ex.getMessage());
	    	} 
		}
		nDef.setLastNotification(current);
	
		return current;
	}
    public boolean sendMail(MimeMessage mailMsg) {
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
	private void buildToLists(Folder folder, Set userIds, Set indivUserIds) {
		List notifications = folder.getNotificationDef().getDistribution();
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
		

	//Create a new job to retry the mail
	private void scheduleMailRetry(Folder folder, MimeMessage mail) throws ConfigurationException {
		GregorianCalendar start = new GregorianCalendar();
		start.add(Calendar.HOUR_OF_DAY, 1);
		//add time to jobName - may have multple from same folder
 		String jobName = folder.toString() + ":" + start.getTime();
   		
 		String className = "com.sitescape.ef.jobs.SendMail";
  		try {		
			JobDetail jobDetail = new JobDetail(jobName, RETRY_NOTIFICATION_GROUP, 
					Class.forName(className),false, false, false);
			jobDetail.setDescription("Send email notification for " + folder);
			JobDataMap data = new JobDataMap();
			data.put("folder",folder.getId());
			data.put("zoneName",folder.getZoneName());
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
   			throw new ConfigurationException("Cannot start folder scheduler", se);
   		}
  			
	}
	private class MimeHelper implements MimeMessagePreparator {
		FolderEmailFormatter processor;
		Folder folder;
		Collection toIds;
		Collection entries;
		String[] addrs;
		MimeMessage message;
		String from;

		private MimeHelper(FolderEmailFormatter processor, Folder folder) {
			this.processor = processor;
			this.folder = folder;
			this.from = processor.getFrom(folder);
			
		}
		protected MimeMessage getMessage() {
			return message;
		}
		protected void setToIds(Collection toIds) {
			this.toIds = toIds;
			
		}
		protected void setEntries(Collection entries) {
			this.entries = entries;
		}
		protected void setAddrs(String[] addrs) {
			this.addrs = addrs;
		}
		
		public void prepare(MimeMessage mimeMessage) throws MessagingException {
			//make sure nothing saved yet
			Locale locale;
			message = null;
			String zoneName = folder.getZoneName();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			helper.setFrom(from);
			if ((toIds != null) && !toIds.isEmpty()) {
				User user = coreDao.loadUser((Long)toIds.iterator().next(), zoneName);
				locale = user.getLocale();
				helper.setSubject(processor.getSubject(folder, locale));
				for (Iterator iter=toIds.iterator();iter.hasNext();) {
					user = coreDao.loadUser((Long)iter.next(), zoneName);
					String email = user.getEmailAddress();
					try	{
						if (!Validator.isNull(email)) helper.addTo(email);
					} catch (AddressException ae) {
						logger.error("Skipping email notifications for " + user.getTitle() + " Bad email address");
					}
				}
				
			} else {
				locale = Locale.getDefault();
			}
			if (addrs != null) { 
				for (int i=0; i<addrs.length; ++i) {
					String email = addrs[i];
					try {
						if (!Validator.isNull(email)) helper.addTo(email);
					} catch (AddressException ae) {
						logger.error("Skipping email notifications for " + email + " Bad email address");
					}
				}
			}
			mimeMessage.addHeader("Content-Transfer-Encoding", "8bit");
			Map result = processor.buildNotificationMessage(folder, entries, locale);
			helper.setText((String)result.get(FolderEmailFormatter.PLAIN), (String)result.get(FolderEmailFormatter.HTML));
			//save message incase cannot connect and need to resend;
			message = mimeMessage;
		}

	}


}
