package com.sitescape.team.smtp.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import org.subethamail.smtp.MessageListener;
import org.subethamail.smtp.TooMuchDataException;
import org.subethamail.smtp.server.SMTPServer;


import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.context.request.RequestContextUtil;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.PostingDef;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.mail.EmailPoster;
import com.sitescape.team.module.zone.ZoneModule;
import com.sitescape.team.smtp.SMTPManager;
import com.sitescape.team.util.SessionUtil;

public class SMTPManagerImpl extends CommonDependencyInjection implements SMTPManager, SMTPManagerImplMBean, InitializingBean, DisposableBean, MessageListener {

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
			Collection<MessageListener> listeners = new ArrayList<MessageListener>(1);
			listeners.add(this);
			this.server = new SMTPServer(listeners);
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

	public boolean accept(String from, String recipient)
	{
		if(determineSender(from, recipient) == null) {
			return false;
		}
		PostingDef posting = getCoreDao().findPosting(recipient, RequestContextHolder.getRequestContext().getZoneId());
		return posting != null;
	}

	/**
	 * Cache the messages in memory. Now avoids unnecessary memory copying. 
	 */
	public void deliver(String from, String recipient, InputStream data) throws TooMuchDataException, IOException
	{
		SessionUtil.sessionStartup();
		try {
			if(determineSender(from, recipient) == null) {
				logger.warn("accept() found sender, but deliver() cannot, mail from " + from + " to " + recipient);
				return;
			}
			PostingDef posting = getCoreDao().findPosting(recipient, RequestContextHolder.getRequestContext().getZoneId());
			if(posting == null) {
				logger.debug("Unable to find PostingDef for message to " + from);
				return;
			}
	
			logger.debug("Delivering new message to " + recipient);
			Folder folder = (Folder)posting.getBinder();
			Session session = Session.getDefaultInstance(new Properties());
			EmailPoster processor = (EmailPoster)processorManager.getProcessor(folder,EmailPoster.PROCESSOR_KEY);
			try {
				MimeMessage msgs[] = {new MimeMessage(session, data)};
				processor.postMessages(folder, posting, msgs, session);
			} catch (javax.mail.MessagingException ex) {
				logger.debug("Error processing message to " + from + ": " + ex.getMessage());
			}
		} finally {
			SessionUtil.sessionStop();
		}
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
