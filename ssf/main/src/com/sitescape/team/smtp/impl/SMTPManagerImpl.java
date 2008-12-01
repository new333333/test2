package com.sitescape.team.smtp.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.MessageHandlerFactory;
import org.subethamail.smtp.RejectException;
import org.subethamail.smtp.TooMuchDataException;
import org.subethamail.smtp.server.ConnectionContext;
import org.subethamail.smtp.server.SMTPServer;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextUtil;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.SimpleName;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.mail.EmailPoster;
import com.sitescape.team.module.zone.ZoneModule;
import com.sitescape.team.smtp.SMTPManager;
import com.sitescape.team.util.SessionUtil;
import com.sitescape.team.runas.RunasCallback;
import com.sitescape.team.runas.RunasTemplate;

public class SMTPManagerImpl extends CommonDependencyInjection implements SMTPManager, SMTPManagerImplMBean, InitializingBean, DisposableBean {

	protected Log logger = LogFactory.getLog(getClass());
	
	protected boolean enabled = false;
	protected boolean tls = false;
	protected int port = 2525;

	protected SMTPServer server = null;
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public boolean isEnabled() {
		return enabled;
	}

	public void setTls(boolean tls) {
		this.tls = tls;
	}
	public boolean isTls() {
		return tls;
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
	
	protected BinderModule binderModule;
	public void setBinderModule(BinderModule binderModule)
	{
		this.binderModule = binderModule;
	}
	public BinderModule getBinderModule()
	{
		return binderModule;
	}
	
	public void afterPropertiesSet() throws Exception {
		if(isEnabled()) {
			this.server = new SMTPServer(new MessageHandlerFactory() {
				public MessageHandler create(MessageContext ctx) {
					return new Handler(ctx);
				}
			});
			this.server.setAnnounceTLS(this.tls);
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
		List<Recipient> recipients; 
		
		public Handler(MessageContext ctx)
		{
			this.ctx = ctx;
			this.recipients = new LinkedList<Recipient>();
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
		
		public void from(String from) throws RejectException
		{
			this.from = from;
		}
		
		public void recipient(String recipient) throws RejectException
		{
			//parse reciptients now, so other recipients can be handled by someone else
			String[] parts = recipient.split("@");
			if(parts.length != 2)  throw new RejectException(550, "Requested action not taken: mailbox " + recipient + " not known");
			String localPart = parts[0];
			String hostname = parts[1];
//			SessionUtil.sessionStartup();
			try {
				//no request context
				Long zoneId = getZoneModule().getZoneIdByVirtualHost(hostname);
				if (!getZoneModule().getZoneConfig(zoneId).getMailConfig().isSimpleUrlPostingEnabled()) {
					throw new RejectException(550, "Requested action not taken: mailbox " + recipient + " unavailable");
				}
				//skip modules to load info, so don't have to worry about user context
				SimpleName simpleUrl = getCoreDao().loadSimpleNameByEmailAddress(localPart, zoneId);
				if (simpleUrl == null || !simpleUrl.getBinderType().equals(EntityType.folder.name())) {
					throw new RejectException(550, "Requested action not taken: mailbox " + recipient + " unavailable");
				}
				Binder binder = getCoreDao().loadBinder(simpleUrl.getBinderId(), zoneId);
				if(!binder.getPostingEnabled()) {
					throw new RejectException(550, "Requested action not taken: mailbox " + recipient + " unavailable");
				}
				this.recipients.add(new Recipient(recipient, simpleUrl));
			} finally {
//				SessionUtil.sessionStop();	
			}
		}
		
		public void data(InputStream data) throws TooMuchDataException, IOException, RejectException
		{
			SessionUtil.sessionStartup();
			Session session = Session.getDefaultInstance(new Properties());
			try {
				MimeMessage msgs[] = new MimeMessage[1];
				msgs[0] = new MimeMessage(session, data);
				List errors = new ArrayList();
				for(Recipient recipient : recipients) {
					logger.debug("Delivering new message to " + recipient.email);			
					//Run as background processing agent, same as other posting jobs.  
					User user = getProfileDao().getReservedUser(ObjectKeys.JOB_PROCESSOR_INTERNALID, recipient.simpleName.getZoneId());
					RequestContextUtil.setThreadContext(user).resolve();
					
					Binder binder = getCoreDao().loadBinder(recipient.simpleName.getBinderId(),recipient.simpleName.getZoneId());
					EmailPoster processor = (EmailPoster)processorManager.getProcessor(binder,EmailPoster.PROCESSOR_KEY);
					errors.addAll(processor.postMessages((Folder)binder, recipient.email, msgs, session));
			   		RequestContextHolder.clear();
			   		getCoreDao().clear(); //clear session incase next from different zone
				}
				if(errors.size() > 0) {
					Message m = (Message) errors.get(0);
					throw new RejectException(554, m.getSubject());
				}
			} catch (javax.mail.MessagingException ex) {
				logger.debug("Error processing message to " + from + ": " + ex.getMessage());
				throw new RejectException(554, "Server error");
			} finally {
				SessionUtil.sessionStop();
		   		RequestContextHolder.clear();
			}
		}
	
		public void resetMessageState()
		{
			from = null;
			recipients = new LinkedList<Recipient>();
		}
		
		protected class Recipient {
			String email;
			SimpleName simpleName;
			public Recipient(String email, SimpleName simpleName) {
				this.email = email;
				this.simpleName = simpleName;
			}
		}
	}
}
