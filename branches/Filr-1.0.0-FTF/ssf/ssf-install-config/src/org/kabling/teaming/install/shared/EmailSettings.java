package org.kabling.teaming.install.shared;

import java.io.Serializable;

/**
 * Email Configuration Settings
 * 
 * Edit the InternalInboundSMTP, Outbound and Inbound settings as required.
 * 
 * InternalInboundSMTP: For the internal SMTP server, you can enable it, specify the bind address the server is to listen on, specify the
 * port the server is to listen on and specify whether the server should announce the availability of a TLS connection.
 * 
 * An empty bindAddress on the InternalInboundSMTP will cause Teaming's internal SMTP server to listen on the specified port on ALL
 * addresses.
 * 
 * Outbound:
 * 
 * Inbound: (Deprecated) For inbound mail (postings) you need to specify either pop3 or imap (or pop3s/imaps for SSL connections to mail
 * servers), and fill out the settings for which one you choose. These settings are not used until you enable incoming email within the
 * product. If you do not plan on using inbound email, you can ignore these settings.
 **/
public class EmailSettings implements Serializable
{

	public enum EmailProtocol
	{
		SMTP, SMTPS
	}

	private static final long serialVersionUID = -645027619353219289L;

	// Internal Inbound SMTP Settings
	private boolean internalInboundSMTPEnabled;
	private String internalInboundSMTPBindAddress;
	private int internalInboundSMTPPort;
	private boolean internalInboundSMTPTLSEnabld;

	// Outbound
	private String defaultTZ;
	private boolean allowSendToAllUsers;
	private EmailProtocol transportProtocol;
	

	// SMTP
	private String smtpHost;
	private String smtpUser;
	private int smtpPort;
	private String smtpPassword;
	private boolean smtpSendPartial;
	private boolean smtpAuthEnabled;
	private int smtpConnectionTimeout;

	// SMTPS

	private String smtpsHost;
	private String smtpsUser;
	private int smtpsPort;
	private String smtpsPassword;
	private boolean smtpsSendPartial;
	private boolean smtpsAuthEnabled;
	private int smtpsConnectionTimeout;

	// Inbound is not done as it has been deprecated with Boulder

	public EmailSettings()
	{
	}

	public boolean isInternalInboundSMTPEnabled()
	{
		return internalInboundSMTPEnabled;
	}

	public void setInternalInboundSMTPEnabled(boolean internalInboundSMTPEnabled)
	{
		this.internalInboundSMTPEnabled = internalInboundSMTPEnabled;
	}

	public String getInternalInboundSMTPBindAddress()
	{
		return internalInboundSMTPBindAddress;
	}

	public void setInternalInboundSMTPBindAddress(String internalInboundSMTPBindAddress)
	{
		this.internalInboundSMTPBindAddress = internalInboundSMTPBindAddress;
	}

	public int getInternalInboundSMTPPort()
	{
		return internalInboundSMTPPort;
	}

	public void setInternalInboundSMTPPort(int internalInboundSMTPPort)
	{
		this.internalInboundSMTPPort = internalInboundSMTPPort;
	}

	public boolean isInternalInboundSMTPTLSEnabld()
	{
		return internalInboundSMTPTLSEnabld;
	}

	public void setInternalInboundSMTPTLSEnabld(boolean internalInboundSMTPTLSEnabld)
	{
		this.internalInboundSMTPTLSEnabld = internalInboundSMTPTLSEnabld;
	}

	public String getDefaultTZ()
	{
		return defaultTZ;
	}

	public void setDefaultTZ(String defaultTZ)
	{
		this.defaultTZ = defaultTZ;
	}

	public boolean isAllowSendToAllUsers()
	{
		return allowSendToAllUsers;
	}

	public void setAllowSendToAllUsers(boolean allowSendToAllUsers)
	{
		this.allowSendToAllUsers = allowSendToAllUsers;
	}

	public EmailProtocol getTransportProtocol()
	{
		return transportProtocol;
	}

	public void setTransportProtocol(EmailProtocol transportProtocol)
	{
		this.transportProtocol = transportProtocol;
	}

	public String getSmtpHost()
	{
		return smtpHost;
	}

	public void setSmtpHost(String smtpHost)
	{
		this.smtpHost = smtpHost;
	}

	public String getSmtpUser()
	{
		return smtpUser;
	}

	public void setSmtpUser(String smtpUser)
	{
		this.smtpUser = smtpUser;
	}

	public int getSmtpPort()
	{
		return smtpPort;
	}

	public void setSmtpPort(int smtpPort)
	{
		this.smtpPort = smtpPort;
	}

	public boolean isSmtpSendPartial()
	{
		return smtpSendPartial;
	}

	public void setSmtpSendPartial(boolean smtpSendPartial)
	{
		this.smtpSendPartial = smtpSendPartial;
	}

	public boolean isSmtpAuthEnabled()
	{
		return smtpAuthEnabled;
	}

	public void setSmtpAuthEnabled(boolean smtpAuthEnabled)
	{
		this.smtpAuthEnabled = smtpAuthEnabled;
	}

	public String getSmtpsHost()
	{
		return smtpsHost;
	}

	public void setSmtpsHost(String smtpsHost)
	{
		this.smtpsHost = smtpsHost;
	}

	public String getSmtpsUser()
	{
		return smtpsUser;
	}

	public void setSmtpsUser(String smtpsUser)
	{
		this.smtpsUser = smtpsUser;
	}

	public int getSmtpsPort()
	{
		return smtpsPort;
	}

	public void setSmtpsPort(int smtpsPort)
	{
		this.smtpsPort = smtpsPort;
	}

	public boolean isSmtpsSendPartial()
	{
		return smtpsSendPartial;
	}

	public void setSmtpsSendPartial(boolean smtpsSendPartial)
	{
		this.smtpsSendPartial = smtpsSendPartial;
	}

	public boolean isSmtpsAuthEnabled()
	{
		return smtpsAuthEnabled;
	}

	public void setSmtpsAuthEnabled(boolean smtpsAuthEnabled)
	{
		this.smtpsAuthEnabled = smtpsAuthEnabled;
	}

	public String getSmtpPassword() {
		return smtpPassword;
	}

	public void setSmtpPassword(String smtpPassword) {
		this.smtpPassword = smtpPassword;
	}

	public String getSmtpsPassword() {
		return smtpsPassword;
	}

	public void setSmtpsPassword(String smtpsPassword) {
		this.smtpsPassword = smtpsPassword;
	}

	public int getSmtpConnectionTimeout()
	{
		return smtpConnectionTimeout;
	}

	public void setSmtpConnectionTimeout(int smtpConnectionTimeout)
	{
		this.smtpConnectionTimeout = smtpConnectionTimeout;
	}

	public int getSmtpsConnectionTimeout()
	{
		return smtpsConnectionTimeout;
	}

	public void setSmtpsConnectionTimeout(int smtpsConnectionTimeout)
	{
		this.smtpsConnectionTimeout = smtpsConnectionTimeout;
	}
	
	
}
