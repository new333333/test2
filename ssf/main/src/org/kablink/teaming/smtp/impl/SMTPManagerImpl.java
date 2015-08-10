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
package org.kablink.teaming.smtp.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SessionUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.util.MiscUtil;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.MessageHandlerFactory;
import org.subethamail.smtp.RejectException;
import org.subethamail.smtp.TooMuchDataException;
import org.subethamail.smtp.auth.LoginFailedException;
import org.subethamail.smtp.auth.EasyAuthenticationHandlerFactory;
import org.subethamail.smtp.auth.UsernamePasswordValidator;
import org.subethamail.smtp.server.SMTPServer;

/**
 * Implementation class wrapping the inbound SMTP email server.
 * 
 * @author ?
 */
public class SMTPManagerImpl extends CommonDependencyInjection implements SMTPManager, SMTPManagerImplMBean, InitializingBean, DisposableBean {
	private Log m_logger = LogFactory.getLog(getClass());
	
	private boolean		m_enabled;		//
	private boolean		m_tls;			//
	private int			m_port = 2525;	//
	private SMTPServer	m_server;		//
	private String		m_bindAddress;	//
	private String		m_keystorePass;	//
	private String		m_keystoreFile;	//
	private String 		m_password;		//
	private String		m_username;		//

	// The following control which key material to use by default.
	private final static String DEFAULT_KEYSTORE_PASS	= "changeit";
	private final static String	DEFAULT_KEYSTORE_FILE	= "conf/.keystore";
	
	public void setEnabled(boolean enabled) {
		m_enabled = enabled;
		m_logger.debug("Inbound SMTP Server:  " + (enabled ? "Enabled." : "Disabled."));
	}
	@Override
	public boolean isEnabled() {
		return m_enabled;
	}

	public void setTls(boolean tls) {
		m_tls = tls;
		m_logger.debug("Inbound SMTP Server:  " + (tls ? "Will announce TLS support." : "Will not announce TLS support."));
	}
	@Override
	public boolean isTls() {
		return m_tls;
	}

	public void setBindAddress(String bindAddress) {
		if (!(MiscUtil.hasString(bindAddress))) {
			bindAddress = null;
		}
		m_bindAddress = bindAddress;
		m_logger.debug("Inbound SMTP Server:  m_bindAddress is set to:  \"" + ((null == bindAddress) ? "null" : bindAddress) + "\".");
	}
	@Override
	public String getBindAddress() {
		return m_bindAddress;
	}
	
	public void setUsername(String username) {
		if (!(MiscUtil.hasString(username))) {
			username = null;
		}
		m_username = username;
		m_logger.debug("Inbound SMTP Server:  m_username is set to:  \"" + ((null == username) ? "null" : username) + "\".");
	}
	@Override
	public String getUsername() {
		return m_username;
	}
	
	public void setPassword(String password) {
		if (!(MiscUtil.hasString(password))) {
			password = null;
		}
		m_password = password;
		m_logger.debug("Inbound SMTP Server:  m_password is set to:  \"" + ((null == password) ? "null" : "<sorry, it's not logged>") + "\".");
	}
	public String getPassword() {
		if (null == m_password) {
			m_password = SPropsUtil.getString("smtp.service.authentication.password", "");
		}
		return ((0 == m_password.length()) ? null : m_password);
	}
	
	public void setPort(int port) {
		m_port = port;
		m_logger.debug("Inbound SMTP Server:  m_port is set to:  \"" + port + "\".");
	}
	@Override
	public int getPort() {
		return m_port;
	}
	
	private ZoneModule m_zoneModule;
	public void setZoneModule(ZoneModule zoneModule) {
		m_zoneModule = zoneModule;
	}
	public ZoneModule getZoneModule() {
		return m_zoneModule;
	}
	
	private BinderModule m_binderModule;
	public void setBinderModule(BinderModule binderModule) {
		m_binderModule = binderModule;
	}
	public BinderModule getBinderModule() {
		return m_binderModule;
	}

	public void setKeystorePass(String keystorePass) {
		m_keystorePass = keystorePass;
		m_logger.debug("Inbound SMTP Server:  m_keystorePass is set to:  \"" + ((null == m_keystorePass) ? "null" : "<sorry, it's not logged>") + "\".");
	}
	public String getKeystorePass() {
		if (!(MiscUtil.hasString(m_keystorePass))) {
			setKeystorePass(SPropsUtil.getString("smtp.service.keystore.password", DEFAULT_KEYSTORE_PASS));
		}
		return m_keystorePass;
	}
	
	public void setKeystoreFile(String keystoreFile) {
		m_keystoreFile = keystoreFile;
		m_logger.debug("Inbound SMTP Server:  m_keystoreFile is set to:  \"" + m_keystoreFile + "\".");
	}
	@Override
	public String getKeystoreFile() {
		if (!(MiscUtil.hasString(m_keystoreFile))) {
		     setKeystoreFile(DEFAULT_KEYSTORE_FILE);
		}
		return m_keystoreFile;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		// Is the inbound SMTP m_server is not m_enabled...
		if(!(isEnabled())) {
			// ...bail.
			return;
		}
		
		// Which, if any, specific address is the inbound SMTP m_server
		// supposed to be bound to?
		InetAddress	iNetAddr;
		if (!(MiscUtil.hasString(m_bindAddress))) {
			iNetAddr = null;
			m_logger.debug("Inbound SMTP Server:  No m_bindAddress, binding to all addresses.");
		}
		else if (m_bindAddress.equals("localhost")) {
			iNetAddr = InetAddress.getLocalHost();
			m_logger.debug("Inbound SMTP Server:  Binding to \"localhost\".");
		}
		else {
			try {
				iNetAddr = InetAddress.getByName(m_bindAddress);
				m_logger.debug("Inbound SMTP Server:  Binding to \"" + m_bindAddress + "\".");
			}
			catch (UnknownHostException e) {
				iNetAddr = null;
				setEnabled(false);
				m_logger.error("Inbound SMTP Server:  Cannot resolve m_bindAddress.  Inbound SMTP m_server will be disabled.", e);
			}
		}
		m_logger.debug("Inbound SMTP Server:  " + ((null == iNetAddr) ? "Bound to all addresses." : "Bound to a specific address."));
		
		// If the bind address evaluation failed...
		if(!(isEnabled())) {
			// ...bail.
			m_logger.debug("Inbound SMTP Server:  Address binding failed, inbound SMTP m_server is disabled.");
			return;
		}
			
		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
		// Construct the SMTPServer object.  We must do this prior to
		// the TLS handling because it loads a default SSLFilter
		// that the TLS handling needs to override.  If we don't do
		// this first, it would override the SSLFilter setup in the TLS
		// handling below.
		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

		// Key store for key and signing certificates.
		String ksPath = getKeystoreFile();
		if (!(new File(ksPath).isAbsolute())) {
			String ksTail = ksPath;
			if (!(ksTail.startsWith(File.separator))) {
				ksTail = (File.separator + ksTail); 
			}
			ksTail = (File.separator + ".." + File.separator + ".." + ksTail);
			ksPath = SpringContextUtil.getServletContext().getRealPath(ksTail);
		}
		InputStream keyStoreIS = new FileInputStream(ksPath);
		char[] keyktorePass = getKeystorePass().toCharArray();
		KeyStore ksKeys = KeyStore.getInstance("JKS");
		ksKeys.load(keyStoreIS, keyktorePass);
		 
		// KeyManagers decide which key material to use.
		String kmfAlgorithm = KeyManagerFactory.getDefaultAlgorithm();	// Was "SunX509" which only seems to work with an IBM JDK.
		m_logger.debug("KeyManagerFactory Algorithm:  " + kmfAlgorithm);
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(kmfAlgorithm);
		kmf.init(ksKeys, keyktorePass);
		KeyManager[] keyManagers = kmf.getKeyManagers();
		 
		// SSLContext based on the Tomcat keystore and default trust
		// managers.
		final SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(keyManagers, null, new java.security.SecureRandom());

		// MessageHandler to use for messages as they're received.
		MessageHandlerFactory mhf = new MessageHandlerFactory() {
			@Override
			public MessageHandler create(MessageContext ctx) {
				return new Handler();
			}
		};
		
		m_server = new SMTPServer(mhf) {
			@Override
			public SSLSocket createSSLSocket(Socket socket) throws IOException {
				InetSocketAddress remoteAddress = ((InetSocketAddress) socket.getRemoteSocketAddress());
				
				SSLSocketFactory sf = sslContext.getSocketFactory();
				SSLSocket s = ((SSLSocket) (sf.createSocket(socket, remoteAddress.getHostName(), socket.getPort(), true)));
 
				// We are a server.
				s.setUseClientMode(false);
 
				// Select strong protocols and cipher suites.
				s.setEnabledProtocols(   SMTPStrongTls.intersection(s.getSupportedProtocols(),    SMTPStrongTls.ENABLED_PROTOCOLS)    );
				s.setEnabledCipherSuites(SMTPStrongTls.intersection(s.getSupportedCipherSuites(), SMTPStrongTls.ENABLED_CIPHER_SUITES));
 
				// Client must authenticate
				// s.setNeedClientAuth(true);
 
				return s;
			}
		};

		// If we have both a username and password to authenticate
		// with...
		if (MiscUtil.hasString(getUsername()) && MiscUtil.hasString(getPassword())) {
			// ...set an AuthenticationHandler to handle it.
			m_server.setAuthenticationHandlerFactory(new EasyAuthenticationHandlerFactory(new Authenticator()));
		}
		
		// Finally, complete the initializations of the inbound SMTP
		// m_server.
		m_server.setEnableTLS(m_tls);
		m_server.setHideTLS( !m_tls);
		m_server.setBindAddress(iNetAddr);
		m_server.setPort(m_port);
		m_server.start();
	}

	@Override
	public void destroy() throws Exception {
		if(m_server != null) {
			m_server.stop();
		}
	}

	/*
	 * Inner class that will service an authentication request into the
	 * SMTP m_server. 
	 */
	private class Authenticator implements UsernamePasswordValidator {
		public Authenticator() {
			m_logger.debug("Inbound SMTP Server:  Authentication enabled.");
		}
		
		@Override
		public void login(String username, String password) throws LoginFailedException {
			m_logger.debug("Inbound SMTP Server:  login('" + username + "')");
			if ((!(MiscUtil.hasString(username))) || (!(username.equalsIgnoreCase(getUsername()))) ||
			    (!(MiscUtil.hasString(password))) || (!(password.equals(          getPassword())))) {
				m_logger.debug("...login failed.");
				throw new LoginFailedException("Inbound SMTP server - Authentication Failed");
			}
			m_logger.debug("...login succeeded.");
		}
	}

	/*
	 * Inner class that manages messages received by the SMTP m_server.
	 */
	private class Handler implements MessageHandler {
		private String m_from;
		private List<Recipient> m_recipients; 
		
		public Handler() {
			m_recipients = new LinkedList<Recipient>();
		}

		@Override
		public void done() {
			// Nothing to do.
		}
		
		@Override
		public void from(String from) throws RejectException {
			m_from = from;
		}
		
		@Override
		public void recipient(String recipient) throws RejectException {
			// Parse m_recipients now, so other m_recipients can be handled
			// by someone else.
			String[] parts = recipient.split("@");
			if(parts.length != 2)  throw new RejectException(550, "Requested action not taken: mailbox " + recipient + " not known.");
			String localPart = parts[0];
			String hostname = parts[1];

			// No request context.
			Long zoneId = getZoneModule().getZoneIdByVirtualHost(hostname);
			if (!getZoneModule().getZoneConfig(zoneId).getMailConfig().isSimpleUrlPostingEnabled()) {
				throw new RejectException(550, "Requested action not taken: mailbox " + recipient + " unavailable.  Simple URL posting is disabled.");
			}
			
			// Skip modules to load info, so don't have to worry about
			// user context.
			SimpleName simpleUrl = getCoreDao().loadSimpleNameByEmailAddress(localPart, zoneId);
			if (simpleUrl == null || !simpleUrl.getBinderType().equals(EntityType.folder.name())) {
				throw new RejectException(550, "Requested action not taken: mailbox " + recipient + " unavailable.  Target entity is not a folder.");
			}
			Binder binder = getCoreDao().loadBinder(simpleUrl.getBinderId(), zoneId);
			if(!binder.getPostingEnabled()) {
				throw new RejectException(550, "Requested action not taken: mailbox " + recipient + " unavailable.  Posting disabled on target binder.");
			}
			m_recipients.add(new Recipient(recipient, simpleUrl));
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public void data(InputStream data) throws TooMuchDataException, IOException, RejectException {
			Session session = Session.getDefaultInstance(new Properties());
			SessionUtil.sessionStartup();
			try {
				MimeMessage msgs[] = new MimeMessage[1];

				msgs[0] = new MimeMessage(session, data);
				/*
					// - - - - - - - - - - - - - - - - - - - - - - - - 
					// DRF (20141119):  I added this to debug a MIME
					//    stream a customer was having problems with.
					//    If you use this block in place of the
					//    'new MimeMessage(session, data);' above and
					//    set a breakpoint on the 'ba = s.getBytes();',
					//    you can patch 's' with your MIME string so
					//    that it gets processed instead of the data
					//    passed in.
					// - - - - - - - - - - - - - - - - - - - - - - - -
 
					byte[] baIn = new byte[data.available()];
					int baSize = data.read(baIn);
					byte[] ba = new byte[baSize];
					System.arraycopy(baIn, 0, ba, 0, baSize);
					String s = new String(ba);
					
					ba = s.getBytes();	// Breakpoint here to patch 's'!!!
										
					ByteArrayInputStream bais = new ByteArrayInputStream(ba);
					msgs[0] = new MimeMessage(session, bais);	//! data);
				*/
				
				boolean msgInitiallyDeleted = msgs[0].isSet(Flags.Flag.DELETED);
				List errors = new ArrayList();
				for(Recipient recipient : m_recipients) {
					m_logger.debug("Delivering new message to " + recipient.m_email);			
					//Run as background processing agent, same as other posting jobs.  
					User user = getProfileDao().getReservedUser(ObjectKeys.JOB_PROCESSOR_INTERNALID, recipient.m_simpleName.getId().getZoneId());
					RequestContextUtil.setThreadContext(user).resolve();
					
					Binder binder = getCoreDao().loadBinder(recipient.m_simpleName.getBinderId(),recipient.m_simpleName.getId().getZoneId());
					EmailPoster processor = (EmailPoster)processorManager.getProcessor(binder,EmailPoster.PROCESSOR_KEY);
					errors.addAll(processor.postMessages((Folder)binder, recipient.m_email, msgs, session, null));
					msgs[0].setFlag(Flags.Flag.DELETED, msgInitiallyDeleted);
			   		RequestContextHolder.clear();
			   		getCoreDao().clear(); // Clear session in case next m_from different zone.
				}
				msgs[0].setFlag(Flags.Flag.DELETED, true);
				if(errors.size() > 0) {
					Message m = (Message) errors.get(0);
					throw new RejectException(554, m.getSubject());
				}
			} catch (javax.mail.MessagingException ex) {
				m_logger.debug("Error processing message to " + m_from + ": " + ex.getMessage());
				throw new RejectException(554, "Server error");
			} finally {
				SessionUtil.sessionStop();
		   		RequestContextHolder.clear();
			}
		}

		/*
		 * Inner class used to bind the email address of a recipient
		 * with their simple name.
		 */
		private class Recipient {
			private String m_email;
			private SimpleName m_simpleName;
			public Recipient(String email, SimpleName simpleName) {
				m_email = email;
				m_simpleName = simpleName;
			}
		}
	}
}
