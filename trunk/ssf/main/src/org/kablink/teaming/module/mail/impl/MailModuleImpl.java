/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.module.mail.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.OrTerm;
import javax.mail.search.RecipientStringTerm;
import javax.mail.search.SearchTerm;

import org.dom4j.Element;

import org.hibernate.StaleObjectStateException;

import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.EmailLog;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.NotifyStatus;
import org.kablink.teaming.domain.PostingDef;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.Subscription;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserEntityPK;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.EmailLog.EmailLogStatus;
import org.kablink.teaming.domain.EmailLog.EmailLogType;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.jobs.FillEmailSubscription;
import org.kablink.teaming.jobs.ScheduleInfo;
import org.kablink.teaming.jobs.SendEmail;
import org.kablink.teaming.jobs.ZoneSchedule;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.definition.notify.Notify;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.ical.IcalModule;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.mail.ConnectionCallback;
import org.kablink.teaming.module.mail.EmailFormatter;
import org.kablink.teaming.module.mail.EmailPoster;
import org.kablink.teaming.module.mail.JavaMailSender;
import org.kablink.teaming.module.mail.MailModule;
import org.kablink.teaming.module.mail.MailSentStatus;
import org.kablink.teaming.module.mail.MimeEntryPreparator;
import org.kablink.teaming.module.mail.MimeMapPreparator;
import org.kablink.teaming.module.mail.MimeMessagePreparator;
import org.kablink.teaming.module.mail.MimeNotifyPreparator;
import org.kablink.teaming.module.mail.VibeMimeMessage;
import org.kablink.teaming.module.report.ReportModule;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.util.Constants;
import org.kablink.teaming.util.FilePathUtil;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.PortabilityUtil;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.util.Validator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jndi.JndiAccessor;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.MailSendException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * The public methods exposed by this implementation are not transaction 
 * demarcated. If transactions are needed, the FolderEmailProcessors will be 
 * responsible.
 * 
 * Of course, this finer granularity transactional control will be of no effect
 * if the caller of this service was already transactional (i.e., it controls
 * transaction boundary that is more coarse). Whenever possible, this practice 
 * is discouraged for obvious performance/scalability reasons.  
 *   
 * @author Janet McCann
 */
@SuppressWarnings("unchecked")
public class MailModuleImpl extends CommonDependencyInjection implements MailModule, ZoneSchedule, InitializingBean {
	protected ConcurrentHashMap<String, List> mailPosters = new ConcurrentHashMap();
	protected Map<String, JavaMailSender> mailSenders = new HashMap();
	protected JavaMailSender mailSender;
	protected JndiAccessor jndiAccessor;
	protected Map defaultProps = new HashMap();
	private String mailRootDir;
	protected boolean useAliases=false;
	protected boolean sendVTODO = true;
	protected int rcptToLimit = 500;
	
	public MailModuleImpl() {
	}

	private TransactionTemplate transactionTemplate;
    protected TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}
    
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}
	
	private IcalModule icalModule;
	public IcalModule getIcalModule() {
		return icalModule;
	}
	
	public void setIcalModule(IcalModule icalModule) {
		this.icalModule = icalModule;
	}	

	private ReportModule reportModule;
	public ReportModule getReportModule() {
		return reportModule;
	}
	
	public void setReportModule(ReportModule reportModule) {
		this.reportModule = reportModule;
	}
	
	private BinderModule binderModule;
	public BinderModule getBinderModule() {
		return binderModule;
	}
	
	public void setBinderModule(BinderModule binderModule) {
		this.binderModule = binderModule;
	}

	private FolderModule folderModule;
	public FolderModule getFolderModule() {
		return folderModule;
	}
	
	public void setFolderModule(FolderModule folderModule) {
		this.folderModule = folderModule;
	}

	public String getMailRootDir() {
		return mailRootDir;
	}

	public void setMailRootDir(String mailRootDir) {
		if(mailRootDir.endsWith(Constants.SLASH))
			this.mailRootDir = mailRootDir;
		else
			this.mailRootDir = mailRootDir + Constants.SLASH;
	}

	public void setJndiAccessor(JndiAccessor jndiAccessor) {
		this.jndiAccessor = jndiAccessor;
	}
	
	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}
	
	/**
	 * Called after bean is initialized.  
	 */
	@Override
	public void afterPropertiesSet() {
		// Get alias setting.
		useAliases = SPropsUtil.getBoolean("mail.posting.useAliases", false);
		
		// Get send tasks in email.
		sendVTODO = SPropsUtil.getBoolean("mail.sendVTODO", true);
		rcptToLimit = SPropsUtil.getInt("mail.rcpt.limit", 500);
		
		// Preload mailSenders so retry mail will work.  Needs name
		// available.  Posters are also senders, when replying on
		// failures.
		List<Element> senders = SZoneConfig.getAllElements("//mailConfiguration/notify | //mailConfiguration/posting");
		for (Element sEle:  senders) {
			String jndiName = sEle.attributeValue("session");
			if (Validator.isNotNull(jndiName)) {
				try {
					jndiName = PortabilityUtil.getJndiName(jndiName);
					JavaMailSender sender = (JavaMailSender)mailSender.getClass().newInstance();				
					SpringContextUtil.applyDependencies(sender, "mailSender");
					Object jndiObj = jndiAccessor.getJndiTemplate().lookup(jndiName);
					
					// System.out.println(ClassLoaderUtils.showClassLoaderHierarchy(jndiObj, "jndi")); // $$$ TODO
					// System.out.println(ClassLoaderUtils.showClassLoaderHierarchy(javax.mail.Session.class.getClassLoader()));
					
					sender.setName(jndiName); //set the name first so session properties can be found		
					sender.setSession((javax.mail.Session) jndiObj);
					mailSenders.put(jndiName, sender);
				
				}
				catch (Exception ex) {
					logger.error("EXCEPTION:  Error locating " + jndiName + " " + getMessage(ex));
					logger.debug("EXCEPTION", ex);
				}
			}		
		}


	}

	protected FillEmailSubscription getSubscriptionJob(Workspace zone) {
    	String jobClass = getMailProperty(RequestContextHolder.getRequestContext().getZoneName(), MailModule.Property.SUBSCRIPTION_JOB);
    	if (Validator.isNotNull(jobClass)) {
    		try {
    			return (FillEmailSubscription)ReflectHelper.getInstance(jobClass);
    		}
    		catch (Exception e) {
 			   logger.error("EXCEPTION:  Cannot instantiate FillEmailSubscription custom class", e);
    		}
    	}
    	String className = SPropsUtil.getString("job.fill.email.subscription.class", "org.kablink.teaming.jobs.DefaultFillEmailSubscription");
    	return (FillEmailSubscription)ReflectHelper.getInstance(className);
 	}
	
	protected SendEmail getEmailJob(Workspace zone) {
    	String jobClass = getMailProperty(RequestContextHolder.getRequestContext().getZoneName(), MailModule.Property.SENDMAIL_JOB);
    	if (Validator.isNotNull(jobClass)) {
    		try {
    			return (SendEmail)ReflectHelper.getInstance(jobClass);
    		}
    		catch (Exception e) {
 			   logger.error("EXCEPTION:  Cannot instantiate SendEmail custom class", e);
    		}
    	}
    	String className = SPropsUtil.getString("job.send.email.class", "org.kablink.teaming.jobs.DefaultSendEmail");
    	return (SendEmail)ReflectHelper.getInstance(className);
	}
	
	/**
	 * Called on zone delete.
	 * 
	 * @param zone
	 */
	@Override
	public void stopScheduledJobs(Workspace zone) {
		FillEmailSubscription sub = getSubscriptionJob(zone);
		sub.remove(zone.getId());
	}
	
	/**
	 * Called on zone startup.
	 * 
	 * @param zone
	 */
	@Override
	public void startScheduledJobs(Workspace zone) {
		if (zone.isDeleted()) {
			return;
		}
		
		// Make sure a delete job is scheduled for the zone.
		FillEmailSubscription sub = getSubscriptionJob(zone);
		String minString = (String)getMailProperty(zone.getName(), MailModule.Property.SUBSCRIPTION_MINUTES);
		int minutes = 5;
		try {
			minutes = Integer.parseInt(minString);
		}
		catch (Exception ex) {};
		sub.schedule(zone.getId(), null, minutes);
	}

	public File getMailDirPath(Binder binder) {
		return new File(mailRootDir + FilePathUtil.getBinderDirPath(binder));
	}
	
	@Override
	public String getMailProperty(String zoneName, MailModule.Property property) {
		return getMailProperty(zoneName, property.getKey());
	}
	
	@Override
	public String getMailProperty(String zoneName, String name) {
		String val = SZoneConfig.getString(zoneName, "mailConfiguration/property[@name='" + name + "']");
		if (Validator.isNull(val)) {
			val = (String)defaultProps.get(name);
		}
		return val;
	}
	
	@Override
	public String getMailAttribute(String zoneName, String node, String name) {
		Element result = SZoneConfig.getElement(zoneName, "mailConfiguration/" + node);
		if (null == result) {
			return null;
		}
		return result.attributeValue(name);
	}

	protected JavaMailSender getMailSender(String jndiName) {
		JavaMailSender sender=null;
		sender = mailSenders.get(jndiName);
		if (null == sender) {
			throw new ConfigurationException("Missing JavaMailSender bean");
		}
		return sender;
	}
	
	@Override
	public JavaMailSender getMailSender(Binder binder) {
		return getMailSender(getNotificationMailSenderName(binder));
	}
	
	@Override
	public String getNotificationDefaultFrom(Binder binder) {
		return getMailSender(binder).getDefaultFrom();
	}

	@Override
	public String getNotificationMailSenderName(Binder binder) {
		String jndiName;
		if (binder.isZone()) jndiName = PortabilityUtil.getJndiName(getMailAttribute(binder.getName(), "notify", "session"));
		else jndiName = PortabilityUtil.getJndiName(getMailAttribute(RequestContextHolder.getRequestContext().getZoneName(), "notify", "session"));
		return jndiName;
	}

	protected List<String> getMailPosters(String zoneName) {
		// Posting map is indexed by zoneName.  
		List<String> result = mailPosters.get(zoneName);
	    if (null != result) {
	    	return result;
	    }
		List<Element> posters = SZoneConfig.getElements("mailConfiguration/posting");
		if (null == posters) {
			posters = new ArrayList();
		}
		result = new ArrayList();
		for (Element nElement:  posters) {
			String jndiName = PortabilityUtil.getJndiName(nElement.attributeValue("session"));
			if (mailSenders.containsKey(jndiName)) {
				result.add(jndiName); 
			}
			else {
				logger.error("Error locating mail poster " + jndiName);
			}
		}
		mailPosters.put(zoneName, result);
		return result;
	}
	
	/**
	 * Read mail from all incoming mail servers.
	 */
	@Override
	public void receivePostings() {
		String folderName="inbox";
		String prefix, auth;
		List<String> posters = getMailPosters(RequestContextHolder.getRequestContext().getZoneName());
		List<PostingDef> allPostings = getCoreDao().loadPostings(RequestContextHolder.getRequestContext().getZoneId());
		
		/*
		 * There are 2 types of posting:
		 * 
		 * 1. Using aliases, where mail is sent to different address
		 *    which are aliases for the address configured in ssf.xml
		 * 	  This works as long as the mail server doesn't convert the
		 *    alias to the real email address, as does GroupWise!
		 *    
		 * 2. Each posting definition defines a userName/password that
		 *    is used to connect to the store as configured in ssf.xml
		 */
		
		List<PostingDef>aliases = new ArrayList();
		List<PostingDef>useUserNamePwd = new ArrayList();
		for (PostingDef p:  allPostings) {
			if (!p.isEnabled()) {
				continue;
			}
			
			if (null == p.getBinder()) {
				continue;
			}
			
			if (useAliases)
			     aliases.add(       p);
			else useUserNamePwd.add(p);
		}
		
		SearchTerm[] aliasSearch = new SearchTerm[2];
		
		for (String jndiName:  posters) {  // Multiple aren't tested.
			JavaMailSender sender;
			try {
				sender = getMailSender(jndiName);
			}
			catch (Exception ex) {
				continue;
			}
			Session session = sender.getSession();
			String protocol = session.getProperty("mail.store.protocol");
			
			// See if need password.
			prefix = "mail." + protocol + ".";
			auth = session.getProperty(prefix + "auth");
			if (Validator.isNull(auth)) { 
				auth = session.getProperty("mail.auth");
			}
			String password = null;
			if ("true".equals(auth)) {
				password = session.getProperty(prefix + "password");
				if (Validator.isNull(password)) { 
					password = session.getProperty("mail.password");
				}
				if (Validator.isNull(password)) { // See if moved to properties file.
					String [] pieces = sender.getName().split("/");
					password = SPropsUtil.getString("mail." + pieces[pieces.length-1].trim() + ".in.password", "");
				}
			}
			String hostName = session.getProperty(prefix + "host");
			if (Validator.isNull(hostName)) {
				hostName = session.getProperty("mail.host");
			}

			javax.mail.Folder mFolder=null;
			Store store=null;
			try {				
				store = session.getStore(protocol);
			}
			catch (Exception ex) {
				logger.error("EXCEPTION:  Error posting mail from [" + hostName + "]", ex);
				continue;
			}
			try {				
				if (!aliases.isEmpty()) {
					if (Validator.isNotNull(password)) {
						// Rest of defaults from jndi setting.
						store.connect(null, null, password);
					}
					else {
						store.connect();
					}
					mFolder = store.getFolder(folderName);				
					mFolder.open(javax.mail.Folder.READ_WRITE);
					
					// Determine which alias a message belongs to and
					// post it.
					for (PostingDef postingDef: aliases) {
						try {
							aliasSearch[0] = new RecipientStringTerm(Message.RecipientType.TO,postingDef.getEmailAddress());
							aliasSearch[1] = new RecipientStringTerm(Message.RecipientType.CC,postingDef.getEmailAddress());
							Message aliasMsgs[]=mFolder.search(new OrTerm(aliasSearch));
							if (0 == aliasMsgs.length) {
								continue;
							}
							Folder folder = ((Folder) postingDef.getBinder());
							EmailPoster processor = (EmailPoster)processorManager.getProcessor(folder,EmailPoster.PROCESSOR_KEY);
							sendErrors(folder, postingDef, sender, processor.postMessages(folder,postingDef.getEmailAddress(), aliasMsgs, session, null));
						}
						catch (Exception ex) {
							logger.error("Error posting mail from [" + hostName + "]"+postingDef.getEmailAddress(), ex);
						}
					}				
					try {
						mFolder.close(true);
					}
					catch (Exception ex) {};
				}
					
			}
			catch (AuthenticationFailedException ax) {
				logger.error("EXCEPTION:  Error posting mail from [" + hostName + "] " + getMessage(ax));
				logger.debug("EXCEPTION", ax);
				continue;
			}
			catch (MessagingException mx) {
				logger.error("EXCEPTION:  Error posting mail from [" + hostName + "] " + getMessage(mx));
				logger.debug("EXCEPTION", mx);
				continue;
			}
			catch (Exception ex) {
				logger.error("EXCEPTION:  Error posting mail from [" + hostName + "]", ex);
				logger.debug("EXCEPTION", ex);
			}
			finally  {
				// Close folder and expunge.
				if ((null != mFolder) && mFolder.isOpen()) {
					try {
						mFolder.close(true);
					}
					catch (Exception ex1) {};
				}
				
				// Close connection. 
				try {
					store.close();
				}
				catch (Exception ex) {};
			}
			
			// Now try connecting by user/password.
			for (PostingDef postingDef: useUserNamePwd) {
				mFolder = null;
				Folder folder = (Folder)postingDef.getBinder();
				EmailPoster processor = (EmailPoster)processorManager.getProcessor(folder,EmailPoster.PROCESSOR_KEY);
				try {
					store.connect(null, postingDef.getEmailAddress(), postingDef.getCredentials());
					mFolder = store.getFolder(folderName);				
					mFolder.open(javax.mail.Folder.READ_WRITE);
					sendErrors(folder, postingDef, sender, processor.postMessages(folder, postingDef.getEmailAddress(), mFolder.getMessages(), session, null));							
				}
				catch (AuthenticationFailedException ax) {
					logger.error("EXCEPTION:  Error posting mail from [" + hostName + "]"+postingDef.getEmailAddress() + " " + getMessage(ax));
					logger.debug("EXCEPTION", ax);
					continue;
				}
				catch (MessagingException mx) {
					logger.error("EXCEPTION:  Error posting mail from [" + hostName + "]"+postingDef.getEmailAddress() + " " + getMessage(mx));
					logger.debug("EXCEPTION", mx);
					continue;
				}
				catch (Exception ex) {
					logger.error("EXCEPTION:  Error posting mail from [" + hostName + "]"+postingDef.getEmailAddress(), ex);
					logger.debug("EXCEPTION", ex);
					continue;
				}
				finally {
					if (null != mFolder) {
						try {
							mFolder.close(true);
						}
						catch (Exception ex) {};
					}
					
					try {
						store.close();
					}
					catch (Exception ex) {};
				}
				
			}

		}		
	}
	
	private String getMessage(Exception ex) {
		if (Validator.isNotNull(ex.getLocalizedMessage())) {
			return ex.getLocalizedMessage();
		}
		return ex.getMessage();
	}
	
	private void sendErrors(Binder binder, PostingDef postingDef, JavaMailSender srcSender, List errors) {
		if (!errors.isEmpty()) {
			try	{
				JavaMailSender sender;
				if (!useAliases)  {
					// Need our own sender, so we can change the
					// username/password.
					sender = (JavaMailSender)mailSender.getClass().newInstance();				
					SpringContextUtil.applyDependencies(sender, "mailSender");	
					sender.setSession(srcSender.getSession(), postingDef.getEmailAddress(), postingDef.getCredentials());
				}
				else {
					sender = srcSender;
				}
				sender.send((MimeMessage[])errors.toArray(new MimeMessage[errors.size()]));
			}
			catch (MailAuthenticationException ax) {
				logger.error("EXCEPTION:  Authentication Exception:" + getMessage(ax));
				logger.debug("EXCEPTION", ax);
			}
			catch (MailSendException ms) {
				logger.error("EXCEPTION:  Error sending posting reject:" + getMessage(ms));
				logger.debug("EXCEPTION", ms);
				if ((null != binder) && ms.getFailedMessages().isEmpty()) {  // If not empty, trouble unreachable users; will have sent to some.
					logger.error("Error sending posting reject:" + getMessage(ms));						
					SendEmail job = getEmailJob(RequestContextHolder.getRequestContext().getZone());
					for (int i = 0; i < errors.size(); ++i) {
						if (!useAliases && !Validator.isNull(postingDef.getCredentials()))  {
							job.schedule(srcSender, postingDef.getEmailAddress(), postingDef.getCredentials(), (MimeMessage)errors.get(i), binder.getTitle(), getMailDirPath(binder), false);
						}
						else {
							job.schedule(srcSender, (MimeMessage)errors.get(i), binder.getTitle(), getMailDirPath(binder), false);							
						}
					}
				}
			}
			catch (Exception ex) {
				logger.error("EXCEPTION:  Error sending posting reject:" + getMessage(ex));
				logger.debug("EXCEPTION", ex);
			}
		}
		
	}

    /**
     * Fill any subscriptions to modified entries.  Handles user
     * requested subscriptions and administrator notifications that
     * are not digest.
     * 
     * @param begin		Represents the last time we got through this without errors and used as a query optimization.
     * 
     * @return
     */
	@Override
	public Date fillSubscriptions(final Date begin)  {
		final String updateString="update org.kablink.teaming.domain.NotifyStatus set lastFullSent=:p1 where ownerId in (:p2)";
 		final JavaMailSender mailSender = getMailSender(RequestContextHolder.getRequestContext().getZone());
 		if (null == mailSender) {
 			return null;
 		}
		Date last = null;
		try {
			last = ((Date) mailSender.send(new ConnectionCallback() {
				@Override
				public Object doWithConnection(Transport transport) throws MailException {
					final Map values = new HashMap();
					Date end = new Date();
					values.put("p1", end);
					Map params = new HashMap();
					Folder currentFolder = null;
					List<Subscription> folderSubscriptions = null;
					EmailFormatter processor=null;
					MimeNotifyPreparator mHelper = null;
					String timeZone = getMailProperty(RequestContextHolder.getRequestContext().getZoneName(), MailModule.Property.DEFAULT_TIMEZONE);
					
					// Will be sorted by owningBinderkey.
					List<NotifyStatus> uStatus = getCoreDao().loadNotifyStatus("lastFullSent", begin, end, 100, RequestContextHolder.getRequestContext().getZoneId());
					logger.debug("MailModuleImpl.fillSubscriptions():  List<NotifiyStatus> size:" + ((null == uStatus) ? "<null>" : uStatus.size()));
					List ids = new ArrayList();
					while (!uStatus.isEmpty()) {
						// Get IDs to log folderEntries.
						ids.clear();
						for (NotifyStatus status: uStatus) {
							ids.add(status.getOwnerId());
						}
						params.put("ids", ids);
						
						List<FolderEntry> entries = getCoreDao().loadObjects("from org.kablink.teaming.domain.FolderEntry where id in (:ids)", params);
						for (NotifyStatus eStatus:uStatus) {
							// Find corresponding folderEntry,
							// attempting to keep in folder order.
							FolderEntry entry = null;
							for (FolderEntry fEntry:entries) {
								if (fEntry.getId().equals(eStatus.getOwnerId())) {
									entry = fEntry;
									break;
								}
							}
							if (null == entry) {
								continue;
							}
							logger.debug("MailModuleImpl.fillSubscriptions(): Checking subscriptions for: '" + entry.getTitle() + "', ID: " + entry.getId());
							
							// Get the subscriptions from the entry's
							// folder.
							folderSubscriptions = getFolderSubscriptions(entry.getParentFolder());
							
							// We base the notification off the entry's root folder.
							if (!(entry.getRootFolder().equals(currentFolder))) {
								currentFolder = entry.getRootFolder();
								processor = ((EmailFormatter) processorManager.getProcessor(currentFolder,EmailFormatter.PROCESSOR_KEY));
								mHelper = new MimeNotifyPreparator(processor, currentFolder, begin, logger, sendVTODO);
								mHelper.setDefaultFrom(getDefaultFrom(mailSender));		
								mHelper.setTimeZone(timeZone);
							}

							// We need to be the top level entry to
							// deal with entry subscriptions.
							FolderEntry parent = entry.getTopEntry();
							if (null == parent) {
								parent = entry;
							}
							
							// Set the X-* fields required for GW integration.
							mHelper.setEntryPermalinkUrl(PermaLinkUtil.getPermalinkForEmail(entry,  true));
							mHelper.setRootPermalinkUrl( PermaLinkUtil.getPermalinkForEmail(parent, true));
							
							// Handle subscriptions plus notifications for 3 types. 
							List subscriptions;
							if (parent.isSubscribed()) {
								// Make sure entry subscription is
								// first in list so overrides folder
								// subscriptions for the same user.
								subscriptions = loadSubscriptionsByEntityWithACLCheck(parent);
								subscriptions.addAll(folderSubscriptions);
							}
							
							else {
								subscriptions = folderSubscriptions;
							}
							
							// We still have to add in notifications,
							// so continue even if subscriptions is
							// empty.
							
							// Users wanting individual, message style
							// email with attachments.
							Map<Locale, Collection> messageResults = processor.buildDistributionList(entry, subscriptions, Subscription.MESSAGE_STYLE_EMAIL_NOTIFICATION, false);
							Map<Locale, Collection> messageResultsRedacted = processor.buildDistributionList(entry, subscriptions, Subscription.MESSAGE_STYLE_EMAIL_NOTIFICATION, true);
							
							// Users wanting individual, message style
							// email without attachments.
							Map<Locale, Collection> messageNoAttsResults = processor.buildDistributionList(entry, subscriptions, Subscription.MESSAGE_STYLE_NO_ATTACHMENTS_EMAIL_NOTIFICATION, false);
							Map<Locale, Collection> messageNoAttsResultsRedacted = processor.buildDistributionList(entry, subscriptions, Subscription.MESSAGE_STYLE_NO_ATTACHMENTS_EMAIL_NOTIFICATION, true);
							
							// Users wanting individual, text message
							// email.
							Map<Locale, Collection> messageTxtResults = processor.buildDistributionList(entry, subscriptions, Subscription.MESSAGE_STYLE_TXT_EMAIL_NOTIFICATION, false);
							Map<Locale, Collection> messageTxtResultsRedacted = processor.buildDistributionList(entry, subscriptions, Subscription.MESSAGE_STYLE_TXT_EMAIL_NOTIFICATION, true);
							
							mHelper.setEntry(entry);
							mHelper.setStartDate(eStatus.getLastFullSent());
							if (!messageTxtResults.isEmpty()) {
								mHelper.setType(Notify.NotifyType.text);
								mHelper.setRedacted(false);
								mHelper.setSendAttachments(false);
								doSubscription(transport, currentFolder, mailSender, mHelper, messageTxtResults);
							}	
							if (!messageNoAttsResults.isEmpty()) {
								mHelper.setType(Notify.NotifyType.full);
								mHelper.setRedacted(false);
								mHelper.setSendAttachments(false);
								doSubscription(transport, currentFolder, mailSender, mHelper, messageNoAttsResults);
							}
							if (!messageResults.isEmpty()) {
								mHelper.setType(Notify.NotifyType.full);
								mHelper.setRedacted(false);
								mHelper.setSendAttachments(true);
								doSubscription(transport, currentFolder, mailSender, mHelper, messageResults);
							}
							
							if (!messageTxtResultsRedacted.isEmpty()) {
								mHelper.setType(Notify.NotifyType.text);
								mHelper.setRedacted(true);
								mHelper.setSendAttachments(false);
								doSubscription(transport, currentFolder, mailSender, mHelper, messageTxtResultsRedacted);
							}	
							if (!messageNoAttsResultsRedacted.isEmpty()) {
								mHelper.setType(Notify.NotifyType.full);
								mHelper.setRedacted(true);
								mHelper.setSendAttachments(false);
								doSubscription(transport, currentFolder, mailSender, mHelper, messageNoAttsResultsRedacted);
							}
							if (!messageResultsRedacted.isEmpty()) {
								mHelper.setType(Notify.NotifyType.full);
								mHelper.setRedacted(true);
								mHelper.setSendAttachments(true);
								doSubscription(transport, currentFolder, mailSender, mHelper, messageResultsRedacted);
							}
						}

						// Update the NotifyStatus objects we just
						// processed.
						values.put("p2", ids);
						for (int count = 0; count < 10; ++count) {
							try {
								getTransactionTemplate().execute(new TransactionCallback() {
									@Override
									public Object doInTransaction(TransactionStatus status) {
										getCoreDao().executeUpdate(updateString, values);
										return null;
									}});
							}
							catch (org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException ol) {
								continue;
							}
							catch (org.springframework.dao.DataIntegrityViolationException di) {
								continue;
							}
							catch (StaleObjectStateException so) {
								continue;
							}
							
							// Assume we got through.
							break;
						}
						getCoreDao().evict(uStatus);
						getCoreDao().evict(entries);
						
						// See if there any more NotifyStatus objects
						// waiting.
						uStatus = getCoreDao().loadNotifyStatus(
							"lastFullSent",
							begin,
							end,
							100,
							RequestContextHolder.getRequestContext().getZoneId());
					}
					
					return end;
				}}));
		}
		catch(Exception ex) {
			logger.error("EXCEPTION:  Could not fill e-mail subscriptions: " + getMessage(ex));
			logger.debug("EXCEPTION", ex);
		}
		
		return last;
	}

	/*
	 * Returns a List<Subscription> containing the Subscription's from
	 * a Folder up through the Folder's hierarchy.
	 * 
	 * If a Subscription is found where the user no longer has access
	 * to the subscribed to folder, the Subscription is deleted and
	 * NOT returned in the reply list.
	 */
	private List<Subscription> getFolderSubscriptions(Folder folder) {
		List<DefinableEntity> deList = new ArrayList<DefinableEntity>();
		while (null != folder) {
			deList.add(folder);
			folder = folder.getParentFolder();
		}
		return loadSubscriptionsByEntityWithACLCheck(deList);
	}

	/*
	 * Returns a List<Subscription> of the Subscription objects
	 * corresponding to a List<DefinableEntity>.
	 * 
	 * If a Subscription is found where the user no longer has access
	 * to the subscribed to entity, the Subscription is deleted and
	 * NOT returned in the reply list.
	 */
	private List<Subscription> loadSubscriptionsByEntityWithACLCheck(List<DefinableEntity> deList) {
		SimpleProfiler.start("MailModuleImpl.loadSubscriptionsByEntityWithACLCheck()");
    	try {
			// If we don't have any DefinableEntity's...
			int deCount = ((null == deList) ? 0 : deList.size());
			if (0 == deCount) {
				// ...bail.
				return new ArrayList<Subscription>();
			}
	
			// Load the Subscription's for the DefinableEntity's.
			List<Subscription> reply;
			SimpleProfiler.start("MailModuleImpl.loadSubscriptionsByEntityWithACLCheck( Loading Subscription's )");
	    	try {
				CoreDao cd = getCoreDao();
				if (1 == deCount) {
					reply = cd.loadSubscriptionByEntity(deList.get(0).getEntityIdentifier());
					if (null == reply) {
						return new ArrayList<Subscription>();
					}
				}
				else {
					reply = new ArrayList<Subscription>();
					for (DefinableEntity de:  deList) {
						List<Subscription> deSubList =  cd.loadSubscriptionByEntity(de.getEntityIdentifier());
						if (MiscUtil.hasItems(deSubList)) {
							reply.addAll(deSubList);
						}
					}
				}
	    	}
	    	
	    	finally {
	    		SimpleProfiler.stop("MailModuleImpl.loadSubscriptionsByEntityWithACLCheck( Loading Subscription's )");
	    	}
	
			// Did we find any Subscription's?
			if (MiscUtil.hasItems(reply)) {
				// Yes!  Can we find any User's that correspond to
				// them?
				List<Principal> pList = MiscUtil.resolveSubscriptionPrincipals(reply);
				if (MiscUtil.hasItems(pList)) {
					// Yes!  Scan the Subscription's.
					SimpleProfiler.start("MailModuleImpl.loadSubscriptionsByEntityWithACLCheck( Validating User/Subscription/DefinableEntity Access )");
			    	try {
						BinderModule bm = getBinderModule();
						FolderModule fm = getFolderModule();
						int subCount = reply.size();
						for (int i = (subCount - 1); i >= 0; i -= 1) {
							// Can we find the User this Subscription
							// is for?
							Subscription sub     = reply.get(i);
							UserEntityPK subUE   = sub.getId();
							Long         subUID  = subUE.getPrincipalId();
							User         subUser = MiscUtil.findUserInPListById(pList, subUID);
							if (null != subUser) {
								// Yes!  Do they still have access to
								// the subscribed to entity?
								DefinableEntity subDE = MiscUtil.findDEInDEListById(deList, subUE.getEntityId());
								if (!(fm.testReadAccess(subUser, ((WorkArea) subDE), true))) {
									// No!  Delete their subscription
									// to it...
									boolean isSubFE = EntityType.folderEntry.equals(subDE.getEntityType());
									if (isSubFE)
									     fm.setSubscription(subUser, subDE.getParentBinder().getId(), subDE.getId(), null);	// null -> Delete this
									else bm.setSubscription(subUser,                                  subDE.getId(), null);	// ...user's subscription.
									reply.remove(i);	// ...and remove it from the reply list.
									logger.info("loadSubscriptionsByEntityWithACLCheck():  '" + subUser.getTitle() + "'s subscription to '" + subDE.getTitle() + "' has been deleted because they no longer have access to the " + (isSubFE ? "entry" : "folder") + ".");
								}
							}
						}
			    	}
			    	
			    	finally {
			    		SimpleProfiler.stop("MailModuleImpl.loadSubscriptionsByEntityWithACLCheck( Validating User/Subscription/DefinableEntity Access )");
			    	}
				}
			}
	
			// If we get here, reply refers to a List<Subscription> of
			// the Subscription objects corresponding to a
			// List<DefinableEntity> where the subscribing user still
			// has access to the subscribed to entities.  Return it.
			return reply;
    	}
    	
    	finally {
    		SimpleProfiler.stop("MailModuleImpl.loadSubscriptionsByEntityWithACLCheck()");
    	}
	}
	
	private List<Subscription> loadSubscriptionsByEntityWithACLCheck(DefinableEntity de) {
		// Create a List<DefinableEntity> with the entity...
		List<DefinableEntity> deList = new ArrayList<DefinableEntity>();
		deList.add(de);
		
		// ...and always use the initial form of the method.
		return loadSubscriptionsByEntityWithACLCheck(deList);
	}

	/*
	 * ?
	 */
	private void doSubscription(Transport transport, Folder folder, JavaMailSender mailSender, MimeNotifyPreparator mHelper, Map<Locale, Collection> results) {
		for (Iterator iter = results.entrySet().iterator(); iter.hasNext();) {			
			// Use spring callback to wrap exceptions into something
			// more useful than Java's. 
			Map.Entry<Locale, Collection> e = (Map.Entry)iter.next();
			mHelper.setLocale(e.getKey());
			
			ArrayList<String> rcpts = new ArrayList<String>(e.getValue());
			
	 		// Add an entry into the email log for this request.
	 		Date now = new Date();
	  		String fromAddress = "";
			try                  {fromAddress = mailSender.getDefaultFrom();  }
			catch (Exception e2) {fromAddress = NLT.get("mail.noFromAddress");}
			if (fromAddress == null || fromAddress.equals("")) {
				fromAddress = NLT.get("mail.noFromAddress");
			}
	  		EmailLogType logType = EmailLogType.binderNotification;
	  		EmailLog emailLog = new EmailLog(logType, now, rcpts, fromAddress, EmailLogStatus.queued);
	  		mHelper.setEmailLog(emailLog);
	  		
	  		// Break to list into pieces if big.
			for (int i = 0; i < rcpts.size(); i += rcptToLimit) {
				try {
					List subList = rcpts.subList(i, Math.min(rcpts.size(), i+rcptToLimit));
					if (SPropsUtil.getBoolean("mail.notifyAsBCC", true))
					     mHelper.setBccAddrs(subList);
					else mHelper.setToAddrs( subList);
					MimeMessage mimeMessage = new VibeMimeMessage(mailSender.getSession());
					mHelper.prepare(mimeMessage);
					if (null == mimeMessage.getSentDate()) {
						mimeMessage.setSentDate(new Date());
					}
					mimeMessage.saveChanges();
			  		try                  {emailLog.setSubj((String)mimeMessage.getSubject());}
			  		catch (Exception e1) {emailLog.setSubj(NLT.get("mail.noSubject"));       }
					mailSender.send(transport, mimeMessage);
				}
				catch (MailSendException sx) {
		    		logger.error("EXCEPTION:  Error sending mail:" + getMessage(sx));
					logger.debug("EXCEPTION", sx);
					emailLog.setComments("Error sending mail:" + getMessage(sx));
					emailLog.setStatus(EmailLogStatus.error);
		 			Exception[] exceptions = sx.getMessageExceptions();
		 			if ((null != exceptions) && (0 < exceptions.length)) {
		 				logger.error(sx.toString());
		 			}
		 			else {
		 				SendEmail job = getEmailJob(RequestContextHolder.getRequestContext().getZone());
		 				job.schedule(mailSender, mHelper.getMessage(), folder.getTitle(), getMailDirPath(folder), false);
		 			}
			   	}
				catch (Exception ex) {
			   		// Message gets thrown away here.
		       		logger.error("EXCEPTION:  " + getMessage(ex));
					logger.debug("EXCEPTION", ex);
					emailLog.setComments("Error handling subscriptions:" + getMessage(ex));
					emailLog.setStatus(EmailLogStatus.error);
		    	}
			}
			getReportModule().addEmailLog(emailLog);
		}
	}

	/**
	 * Send email notifications for recent changes.  Only used for
	 * digest style messages.
	 * 
	 * @param binderId
     * @param begin		Represents the last time we got through this without errors and used as a query optimization.
	 * 
	 * @return
	 */
    @Override
	public Date sendNotifications(final Long binderId, final Date begin) {
		final String updateString="update org.kablink.teaming.domain.NotifyStatus set lastDigestSent=:p1 where ownerId in (:p2)";
		final JavaMailSender mailSender = getMailSender(RequestContextHolder.getRequestContext().getZone());
		if (null == mailSender) {
			return null;
		}
		Date last = ((Date) mailSender.send(new ConnectionCallback() {
			@Override
			public Object doWithConnection(Transport transport) throws MailException {
		   		final Map values = new HashMap();
				Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
				Binder binder = coreDao.loadBinder(binderId, RequestContextHolder.getRequestContext().getZoneId()); 
				Date end = new Date();
				values.put("p1", end);
				Map params = new HashMap();
				Folder currentFolder = null;
				List<Subscription> folderSubscriptions = null;
				EmailFormatter processor=null;
				MimeNotifyPreparator mHelper = null;
				String timeZone = getMailProperty(RequestContextHolder.getRequestContext().getZoneName(), MailModule.Property.DEFAULT_TIMEZONE);
				
				// Will be sorted by owningBinderkey.
				List<NotifyStatus> uStatus;
				if (binder.isRoot())
				     uStatus = getCoreDao().loadNotifyStatus(        "lastDigestSent", begin, end, 100, zoneId);
				else uStatus = getCoreDao().loadNotifyStatus(binder, "lastDigestSent", begin, end, 100, zoneId);				
				List ids = new ArrayList();
				while (!uStatus.isEmpty()) {
					// Get IDs to log folderEntries.
					ids.clear();
					NotifyStatus current = uStatus.get(0);
					uStatus.remove(0);
					Folder notifyFolder = ((Folder) coreDao.loadBinder(current.getOwningBinderId(), RequestContextHolder.getRequestContext().getZoneId())); 
					currentFolder = notifyFolder.getRootFolder();
					
					// If doing global notification, make sure this
					// binder doesn't have its own schedule.
					if (binder.isRoot()) {
						// Skip any folders that have their own
						// schedule.
						ScheduleInfo config = getFolderModule().getNotificationSchedule(zoneId, currentFolder.getId());
						if ((null != config) && config.isEnabled() && (null != config.getSchedule())) {
							// This folder has its own schedule, so
							// leave its entries along during global
							// notifications.
							continue;
						}
					}
					
					// Find other entries for same folder tree.
					// Ordered by owingBinderKey.
					List<NotifyStatus>currentStatus = new ArrayList();
					currentStatus.add(current);
					ids.add(current.getOwnerId());
					for (NotifyStatus eStatus: uStatus) {
						if (eStatus.getOwningBinderId().equals(current.getOwningBinderId()) || eStatus.getOwningBinderId().equals(currentFolder.getId())) {
							currentStatus.add(eStatus);
							ids.add(eStatus.getOwnerId());
							continue;
						}
						
						Folder parent = ((Folder) coreDao.loadBinder(eStatus.getOwningBinderId(), RequestContextHolder.getRequestContext().getZoneId())); 
						if (parent.getRootFolder().equals(currentFolder)) {
							currentStatus.add(eStatus);
							ids.add(eStatus.getOwnerId());
							continue;
						}
						
						// Done with this folder tree.
						break;				
					}
					
					// Remove from pending list
					uStatus.removeAll(currentStatus);
					
					params.put("ids", ids);
					List<FolderEntry> entriesFull = getCoreDao().loadObjects("from org.kablink.teaming.domain.FolderEntry where id in (:ids) order by HKey.sortKey", params);
					List<FolderEntry> entries = new ArrayList<FolderEntry>();
					for (FolderEntry fe:  entriesFull) {
						if (!(fe.isPreDeleted())) {
							entries.add(fe);
						}
					}
					
					if (!entries.isEmpty()) {
						// Handle digest subscriptions and
						// notifications get folder specific helper to
						// build message
						processor = ((EmailFormatter) processorManager.getProcessor(currentFolder, EmailFormatter.PROCESSOR_KEY));
						folderSubscriptions = getFolderSubscriptions(notifyFolder);
						List<Object[]> digestResults         = processor.buildDistributionList(currentFolder, entries, folderSubscriptions, Subscription.DIGEST_STYLE_EMAIL_NOTIFICATION, false);		
						List<Object[]> digestResultsRedacted = processor.buildDistributionList(currentFolder, entries, folderSubscriptions, Subscription.DIGEST_STYLE_EMAIL_NOTIFICATION, true );
						
						mHelper = new MimeNotifyPreparator(processor, currentFolder, begin, logger, sendVTODO);
						mHelper.setDefaultFrom(getDefaultFrom(mailSender));		
						mHelper.setTimeZone(timeZone);
						
						for (int i = 0; i < digestResults.size(); ++i) {
							Object row[] = ((Object []) digestResults.get(i));
							Collection msgs = ((Collection) row[0]);
							if (msgs.isEmpty()) {
								// Didn't have access.
								continue;
							}
							mHelper.setEntries(msgs);
							mHelper.setType(Notify.NotifyType.summary);
							mHelper.setRedacted(false);
							mHelper.setSendAttachments(false);
							doSubscription(transport, currentFolder, mailSender, mHelper, (Map)row[1]);
						}
						
						for (int i = 0; i < digestResultsRedacted.size(); ++i) {
							Object row[] = ((Object []) digestResultsRedacted.get(i));
							Collection msgs = ((Collection) row[0]);
							if (msgs.isEmpty()) {
								// Didn't have access.
								continue;
							}
							mHelper.setEntries(msgs);
							mHelper.setType(Notify.NotifyType.summary);
							mHelper.setRedacted(true);
							mHelper.setSendAttachments(false);
							doSubscription(transport, currentFolder, mailSender, mHelper, (Map)row[1]);
						}
					}
					
					// Update the NotifyStatus objects we just 
					// processed.
					values.put("p2", ids);
					for (int count=0; count<10; ++count) {
						try {
							getTransactionTemplate().execute(new TransactionCallback() {
								@Override
								public Object doInTransaction(TransactionStatus status) {
									getCoreDao().executeUpdate(updateString, values);
									return null;
								}});
						}
						catch (org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException ol) {
							continue;
						}
						catch (org.springframework.dao.DataIntegrityViolationException di) {
							continue;
						}
						catch (StaleObjectStateException so) {
							continue;
						}
						
						// Assume we got through.
						break;
					}
					getCoreDao().evict(currentStatus);
					getCoreDao().evict(entries);
					
					if (uStatus.isEmpty()) {
						// See if there are more NotifyStatus objects
						// waiting.
						if (binder.isRoot())
						     uStatus = getCoreDao().loadNotifyStatus(        "lastDigestSent", begin, end, 100, RequestContextHolder.getRequestContext().getZoneId());
						else uStatus = getCoreDao().loadNotifyStatus(binder, "lastDigestSent", begin, end, 100, RequestContextHolder.getRequestContext().getZoneId());				
					}
				}
				
				return end;
			}}));
		
		return last;    
    }
 
    /**
     * Used for re-try mail.  MimeMessage has been serialized.
     * 
     * @param mailSenderName
     * @param input
     */
    @Override
	public void sendMail(String mailSenderName, java.io.InputStream input) {
    	JavaMailSender mailSender = getMailSender(mailSenderName);
 		
    	// Add an entry into the email log for this request.
  		EmailLog emailLog = new EmailLog(EmailLogType.retry, EmailLogStatus.sent);
		try {
        	MimeMessage mailMsg = new MimeMessage(mailSender.getSession(), input);
        	emailLog.fillFromMimeMessage(mailMsg);

			mailSender.send(mailMsg);
 		}
		catch (MessagingException mx) {
 			emailLog.setComments(NLT.get("errorcode.sendMail.badInputStream", new Object[] {getMessage(mx)}));
 			emailLog.setStatus(EmailLogStatus.error);
 			getReportModule().addEmailLog(emailLog);
 			throw new MailPreparationException(NLT.get("errorcode.sendMail.badInputStream", new Object[] {getMessage(mx)}));
		}
 		getReportModule().addEmailLog(emailLog);
    }
    
    /**
     * Used for re-try mail.  MimeMessage has been serialized.
     * 
     * @param mailSenderName
     * @param account
     * @param password
     * @param input
     */
    @Override
	public void sendMail(String mailSenderName, String account, String password, java.io.InputStream input) {
    	JavaMailSender mailSender = getMailSender(mailSenderName);

    	// Add an entry into the email log for this request.
  		EmailLog emailLog = new EmailLog(EmailLogType.retry, EmailLogStatus.sent);
  		try {
    	   	if (Validator.isNotNull(account)) {
    			// Need to get our own sender, so the account/password
    	   		// can be changed.
    	   		Session session = mailSender.getSession();
    	   		try {
    	   			mailSender = (JavaMailSender)mailSender.getClass().newInstance();				
    	   		}
    	   		catch (Exception ia) {
    	   			logger.error("EXCEPTION:  Cannot create sender");
    				logger.debug("EXCEPTION", ia);
    	   			return;
    	   		}
    	   		SpringContextUtil.applyDependencies(mailSender, "mailSender");	
    	   		mailSender.setSession(session, account, password);
    		}
        	MimeMessage mailMsg = new MimeMessage(mailSender.getSession(), input);
        	emailLog.fillFromMimeMessage(mailMsg);
			mailSender.send(mailMsg);
 		}
  		catch (MessagingException mx) {
 			emailLog.setComments(NLT.get("errorcode.sendMail.badInputStream", new Object[] {getMessage(mx)}));
 			emailLog.setStatus(EmailLogStatus.error);
 			getReportModule().addEmailLog(emailLog);
			throw new MailPreparationException(NLT.get("errorcode.sendMail.badInputStream", new Object[] {getMessage(mx)}));
		}
 		getReportModule().addEmailLog(emailLog);
    }
    
    /**
     * Prepare mail and send it - caller must retry if desired.
     * 
     * @param mailSenderName
     * @param mHelper
     */
    @Override
	public void sendMail(String mailSenderName, MimeMessagePreparator mHelper) {
    	JavaMailSender mailSender = getMailSender(mailSenderName);
		mHelper.setDefaultFrom(getDefaultFrom(mailSender));
		
		// Use spring callback to wrap exceptions into something more
		// useful than Java's.  Add an entry into the email log for
		// this request
 		MimeMessage msg = mailSender.createMimeMessage();
   		EmailLog emailLog = new EmailLog(EmailLogType.sendMail, EmailLogStatus.sent);
		try {
			mHelper.prepare(msg);
			emailLog = new EmailLog(EmailLogType.sendMail, new Date(), getMessageRecipients(msg),
					mailSender.getDefaultFrom(), EmailLogStatus.sent);
	   	}
		catch (Exception ex) {
	   		// Message gets thrown away here.
       		logger.error("EXCEPTION:  " + getMessage(ex));
			logger.debug("EXCEPTION", ex);
			emailLog.setComments("Error in sendMail:" + getMessage(ex));
			emailLog.setStatus(EmailLogStatus.error);
    	}
		emailLog.fillFromMimeMessage(msg);
		mailSender.send(msg);
		getReportModule().addEmailLog(emailLog);
	}
    
    private Address[] getMessageRecipients(MimeMessage msg) throws Exception {
    	Address[] reply = msg.getRecipients(RecipientType.TO);
    	if ((null == reply) || (0 == reply.length)) {
        	reply = msg.getRecipients(RecipientType.CC);
        	if ((null == reply) || (0 == reply.length)) {
            	reply = msg.getRecipients(RecipientType.BCC);
            	if (null == reply) {
            		reply = new Address[0];
            	}
        	}
    	}
    	return reply;
    }
    
    /**
     * Used to send prepared mail now..
     * 
     * @param mailMsg
     */
    @Override
	public void sendMail(MimeMessage mailMsg) {
       	Binder zone = RequestContextHolder.getRequestContext().getZone();
        sendMail(getMailSender(zone).getName(), mailMsg);
    }
    
    /**
     * Used to send prepared mail now.
     * 
     * @param mailSenderName
     * @param mailMsg
     */
    @Override
	public void sendMail(String mailSenderName, MimeMessage mailMsg) {
    	JavaMailSender mailSender = getMailSender(mailSenderName);
    	
    	// Add an entry into the email log for this request.
  		EmailLog emailLog = new EmailLog(EmailLogType.retry, mailMsg, EmailLogStatus.sent);
		emailLog.fillFromMimeMessage(mailMsg);
		mailSender.send(mailMsg);
		getReportModule().addEmailLog(emailLog);
    }

    /**
     * ?
     * 
     * @param binder
     * @param message
     * @param comment
     */
    @Override
	public MailSentStatus sendMail(Binder binder, Map message, String comment) {
  		JavaMailSender mailSender = getMailSender(binder);
  		
  		// Start with the TO addresses.
		Collection<InternetAddress> targetAddresses = ((Collection) message.get(MailModule.TO));
		if (null == targetAddresses) {
			targetAddresses = new ArrayList<InternetAddress>();
		}

		// Are there any BCC recipients?
		Collection bccAddresses = ((Collection) message.get(MailModule.BCC));
		boolean notifyAsBCC = SPropsUtil.getBoolean("mail.notifyAsBCC", true);
		if (notifyAsBCC && (null != bccAddresses)) {
			// Yes!  Add them to the targetAddresses since we'll send
			// them the same way.
			targetAddresses.addAll(bccAddresses);
			bccAddresses = null;
		}
		
		// Are there any CC recipients?
		Collection ccAddresses = ((Collection) message.get(MailModule.CC));
		
		// Throw an exception if there are no recipients.
		if ((!(MiscUtil.hasItems(    targetAddresses))) &&
				(!(MiscUtil.hasItems(bccAddresses   ))) &&
				(!(MiscUtil.hasItems(ccAddresses    )))) {
			throw new MailPreparationException(NLT.get("errorcode.noRecipients"));
		}

  		// Add an entry into the email log for this request.
		Date now = new Date();
  		InternetAddress from = (InternetAddress)message.get(MailModule.FROM);
  		EmailLogType logType = EmailLogType.sendMail;
  		EmailLog emailLog = new EmailLog(logType, now, targetAddresses, from, EmailLogStatus.sent);
  		emailLog.setSubj((String)message.get(MailModule.SUBJECT));

  		// Setup the message to be sent.
		MailStatus status = new MailStatus(message);
		Map currentMessage = new HashMap(message);	// Make a changeable copy.
 		currentMessage.remove(MailModule.TO);		// These will have been added to targetAddresses.
 		if (notifyAsBCC) {							// When sending TO's as BCC's...
 			currentMessage.remove(MailModule.BCC);	// ...these will have been added to targetAddresses.
 		}
 		
		// Based on the ssf*.properties setting, we use BCC to hide
		// email addresses from other recipients.
		String mailMode;
		if (notifyAsBCC)
		     mailMode = MailModule.BCC;
		else mailMode = MailModule.TO;
		
		// Handle large recipient list by breaking into pieces. 
		ArrayList rcpts = new ArrayList(targetAddresses);
		for (int i = 0; ((i < rcpts.size()) || (0 == i)); i += rcptToLimit) {	// Must run loop at least once.
			// If this is the second or later email...
			if (0 < i) {
				// ...remove the BCC's and CC's.
		 		currentMessage.remove(MailModule.BCC);	// If there were any, these would have...
		 		currentMessage.remove(MailModule.CC );	// ...been sent with the first email.
			}
			
			List subList = rcpts.subList(i, Math.min(rcpts.size(), (i + rcptToLimit)));
			currentMessage.put(mailMode, subList);
			
	 		MimeMessagePreparator helper = new MimeMapPreparator(currentMessage, logger, sendVTODO);
	 		try {
	 			helper.setDefaultFrom(getDefaultFrom(mailSender));		
	 			mailSender.send(helper);
	 		}
	 		catch (MailSendException sx) {
	 			logger.error("EXCEPTION:  Error sending mail:" + getMessage(sx));
				logger.debug("EXCEPTION", sx);
				String emailLogComment = emailLog.getComments();
				if (null == emailLogComment) {
					emailLogComment = "";
				}
				if (!emailLogComment.equals("")) emailLogComment += "\n";
				emailLogComment += "Error sending mail:" + getMessage(sx);
				emailLog.setComments(emailLogComment);
				emailLog.setStatus(EmailLogStatus.error);

				Exception[] exceptions = sx.getMessageExceptions();
	 			if ((null != exceptions) && (0 < exceptions.length) && (exceptions[0] instanceof SendFailedException)) {
	 				// If sent to anyone; or only 1 recipient and
	 				// couldn't send don't try again.
	 				SendFailedException sf = ((SendFailedException) exceptions[0]);
	 				status.addFailures(sf.getInvalidAddresses());
	 				status.addFailures(sf.getValidUnsentAddresses());	 				
	 			}
	 			else {
	 				SendEmail job = getEmailJob(RequestContextHolder.getRequestContext().getZone());
	 				job.schedule(mailSender, helper.getMessage(), comment, getMailDirPath(binder), false);
	 				try {
	 					status.addQueued(helper.getMessage().getAllRecipients());
	 				}
	 				catch (MessagingException ignore) {}
	 			}
	 	   	}
	 		catch (MailAuthenticationException ax) {
	       		logger.error("EXCEPTION:  Authentication Exception:" + getMessage(ax));				
				logger.debug("EXCEPTION", ax);
				String emailLogComment = emailLog.getComments();
				if (null == emailLogComment)     emailLogComment = "";
				if (!emailLogComment.equals("")) emailLogComment += "\n";
				emailLogComment += "Authentication Exception:" + getMessage(ax);
				emailLog.setComments(emailLogComment);
				emailLog.setStatus(EmailLogStatus.error);

				SendEmail job = getEmailJob(RequestContextHolder.getRequestContext().getZone());
	       		job.schedule(mailSender, helper.getMessage(), comment, getMailDirPath(binder), false);
	       		try {
	       			status.addQueued(helper.getMessage().getAllRecipients());
	       		}
	       		catch (MessagingException ignore) {}
	 		}
		}
		getReportModule().addEmailLog(emailLog);
		return status;
    }

    /**
     * ?
     * 
     * @param entry
     * @param message
     * @param comment
     * @param sendAttachments
     * 
     * @return
     */
    @Override
	public MailSentStatus sendMail(Entry entry, Map message, String comment, boolean sendAttachments) {
  		JavaMailSender mailSender = getMailSender(entry.getParentBinder());
		EmailFormatter processor = ((EmailFormatter) processorManager.getProcessor(entry.getParentBinder(), EmailFormatter.PROCESSOR_KEY));
		User user = RequestContextHolder.getRequestContext().getUser();
  		
  		// Start with the TO addresses.
		Collection<InternetAddress> targetAddresses = ((Collection) message.get(MailModule.TO));
		if (null == targetAddresses) {
			targetAddresses = new ArrayList<InternetAddress>();
		}

		// Are there any BCC recipients?
		Collection bccAddresses = ((Collection) message.get(MailModule.BCC));
		boolean notifyAsBCC = SPropsUtil.getBoolean("mail.notifyAsBCC", true);
		if (notifyAsBCC && (null != bccAddresses)) {
			// Yes!  Add them to the targetAddresses since we'll send
			// them the same way.
			targetAddresses.addAll(bccAddresses);
			bccAddresses = null;
		}
		
		// Are there any CC recipients?
		Collection ccAddresses = ((Collection) message.get(MailModule.CC));
		
		// Throw an exception if there are no recipients.
		if ((!(MiscUtil.hasItems(    targetAddresses))) &&
				(!(MiscUtil.hasItems(bccAddresses   ))) &&
				(!(MiscUtil.hasItems(ccAddresses    )))) {
			throw new MailPreparationException(NLT.get("errorcode.noRecipients"));
		}

  		// Add an entry into the email log for this request.
		Date now = new Date();
  		InternetAddress from = (InternetAddress)message.get(MailModule.FROM);
  		EmailLogType logType = EmailLogType.sendMail;
  		EmailLog emailLog = new EmailLog(logType, now, targetAddresses, from, EmailLogStatus.sent);
  		emailLog.setSubj((String)message.get(MailModule.SUBJECT));
  		if (sendAttachments) {
  			SortedSet<FileAttachment> atts = entry.getFileAttachments();
  			List<String> fileNames = new ArrayList<String>();
  			for (FileAttachment fa : atts) {
  				fileNames.add(fa.getFileItem().getName());
  			}
  			emailLog.setFileAttachments(fileNames);
  		}

  		// Setup the message to be sent.
  		MailStatus status = new MailStatus(message);
		Map currentMessage = new HashMap(message);	// Make a changeable copy.
 		currentMessage.remove(MailModule.TO);		// These will have been added to targetAddresses.
 		if (notifyAsBCC) {							// When sending TO's as BCC's...
 			currentMessage.remove(MailModule.BCC);	// ...these will have been added to targetAddresses.
 		}
		
		// Based on the ssf*.properties setting, we use BCC to hide
		// email addresses from other recipients.
		String mailMode;
		if (notifyAsBCC)
		     mailMode = MailModule.BCC;
		else mailMode = MailModule.TO;
		
		// Handle large recipient list by breaking into pieces. 
		ArrayList rcpts = new ArrayList(targetAddresses);
		for (int i = 0; ((i < rcpts.size()) || (0 == i)); i += rcptToLimit) {	// Must run loop at least once.
			// If this is the second or later email...
			if (0 < i) {
				// ...remove the BCC's and CC's.
		 		currentMessage.remove(MailModule.BCC);	// If there were any, these would have...
		 		currentMessage.remove(MailModule.CC );	// ...been sent with the first email.
			}
			
			List subList = rcpts.subList(i, Math.min(rcpts.size(), (i + rcptToLimit)));
			currentMessage.put(mailMode, subList);
			
			MimeEntryPreparator helper = new MimeEntryPreparator(processor, entry, currentMessage, logger, sendVTODO);
	 		helper.setDefaultFrom(getDefaultFrom(mailSender));		
	 		helper.setTimeZone(user.getTimeZone().getID());
	 		helper.setLocale(user.getLocale());
	 		helper.setType(Notify.NotifyType.interactive);
	 		helper.setSendAttachments(sendAttachments);
	  		helper.setEmailLog(emailLog);

	 		try {
	 			mailSender.send(helper);
	 		}
	 		catch (MailSendException sx) {
	 			logger.error("EXCEPTION:  Error sending mail:" + getMessage(sx));
				logger.debug("EXCEPTION", sx);
				String emailLogComment = emailLog.getComments();
				if (null == emailLogComment)     emailLogComment = "";
				if (!emailLogComment.equals("")) emailLogComment += "\n";
				emailLogComment += "Error sending mail:" + getMessage(sx);
				emailLog.setComments(emailLogComment);
				emailLog.setStatus(EmailLogStatus.error);
				
	 			Exception[] exceptions = sx.getMessageExceptions();
	 			if ((null != exceptions) && (0 < exceptions.length) && (exceptions[0] instanceof SendFailedException)) {
	 				SendFailedException sf = (SendFailedException)exceptions[0];
	 				status.addFailures(sf.getInvalidAddresses());
	 				status.addFailures(sf.getValidUnsentAddresses());
	 			}
	 			else {
	 				SendEmail job = getEmailJob(RequestContextHolder.getRequestContext().getZone());
	 				job.schedule(mailSender, helper.getMessage(), comment, getMailDirPath(entry.getParentBinder()), false);
	 				try {
	 					status.addQueued(helper.getMessage().getAllRecipients());
	 				}
	 				catch (MessagingException ignore) {}
	 			}
	 	   	}
	 		catch (MailAuthenticationException ax) {
	       		logger.error("EXCEPTION:  Authentication Exception:" + getMessage(ax));				
				logger.debug("EXCEPTION", ax);
				String emailLogComment = emailLog.getComments();
				if (null == emailLogComment)     emailLogComment = "";
				if (!emailLogComment.equals("")) emailLogComment += "\n";
				emailLogComment += "Authentication Exception:" + getMessage(ax);
				emailLog.setComments(emailLogComment);
				emailLog.setStatus(EmailLogStatus.error);
				
	      		SendEmail job = getEmailJob(RequestContextHolder.getRequestContext().getZone());
	       		job.schedule(mailSender, helper.getMessage(), comment, getMailDirPath(entry.getParentBinder()), false);
	       		try {
	       			status.addQueued(helper.getMessage().getAllRecipients());
	       		}
	       		catch (MessagingException ignore) {}
	 		}
		}
		getReportModule().addEmailLog(emailLog);
		return status;
    }

    /**
     * Schedule mail delivery.
     * 
     * @param binder
     * @param message
     * @param comment
     * 
     * @throws Exception
     */
    @Override
	public void scheduleMail(Binder binder, Map message, String comment) throws Exception {
  		SendEmail job = getEmailJob(RequestContextHolder.getRequestContext().getZone());
  		JavaMailSender mailSender = getMailSender(binder);
 		Collection<InternetAddress> addrs = ((Collection) message.get(MailModule.TO));
 		if (null != ((Collection) message.get(MailModule.BCC))) {
 			// We now send all mail using BCC, so combine the two
 			// lists.
 			addrs.addAll((Collection) message.get(MailModule.BCC));
 		}
 		if ((null == addrs) || addrs.isEmpty()) {
 			return;
 		}
  		
 		// Add an entry into the email log for this request.
 		Date now = new Date();
  		InternetAddress from = (InternetAddress)message.get(MailModule.FROM);
  		EmailLogType logType = (EmailLogType) message.get(MailModule.LOG_TYPE);
  		EmailLog emailLog = new EmailLog(logType, now, addrs, from, EmailLogStatus.queued);
  		emailLog.setSubj((String)message.get(MailModule.SUBJECT));
  		
		// Handle large recipient list. 
		ArrayList rcpts = new ArrayList(addrs);
		Map currentMessage = new HashMap(message);
 		currentMessage.remove(MailModule.BCC);	// These are added to addrs.
 		currentMessage.remove(MailModule.TO );	// These get sent as BCC now.
		for (int i = 0; i < rcpts.size(); i += rcptToLimit) {
			List subList = rcpts.subList(i, Math.min(rcpts.size(), i + rcptToLimit));
			currentMessage.put(MailModule.BCC, subList);
	 		MimeMessagePreparator helper = new MimeMapPreparator(currentMessage, logger, sendVTODO);
	 		helper.setDefaultFrom(getDefaultFrom(message, mailSender));
	 		MimeMessage msg = mailSender.createMimeMessage();
			helper.prepare(msg);
			job.schedule(mailSender, msg, comment, getMailDirPath(binder), true);
	 		currentMessage.remove(MailModule.CC);	// If these are to long, don't have a solution.
		}
		getReportModule().addEmailLog(emailLog);
	}
    
    /*
     * Returns the default from email address to use for the given
     * email.
     */
    private static String getDefaultFrom(Map message, JavaMailSender mailSender) {
 		InternetAddress defaultFromIA = ((null != message) ? ((InternetAddress) message.get(MailModule.FROM)) : null);
 		String reply;
 		if (null == defaultFromIA)
 		     reply = mailSender.getDefaultFrom();
 		else reply = defaultFromIA.getAddress();
 		return reply;
    }
    
    private static String getDefaultFrom(JavaMailSender mailSender) {
    	// Always use the initial form of the method.
    	return getDefaultFrom(null, mailSender);
    }

    /*
     * Inner class used to ...
     */
    class MailStatus implements MailSentStatus {
    	Set<Address> failures;
    	Set<Address> queuedTo;
       	Set<Address> sentTo = new HashSet();
       	
       	protected MailStatus(Map message) {
    		Collection<InternetAddress> addrs = ((Collection) message.get(MailModule.TO));
    		if (null != addrs) {
    			sentTo.addAll(addrs);
    		}
    		
    		addrs = ((Collection) message.get(MailModule.CC));
       		if (null != addrs) {
       			sentTo.addAll(addrs);
       		}
       		
    		addrs = ((Collection) message.get(MailModule.BCC));
       		if (null != addrs) {
       			sentTo.addAll(addrs);
       		}
       	 
       	}
       	
    	@Override
		public Collection<Address> getFailedToSend() {
    		if (null == failures) {
    			return Collections.EMPTY_SET;
    		}
    		return failures;
    	}
    	
    	@Override
		public Collection<Address> getQueuedToSend() {
    		if (null == queuedTo) {
    			return Collections.EMPTY_SET;
    		}
    		return queuedTo;
    	}
    	
    	@Override
		public Collection<Address> getSentTo() {
    		if (null == sentTo) {
    			return Collections.EMPTY_SET;
    		}
    		sentTo.removeAll(getQueuedToSend());
    		sentTo.removeAll(getFailedToSend());
    		return sentTo;
    	}
    	
    	protected void addFailures(Address[] addrs) {
    		if (null == addrs) {
    			return;
    		}
    		if (null == failures) {
    			failures = new HashSet();
    		}
    		for (int i = 0; i < addrs.length; ++i) {
    			failures.add(addrs[i]);
    		}
    	}
    	
    	protected void addQueued(Address[] addrs) {
    		if (null == addrs) {
    			return;
    		}
       		if (null == queuedTo) {
       			queuedTo = new HashSet();
       		}
    		for (int i = 0; i < addrs.length; ++i) {
    			queuedTo.add(addrs[i]);
    		}
    	}
    }
}
