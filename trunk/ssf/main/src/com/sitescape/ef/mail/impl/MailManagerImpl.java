
package com.sitescape.ef.mail.impl;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;
import javax.mail.search.OrTerm;
import javax.mail.search.RecipientStringTerm;
import javax.mail.search.SearchTerm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.springframework.jndi.JndiAccessor;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.PostingDef;
import com.sitescape.ef.domain.Subscription;
import com.sitescape.ef.jobs.FailedEmail;
import com.sitescape.ef.jobs.SendEmail;
import com.sitescape.ef.mail.FolderEmailFormatter;
import com.sitescape.ef.mail.JavaMailSender;
import com.sitescape.ef.mail.MailManager;
import com.sitescape.ef.mail.MimeMessagePreparator;
import com.sitescape.ef.module.definition.notify.Notify;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.repository.RepositoryUtil;
import com.sitescape.ef.util.Constants;
import com.sitescape.ef.util.PortabilityUtil;
import com.sitescape.ef.util.SZoneConfig;
import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.util.Validator;
/**
 * The public methods exposed by this implementation are not transaction 
 * demarcated. If transactions are needed, the FolderEmailProcessors will be 
 * responsible.
 * Of course, this finer granularity transactional control will be of no effect
 * if the caller of this service was already transactional (i.e., it controls
 * transaction boundary that is more coarse). Whenever possible, this practise 
 * is discouraged for obvious performance/scalability reasons.  
 *   
 * @author Janet McCann
 *
 */
public class MailManagerImpl extends CommonDependencyInjection implements MailManager {
	protected Log logger = LogFactory.getLog(getClass());
	protected Map zoneProps = new HashMap();
	protected Map mailPosters = new HashMap();
	protected Map mailSenders = new HashMap();
	protected JavaMailSender mailSender;
	protected JndiAccessor jndiAccessor;
	protected Map defaultProps = new HashMap();
	private String mailRootDir;

	public MailManagerImpl() {
		defaultProps.put(MailManager.POSTING_JOB, "com.sitescape.ef.jobs.DefaultEmailPosting");
		defaultProps.put(MailManager.NOTIFY_TEMPLATE_TEXT, "mailText.xslt");
		defaultProps.put(MailManager.NOTIFY_TEMPLATE_HTML, "mailHtml.xslt");
		defaultProps.put(MailManager.NOTIFY_TEMPLATE_CACHE_DISABLED, "false");
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
	public File getMailDirPath(Binder binder) {
		return new File(new StringBuffer(mailRootDir).append(RequestContextHolder.getRequestContext().getZoneName()).append(File.separator).append(binder.getId().toString()).append(File.separator).toString());
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

	public String getMailAttribute(Binder binder, String node, String name) {
		String result = getMailAttribute(RequestContextHolder.getRequestContext().getZoneName(), "binder[@id='" + binder.getId().toString() +"']/" + node, name);
		if (result != null) return result;
		if (binder.getParentBinder() != null) return getMailAttribute(binder.getParentBinder(), node, name);
		return getMailAttribute(RequestContextHolder.getRequestContext().getZoneName(), node, name);

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
	public JavaMailSender getMailSender(Binder binder) {
		JavaMailSender sender=null;
		String jndiName = PortabilityUtil.getJndiName(getMailAttribute(binder, "notify", "session"));
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
	 * Read mail from all incoming mail servers.
	 *
	 */
	public void receivePostings() {
		String storeProtocol, prefix, auth;
		List posters = getMailPosters(RequestContextHolder.getRequestContext().getZoneName());
		List<PostingDef> postings = getCoreDao().loadPostings(RequestContextHolder.getRequestContext().getZoneId());
		SearchTerm[] aliasSearch = new SearchTerm[2];
		
		for (int i=0; i<posters.size(); ++i) {
			Session session = (Session)posters.get(i);
			storeProtocol = session.getProperty("mail.store.protocol");
			prefix = "mail." + storeProtocol + ".";
			String hostName = session.getProperty(prefix + "host");
			if (Validator.isNull(hostName)) {
				hostName = session.getProperty("mail.host");
			}
			auth = session.getProperty(prefix + "auth");
			if (Validator.isNull(auth)) 
				auth = session.getProperty("mail.auth");
			String from = session.getProperty(prefix + "user");
			if (Validator.isNull(from)) 
				from = session.getProperty("mail.user");
			javax.mail.Folder mFolder=null;
			Store store=null;
			try {
				
				store = session.getStore();
				if ("true".equals(auth)) {
					String password = session.getProperty(prefix + "password");
					if (Validator.isNull(password)) 
						password = session.getProperty("mail.password");
					store.connect(null, null, password);
				} else {
					store.connect();
				}

				mFolder = store.getFolder("inbox");
				
				mFolder.open(javax.mail.Folder.READ_WRITE);
				
				//determine which alias a message belongs to and post it
				for (PostingDef postingDef: postings) {
					if (!postingDef.isEnabled()) continue;
					if (postingDef.getBinder() == null) continue;
					aliasSearch[0] = new RecipientStringTerm(Message.RecipientType.TO,postingDef.getEmailAddress());
					aliasSearch[1] = new RecipientStringTerm(Message.RecipientType.CC,postingDef.getEmailAddress());
					Message aliasMsgs[]=mFolder.search(new OrTerm(aliasSearch));
					if (aliasMsgs.length == 0) continue;
					
					Folder folder = (Folder)postingDef.getBinder();
					FolderEmailFormatter processor = (FolderEmailFormatter)processorManager.getProcessor(folder,FolderEmailFormatter.PROCESSOR_KEY);
					sendErrors(folder, processor.postMessages(folder,postingDef, aliasMsgs, session));
				}				

			} catch (Exception ex) {
				logger.error("Error posting mail from " + hostName + " " + ex);				
			} finally  {
				//Close connection and expunge
				if (mFolder != null) try {mFolder.close(true);} catch (Exception ex) {};
				if (store != null) try {store.close();} catch (Exception ex) {};
			}						
		}		
		
	}
	private void sendErrors(Binder binder, List errors) {
		if (!errors.isEmpty()) {
			for (int i=0; i<errors.size(); ++i) {
				MimeMessage mailMsg = null;
				try {
					mailMsg = (MimeMessage)errors.get(i);
					mailMsg.saveChanges();
					Transport.send(mailMsg);
				} catch (MailParseException px) {
					logger.error(px.getMessage());	    		
				} catch (MailSendException sx) {
					if (binder != null) {
				  		FailedEmail process = (FailedEmail)processorManager.getProcessor(binder, FailedEmail.PROCESSOR_KEY);
				   		process.schedule(binder, RequestContextHolder.getRequestContext().getZoneName(), mailSender, mailMsg, getMailDirPath(binder));			
					}
					logger.error("Error sending posting reject:" + sx.getMessage());
				} catch (MailAuthenticationException ax) {
					logger.error("Authentication Exception:" + ax.getMessage());
				} catch (Exception ex) {
					logger.error("Error sending posting reject:" + ex.getMessage());
				}
			}
		}
		
	}
	public void fillSubscription(Long folderId, Long entryId, Date stamp) {
		FolderEntry entry = getFolderDao().loadFolderEntry(folderId, entryId, RequestContextHolder.getRequestContext().getZoneId());
		Folder folder = entry.getParentFolder();
		FolderEmailFormatter processor = (FolderEmailFormatter)processorManager.getProcessor(folder,FolderEmailFormatter.PROCESSOR_KEY);
		//subscriptions are made to toplevel entries only
		FolderEntry parent = entry.getTopEntry();
		if (parent == null) parent = entry;
		List subscriptions = getCoreDao().loadSubscriptionByEntity(parent.getEntityIdentifier());
		if (subscriptions.isEmpty()) return;
		// Users wanting individual, message style email with attachments
		Map messageResults = processor.buildDistributionList(entry, subscriptions, Subscription.MESSAGE_STYLE_EMAIL_NOTIFICATION);
		// Users wanting individual, message style email without attachments
		Map messageNoAttsResults = processor.buildDistributionList(entry, subscriptions, Subscription.MESSAGE_STYLE_NO_ATTACHMENTS_EMAIL_NOTIFICATION);


		JavaMailSender mailSender = getMailSender(folder);
		MimeHelper mHelper = new MimeHelper(processor, folder, stamp);
		mHelper.setDefaultFrom(mailSender.getDefaultFrom());		
		mHelper.setEntry(entry);

		mHelper.setType(Notify.FULL);
		mHelper.setSendAttachments(false);
		doSubscription (folder, mailSender, mHelper, messageNoAttsResults);
	
		mHelper.setType(Notify.FULL);
		mHelper.setSendAttachments(true);
		doSubscription (folder, mailSender, mHelper, messageResults);
	}
	
	private void doSubscription (Folder folder, JavaMailSender mailSender, MimeHelper mHelper, Map results) {
		for (Iterator iter=results.entrySet().iterator(); iter.hasNext();) {			
			//Use spring callback to wrap exceptions into something more useful than javas 
			try {
				Map.Entry e = (Map.Entry)iter.next();
				mHelper.setLocale((Locale)e.getKey());
				mHelper.setToAddrs((Set)e.getValue());
				mailSender.send(mHelper);
			} catch (MailSendException sx) {
	    		logger.error("Error sending mail:" + sx.getMessage());
		  		FailedEmail process = (FailedEmail)processorManager.getProcessor(folder, FailedEmail.PROCESSOR_KEY);
		   		process.schedule(folder, RequestContextHolder.getRequestContext().getZoneName(), mailSender, mHelper.getMessage(), getMailDirPath(folder));
 	    	} catch (Exception ex) {
	       		logger.error(ex.getMessage());
	    	} 
		}

	}
	/**
	 * Send email notifications for recent changes
	 */
    public Date sendNotifications(Long folderId, Date start) {
 		Folder folder = (Folder)coreDao.loadBinder(folderId, RequestContextHolder.getRequestContext().getZoneId()); 
		Date until = new Date();
		//get folder specific helper to build message
  		FolderEmailFormatter processor = (FolderEmailFormatter)processorManager.getProcessor(folder,FolderEmailFormatter.PROCESSOR_KEY);
  		List entries = processor.getEntries(folder, start, until);
 		if (entries.isEmpty()) {
			return until;
		}
 		List subscriptions = getCoreDao().loadSubscriptionByEntity(folder.getEntityIdentifier());
		List digestResults = processor.buildDistributionList(folder, entries, subscriptions);
		// Users wanting individual, message style email with attachments
		List messageResults = processor.buildDistributionList(folder, entries, subscriptions, Subscription.MESSAGE_STYLE_EMAIL_NOTIFICATION);
		// Users wanting individual, message style email without attachments
		List messageNoAttsResults = processor.buildDistributionList(folder, entries, subscriptions, Subscription.MESSAGE_STYLE_NO_ATTACHMENTS_EMAIL_NOTIFICATION);
		
		JavaMailSender mailSender = getMailSender(folder);
		MimeHelper mHelper = new MimeHelper(processor, folder, start);
		mHelper.setDefaultFrom(mailSender.getDefaultFrom());		

		for (int i=0; i<digestResults.size(); ++i) {
			Object row[] = (Object [])digestResults.get(i);
			mHelper.setEntries((Collection)row[0]);
			mHelper.setType(Notify.SUMMARY);
			mHelper.setSendAttachments(false);
			doSubscription(folder, mailSender, mHelper, (Map)row[1]);
		}
		
		for (int i=0; i<messageNoAttsResults.size(); ++i) {
			Object row[] = (Object [])messageNoAttsResults.get(i);
			mHelper.setEntries((Collection)row[0]);
			mHelper.setType(Notify.FULL);
			mHelper.setSendAttachments(false);
			doSubscription(folder, mailSender, mHelper, (Map)row[1]);
		}

		for (int i=0; i<messageResults.size(); ++i) {
			Object row[] = (Object [])messageResults.get(i);
			mHelper.setEntries((Collection)row[0]);
			mHelper.setType(Notify.FULL);
			mHelper.setSendAttachments(true);
			doSubscription(folder, mailSender, mHelper, (Map)row[1]);
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
    public boolean sendMail(String mailSenderName, MimeMessagePreparator mHelper) {
    	JavaMailSender mailSender = (JavaMailSender)mailSenders.get(mailSenderName);
		mHelper.setDefaultFrom(mailSender.getDefaultFrom());
		//Use spring callback to wrap exceptions into something more useful than javas 
		try {
			mailSender.send(mHelper);
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


    public void scheduleMail(Binder binder, Map message, String comment) {
  		SendEmail process = (SendEmail)processorManager.getProcessor(binder, SendEmail.PROCESSOR_KEY);
   		process.schedule(getMailSender(binder).getName(), message, comment);
	}
    private class MimeHelper implements MimeMessagePreparator {
		FolderEmailFormatter processor;
		Folder folder;
		Collection toAddrs;
		Collection entries;
		Object entry;
		MimeMessage message;
		String messageType;
		Date startDate;
		String defaultFrom;
		Locale locale;
		boolean sendAttachments=false;
		
		private MimeHelper(FolderEmailFormatter processor, Folder folder, Date startDate) {
			this.processor = processor;
			this.folder = folder;
			this.startDate = startDate;			
		}
		protected void setToAddrs(Collection toAddrs) {
			this.toAddrs = toAddrs;			
		}
		protected void setEntry(Object entry) {
			this.entry = entry;
			
		}
		protected void setSendAttachments(boolean sendAttachments) {
			this.sendAttachments = sendAttachments;
		}
		protected void setEntries(Collection entries) {
			this.entries = entries;
		}
		protected void setLocale(Locale locale) {
			this.locale = locale;
		}
		protected void setType(String type) {
			messageType = type;
		}
		public MimeMessage getMessage() {
			return message;
		}
		public void setDefaultFrom(String from) {
			this.defaultFrom = from;
		}
		public void prepare(MimeMessage mimeMessage) throws MessagingException {
			//make sure nothing saved yet
			Notify notify = new Notify();
			notify.setType(messageType);
			notify.setAttachmentsIncluded(sendAttachments);
			notify.setStartDate(startDate);
			notify.setLocale(locale);
			notify.setDateFormat(DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.FULL, locale));
			message = null;
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			helper.setSubject(processor.getSubject(folder, notify));
			for (Iterator iter=toAddrs.iterator();iter.hasNext();) {
				String email = (String)iter.next();
				try	{
					if (!Validator.isNull(email)) helper.addTo(email);
				} catch (AddressException ae) {
					logger.error("Skipping email notifications for " + email + " Bad email address");
				}
			}
			String from = processor.getFrom(folder, notify);
			if (Validator.isNull(from)) {
				from = defaultFrom;
			}
			helper.setFrom(from);

			mimeMessage.addHeader("Content-Transfer-Encoding", "8bit");
			Map result ;
			if (entry != null)
				result = processor.buildNotificationMessage(folder, (FolderEntry)entry, notify);
			else
				result = processor.buildNotificationMessage(folder, entries, notify);
//currently not implemented 			
//			helper.setText((String)result.get(FolderEmailFormatter.PLAIN), (String)result.get(FolderEmailFormatter.HTML));
			helper.setText((String)result.get(FolderEmailFormatter.HTML),true);
			if (sendAttachments) {
				Set atts = notify.getAttachments();
				for (Iterator iter=atts.iterator(); iter.hasNext();) {
					FileAttachment fAtt = (FileAttachment)iter.next();			
					FolderEntry entry = (FolderEntry)fAtt.getOwner().getEntity();
					DataSource ds = RepositoryUtil.getDataSource(fAtt.getRepositoryName(), entry.getParentFolder(), 
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
