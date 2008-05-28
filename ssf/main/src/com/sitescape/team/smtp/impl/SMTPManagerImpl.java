package com.sitescape.team.smtp.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.MessageHandlerFactory;
import org.subethamail.smtp.MessageListener;
import org.subethamail.smtp.RejectException;
import org.subethamail.smtp.TooMuchDataException;
import org.subethamail.smtp.server.ConnectionContext;
import org.subethamail.smtp.server.SMTPServer;


import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.context.request.RequestContextUtil;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.PostingDef;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.folder.FolderModule;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.mail.EmailPoster;
import com.sitescape.team.module.zone.ZoneModule;
import com.sitescape.team.smtp.SMTPManager;
import com.sitescape.team.util.SessionUtil;

public class SMTPManagerImpl extends CommonDependencyInjection implements SMTPManager, SMTPManagerImplMBean, InitializingBean, DisposableBean {

	protected Log logger = LogFactory.getLog(getClass());
	
	protected boolean enabled = false;
	protected int port = 2525;

	protected SMTPServer server = null;
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public boolean isEnabled() {
		return enabled;
	}

	public void setPort(int port) {
		this.port = port;
	}
	public int getPort() {
		return port;
	}
	
	protected ZoneModule zoneModule;
	public void setZoneModule(ZoneModule zoneModule)
	{
		this.zoneModule = zoneModule;
	}
	public ZoneModule getZoneModule()
	{
		return zoneModule;
	}
	
	public void afterPropertiesSet() throws Exception {
		if(isEnabled()) {
			this.server = new SMTPServer(new MessageHandlerFactory() {
				public MessageHandler create(MessageContext ctx) {
					return new Handler(ctx);
				}
			});
			this.server.setPort(this.port);
			this.server.start();
		}
	}
	
	public void destroy() throws Exception
	{
		if(this.server != null)
		{
			this.server.stop();
		}
	}

	class Handler implements MessageHandler
	{
		MessageContext ctx;
		String from;
		List<String> recipients;
		
		public Handler(MessageContext ctx)
		{
			this.ctx = ctx;
			this.recipients = new LinkedList<String>();
		}

		public List<String> getAuthenticationMechanisms()
		{
			return new ArrayList<String>();
		}
		
		public boolean auth(String clientInput, StringBuilder response, ConnectionContext ctx) throws RejectException
		{
			return true;
		}

		public void resetState()
		{
		}
		
		public void from(String from)
		{
			this.from = from;
		}
		
		public void recipient(String recipient)
		{
			this.recipients.add(recipient);
		}
		
		public void data(InputStream data) throws TooMuchDataException, IOException, RejectException
		{
			SessionUtil.sessionStartup();
			MimeMessage msgs[] = null;
			Session session = Session.getDefaultInstance(new Properties());
			try {
				try {
					msgs = new MimeMessage[1];
					msgs[0] = new MimeMessage(session, data);
				} catch (javax.mail.MessagingException ex) {
					logger.debug("Error processing message to " + from + ": " + ex.getMessage());
					throw new RejectException(554, "Server error");
				}
				for(String recipient : recipients) {
					if(determineSender(from, recipient) == null) {
						throw new RejectException(550, "Requested action not taken: mailbox " + recipient + " unavailable");
					}
					PostingDef posting = getCoreDao().findPosting(recipient, RequestContextHolder.getRequestContext().getZoneId());
					if(posting == null) {
						throw new RejectException(550, "Requested action not taken: mailbox " + recipient + " unavailable");
					}
	
					logger.debug("Delivering new message to " + recipient);
					Folder folder = (Folder)posting.getBinder();
					EmailPoster processor = (EmailPoster)processorManager.getProcessor(folder,EmailPoster.PROCESSOR_KEY);
					try {
						List errors = processor.postMessages(folder, posting, msgs, session);
						if(errors.size() > 0) {
							Message m = (Message) errors.get(0);
							throw new RejectException(554, m.getSubject());
						}
					} catch (javax.mail.MessagingException ex) {
						logger.debug("Error processing message to " + from + ": " + ex.getMessage());
						throw new RejectException(554, "Server error");
					}
				}
			} finally {
				SessionUtil.sessionStop();
			}
		}
	
		public void resetMessageState()
		{
			from = null;
			recipients = new LinkedList<String>();
		}

		private User determineSender(String from, String recipient)
		{
			String[] recipientParts = recipient.split("@");
			if(recipientParts.length != 2) {
				logger.info("Ignoring mail posted to invalid email address: " + recipient);
				return null;
			}
			Long zone = getZoneModule().getZoneIdByVirtualHost(recipientParts[1]);
			List<Principal> ps = getProfileDao().loadPrincipalByEmail(from, zone);
			User user = null;
			for (Principal p:ps) {
	            //Make sure it is a user
	            try {
	            	User principal = (User)getProfileDao().loadUser(p.getId(), zone);
	            	if (user == null) user = principal;
	            	else if (!principal.equals(user)) {
	        			logger.error("Multiple users with same email address, cannot use for incoming email");
	        			break;
	            	}
	            } catch (Exception ignoreEx) {};  
			}
			if(user == null) {
				user = getProfileDao().getReservedUser(ObjectKeys.ANONYMOUS_POSTING_USER_INTERNALID, zone);
			}
			if(user != null) {
				RequestContextUtil.setThreadContext(user).resolve();
			}
			return user;
		}
	}
}
