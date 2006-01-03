
package com.sitescape.ef.module.mail.impl;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.Locale;


import javax.activation.DataSource;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;
import javax.mail.Session;
import javax.mail.search.SearchTerm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

import com.sitescape.ef.context.request.RequestContext;
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
import com.sitescape.ef.domain.EmailAlias;
import com.sitescape.ef.module.definition.notify.Notify;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.module.ldap.LdapModule;
import com.sitescape.ef.module.mail.MailModule;
import com.sitescape.ef.module.mail.FolderEmailFormatter;
import com.sitescape.ef.jobs.ScheduleInfo;
import com.sitescape.ef.repository.RepositoryService;
import com.sitescape.ef.repository.RepositoryServiceUtil;
import com.sitescape.ef.util.ConfigPropertyNotFoundException;
import com.sitescape.ef.util.PortabilityUtil;
import com.sitescape.ef.util.SPropsUtil;
import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.ef.module.mail.JavaMailSender;
import com.sitescape.ef.jobs.FailedEmail;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.jndi.JndiAccessor;
import com.sitescape.util.Validator;
import com.sitescape.ef.util.SZoneConfig;
/**
 * @author Janet McCann
 *
 */
public class MailModuleImpl extends CommonDependencyInjection implements MailModule {
	protected Log logger = LogFactory.getLog(getClass());
	protected Map zoneProps = new HashMap();
	protected Map mailPosters = new HashMap();
	protected Map mailSenders = new HashMap();
	protected JavaMailSender mailSender;
	protected JndiAccessor jndiAccessor;
	protected Map defaultProps = new HashMap();
	private String dataRootDir;
	private String subDirName;

	public MailModuleImpl() {
		defaultProps.put(MailModule.POSTING_JOB, "com.sitescape.ef.jobs.DefaultEmailPosting");
		defaultProps.put(MailModule.NOTIFY_TEMPLATE_TEXT, "mailText.xslt");
		defaultProps.put(MailModule.NOTIFY_TEMPLATE_HTML, "mailHtml.xslt");
		defaultProps.put(MailModule.NOTIFY_TEMPLATE_CACHE_DISABLED, "false");
	}

	public void setDataRootDirProperty(String dataRootDirProperty)
			throws ConfigPropertyNotFoundException, IOException {
		this.dataRootDir = SPropsUtil.getDirPath(dataRootDirProperty);
	}

	public void setSubDirName(String subDirName) {
		this.subDirName = subDirName;
	}
	public void setJndiAccessor(JndiAccessor jndiAccessor) {
		this.jndiAccessor = jndiAccessor;
	}
	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}
	public File getMailDirPath(Folder folder) {
		return new File(new StringBuffer(dataRootDir).append(folder.getZoneName()).append(File.separator).append(subDirName).append(File.separator).append(folder.getId().toString()).append(File.separator).toString());
	}
	public String getMailProperty(String zoneName, String name) {
		String val = SZoneConfig.getString(zoneName, "mailConfiguration/property[@name='" + name + "']");
		if (Validator.isNull(val)) {
			val = (String)defaultProps.get(name);
		}
		return val;
	}
	public String getMailAttribute(String zoneName, String node, String name) {
		Element result = SZoneConfig.getElement(zoneName, "mailConfiguration/" + node);
		if (result == null) return null;
		return result.attributeValue(name);
	}
	public String getMailAttribute(Folder folder, String node, String name) {
		
		String result = getMailAttribute(folder.getZoneName(), "folder[@id='" + folder.getId().toString() +"']/" + node, name);
		if (result != null) return result;
		return getMailAttribute(folder.getZoneName(), node, name);
	}

	private synchronized JavaMailSender getSender(String jndiName) {
		if (mailSenders.containsKey(jndiName)) 
			return (JavaMailSender)mailSenders.get(jndiName);
		try {
			JavaMailSender sender = (JavaMailSender)mailSender.getClass().newInstance();
		
			SpringContextUtil.applyDependencies(sender, "mailSender");
			sender.setSession((javax.mail.Session)jndiAccessor.getJndiTemplate().lookup(jndiName));
			sender.setName(jndiName);
			mailSenders.put(jndiName, sender);
			return sender;
		} catch (Exception ex) {
			logger.error("Error locating " + jndiName + " " + ex.getLocalizedMessage());
			return null;
		}
	}
	public JavaMailSender getMailSender(Folder folder) {
		JavaMailSender sender=null;
		String jndiName = PortabilityUtil.getJndiName(getMailAttribute(folder, "notify", "session"));
		if (!Validator.isNull(jndiName)) 
	    sender = getSender(jndiName);
		if (sender == null) throw new ConfigurationException("Missing JavaMailSender bean");
		return sender;
	}

	public JavaMailSender getMailSender(String zoneName) {
		JavaMailSender sender=null;
		String jndiName = PortabilityUtil.getJndiName(getMailAttribute(zoneName, "notify", "session"));
	    sender = getSender(jndiName);
		if (sender == null) throw new ConfigurationException("Missing JavaMailSender bean");
		return sender;
	}
	public synchronized List getMailPosters(String zoneName) {
		//posting map is indexed by zoneName.  Value is a list of mail sessions
		List result = (List)mailPosters.get(zoneName);
	    if (result != null) return result;
		List posters = SZoneConfig.getElements("mailConfiguration/posting");
		if (posters == null) posters = new ArrayList();
		result = new ArrayList();
		for (int i=0; i<posters.size(); ++i) {
			Element nElement = (Element)posters.get(i);
			String jndiName = PortabilityUtil.getJndiName(nElement.attributeValue("session"));
			try {
				result.add((javax.mail.Session)jndiAccessor.getJndiTemplate().lookup(jndiName));
			} catch (Exception ex) {
				logger.error("Error locating " + jndiName + " " + ex.getLocalizedMessage());
				return null;
			}

		}
		mailPosters.put(zoneName, result);
		return result;
	}
	/**
	 * Read mail from all incomming mail servers.
	 *
	 */
	public void receivePostings(ScheduleInfo config) {
		String storeProtocol, prefix, auth;
		List posters = getMailPosters(config.getZoneName());
		for (int i=0; i<posters.size(); ++i) {
			Session session = (Session)posters.get(i);
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
				List pDefs = getCoreDao().loadPostings(config.getZoneName());
				for (int j=0; j<pDefs.size(); ++j) {
					PostingDef pDef = (PostingDef)pDefs.get(j); 
					SearchTerm term = pDef.getSearchTerm();
					if (term != null) {
						Message msgs[] = mFolder.search(term);
						if (pDef.isEnabled()) {
							Folder folder = (Folder)pDef.getBinder();
//TODO: set request context to user who sent the message and clear after
							FolderEmailFormatter processor = (FolderEmailFormatter)processorManager.getProcessor(folder,FolderEmailFormatter.PROCESSOR_KEY);
							processor.postMessages(folder, pDef, msgs, session);
						} 
						for (int m=0; m<msgs.length; ++m) {
							Message msg = msgs[i];
							msg.setFlag(Flags.Flag.DELETED, true);
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
    public Date sendNotifications(Long folderId, Date start) {
        String zoneName = RequestContextHolder.getRequestContext().getZoneName();
 		Folder folder = (Folder)coreDao.loadBinder(folderId, zoneName); 
		Date until = new Date();
		//get folder specific helper to build messages
  		FolderEmailFormatter processor = (FolderEmailFormatter)processorManager.getProcessor(folder,FolderEmailFormatter.PROCESSOR_KEY);
		NotificationDef nDef = folder.getNotificationDef();
		Folder top = folder.getTopFolder();
		if (top == null) top = folder;
		List entries = folderDao.loadFolderTreeUpdates(top, start ,until, processor.getLookupOrder(folder));
 		if (entries.isEmpty()) {
			return until;
		}
		
		List notifications = nDef.getDistribution(); 
		String [] us = nDef.getEmailAddress();
		if ((us.length == 0) && (notifications.size() == 0)) return until;
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
		MimeHelper mHelper = new MimeHelper(mailSender, processor, folder, start);
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
//				mHelper.setType(Notify.FULL);
//				mailSender.send(mHelper);
			} catch (MailSendException sx) {
	    		logger.error("Error sending mail:" + sx.getMessage());
		  		FailedEmail process = (FailedEmail)processorManager.getProcessor(folder, FailedEmail.PROCESSOR_KEY);
		   		process.schedule(folder, mailSender, mHelper.getMessage(), getMailDirPath(folder));
 	    	} catch (Exception ex) {
	       		logger.error(ex.getMessage());
	    	} 
		}
		
		return until;
	}
    public boolean sendMail(String mailSenderName, java.io.InputStream input) {
    	JavaMailSender mailSender = (JavaMailSender)mailSenders.get(mailSenderName);
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
		List users = coreDao.loadEnabledUsers(userIds, folder.getZoneName());
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
		Date startDate;

		private MimeHelper(JavaMailSender mailSender, FolderEmailFormatter processor, Folder folder, Date startDate) {
			this.processor = processor;
			this.folder = folder;
			this.mailSender = mailSender;
			this.startDate = startDate;
			
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
			notify.setStartDate(startDate);
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
				RepositoryService service = RepositoryServiceUtil.lookupRepositoryService(fAtt.getRepositoryServiceName());
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
