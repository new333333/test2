/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */

package com.sitescape.team.module.mail.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
import java.util.TimeZone;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.search.OrTerm;
import javax.mail.search.RecipientStringTerm;
import javax.mail.search.SearchTerm;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.springframework.core.io.InputStreamSource;
import org.springframework.jndi.JndiAccessor;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.ClassLoaderUtils;
import org.springframework.beans.factory.InitializingBean;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.PostingDef;
import com.sitescape.team.domain.Subscription;
import com.sitescape.team.jobs.FailedEmail;
import com.sitescape.team.jobs.SendEmail;
import com.sitescape.team.module.definition.notify.Notify;
import com.sitescape.team.module.ical.IcalModule;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.mail.FolderEmailFormatter;
import com.sitescape.team.module.mail.JavaMailSender;
import com.sitescape.team.module.mail.MailModule;
import com.sitescape.team.module.mail.MimeMessagePreparator;
import com.sitescape.team.repository.RepositoryUtil;
import com.sitescape.team.util.ByteArrayResource;
import com.sitescape.team.util.Constants;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.PortabilityUtil;
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.team.util.SpringContextUtil;
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
public class MailModuleImpl extends CommonDependencyInjection implements MailModule, InitializingBean {
	protected Log logger = LogFactory.getLog(getClass());
	protected Map zoneProps = new HashMap();
	protected Map mailPosters = new HashMap();
	protected Map<String, JavaMailSender> mailSenders = new HashMap();
	protected JavaMailSender mailSender;
	protected JndiAccessor jndiAccessor;
	protected Map defaultProps = new HashMap();
	private String mailRootDir;

	public MailModuleImpl() {
		defaultProps.put(MailModule.POSTING_JOB, "com.sitescape.team.jobs.DefaultEmailPosting");
		defaultProps.put(MailModule.NOTIFY_TEMPLATE_TEXT, "mailText.xslt");
		defaultProps.put(MailModule.NOTIFY_TEMPLATE_HTML, "mailHtml.xslt");
		defaultProps.put(MailModule.NOTIFY_TEMPLATE_CACHE_DISABLED, "false");
	}

	private IcalModule icalModule;
	public IcalModule getIcalModule() {
		return icalModule;
	}
	public void setIcalModule(IcalModule icalModule) {
		this.icalModule = icalModule;
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
	public void afterPropertiesSet() {
		//preload mailSenders so retry mail will work.  Needs name availailable 
		List<Element> senders = SZoneConfig.getAllElements("//mailConfiguration//notify");
		for (Element sEle:senders) {
			String jndiName = sEle.attributeValue("session");
			if (Validator.isNotNull(jndiName)) {
				try {
					jndiName = PortabilityUtil.getJndiName(jndiName);
					JavaMailSender sender = (JavaMailSender)mailSender.getClass().newInstance();				
					SpringContextUtil.applyDependencies(sender, "mailSender");
					Object jndiObj = jndiAccessor.getJndiTemplate().lookup(jndiName);
					
					//System.out.println(ClassLoaderUtils.showClassLoaderHierarchy(jndiObj, "jndi")); // $$$ TODO
					//System.out.println(ClassLoaderUtils.showClassLoaderHierarchy(javax.mail.Session.class.getClassLoader()));
					
					sender.setSession((javax.mail.Session) jndiObj);
					sender.setName(jndiName);		
					mailSenders.put(jndiName, sender);
				
				} catch (Exception ex) {
					logger.error("Error locating " + jndiName + " " + ex.getLocalizedMessage());
				}
			}		
		}
		
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

// Think this is overkill
//	public String getMailAttribute(Binder binder, String node, String name) {
//		String result = getMailAttribute(RequestContextHolder.getRequestContext().getZoneName(), "binder[@id='" + binder.getId().toString() +"']/" + node, name);
//		if (result != null) return result;
//		if (!binder.isRoot()) return getMailAttribute(binder.getParentBinder(), node, name);
//		return getMailAttribute(RequestContextHolder.getRequestContext().getZoneName(), node, name);

//	}
	protected JavaMailSender getMailSender(String jndiName) {
		JavaMailSender sender=null;
		sender = mailSenders.get(jndiName);
		if (sender == null) throw new ConfigurationException("Missing JavaMailSender bean");
		return sender;
	}
	public JavaMailSender getMailSender(Binder binder) {
		String jndiName;
		if (binder.isZone()) jndiName = PortabilityUtil.getJndiName(getMailAttribute(binder.getName(), "notify", "session"));
		else jndiName = PortabilityUtil.getJndiName(getMailAttribute(RequestContextHolder.getRequestContext().getZoneName(), "notify", "session"));
//		else jndiName = PortabilityUtil.getJndiName(getMailAttribute(binder, "notify", "session"));
		return getMailSender(jndiName);
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
			int port = Integer.parseInt(session.getProperty(prefix + "port"));
			if (Validator.isNull(session.getProperty(prefix + "port"))) {
				port = Integer.parseInt(session.getProperty("mail.port"));
			}
			auth = session.getProperty(prefix + "auth");
			if (Validator.isNull(auth)) 
				auth = session.getProperty("mail.auth");
			String user = session.getProperty(prefix + "user");
			if (Validator.isNull(user)) 
				user = session.getProperty("mail.user");
			javax.mail.Folder mFolder=null;
			Store store=null;
			try {
				
				store = session.getStore();
				if ("true".equals(auth)) {
					String password = session.getProperty(prefix + "password");
					if (Validator.isNull(password)) 
						password = session.getProperty("mail.password");
					store.connect(hostName, port, user, password);
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
				logger.error("Error posting mail from [" + hostName + "]", ex);
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
					logger.error(px.getLocalizedMessage());	    		
				} catch (MailSendException sx) {
					if (binder != null) {
				  		FailedEmail process = (FailedEmail)processorManager.getProcessor(binder, FailedEmail.PROCESSOR_KEY);
				   		process.schedule(binder, mailSender, mailMsg, getMailDirPath(binder));			
					}
					logger.error("Error sending posting reject:" + sx.getLocalizedMessage());
				} catch (MailAuthenticationException ax) {
					logger.error("Authentication Exception:" + ax.getLocalizedMessage());
				} catch (Exception ex) {
					logger.error("Error sending posting reject:" + ex.getLocalizedMessage());
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
		mHelper.setTimeZone(getMailProperty(RequestContextHolder.getRequestContext().getZoneName(), MailModule.DEFAULT_TIMEZONE));
		
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
	    		logger.error("Error sending mail:" + sx.getLocalizedMessage());
		  		FailedEmail process = (FailedEmail)processorManager.getProcessor(folder, FailedEmail.PROCESSOR_KEY);
		   		process.schedule(folder, mailSender, mHelper.getMessage(), getMailDirPath(folder));
		   	} catch (MailAuthenticationException ax) {
	    		logger.error("Error sending mail:" + ax.getLocalizedMessage());
		  		FailedEmail process = (FailedEmail)processorManager.getProcessor(folder, FailedEmail.PROCESSOR_KEY);
		   		process.schedule(folder, mailSender, mHelper.getMessage(), getMailDirPath(folder));		   	 
		   	} catch (Exception ex) {
	       		logger.error(ex.getLocalizedMessage());
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
		mHelper.setTimeZone(getMailProperty(RequestContextHolder.getRequestContext().getZoneName(), MailModule.DEFAULT_TIMEZONE));

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
    //used for re-try mail.  MimeMessage has been serialized 
    public void sendMail(String mailSenderName, java.io.InputStream input) {
    	JavaMailSender mailSender = getMailSender(mailSenderName);
    	try {
        	MimeMessage mailMsg = new MimeMessage(mailSender.getSession(), input);
			mailSender.send(mailMsg);
 		} catch (MessagingException mx) {
			throw new MailPreparationException(NLT.get("errorcode.sendMail.badInputStream", new Object[] {mx.getLocalizedMessage()}));
		}
    }
    //prepare mail and send it - caller must retry if desired
    public void sendMail(String mailSenderName, MimeMessagePreparator mHelper) {
    	JavaMailSender mailSender = getMailSender(mailSenderName);
		mHelper.setDefaultFrom(mailSender.getDefaultFrom());
		//Use spring callback to wrap exceptions into something more useful than javas 
		mailSender.send(mHelper);
	}
    //used to send prepared mail now.
    public void sendMail(MimeMessage mailMsg) {
       	Binder zone = RequestContextHolder.getRequestContext().getZone();
        sendMail(getMailSender(zone).getName(), mailMsg);
    }
    //used to send prepared mail now.    
    public void sendMail(String mailSenderName, MimeMessage mailMsg) {
    	JavaMailSender mailSender = getMailSender(mailSenderName);
		mailSender.send(mailMsg);
    }
    //send mail now, if fails, reschedule
    public boolean sendMail(Binder binder, Map message, String comment) {
  		SendEmail process = (SendEmail)processorManager.getProcessor(binder, SendEmail.PROCESSOR_KEY);
   		return process.sendMail(getMailSender(binder).getName(), message, comment);
	}
    // schedule mail delivery - message cannot contain attachments
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
		TimeZone timezone;
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
		protected void setTimeZone(String timezone) {			
			this.timezone = TimeZone.getTimeZone(timezone);
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
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.FULL, locale);
			if (timezone == null) timezone = TimeZone.getDefault();
			df.setTimeZone(timezone);
			notify.setDateFormat(df);
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
			
			prepareAttachments(notify, helper);
			prepareICalendars(notify, helper);
			
			//save message incase cannot connect and need to resend;
			message = mimeMessage;
		}
		
		private void prepareICalendars(Notify notify, MimeMessageHelper helper) throws MessagingException {
			int c = 0;
			Iterator entryEventsIt = notify.getEvents().entrySet().iterator();
			while (entryEventsIt.hasNext()) { 
				Map.Entry mapEntry = (Map.Entry)entryEventsIt.next();
				DefinableEntity entry = (DefinableEntity)mapEntry.getKey();
				List events = (List)mapEntry.getValue();
				Calendar iCal = getIcalModule().generate(entry, events, getMailProperty(RequestContextHolder.getRequestContext().getZoneName(), MailModule.DEFAULT_TIMEZONE));
				
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				CalendarOutputter calendarOutputter = new CalendarOutputter();
				try {
					calendarOutputter.output(iCal, out);
				} catch (IOException e) {
					logger.error(e);
				} catch (ValidationException e) {
					logger.error(e);
					
				}
				// helper.addInline("", new ByteArrayResource(out.toByteArray()), "text/calendar");
				
				
				DataSource dataSource = createDataSource(new ByteArrayResource(out.toByteArray()), "text/calendar", "iCalendar" + c + ".ics");
				
				MimeBodyPart mimeBodyPart = new MimeBodyPart();
				mimeBodyPart.setDisposition(MimeBodyPart.INLINE);
				mimeBodyPart.setFileName("iCalendar" + c + ".ics");
				// We're using setHeader here to remain compatible with JavaMail 1.2,
				// rather than JavaMail 1.3's setContentID.
				// mimeBodyPart.setHeader(HEADER_CONTENT_ID, "<" + contentId + ">");
				mimeBodyPart.setDataHandler(new DataHandler(dataSource));
				helper.getMimeMultipart().addBodyPart(mimeBodyPart);
				c++;
			}
			notify.clearEvents();
		}
		
		protected DataSource createDataSource(
			    final InputStreamSource inputStreamSource, final String contentType, final String name) {

				return new DataSource() {
					public InputStream getInputStream() throws IOException {
						return inputStreamSource.getInputStream();
					}
					public OutputStream getOutputStream() {
						throw new UnsupportedOperationException("Read-only javax.activation.DataSource");
					}
					public String getContentType() {
						return contentType;
					}
					public String getName() {
						return name;
					}
				};
			}
		
		private void prepareAttachments(Notify notify, MimeMessageHelper helper) throws MessagingException {
			if (sendAttachments) {
				Set atts = notify.getAttachments();
				for (Iterator iter=atts.iterator(); iter.hasNext();) {
					FileAttachment fAtt = (FileAttachment)iter.next();			
					FolderEntry entry = (FolderEntry)fAtt.getOwner().getEntity();
					DataSource ds = RepositoryUtil.getDataSourceVersioned(fAtt.getRepositoryName(), entry.getParentFolder(), 
							entry, fAtt.getFileItem().getName(), fAtt.getHighestVersion().getVersionName(), helper.getFileTypeMap());

					helper.addAttachment(fAtt.getFileItem().getName(), ds);
				}
			}
			notify.clearAttachments();
		}

	}


}
