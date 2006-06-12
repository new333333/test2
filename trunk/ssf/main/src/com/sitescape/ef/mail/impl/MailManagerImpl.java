
package com.sitescape.ef.mail.impl;

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

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.domain.NotificationDef;
import com.sitescape.ef.domain.Notification;
import com.sitescape.ef.domain.UserNotification;
import com.sitescape.ef.domain.PostingDef;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.mail.FolderEmailFormatter;
import com.sitescape.ef.mail.JavaMailSender;
import com.sitescape.ef.mail.MailManager;
import com.sitescape.ef.mail.MimeMessagePreparator;
import com.sitescape.ef.module.definition.notify.Notify;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.jobs.ScheduleInfo;
import com.sitescape.ef.repository.RepositoryUtil;
import com.sitescape.ef.util.ConfigPropertyNotFoundException;
import com.sitescape.ef.util.PortabilityUtil;
import com.sitescape.ef.util.SPropsUtil;
import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.ef.jobs.FailedEmail;
import com.sitescape.ef.jobs.SendEmail;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.jndi.JndiAccessor;
import com.sitescape.util.Validator;
import com.sitescape.ef.util.SZoneConfig;
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
	private String dataRootDir;
	private String subDirName;

	public MailManagerImpl() {
		defaultProps.put(MailManager.POSTING_JOB, "com.sitescape.ef.jobs.DefaultEmailPosting");
		defaultProps.put(MailManager.NOTIFY_TEMPLATE_TEXT, "mailText.xslt");
		defaultProps.put(MailManager.NOTIFY_TEMPLATE_HTML, "mailHtml.xslt");
		defaultProps.put(MailManager.NOTIFY_TEMPLATE_CACHE_DISABLED, "false");
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
	public File getMailDirPath(Binder binder) {
		return new File(new StringBuffer(dataRootDir).append(binder.getZoneName()).append(File.separator).append(subDirName).append(File.separator).append(binder.getId().toString()).append(File.separator).toString());
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
		String result = getMailAttribute(binder.getZoneName(), "binder[@id='" + binder.getId().toString() +"']/" + node, name);
		if (result != null) return result;
		if (binder.getParentBinder() != null) return getMailAttribute(binder.getParentBinder(), node, name);
		return getMailAttribute(binder.getZoneName(), node, name);

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
	public void receivePostings(ScheduleInfo config) {
		String storeProtocol, prefix, auth;
		List posters = getMailPosters(config.getZoneName());
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
				logger.error("Error posting mail from " + hostName + " " + ex.getLocalizedMessage());
				
			}						
		}
		
	}
	/**
	 * Send email notifications for recent changes
	 */
    public Date sendNotifications(Long folderId, Date start) {
        String zoneName = RequestContextHolder.getRequestContext().getZoneName();
 		Folder folder = (Folder)coreDao.loadBinder(folderId, zoneName); 
		Date until = new Date();
		//get folder specific helper to build message
  		FolderEmailFormatter processor = (FolderEmailFormatter)processorManager.getProcessor(folder,FolderEmailFormatter.PROCESSOR_KEY);
  		List entries = processor.getEntries(folder, start, until);
 		if (entries.isEmpty()) {
			return until;
		}
 		
		List digestResults = processor.buildDigestDistributionList(folder, entries);
		// Users wanting individual, message style email
		List messageResults = processor.buildMessageDistributionList(folder, entries);
		
		JavaMailSender mailSender = getMailSender(folder);
		MimeHelper mHelper = new MimeHelper(processor, folder, start);
		mHelper.setDefaultFrom(mailSender.getDefaultFrom());		

		for (int i=0; i<digestResults.size(); ++i) {
			Object row[] = (Object [])digestResults.get(i);
			
			//Use spring callback to wrap exceptions into something more useful than javas 
			try {
				mHelper.setEntries((Collection)row[0]);
				mHelper.setType(Notify.SUMMARY);
				for (Iterator iter=((Map)row[1]).entrySet().iterator(); iter.hasNext();) {
					Map.Entry e = (Map.Entry)iter.next();
					mHelper.setLocale((Locale)e.getKey());
					mHelper.setToAddrs((Set)e.getValue());
					mailSender.send(mHelper);
				}
			} catch (MailSendException sx) {
	    		logger.error("Error sending mail:" + sx.getMessage());
		  		FailedEmail process = (FailedEmail)processorManager.getProcessor(folder, FailedEmail.PROCESSOR_KEY);
		   		process.schedule(folder, mailSender, mHelper.getMessage(), getMailDirPath(folder));
 	    	} catch (Exception ex) {
	       		logger.error(ex.getMessage());
	    	} 
		}
		
		for (int i=0; i<messageResults.size(); ++i) {
			Object row[] = (Object [])messageResults.get(i);
			
			//Use spring callback to wrap exceptions into something more useful than javas 
			try {
				mHelper.setEntries((Collection)row[0]);
				mHelper.setType(Notify.FULL);
				for (Iterator iter=((Map)row[1]).entrySet().iterator(); iter.hasNext();) {
					Map.Entry e = (Map.Entry)iter.next();
					mHelper.setLocale((Locale)e.getKey());
					mHelper.setToAddrs((Set)e.getValue());
					mailSender.send(mHelper);
				}
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
		MimeMessage message;
		String messageType;
		Date startDate;
		String defaultFrom;
		Locale locale;
		
		private MimeHelper(FolderEmailFormatter processor, Folder folder, Date startDate) {
			this.processor = processor;
			this.folder = folder;
			this.startDate = startDate;			
		}
		protected void setToAddrs(Collection toAddrs) {
			this.toAddrs = toAddrs;			
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
			Map result = processor.buildNotificationMessage(folder, entries, notify);
			helper.setText((String)result.get(FolderEmailFormatter.PLAIN), (String)result.get(FolderEmailFormatter.HTML));
			Set atts = notify.getAttachments();
			for (Iterator iter=atts.iterator(); iter.hasNext();) {
				FileAttachment fAtt = (FileAttachment)iter.next();			
				FolderEntry entry = (FolderEntry)fAtt.getOwner().getEntity();
				DataSource ds = RepositoryUtil.getDataSource(fAtt.getRepositoryServiceName(), entry.getParentFolder(), 
							entry, fAtt.getFileItem().getName(), helper.getFileTypeMap());

				helper.addAttachment(fAtt.getFileItem().getName(), ds);
			}
			notify.clearAttachments();
			//save message incase cannot connect and need to resend;
			message = mimeMessage;
		}

	}


}
