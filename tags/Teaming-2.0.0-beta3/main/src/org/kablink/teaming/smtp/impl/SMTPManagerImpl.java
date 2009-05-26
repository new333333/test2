/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.smtp.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.SSLContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.filter.SSLFilter;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.context.request.RequestContextUtil;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.SimpleName;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.mail.EmailPoster;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.smtp.SMTPManager;
import org.kablink.teaming.util.SessionUtil;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.MessageHandlerFactory;
import org.subethamail.smtp.RejectException;
import org.subethamail.smtp.TooMuchDataException;
import org.subethamail.smtp.command.StartTLSCommand;
import org.subethamail.smtp.server.ConnectionContext;
import org.subethamail.smtp.server.SMTPServer;
import org.subethamail.smtp.server.io.DummySSLSocketFactory;


public class SMTPManagerImpl extends CommonDependencyInjection implements SMTPManager, SMTPManagerImplMBean, InitializingBean, DisposableBean {

	protected Log logger = LogFactory.getLog(getClass());
	
	protected boolean enabled = false;
	protected boolean tls = false;
	protected int port = 2525;
	protected String bindAddress = null;

	protected SMTPServer server = null;
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		logger.debug("Inbound SMTP Server:  " + (enabled ? "Enabled." : "Disabled."));
	}
	public boolean isEnabled() {
		return enabled;
	}

	public void setTls(boolean tls) {
		this.tls = tls;
		logger.debug("Inbound SMTP Server:  " + (tls ? "Will announce TLS support." : "Will not announce TLS support."));
	}
	public boolean isTls() {
		return tls;
	}

	public void setBindAddress(String bindAddress) {
		if ((null != bindAddress) && (0 == bindAddress.length())) {
			bindAddress = null;
		}
		this.bindAddress = bindAddress;
		logger.debug("Inbound SMTP Server:  bindAddress is set to:  \"" + ((null == bindAddress) ? "null" : bindAddress) + "\".");
	}
	public String getBindAddress() {
		return bindAddress;
	}
	
	public void setPort(int port) {
		this.port = port;
		logger.debug("Inbound SMTP Server:  port is set to:  \"" + port + "\".");
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
		// Is the inbound SMTP server is not enabled...
		if(!(isEnabled())) {
			// ...bail.
			return;
		}
		
		// Which, if any, specific address is the inbound SMTP server
		// supposed to be bound to?
		InetAddress	iNetAddr;
		if ((null == this.bindAddress) || (0 == this.bindAddress.length())) {
			iNetAddr = null;
			logger.debug("Inbound SMTP Server:  No bindAddress, binding to all addresses.");
		}
		else if (this.bindAddress.equals("localhost")) {
			iNetAddr = InetAddress.getLocalHost();
			logger.debug("Inbound SMTP Server:  Binding to \"localhost\".");
		}
		else {
			try {
				iNetAddr = InetAddress.getByName(bindAddress);
				logger.debug("Inbound SMTP Server:  Binding to \"" + bindAddress + "\".");
			}
			catch (UnknownHostException e) {
				iNetAddr = null;
				setEnabled(false);
				logger.error("Inbound SMTP Server:  Cannot resolve bindAddress.  Inbound SMTP server will be disabled.", e);
			}
		}
		logger.debug("Inbound SMTP Server:  " + ((null == iNetAddr) ? "Bound to all addresses." : "Bound to a specific address."));
		
		// If the bind address evaluation failed...
		if(!(isEnabled())) {
			// ...bail.
			logger.debug("Inbound SMTP Server:  Address binding failed, inbound SMTP server is disabled.");
			return;
		}
			
		// Construct the SMTPServer object.  We must do this prior to
		// the TLS handling because it loads a default SSLFilter
		// that the TLS handling needs to override.  If we don't do
		// this first, it would override the SSLFilter setup in the TLS
		// handling below.
		this.server = new SMTPServer(new MessageHandlerFactory() {
			public MessageHandler create(MessageContext ctx) {
				return new Handler(ctx);
			}
		});
		
		// If the inbound SMTP server is supposed to support TLS...
		if (this.tls) {
			// ...put an SSLFilter into effect that has the proper
			// ...cipher suites enabled in the SMTPServer's
			// ...StartTLSCommand.
			DummySSLSocketFactory socketFactory = new DummySSLSocketFactory();
			SSLContext sslContext = socketFactory.getSSLContext();
			String[] cipherSuites = sslContext.getSocketFactory().getSupportedCipherSuites();
			if (logger.isDebugEnabled()) {
				int cipherSuiteCount = ((null == cipherSuites) ? 0 : cipherSuites.length);
				logger.debug("There are " + cipherSuiteCount + " supported cipher suites on the SSLContext.");
				for (int i = 0; i < cipherSuiteCount; i += 1) {
					logger.debug("...item " + (i + 1) + ":  " + cipherSuites[i]);
				}
			}
			SSLFilter sslFilter = new SSLFilter(sslContext);
			sslFilter.setEnabledCipherSuites(cipherSuites);
			StartTLSCommand.setSSLFilter(sslFilter);
		}
		

		// Finally, complete the initializations of the inbound SMTP
		// server.
		this.server.setAnnounceTLS(this.tls);
		this.server.setBindAddress(iNetAddr);
		this.server.setPort(this.port);
		this.server.start();
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
			// Parse recipients now, so other recipients can be handled by someone else.
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
		
		@SuppressWarnings("unchecked")
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
			   		getCoreDao().clear(); // Clear session in case next from different zone.
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
