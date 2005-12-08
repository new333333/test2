
package com.sitescape.ef.module.mail.impl;

import java.text.DateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.Locale;
import java.io.FileOutputStream;
import java.io.File;

import javax.activation.DataSource;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;
import javax.mail.Session;
import javax.mail.search.RecipientTerm;
import javax.mail.internet.InternetAddress;
import javax.mail.search.SearchTerm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.xpath.DefaultXPath;
import org.quartz.JobDataMap;
import org.quartz.SimpleTrigger;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;

import com.sitescape.ef.jobs.EmailNotification;
import com.sitescape.ef.jobs.SSStatefulJob;
import com.sitescape.ef.jobs.EmailPosting;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.NotificationDef;
import com.sitescape.ef.domain.Notification;
import com.sitescape.ef.domain.UserNotification;
import com.sitescape.ef.domain.PostingDef;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.module.definition.notify.Notify;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.module.mail.MailModule;
import com.sitescape.ef.module.mail.FolderEmailFormatter;
import com.sitescape.ef.repository.RepositoryService;
import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.ef.util.XmlClassPathConfigFiles;
import com.sitescape.ef.module.mail.JavaMailSender;
import com.sitescape.ef.jobs.FailedEmail;
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
public class MailModuleImpl extends CommonDependencyInjection implements MailModule {
	protected Log logger = LogFactory.getLog(getClass());
	protected XmlClassPathConfigFiles configDocs;
	protected Map zoneProps = new HashMap();
	protected List mailPostingSessions;

	/**
	 * List of  mail sessions for posting eMail to folders.
	 * @param mailPostingSessions
	 */
	public void setMailPostingSessions(List mailPostingSessions) {
		this.mailPostingSessions = mailPostingSessions;
	}
	public void setConfigDocs(XmlClassPathConfigFiles configDocs) {
		this.configDocs = configDocs;
	}
	public XmlClassPathConfigFiles getConfigDocs() {
		return configDocs;
	}
	public String getAttribute(String node, String name, String zoneName) {
		Document doc = configDocs.getAsDom4jDocument(0);
		Element root = doc.getRootElement();
		String value = null;
		Element nElement;
		DefaultXPath path=new DefaultXPath("/mail-configuration/zone[@name=" + zoneName + "]/" + node);
		Object obj = path.evaluate(root);
		if (obj != null && (obj instanceof Element)) {
			nElement = (Element)obj;
			value = nElement.attributeValue(name);
		}
		
		if (!Validator.isNull(value)) return value;
		path=new DefaultXPath("/mail-configuration/zone[@name='']/" + node);
		obj = path.evaluate(root);
		if (obj != null && (obj instanceof Element)) {
			nElement = (Element)obj;
			value = nElement.attributeValue(name);
		}
		return value;
	}
	public String getAttribute(String node, String name, Folder folder) {
		Document doc = configDocs.getAsDom4jDocument(0);
		Element root = doc.getRootElement();
		String value = null;
		Element nElement;
		DefaultXPath path = new DefaultXPath("/mail-configuration/folder[@id=" + folder.getId().toString() +
							"]/" + node);
		Object obj = path.evaluate(root);
		if (obj != null && (obj instanceof Element)) {
			nElement = (Element)obj;
			value = nElement.attributeValue(name);
		}
		if (!Validator.isNull(value)) return value;
		return getAttribute(node, name, folder.getZoneName());
	}

	public JavaMailSender getMailSender(Folder folder) {
		JavaMailSender sender=null;
		String bean = getAttribute("notify", "bean", folder);
		if (!Validator.isNull(bean)) 
			sender =(JavaMailSender)SpringContextUtil.getBean(bean);
		if (sender == null) throw new ConfigurationException("Missing JavaMailSender bean");
		return sender;
	}

	public JavaMailSender getMailSender(String zoneName) {
		JavaMailSender sender=null;
		String bean = getAttribute("notify", "bean", zoneName);
		if (!Validator.isNull(bean)) 
			sender =(JavaMailSender)SpringContextUtil.getBean(bean);
		if (sender == null) throw new ConfigurationException("Missing JavaMailSender bean");
		return sender;
	}

	/**
	 * Read mail for this folder
	 *
	 */
	public void receivePostings() {
		String storeProtocol, prefix, auth;
		for (int i=0; i<mailPostingSessions.size(); ++i) {
			Session session = (Session)mailPostingSessions.get(i);
			storeProtocol = session.getProperty("mail.store.protocol");
			prefix = "mail." + storeProtocol + ".";
			auth = session.getProperty(prefix + "auth");
			if (Validator.isNull(auth)) 
				auth = session.getProperty("mail.auth");
				
			try {
				
				Store store = session.getStore();
				if ("true".equals(auth)) {
					String password = session.getProperty(prefix + "password");
					if (Validator.isNull(password)) 
						password = session.getProperty("mail.password");
					store.connect(null, null, password);
				} else {
					store.connect();
				}

				javax.mail.Folder mFolder = store.getFolder("inbox");
				
				mFolder.open(javax.mail.Folder.READ_WRITE);
				//get list of folders acception postings
				List pDefs = getCoreDao().loadPostings();
				for (int j=0; j<pDefs.size(); ++j) {
					PostingDef pDef = (PostingDef)pDefs.get(j); 
					SearchTerm term = pDef.getSearchTerm();
					if (term != null) {
						Message msgs[] = mFolder.search(term);
						if (pDef.isEnabled()) {
							Folder folder = (Folder)pDef.getBinder();
							FolderEmailFormatter processor = (FolderEmailFormatter)processorManager.getProcessor(folder,FolderEmailFormatter.PROCESSOR_KEY);
							processor.postMessages(folder, msgs, session);
						} else {
							for (int m=0; m<msgs.length; ++m) {
								Message msg = msgs[i];
								msg.setFlag(Flags.Flag.DELETED, true);
							}
						}
					}
				}

				//Close connection and expunge
				mFolder.close(true);
				store.close();
			} catch (Exception ex) {
				logger.error("Error posting mail from " + session.getProperty(prefix + "host") + session.getProperty("mail.host") + " " + ex.getLocalizedMessage());
				
			}						
		}
		
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
		//check access to folder and build lists of users to recieve mail
		buildToLists(folder, userIds, indivUserIds);
		//TODO: need to further seperate list for languages
		//TODO: check read access - remove users who don't have acess
		//TODO: need to check access for each user against each entry
		//expect results will maintain order used to do lookup
		Object[] results = processor.validateIdList(entries, userIds);
		JavaMailSender mailSender = getMailSender(folder);
		MimeHelper mHelper = new MimeHelper(mailSender, processor, folder);
		mHelper.setAddrs(us);

		for (int i=0; i<results.length; ++i) {
			Object row[] = (Object [])results[i];
			//Eventually need to remove individual users from user list and make
			//2 calls - one for digest, one for full
			
			//Use spring callback to wrap exceptions into something more useful than javas 
			try {
				mHelper.setEntries((Collection)row[0]);
				mHelper.setToIds((Collection)row[1]);
				mHelper.setType(Notify.SUMMARY);
				mailSender.send(mHelper);
				mHelper.setType(Notify.FULL);
				mailSender.send(mHelper);
			} catch (MailSendException sx) {
	    		logger.error("Error sending mail:" + sx.getMessage());
		  		FailedEmail process = (FailedEmail)processorManager.getProcessor(folder, FailedEmail.PROCESSOR_KEY);
		   		process.schedule(scheduler, folder, mailSender, mHelper.getMessage());
 	    	} catch (Exception ex) {
	       		logger.error(ex.getMessage());
	    	} 
		}
		
		nDef.setLastNotification(current);
	
		return current;
	}
    public boolean sendMail(String mailSenderName, java.io.InputStream input) {
    	JavaMailSender mailSender = (JavaMailSender)SpringContextUtil.getBean(mailSenderName);
    	try {
        	MimeMessage mailMsg = new MimeMessage(mailSender.getSession(), input);
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
		} catch (MessagingException mx) {
       		logger.error(mx.getMessage());
    		return false;
    	}
    	return true;
    }
    public boolean sendMail(MimeMessage mailMsg) {
        String zoneName = RequestContextHolder.getRequestContext().getZoneName();
        JavaMailSender mailSender = getMailSender(zoneName);

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
		
	private class MimeHelper implements MimeMessagePreparator {
		FolderEmailFormatter processor;
		Folder folder;
		Collection toIds;
		Collection entries;
		String[] addrs;
		MimeMessage message;
		String messageType;
		JavaMailSender mailSender;

		private MimeHelper(JavaMailSender mailSender, FolderEmailFormatter processor, Folder folder) {
			this.processor = processor;
			this.folder = folder;
			this.mailSender = mailSender;
			
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
		protected void setType(String type) {
			messageType = type;
		}
		public void prepare(MimeMessage mimeMessage) throws MessagingException {
			//make sure nothing saved yet
			Locale locale;
			Notify notify = new Notify();
			notify.setType(messageType);
			message = null;
			String zoneName = folder.getZoneName();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			if ((toIds != null) && !toIds.isEmpty()) {
				User user = coreDao.loadUser((Long)toIds.iterator().next(), zoneName);
				locale = user.getLocale();
				notify.setLocale(locale);
				notify.setDateFormat(DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.FULL, locale));
				helper.setSubject(processor.getSubject(folder, notify));
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
				notify.setLocale(locale);
				notify.setDateFormat(DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.FULL, locale));
				helper.setSubject(processor.getSubject(folder, notify));				
			}
			String from = processor.getFrom(folder, notify);
			if (Validator.isNull(from)) {
				from = mailSender.getDefaultFrom();
			}
			helper.setFrom(from);
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
			Map result = processor.buildNotificationMessage(folder, entries, notify);
			helper.setText((String)result.get(FolderEmailFormatter.PLAIN), (String)result.get(FolderEmailFormatter.HTML));
			Set atts = notify.getAttachments();
			for (Iterator iter=atts.iterator(); iter.hasNext();) {
				FileAttachment fAtt = (FileAttachment)iter.next();
				RepositoryService service = (RepositoryService)SpringContextUtil.getBean(fAtt.getRepositoryServiceName());
				if (service != null) {
					FolderEntry entry = (FolderEntry)fAtt.getOwner().getEntry();
					DataSource ds = service.getDataSource(service.openRepositorySession(), entry.getParentFolder(), 
								entry, fAtt.getFileItem().getName(), helper.getFileTypeMap());

					helper.addAttachment(fAtt.getFileItem().getName(), ds);
				}
				
			}
			notify.clearAttachments();
			//save message incase cannot connect and need to resend;
			message = mimeMessage;
		}

	}


}
